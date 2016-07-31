package com.shareyour.antonio.sdcloud;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.shareyour.antonio.sdcloud.abstractactivities.LoggedActivity;
import com.shareyour.antonio.sdcloud.fragments.UtenteProfileFragment;

/**
 * Created by antonio on 27/06/15.
 */
public class UtenteProfileActivity extends LoggedActivity {

    public static final String EXTRA_UTENTE = "utente";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent input = getIntent();
        Long idUtente = input.getLongExtra(EXTRA_UTENTE,-1);
        Log.v(UtenteProfileActivity.class.getName(), " " + idUtente);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, UtenteProfileFragment.newInstance(idUtente)).commit();
        }
    }

}
