package com.shareyour.antonio.sdcloud;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.shareyour.antonio.sdcloud.abstractactivities.SingleFragmentActivity;
import com.shareyour.antonio.sdcloud.fragments.LoginFragment;

/**
 * Created by antonio on 14/06/15.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    protected String getSpecificTitle() {
        return "Login";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
