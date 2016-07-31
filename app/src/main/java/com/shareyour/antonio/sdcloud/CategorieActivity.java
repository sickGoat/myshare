package com.shareyour.antonio.sdcloud;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.shareyour.antonio.sdcloud.abstractactivities.LoggedActivity;
import com.shareyour.antonio.sdcloud.abstractactivities.SingleFragmentActivity;
import com.shareyour.antonio.sdcloud.fragments.CategorieFragment;

/**
 * Created by antonio on 28/06/15.
 */
public class CategorieActivity extends LoggedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("A cosa sei interessato?");
        getActionBar().setDisplayShowTitleEnabled(true);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer,new CategorieFragment())
                    .commit();
        }
    }
}
