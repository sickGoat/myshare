package com.shareyour.antonio.sdcloud;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.shareyour.antonio.sdcloud.abstractactivities.LoggedActivity;
import com.shareyour.antonio.sdcloud.abstractactivities.SingleFragmentActivity;
import com.shareyour.antonio.sdcloud.fragments.ListHallFragment;

/**
 * Created by antonio on 15/06/15.
 */
public class ListHallActiivty extends LoggedActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Le mie iscrizioni");
        getActionBar().setDisplayShowTitleEnabled(true);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer,new ListHallFragment())
                    .commit();
        }
    }
}
