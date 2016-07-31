package com.shareyour.antonio.sdcloud.abstractfragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.shareyour.antonio.sdcloud.ListHallActiivty;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.UtenteProfileActivity;
import com.shareyour.antonio.sdcloud.asynctask.LogoutTask;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

/**
 * Created by antonio on 04/07/15.
 */
public abstract class MenuFragmnet extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UtenteSessionManager manager = UtenteSessionManager.get(getActivity());
        switch (item.getItemId()){
            case R.id.logoutMenu:
                UtenteProxy utente = manager.getUtente();
                String accessToken = utente.getAccessToken();
                new LogoutTask(getActivity()).execute(new Pair<String, UtenteProxy>(accessToken,utente));
                return true;
            case R.id.mioProfiloMenu:
                Intent intent = new Intent(getActivity(), UtenteProfileActivity.class);
                intent.putExtra(UtenteProfileActivity.EXTRA_UTENTE,manager.getUtente().getId());
                startActivity(intent);
                return true;
            case R.id.mieIscrizioniMenu:
                Intent intent1 = new Intent(getActivity(), ListHallActiivty.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
