package com.shareyour.antonio.sdcloud.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.appspot.sd_app_970.clientAPI.model.UtenteInfo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.abstractfragment.MenuFragmnet;
import com.shareyour.antonio.sdcloud.adapters.AmbitoAdapter;
import com.shareyour.antonio.sdcloud.adapters.PostAdapter;
import com.shareyour.antonio.sdcloud.callbacksinterface.AmbitoClickListener;
import com.shareyour.antonio.sdcloud.callbacksinterface.PostClickListener;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;
import com.shareyour.antonio.sdcloud.utilities.FetchPostInput;
import com.shareyour.antonio.sdcloud.utilities.HelperClass;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by antonio on 27/06/15.
 */
public class UtenteProfileFragment extends MenuFragmnet implements AmbitoClickListener,PostClickListener {

    private static final String BUNDLE_UTENTE = "utenteId";

    private static final String CLASSIFICA_UTENTE_TAG = "classificaUTente";

    private static final String COMMENTI_TAG = "commenti";

    private static final int TO_FETCH = 5;

    public int lastEnd = 0;

    private Long mIdUtente;

    private UtenteInfo mUtenteInfo;

    private TextView mUtenteNomeCognome;

    private TextView mUtenteSesso;

    private TextView mUtenteCitta;

    private TextView mUtenteNascita;

    private ProgressBar mProgressBar;

    private RecyclerView mPostRecycler;

    private RecyclerView mAmbitoRecycler;

    private PostAdapter mPostAdapter;

    private AmbitoAdapter mAmbitoAdapter;

    private ArrayList<PostProxy> mPosts = new ArrayList<>();

    private ArrayList<AmbitoProxy> mAmbiti = new ArrayList<>();

    private boolean mLoading = false;

    public static UtenteProfileFragment newInstance(Long utenteId){
        Bundle input = new Bundle();
        input.putLong(BUNDLE_UTENTE, utenteId);
        UtenteProfileFragment fragment = new UtenteProfileFragment();
        fragment.setArguments(input);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if( savedInstanceState == null ){
            Bundle input = getArguments();
            mIdUtente = input.getLong(BUNDLE_UTENTE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
            Pair<String,Long> pairValue = new Pair<>(accessToken,mIdUtente);
            new FetchUtenteInfoTask(this).execute(pairValue);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_utente,container,false);

        mUtenteNomeCognome = (TextView) v.findViewById(R.id.utente_nomeCognome);
        mUtenteCitta = (TextView) v.findViewById(R.id.utente_citta);
        mUtenteNascita = (TextView) v.findViewById(R.id.utente_nascita);
        mUtenteSesso = (TextView) v.findViewById(R.id.utente_sesso);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pbLoading);
        mProgressBar.setVisibility(View.VISIBLE);

        mPostRecycler = (RecyclerView) v.findViewById(R.id.utente_post);
        mPostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPostRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItem = recyclerView.getChildCount();
                int totalItemCOunt = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();
                if ((totalItemCOunt - visibleItem) < (firstVisibleItem + 3)) {
                    if (!mLoading) {
                        String accesToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
                        FetchPostInput input = new FetchPostInput();
                        input.accessToken = accesToken;
                        input.startResult = lastEnd;
                        input.endResult = lastEnd + TO_FETCH;
                        mLoading = true;
                        new FetchPostUtente(UtenteProfileFragment.this).execute(new Pair<FetchPostInput, Long>(input, mIdUtente));
                        Toast.makeText(getActivity(),"Caricamento in corso...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mAmbitoRecycler = (RecyclerView) v.findViewById(R.id.utente_iscrizioni);
        mAmbitoRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPostAdapter = new PostAdapter(mPosts,this,getActivity());
        mAmbitoAdapter = new AmbitoAdapter(mAmbiti,this);

        mPostRecycler.setAdapter(mPostAdapter);
        mAmbitoRecycler.setAdapter(mAmbitoAdapter);


        return v;
    }

    public void updateUI(UtenteInfo utenteInfo){
        mUtenteNomeCognome.setText(utenteInfo.getNome() + " " + utenteInfo.getCognome());
        mUtenteCitta.setText(utenteInfo.getCitta());
        String sesso = utenteInfo.getSesso() ? "Uomo" : "Donna";
        mUtenteSesso.setText(sesso);
        String data = HelperClass.formatDate(utenteInfo.getDataNascita());
        mUtenteNascita.setText(data);

        if( utenteInfo.getIscrizioni() != null )
            mAmbitoAdapter.addElements((ArrayList<AmbitoProxy>) utenteInfo.getIscrizioni());
        if( utenteInfo.getPosts() != null ) {
            mPostAdapter.addElements((ArrayList<PostProxy>) utenteInfo.getPosts());
            lastEnd = utenteInfo.getPosts().size()+1;
        }

    }

    public void updateUIP(ArrayList<PostProxy> posts){
        if( posts.size() > 0 )
            mPostAdapter.addElements(posts);
        if( posts.size() == TO_FETCH ){
            mLoading = false;
            lastEnd = posts.size()+1;
        }
    }

    public Long getIdUtente(){return this.mIdUtente; }

    public UtenteInfo getUtenteInfo(){ return this.mUtenteInfo; }

    public void setUtenteInfo(UtenteInfo utente){ this.mUtenteInfo = utente; }

    public ProgressBar getProgressBar(){ return this.mProgressBar; }

    public PostAdapter getPostAdapter(){ return this.mPostAdapter; }

    public AmbitoAdapter getAmbitoAdapter(){ return this.mAmbitoAdapter; }

    @Override
    public void onAmbitoSelected(AmbitoProxy ambito) {
        /*Fragemnt vedere la classifica*/
        ClassificaFragment fragment = ClassificaFragment.newInstance(ambito.getId(),mIdUtente,ambito.getTesto());
        fragment.show(getActivity().getSupportFragmentManager(),CLASSIFICA_UTENTE_TAG);
    }

    @Override
    public void onAmbitoLongClicked(AmbitoProxy ambito) {}

    @Override
    public void onPostSelected(PostProxy post) {
        CommentiFragment fragment = CommentiFragment.newInstance(post);
        fragment.show(getActivity().getSupportFragmentManager(), COMMENTI_TAG);
    }

    private static class FetchUtenteInfoTask extends AsyncTask<Pair<String,Long>,Void,UtenteInfo>{

        private WeakReference<UtenteProfileFragment> mFragmentRef;

        private FetchUtenteInfoTask(UtenteProfileFragment fragment){
            this.mFragmentRef = new WeakReference<UtenteProfileFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar pbLoading = mFragmentRef.get().getProgressBar();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected UtenteInfo doInBackground(Pair<String, Long>... params) {
            Pair<String,Long> input = params[0];
            String accessToken = input.first;
            Long idUtente = input.second;
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            UtenteInfo utenteInfo = null;
            try {

                utenteInfo = service.getUtenteInfo(accessToken, idUtente).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return utenteInfo;
        }

        @Override
        protected void onPostExecute(UtenteInfo utenteInfo) {
            super.onPostExecute(utenteInfo);
            UtenteProfileFragment fragment = mFragmentRef.get();
            fragment.getProgressBar().setVisibility(View.GONE);
            if( utenteInfo != null ){
                fragment.updateUI(utenteInfo);
            }else {
                Toast.makeText(fragment.getActivity(),
                        "We got some problem bitch!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class FetchPostUtente extends AsyncTask<Pair<FetchPostInput,Long>,Void,ArrayList<PostProxy>>{

        private WeakReference<UtenteProfileFragment> mFragmentRef;

        private FetchPostUtente(UtenteProfileFragment fragment){
            mFragmentRef = new WeakReference<UtenteProfileFragment>(fragment);
        }

        @Override
        protected ArrayList<PostProxy> doInBackground(Pair<FetchPostInput,Long>... params) {
            Pair<FetchPostInput,Long> input = params[0];
            String accessToken = input.first.accessToken;
            int startResult = input.first.startResult;
            int endResult = input.first.endResult;
            Long idUtente = input.second;
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            ArrayList<PostProxy> post = new ArrayList<>();
            try {
                ArrayList<PostProxy> postF = (ArrayList<PostProxy>) service.getPostUtente(accessToken)
                        .setIdUtente(idUtente)
                        .setStartResult(startResult).setEndResult(endResult).execute().getItems();
                post.addAll(postF);
            } catch (Exception e) {
                e.printStackTrace();
                return post;
            }
            return post;
        }

        @Override
        protected void onPostExecute(ArrayList<PostProxy> postProxies) {
            super.onPostExecute(postProxies);
            mFragmentRef.get().updateUIP(postProxies);

        }
    }
}
