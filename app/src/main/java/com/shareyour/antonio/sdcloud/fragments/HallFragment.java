package com.shareyour.antonio.sdcloud.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.InteresseProxy;
import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.abstractfragment.MenuFragmnet;
import com.shareyour.antonio.sdcloud.adapters.PostAdapter;
import com.shareyour.antonio.sdcloud.callbacksinterface.PostClickListener;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.helpers.Serializer;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;
import com.shareyour.antonio.sdcloud.utilities.FetchPostInput;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by antonio on 06/07/15.
 */
public class HallFragment extends MenuFragmnet implements PostClickListener {

    private static final String BUNDLE_AMBITO = "ambitoId";

    private static final String DIALOG_NEWPOST_TAG = "newPostTag";

    private static final int DIALOG_NEW_POST_CODE = 0;

    public static final String EXTRA_ADDED_POST = "newPost";

    private static final String DIALOG_COMMENTI_TAG = "commentiTag";

    private static final int DIALOG_COMMENTI_CODE = 1;

    public static final int TO_FETCH = 5;

    public int lastEnd = 0;

    private AmbitoProxy mAmbito;

    private ArrayList<InteresseProxy> mClassifica = new ArrayList<>();

    private ListView mClassificaRecyclerView;

    private RecyclerView mPostRecyclerView;

    private ImageButton mAddPostButton;

    private ProgressBar mProgressBar;

    private ClassificaAdapter mClassificaAdapter;

    private PostAdapter mPostAdapter;

    private boolean mLoading = false;

    public static HallFragment newInstance(Long idAmbito){
        Bundle args = new Bundle();
        args.putLong(BUNDLE_AMBITO,idAmbito);
        HallFragment fragment = new HallFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState == null ){
            Bundle args = getArguments();
            Long idAmbito = args.getLong(BUNDLE_AMBITO);
            mAmbito = UtenteSessionManager.get(getActivity()).getAmbito(idAmbito);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ambito,container,false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.pbLoading);
        mProgressBar.setVisibility(View.VISIBLE);

        mClassificaRecyclerView = (ListView) v.findViewById(R.id.ambito_classifica);
        mClassificaAdapter = new ClassificaAdapter(mClassifica);
        mClassificaRecyclerView.setAdapter(mClassificaAdapter);

        mPostRecyclerView = (RecyclerView) v.findViewById(R.id.ambito_post);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPostAdapter = new PostAdapter(new ArrayList<PostProxy>(),this,getActivity());
        mPostRecyclerView.setAdapter(mPostAdapter);
        mPostRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItem = recyclerView.getChildCount();
                int totalItemCOunt = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();
                if( (totalItemCOunt - visibleItem) < (firstVisibleItem + 3 )){
                    if( !mLoading ){
                        String accesToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
                        FetchPostInput input = new FetchPostInput();
                        input.accessToken = accesToken;
                        input.startResult = lastEnd;
                        input.endResult = lastEnd + TO_FETCH;
                        new FetchPostTask(HallFragment.this).execute(new Pair<FetchPostInput, Long>(input,mAmbito.getId()));
                        mLoading = true;
                        Toast.makeText(getActivity(),"Caricamento in corso...",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mAddPostButton = (ImageButton) v.findViewById(R.id.addPost_button);
        mAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPostFragment fragment = NewPostFragment.newInstance(mAmbito.getId());
                fragment.setTargetFragment(HallFragment.this, DIALOG_NEW_POST_CODE);
                fragment.show(getActivity().getSupportFragmentManager(), DIALOG_NEWPOST_TAG);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
            Long ambitoId = mAmbito.getId();
            new FetchClassificaTask(this).execute(new Pair<String, Long>(accessToken, ambitoId));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == Activity.RESULT_CANCELED)
            return;
        if( requestCode == DIALOG_NEW_POST_CODE ){
            String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
            FetchPostInput input = new FetchPostInput();
            input.accessToken = accessToken;
            input.startResult = lastEnd;
            input.endResult = lastEnd + 1;
            Long idAmbito = mAmbito.getId();
            new FetchPostTask(this).execute(new Pair<FetchPostInput, Long>(input, idAmbito));
        }

    }

    @Override
    public void onPostSelected(PostProxy post) {
        CommentiFragment fragment = CommentiFragment.newInstance(post);
        fragment.show(getActivity().getSupportFragmentManager(), DIALOG_COMMENTI_TAG);
    }

    public void swapPostAdapter(ArrayList<PostProxy> posts){
        mPostAdapter = new PostAdapter(posts,this,getActivity());
        mPostRecyclerView.setAdapter(mPostAdapter);
    }

    public ProgressBar getProgressBar(){ return this.mProgressBar; }

    public void updateUIC(ArrayList<InteresseProxy> mClassifica){
        mClassificaAdapter = new ClassificaAdapter(mClassifica);
        mClassificaRecyclerView.setAdapter(mClassificaAdapter);

    }

    public void updateUIP(ArrayList<PostProxy> posts){
        mPostAdapter.addElements(posts);
        if( posts.size() == TO_FETCH )
            mLoading = false;
        lastEnd = posts.size();
    }

    private class ClassificaAdapter extends ArrayAdapter<InteresseProxy>{


        private ClassificaAdapter(ArrayList<InteresseProxy> interessi){
            super(getActivity(), 0, interessi);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_classifica,null);
            }

            InteresseProxy interesseProxy = getItem(position);
            TextView textView = (TextView) convertView.findViewById(R.id.classifica_itemText);
            textView.setText(interesseProxy.getTesto());

            return convertView;
        }

        public void addElements(ArrayList<InteresseProxy> interessi){
            addAll(interessi);
            notifyDataSetChanged();
        }

    }

    private static class FetchClassificaTask extends AsyncTask<Pair<String,Long>,Void,Pair<ArrayList<InteresseProxy>,ArrayList<PostProxy>>>{

        private WeakReference<HallFragment> mFragmentRef;

        private FetchClassificaTask(HallFragment fragment){
            mFragmentRef = new WeakReference<HallFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFragmentRef.get().getProgressBar().setVisibility(View.VISIBLE);
        }

        @Override
        protected Pair<ArrayList<InteresseProxy>,ArrayList<PostProxy>> doInBackground(Pair<String, Long>... params) {
            Pair<String,Long> inputPair = params[0];
            String accessToken = inputPair.first;
            Long idAmbito = inputPair.second;

            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            ArrayList<InteresseProxy> classifica = new ArrayList<>();
            ArrayList<PostProxy> posts = new ArrayList<>();
            try {
                ArrayList<InteresseProxy> classificaF = (ArrayList<InteresseProxy>) service
                        .getClassificaAmbito(accessToken, idAmbito)
                        .execute().getItems();
                classifica.addAll(classificaF);

                ArrayList<PostProxy> postF = (ArrayList<PostProxy>) service
                        .getPostByAmbito(accessToken, idAmbito)
                        .setStartResult(mFragmentRef.get().lastEnd)
                        .setEndResult(mFragmentRef.get().lastEnd + TO_FETCH).execute().getItems();

                posts.addAll(postF);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return new Pair<>(classifica,posts);
        }

        @Override
        protected void onPostExecute(Pair<ArrayList<InteresseProxy>,ArrayList<PostProxy>> pairResult) {
            super.onPostExecute(pairResult);
            HallFragment fragment = mFragmentRef.get();
            fragment.getProgressBar().setVisibility(View.GONE);
            ArrayList<InteresseProxy> classifica = pairResult.first;
            ArrayList<PostProxy> posts = pairResult.second;

            fragment.updateUIP(posts);
            fragment.updateUIC(classifica);
        }
    }

    private static class FetchPostTask extends AsyncTask<Pair<FetchPostInput,Long>,Void,ArrayList<PostProxy>>{

        private WeakReference<HallFragment> mFragmentRef;

        private FetchPostTask(HallFragment fragment){
            mFragmentRef = new WeakReference<HallFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PostProxy> doInBackground(Pair<FetchPostInput, Long>... params) {
            Pair<FetchPostInput,Long> inputPair = params[0];
            String accessToken = inputPair.first.accessToken;
            int startResult = inputPair.first.startResult;
            int endResult = inputPair.first.endResult;
            Long idAmbito = inputPair.second;
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            ArrayList<PostProxy> post = new ArrayList<>();
            try {
                ArrayList<PostProxy> postF = (ArrayList<PostProxy>) service.getPostByAmbito(accessToken, idAmbito)
                        .setStartResult(startResult)
                        .setEndResult(endResult)
                        .execute().getItems();
                post.addAll(postF);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return post;
        }

        @Override
        protected void onPostExecute(ArrayList<PostProxy> postProxies) {
            super.onPostExecute(postProxies);
            HallFragment fragment = mFragmentRef.get();
            ProgressBar progressBar = fragment.getProgressBar();
            progressBar.setVisibility(View.GONE);
            fragment.updateUIP(postProxies);
        }
    }
}
