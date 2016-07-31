package com.shareyour.antonio.sdcloud.asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.LoginActivity;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;

/**
 * Created by antonio on 04/07/15.
 */
public class LogoutTask extends AsyncTask<Pair<String,UtenteProxy>,Void,Boolean> {

    Context mContext;

    public LogoutTask(Context ctx){ mContext = ctx;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressBar loadingBar = (ProgressBar) ((Activity) mContext).getWindow()
                .getDecorView().findViewById(R.id.pbLoading);
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Pair<String, UtenteProxy>... params) {
        Pair<String,UtenteProxy> input = params[0];
        String accessToken = input.first;
        UtenteProxy utente = input.second;
        ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                       new AndroidJsonFactory(),null);
        builder.setApplicationName(mContext.getString(R.string.app_name));
        builder.setRootUrl(JSONConstants.BASE_URL);
        ClientAPI service = builder.build();
        try {
            service.logout(accessToken,utente).execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if( aBoolean ){
            UtenteSessionManager.get(mContext).logoutUtente();
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }
    }
}
