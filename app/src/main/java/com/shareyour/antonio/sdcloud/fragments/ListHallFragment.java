package com.shareyour.antonio.sdcloud.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.AmbitoActivity;
import com.shareyour.antonio.sdcloud.CategorieActivity;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.abstractfragment.MenuFragmnet;
import com.shareyour.antonio.sdcloud.adapters.AmbitoAdapter;
import com.shareyour.antonio.sdcloud.asynctask.LogoutTask;
import com.shareyour.antonio.sdcloud.callbacksinterface.AmbitoClickListener;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.helpers.Serializer;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 20/06/15.
 */
public class ListHallFragment extends MenuFragmnet implements AmbitoClickListener {

    private AmbitoAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private ProgressBar mProgressBar;

    private LinearLayout mEmptyLayout;

    private ImageButton mAddIscrizioni;

    private ImageButton mAddIscrizioniFloating;

    private ArrayList<AmbitoProxy> mAmbiti = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        mAmbiti = UtenteSessionManager.get(getActivity()).getIscrizioni();
        mAdapter = new AmbitoAdapter(mAmbiti,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listhall,container,false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoading);
        if( mAmbiti.isEmpty() )
            mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.listhall_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AmbitoAdapter(mAmbiti,this);
        mRecyclerView.setAdapter(mAdapter);

        mEmptyLayout = (LinearLayout) view.findViewById(R.id.empty_view);

        mAddIscrizioni = (ImageButton) view.findViewById(R.id.add_iscrizioniButton);
        mAddIscrizioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategorieActivity();
            }
        });

        mAddIscrizioniFloating = (ImageButton) view.findViewById(R.id.addIscrizioniFloating);
        if(mAmbiti.isEmpty())
            mAddIscrizioniFloating.setVisibility(View.GONE);

        mAddIscrizioniFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategorieActivity();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
        new GetAmbitiTask(this).execute(accessToken);
    }

    private void goToCategorieActivity(){
        Intent intent = new Intent(getActivity(), CategorieActivity.class);
        startActivity(intent);
    }

    public ProgressBar getProgressBar(){ return this.mProgressBar; }

    public AmbitoAdapter getAdapter(){ return this.mAdapter; }

    public List<AmbitoProxy> getAmbiti(){ return this.mAmbiti; }

    public void setAdapter(AmbitoAdapter adapter){ this.mAdapter = adapter; }

    public LinearLayout getEmptyView(){ return this.mEmptyLayout; }

    public RecyclerView getRecyclerView(){ return this.mRecyclerView; }

    public void addAmbiti(ArrayList<AmbitoProxy> ambiti){
        mAmbiti = ambiti;
        mAdapter = new AmbitoAdapter(mAmbiti,this);
        mRecyclerView.setAdapter(mAdapter);
        mAddIscrizioniFloating.setVisibility(View.VISIBLE);
        UtenteSessionManager.get(getActivity()).addIscrizioni(ambiti);
    }

    public void deleteAmbito(Long idAmbito){
        AmbitoProxy ambito = null;
        for( int i = 0 ; i < mAmbiti.size() ; i++ ){
            if( mAmbiti.get(i).getId().equals(idAmbito)){
                ambito = mAmbiti.get(i);
                mAmbiti.remove(ambito);
                UtenteSessionManager.get(getActivity()).deleteIscrizioneById(ambito.getId());
                break;
            }
        }
        mAdapter.removeElement(ambito);
        if( mAdapter.getItemCount() == 0 ){
            mEmptyLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAmbitoSelected(AmbitoProxy ambito) {
        Intent intent = new Intent(getActivity(),AmbitoActivity.class);
        intent.putExtra(AmbitoActivity.EXTRA_AMBITO,ambito.getId());
        intent.putExtra(AmbitoActivity.EXTRA_AMBITO_TESTO,ambito.getTesto());
        startActivity(intent);
    }

    @Override
    public void onAmbitoLongClicked(final AmbitoProxy ambito) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.ambito_delete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UtenteProxy utente = UtenteSessionManager.get(getActivity()).getUtente();
                        Long idAmbito = ambito.getId();
                        Pair<UtenteProxy,Long> input = new Pair<UtenteProxy, Long>(utente,idAmbito);
                        new DeleteIscrizioneTask(ListHallFragment.this).execute(input);
                    }
                });
        builder.create().show();
    }


    private static class GetAmbitiTask extends AsyncTask<String,Void,List<AmbitoProxy>> {

        private WeakReference<ListHallFragment> mFragemntRef;

        private GetAmbitiTask(ListHallFragment fragment){
            this.mFragemntRef = new WeakReference<ListHallFragment>(fragment);
        }

        @Override
        protected List<AmbitoProxy> doInBackground(String... strings) {
            String accessToken = strings[0];
            ListHallFragment fragment = mFragemntRef.get();
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setRootUrl(JSONConstants.BASE_URL);
            builder.setApplicationName(fragment.getResources().getString(R.string.app_name));
            ClientAPI service = builder.build();
            List<AmbitoProxy> ambitoProxies = new ArrayList<AmbitoProxy>();
            try{
                List<AmbitoProxy> ambitiF = service.getIscrizioniUtente(accessToken).execute().getItems();
                ambitoProxies.addAll(ambitiF);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ambitoProxies;
        }

        @Override
        protected void onPostExecute(List<AmbitoProxy> ambitoProxies) {
            super.onPostExecute(ambitoProxies);
            Log.v(ListHallFragment.class.getName()," "+ambitoProxies.size());
            ListHallFragment fragment = mFragemntRef.get();
            fragment.getProgressBar().setVisibility(View.GONE);
            if( ambitoProxies.size() == 0 ){
                fragment.getRecyclerView().setVisibility(View.GONE);
                fragment.getEmptyView().setVisibility(View.VISIBLE);
            }else{
                fragment.addAmbiti((ArrayList<AmbitoProxy>) ambitoProxies);
            }
        }
    }

    private static class DeleteIscrizioneTask extends AsyncTask<Pair<UtenteProxy,Long>,Void,Long>{

        private WeakReference<ListHallFragment> mFragmentRef;

        private DeleteIscrizioneTask(ListHallFragment fragment){
            mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ListHallFragment fragment = mFragmentRef.get();
            fragment.getProgressBar().setVisibility(View.VISIBLE);
        }

        @Override
        protected Long doInBackground(Pair<UtenteProxy, Long>... params) {
            Pair<UtenteProxy,Long> input = params[0];
            UtenteProxy utente = input.first;
            Long idAmbito  = input.second;
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            try {
                service.editIscrizioneUtente(utente.getAccessToken(),idAmbito)
                        .setInteressi(new ArrayList<Long>()).execute();
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }
            return idAmbito;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            ListHallFragment fragment = mFragmentRef.get();
            fragment.getProgressBar().setVisibility(View.GONE);
            if( aLong != -1 ){
                UtenteSessionManager.get(fragment.getActivity()).deleteIscrizioneById(aLong);
                fragment.deleteAmbito(aLong);
            }else{
                Toast.makeText(fragment.getActivity(),"Qualcosa è andato storto",Toast.LENGTH_SHORT);
            }
        }
    }
}