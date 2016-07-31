package com.shareyour.antonio.sdcloud.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.helpers.Serializer;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by antonio on 21/06/15.
 */
public class NewPostFragment extends DialogFragment {

    private final static String BUNDLE_AMBITO_ID = "ambitoId";


    private int mTextLimitCounter = 140;

    private Long mAmbitoId;

    private PostProxy mPost;

    private TextView mUtenteTextView;

    private EditText mTextEdit;

    private ImageButton mSendButton;

    private TextView mCounter;


    public static NewPostFragment newInstance(Long ambitoId){
        Bundle inputBundle = new Bundle();
        inputBundle.putLong(BUNDLE_AMBITO_ID, ambitoId);
        NewPostFragment fragment = new NewPostFragment();
        fragment.setArguments(inputBundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState == null ){
            Bundle inputBundle = getArguments();
            mAmbitoId = inputBundle.getLong(BUNDLE_AMBITO_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_post,null);

        mCounter = (TextView) view.findViewById(R.id.post_textCounter);

        mTextEdit = (EditText) view.findViewById(R.id.newPost_text);
        mTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCounter.setText(Integer.toString(mTextLimitCounter - count));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        UtenteSessionManager manager = UtenteSessionManager.get(getActivity());
        mUtenteTextView = (TextView) view.findViewById(R.id.newPost_utente);
        mUtenteTextView.setText(manager.getUtente().getNome() +" "+ manager.getUtente().getCognome());

        mSendButton = (ImageButton) view.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Launch AsyncTask*/
                /*Controllo su input*/
                String editText = mTextEdit.getText().toString();
                if( editText.trim().length() == 0 ) {
                    Toast.makeText(getActivity(), getString(R.string.post_emptyText), Toast.LENGTH_LONG).show();
                }else{
                    String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
                    new PublishPostTask().execute(accessToken,editText);
                }
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    private void sendResult(int resultCode){
        if( resultCode == Activity.RESULT_CANCELED ) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
        }else if( resultCode == Activity.RESULT_OK ){
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,null);
            this.dismiss();
        }
    }

    private class PublishPostTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSendButton.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String accessToken = params[0];
            String edited = params[1];
            UtenteProxy utente = UtenteSessionManager.get(getActivity()).getUtente();
            mPost = new PostProxy();
            mPost.setTesto(edited);
            mPost.setIdUtente(utente.getId());
            mPost.setIdAmbito(mAmbitoId);
            mPost.setNomeUtente(utente.getNome());
            mPost.setCognomeUtente(utente.getCognome());
            mPost.setData(new DateTime(new Date()));

            ClientAPI.Builder builder = new ClientAPI.Builder( AndroidHttp.newCompatibleTransport(),
                                                new AndroidJsonFactory(),null);
            builder.setApplicationName(NewPostFragment.this.getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI clientAPI = builder.build();
            try {
                /*Return a boolean*/
                clientAPI.addPostAmbito(mAmbitoId,accessToken,mPost).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            /*return risultato chiamata*/
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
                sendResult(Activity.RESULT_OK);
            else
                sendResult(Activity.RESULT_CANCELED);
        }
    }
}
