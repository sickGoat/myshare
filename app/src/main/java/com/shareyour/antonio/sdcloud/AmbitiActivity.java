package com.shareyour.antonio.sdcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.shareyour.antonio.sdcloud.abstractactivities.LoggedActivity;
import com.shareyour.antonio.sdcloud.fragments.AmbitiFragment;

/**
 * Created by antonio on 28/06/15.
 */
public class AmbitiActivity extends LoggedActivity {

    public static final String EXTRA_CATEGORIAID = "categoriaId";

    public static final String EXTRA_CATEGORIA_TEST = "categoriaTesto";

    public static final String EXTRA_CATEGORIA_DOMANDA = "categoriaDomanda";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Long categoriaId = intent.getLongExtra(EXTRA_CATEGORIAID, -1);
        String categoriaTesto = intent.getStringExtra(EXTRA_CATEGORIA_TEST);
        String categoriaDomanda = intent.getStringExtra(EXTRA_CATEGORIA_DOMANDA);
        Log.v(AmbitiActivity.class.toString(),categoriaDomanda);
        getActionBar().setTitle(categoriaDomanda);
        getActionBar().setDisplayShowTitleEnabled(true);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, AmbitiFragment.newInstance(categoriaId,categoriaTesto))
                    .commit();
        }
    }


}
