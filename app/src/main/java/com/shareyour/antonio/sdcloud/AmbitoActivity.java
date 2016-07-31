package com.shareyour.antonio.sdcloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.shareyour.antonio.sdcloud.abstractactivities.LoggedActivity;
import com.shareyour.antonio.sdcloud.fragments.HallFragment;

/**
 * Created by antonio on 20/06/15.
 */
public class AmbitoActivity extends LoggedActivity {

    public static final String EXTRA_AMBITO = "ambito";

    public static final String EXTRA_AMBITO_TESTO = "ambitoTesto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(AmbitoActivity.class.getName(),"AmbitoActivity on create");
        Intent input = getIntent();
        Long idAmbito = input.getLongExtra(EXTRA_AMBITO,-1);
        String ambitoName = input.getStringExtra(EXTRA_AMBITO_TESTO);
        getActionBar().setTitle(ambitoName);
        getActionBar().setDisplayShowTitleEnabled(true);
        Log.v(AmbitoActivity.class.getName(),""+idAmbito);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, HallFragment.newInstance(idAmbito))
                    .commit();
        }
    }

}
