package com.shareyour.antonio.sdcloud.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.ListHallActiivty;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.RegistrazioneActivity;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by antonio on 14/06/15.
 */
public class LoginFragment extends Fragment {


    public EditText mEmailField;

    public EditText mPasswordField;

    public Button mLoginButton;

    public Button mRegistratiButton;

    public LinearLayout mLoadingLinear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtenteSessionManager manager = UtenteSessionManager.get(getActivity());
        UtenteProxy utente = manager.getUtente();
        if( utente != null ){
            /*Utente gia loggato*/
            goToListHall();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        mLoadingLinear = (LinearLayout) view.findViewById(R.id.login_LoadingLinear);
        mEmailField = (EditText) view.findViewById(R.id.loginEmailField);
        mPasswordField = (EditText) view.findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) view.findViewById(R.id.loginButton);
        mRegistratiButton = (Button) view.findViewById(R.id.registratiButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                if (email.length() == 0 || mPasswordField.length() == 0) {
                    Toast.makeText(getActivity(), R.string.login_insertInput, Toast.LENGTH_LONG).show();
                    return;
                }
                String cryptedPassword = cryptMD5(password);
                new LoginTask(LoginFragment.this).execute(email, cryptedPassword);
            }
        });

        mRegistratiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegistrazioneActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

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


    public void goToListHall(){
        Intent intent = new Intent(getActivity(), ListHallActiivty.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private static class LoginTask extends AsyncTask<String,Void,UtenteProxy>{

        private WeakReference<LoginFragment> mFragmentRef;

        private LoginTask(LoginFragment fragment){
            mFragmentRef = new WeakReference<LoginFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginFragment fragment = mFragmentRef.get();
            fragment.mLoadingLinear.setVisibility(View.VISIBLE);
            fragment.mLoginButton.setEnabled(false);
            fragment.mRegistratiButton.setEnabled(false);
        }

        @Override
        protected UtenteProxy doInBackground(String... strings) {
            String email = strings[0];
            String password = strings[1];
            LoginFragment fragment = mFragmentRef.get();
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setRootUrl("https://sd-app-970.appspot.com/_ah/api/");
            builder.setApplicationName(fragment.getResources().getString(R.string.app_name));
            ClientAPI service = builder.build();
            UtenteProxy utente = null;
            try{
                utente = service.login(email,password).execute();
            }catch (Exception e){
                e.printStackTrace();
            }

            return utente;
        }

        @Override
        protected void onPostExecute(UtenteProxy utenteProxy) {
            super.onPostExecute(utenteProxy);
            LoginFragment fragment = mFragmentRef.get();
            if( utenteProxy == null ){
                Toast.makeText(fragment.getActivity(),fragment.getString(R.string.login_errorMessage),Toast.LENGTH_LONG).show();
                fragment.mLoadingLinear.setVisibility(View.GONE);
                fragment.mLoginButton.setEnabled(true);
                fragment.mRegistratiButton.setEnabled(true);
            }else{
                UtenteSessionManager manager = UtenteSessionManager.get(fragment.getActivity());
                manager.setUtente(utenteProxy);
                manager.saveUtente();
                fragment.goToListHall();
            }
        }
    }
}
