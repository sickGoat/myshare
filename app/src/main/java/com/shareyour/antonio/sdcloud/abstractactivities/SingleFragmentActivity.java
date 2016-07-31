package com.shareyour.antonio.sdcloud.abstractactivities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

import com.shareyour.antonio.sdcloud.R;

/**
 * Created by antonio on 14/06/15.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    protected abstract Fragment createFragment();

    protected int getLayoutResId(){return R.layout.activity_fragment; }

    protected abstract String getSpecificTitle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer,createFragment())
                    .commit();
        }
        getActionBar().setTitle(getSpecificTitle());
        getActionBar().setDisplayShowTitleEnabled(true);
    }
}
