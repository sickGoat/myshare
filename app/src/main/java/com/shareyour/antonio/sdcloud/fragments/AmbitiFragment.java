package com.shareyour.antonio.sdcloud.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.CategoriaProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.InteressiActivity;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.abstractfragment.MenuFragmnet;
import com.shareyour.antonio.sdcloud.adapters.AmbitoAdapter;
import com.shareyour.antonio.sdcloud.callbacksinterface.AmbitoClickListener;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.helpers.Serializer;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 28/06/15.
 */
public class AmbitiFragment extends MenuFragmnet implements AmbitoClickListener {

    private static final String BUNDLE_CATEGORIA_ID = "categoriaId";

    private static final String BUNDLE_CATEGORIA_NOME = "categoriaNome";

    private Long mCategoriaid;

    private String mCategoriaTest;

    private AmbitoAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private ProgressBar mProgressbar;

    private ImageButton mFloatingButton;

    private ArrayList<AmbitoProxy> mAmbiti = new ArrayList<>();

    private ArrayList<AmbitoProxy> mAmbitiGiaIscritto = new ArrayList<>();

    public static AmbitiFragment newInstance(Long categoriaId,String categoriaNome){
        Bundle args = new Bundle();
        args.putLong(BUNDLE_CATEGORIA_ID,categoriaId);
        args.putString(BUNDLE_CATEGORIA_NOME,categoriaNome);
        AmbitiFragment fragment = new AmbitiFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        mCategoriaid = args.getLong(BUNDLE_CATEGORIA_ID);
        mCategoriaTest = args.getString(BUNDLE_CATEGORIA_NOME);
        mAmbitiGiaIscritto.addAll(UtenteSessionManager.get(getActivity()).getIscrizioni());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
            new FetchAmbiti(this).execute(accessToken);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmbitiGiaIscritto = UtenteSessionManager.get(getActivity()).getIscrizioni();
        for(AmbitoProxy ambito : mAmbitiGiaIscritto ){
            for( int i = 0 ; i < mAmbiti.size() ; i++ ){
                if( ambito.getId().equals(mAmbiti.get(i).getId())){
                    mAmbiti.remove(i);
                    break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listhall,container,false);

        mProgressbar = (ProgressBar) view.findViewById(R.id.pbLoading);
        mProgressbar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.listhall_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFloatingButton = (ImageButton) view.findViewById(R.id.addIscrizioniFloating);
        mFloatingButton.setVisibility(View.GONE);
        mAdapter = new AmbitoAdapter(mAmbiti,this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAmbitoSelected(AmbitoProxy ambito) {
        Intent intent = new Intent(getActivity(),InteressiActivity.class);
        intent.putExtra(InteressiActivity.EXTRA_AMBITO, Serializer.serializeAmbito(ambito));
        startActivity(intent);
    }

    public Long getIdCategoria(){ return this.mCategoriaid; }

    public ArrayList<AmbitoProxy> getAmbitiGiaIscritto(){ return this.mAmbitiGiaIscritto; }

    public void addAmbiti(ArrayList<AmbitoProxy> ambiti){
        mProgressbar.setVisibility(View.GONE);

        for( AmbitoProxy ambito : mAmbitiGiaIscritto ){
            for( int i = 0 ; i < ambiti.size() ; i++ ){
                if( ambito.getId().equals(ambiti.get(i).getId())){
                    ambiti.remove(i);
                    break;
                }
            }
        }
        this.mAmbiti.addAll(ambiti);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAmbitoLongClicked(AmbitoProxy ambito) {}


    private static class FetchAmbiti extends AsyncTask<String,Void,ArrayList<AmbitoProxy>>{

        private WeakReference<AmbitiFragment> mFragmentRef;

        private FetchAmbiti(AmbitiFragment fragment){
            this.mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<AmbitoProxy> doInBackground(String... params) {
            String accessToken = params[0];
            Long idCategoria = mFragmentRef.get().getIdCategoria();
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            ArrayList<AmbitoProxy> ambiti = new ArrayList<>();
            try {
                ArrayList<AmbitoProxy> ambitiFetched = (ArrayList<AmbitoProxy>) service
                        .getAmbitiCategoria(accessToken,idCategoria).execute().getItems();
                if( ambitiFetched != null )
                    ambiti.addAll(ambitiFetched);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ambiti;
        }

        @Override
        protected void onPostExecute(ArrayList<AmbitoProxy> ambitoProxies) {
            super.onPostExecute(ambitoProxies);
            AmbitiFragment fragment = mFragmentRef.get();
            fragment.addAmbiti(ambitoProxies);
        }
    }
}
