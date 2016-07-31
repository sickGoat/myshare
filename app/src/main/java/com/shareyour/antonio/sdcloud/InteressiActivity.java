package com.shareyour.antonio.sdcloud;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.shareyour.antonio.sdcloud.abstractactivities.LoggedActivity;
import com.shareyour.antonio.sdcloud.abstractactivities.SingleFragmentActivity;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.fragments.InteressiFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by antonio on 28/06/15.
 */
public class InteressiActivity  extends LoggedActivity{

    public static final String EXTRA_AMBITO = "ambito";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String ambito = intent.getStringExtra(EXTRA_AMBITO);
        try {
            JSONObject ambitoJson = new JSONObject(ambito);
            String title = ambitoJson.getString(JSONConstants.JSON_AMBITO_TESTO);
            getActionBar().setTitle(title);
            getActionBar().setDisplayShowTitleEnabled(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, InteressiFragment.newInstance(ambito))
                    .commit();
        }
    }

}
