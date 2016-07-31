package com.shareyour.antonio.sdcloud;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.shareyour.antonio.sdcloud.abstractactivities.SingleFragmentActivity;
import com.shareyour.antonio.sdcloud.fragments.RegistrazioneFragment;

/**
 * Created by antonio on 17/06/15.
 */
public class RegistrazioneActivity extends SingleFragmentActivity {

    private RegistrazioneFragment mFragment;

    @Override
    protected Fragment createFragment() {
        this.mFragment = new RegistrazioneFragment();
        return this.mFragment;
    }

    @Override
    protected String getSpecificTitle() {
        return "Registrati";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
