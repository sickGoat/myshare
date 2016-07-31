package com.shareyour.antonio.sdcloud.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.CommentoProxy;
import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.util.DateTime;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.adapters.CommentoAdapter;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.helpers.Serializer;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by antonio on 21/06/15.
 */
public class  CommentiFragment extends DialogFragment {


    public static final String BUNDLE_POST = "post";

    private PostProxy mPost;

    private ArrayList<CommentoProxy> mCommenti = new ArrayList<>();

    private CommentoAdapter mAdapter;

    private ProgressBar mProgressBar;

    private TextView mUtenteTextView;

    private TextView mTestoTextView;

    private EditText mCommentoText;

    private ImageButton mSendButton;

    private RecyclerView mRecyclerView;



    public static CommentiFragment newInstance(PostProxy post){
        Bundle args = new Bundle();
        args.putString(BUNDLE_POST, Serializer.serializePost(post));
        CommentiFragment fragment = new CommentiFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState == null ){
            Bundle inputBundle = getArguments();
            mPost = Serializer.deserializePost(inputBundle.getString(BUNDLE_POST));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
            Long postId = mPost.getId();
            Pair<String,Long> inputPair = new Pair<>(accessToken,postId);
            new LoadCommentiTask(this).execute(inputPair);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_post,null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoading);
        mProgressBar.setVisibility(View.VISIBLE);

        mUtenteTextView = (TextView) view.findViewById(R.id.post_utente);
        mUtenteTextView.setText(mPost.getNomeUtente()+" "+mPost.getCognomeUtente());

        mTestoTextView = (TextView) view.findViewById(R.id.post_testo);
        mTestoTextView.setText(mPost.getTesto());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.post_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if( mAdapter == null ){
            mAdapter = new CommentoAdapter(mCommenti);
        }
        mRecyclerView.setAdapter(mAdapter);

        mTestoTextView = (TextView) view.findViewById(R.id.post_nuovoTesto);

        mCommentoText = (EditText) view.findViewById(R.id.post_nuovoTesto);

        mSendButton = (ImageButton) view.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testo = mCommentoText.getText().toString();
                if( testo.trim().length() == 0 ) {
                    Toast.makeText(getActivity(), "Non puoi inoltrare commenti vuoti", Toast.LENGTH_SHORT).show();
                    return;
                }
                UtenteProxy utente = UtenteSessionManager.get(getActivity()).getUtente();
                CommentoProxy commento = new CommentoProxy();
                commento.setIdPost(mPost.getId());
                commento.setIdUtente(utente.getId());
                commento.setNomeUtente(utente.getNome());
                commento.setCognomeUtente(utente.getCognome());
                commento.setTesto(testo);

                Pair<Long,CommentoProxy> inputPair = new Pair<Long, CommentoProxy>(mPost.getId(),commento);
                new SendCommentoTask(CommentiFragment.this).execute(inputPair);
            }
        });
        onViewCreated(view,savedInstanceState);
        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    public ProgressBar getProgressBar(){ return this.mProgressBar; }

    public void updateUI(List<CommentoProxy> commenti){
        mAdapter.addElements(commenti);
    }

    private static class LoadCommentiTask extends AsyncTask<Pair<String,Long>,Void,List<CommentoProxy>>{

        private WeakReference<CommentiFragment> mFragmentRef;

        private LoadCommentiTask(CommentiFragment fragment){
            this.mFragmentRef = new WeakReference<CommentiFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFragmentRef.get().getProgressBar().setVisibility(View.VISIBLE);
        }

        @Override
        protected List<CommentoProxy> doInBackground(Pair<String, Long>... params) {
            Pair<String,Long> input = params[0];
            String accessToken = input.first;
            Long postId = input.second;
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            List<CommentoProxy> commenti = new ArrayList<>();
            try {
                List<CommentoProxy> commentiP = service.getCommentiPost(accessToken, postId)
                        .setStartResult(0).setEndResult(2).execute().getItems();
                if( commentiP != null && commentiP.size() > 0)
                    commenti.addAll(commentiP);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commenti;
        }

        @Override
        protected void onPostExecute(List<CommentoProxy> commentoProxies) {
            super.onPostExecute(commentoProxies);
            CommentiFragment fragment = mFragmentRef.get();
            fragment.getProgressBar().setVisibility(View.GONE);
            if( commentoProxies != null ){
                fragment.updateUI(commentoProxies);
            }
        }
    }

    private static class SendCommentoTask extends AsyncTask<Pair<Long,CommentoProxy>,Void,Boolean>{

        private WeakReference<CommentiFragment> mFragmentRef;

        private SendCommentoTask(CommentiFragment fragment){
            mFragmentRef = new WeakReference<CommentiFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFragmentRef.get().getProgressBar().setVisibility(View.VISIBLE);
            Toast.makeText(mFragmentRef.get().getActivity(),"Invio commento...",Toast.LENGTH_SHORT);
        }

        @Override
        protected Boolean doInBackground(Pair<Long,CommentoProxy>... params) {
            Pair<Long,CommentoProxy> inputPair = params[0];
            Long idPost = inputPair.first;
            CommentoProxy commento = inputPair.second;
            commento.setData(new DateTime(new Date(), TimeZone.getTimeZone("GMT")));
            CommentiFragment fragment = mFragmentRef.get();
            String accessToken = UtenteSessionManager.get(fragment.getActivity()).getUtente().getAccessToken();
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(fragment.getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            try{
                service.addCommentoPost(idPost,accessToken,commento).execute();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if( aBoolean ){
                Toast.makeText(mFragmentRef.get().getActivity(),"Commento Inviato con successo",Toast.LENGTH_SHORT).show();
                mFragmentRef.get().dismiss();
            }
        }
    }
}
