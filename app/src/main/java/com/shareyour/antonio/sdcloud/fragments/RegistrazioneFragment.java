package com.shareyour.antonio.sdcloud.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.ClientAPIRequestInitializer;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.shareyour.antonio.sdcloud.LoginActivity;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.utilities.HelperClass;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by antonio on 17/06/15.
 */
public class RegistrazioneFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = RegistrazioneFragment.class.getName();

    private static final String DIALOG_DATE_TAG = "DialogDate";
    private static final int DIALOG_DATE_CODE = 0;

    private EditText mEmailfield;

    private EditText mPasswordfield;

    private EditText mNomefield;

    private EditText mCognomefield;

    private EditText mCittafield;

    private EditText mDatafield;

    private Spinner mSessofield;

    private Button mRegistrabutton;

    private UtenteProxy mUtente;

    private ProgressBar mPprogressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtente = new UtenteProxy();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_registrazione, container, false);

        mEmailfield = (EditText) view.findViewById(R.id.registrazione_email);

        mPasswordfield = (EditText) view.findViewById(R.id.registrazione_password);

        mNomefield = (EditText) view.findViewById(R.id.registrazione_nome);

        mCognomefield = (EditText) view.findViewById(R.id.registrazione_cognome);

        mCittafield = (EditText) view.findViewById(R.id.registrazione_citta);

        mDatafield = (EditText) view.findViewById(R.id.registrazione_dataN);

        mSessofield = (Spinner) view.findViewById(R.id.registrazione_sesso);

        mPprogressBar = (ProgressBar) view.findViewById(R.id.pbLoading);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sesso_field, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSessofield.setAdapter(spinnerAdapter);
        mSessofield.setOnItemSelectedListener(this);

        mRegistrabutton = (Button) view.findViewById(R.id.registrazione_button);
        mRegistrabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUtente.setEmail(mEmailfield.getText().toString());
                mUtente.setPassword(cryptMD5(mPasswordfield.getText().toString()));
                mUtente.setNome(mNomefield.getText().toString());
                mUtente.setCognome(mCognomefield.getText().toString());
                mUtente.setCitta(mCittafield.getText().toString());
                if( !checkInput() ){
                    Toast.makeText(getActivity(),R.string.registrazione_inputError,Toast.LENGTH_LONG).show();
                }else{
                    new RegistrazioneTask(RegistrazioneFragment.this).execute(mUtente);
                }
            }
        });

        mDatafield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.setTargetFragment(RegistrazioneFragment.this,DIALOG_DATE_CODE);
                fragment.show(getActivity().getSupportFragmentManager(),DIALOG_DATE_TAG);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) return;
        if( requestCode == DIALOG_DATE_CODE ){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE_DIALOG);
            DateTime dateTime = new DateTime(date);
            mUtente.setDataNascita(dateTime);
            /*Set EditView Text*/
            mDatafield.setText(HelperClass.formatDate(dateTime));
        }

    }


    public ProgressBar getProgressBar(){ return this.mPprogressBar; }

    /************ PRIVATE METHODS **************/

    private String cryptMD5(String password){
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private boolean checkInput(){
        if( mUtente.getEmail() == null ||
                mUtente.getPassword() == null ||
                mUtente.getNome() == null ||
                mUtente.getCognome() == null ||
                mUtente.getCitta() == null ||
                mUtente.getDataNascita() == null )
                    return false;

        return true;
    }


    /********* ADAPTER VIEW METHODS ******************/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if( position == 0 )
            mUtente.setSesso(true);//uomo
        else
            mUtente.setSesso(false);//cagna
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            mUtente.setSesso(true);
    }

    /*AsyncTask invio registrazione*/
    private static class RegistrazioneTask extends AsyncTask<UtenteProxy,Void,Boolean>{

        private WeakReference<RegistrazioneFragment> mFragment;

        private RegistrazioneTask(RegistrazioneFragment fragment){
            this.mFragment = new WeakReference<RegistrazioneFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RegistrazioneFragment fragment = mFragment.get();
            ProgressBar progressBar = fragment.getProgressBar();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(UtenteProxy... params) {
            UtenteProxy utente = params[0];
            Fragment fragment = mFragment.get();
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(fragment.getString(R.string.app_name));
            builder.setRootUrl("https://sd-app-970.appspot.com/_ah/api/");
            ClientAPI service = builder.build();
            try {
                service.registrazione(utente).execute();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            RegistrazioneFragment fragment = mFragment.get();
            fragment.getProgressBar().setVisibility(View.GONE);
            if( aBoolean){
                Toast.makeText(fragment.getActivity(), "Registrazione Avvenuta con successo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(fragment.getActivity(),LoginActivity.class);
                fragment.startActivity(intent);
            }else{
                Toast.makeText(fragment.getActivity(),"Registrazione non avvenuta",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
