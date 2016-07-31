package com.shareyour.antonio.sdcloud.abstractactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.shareyour.antonio.sdcloud.LoginActivity;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;


/**
 * Created by antonio on 03/07/15.
 */
public class LoggedActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LoggedActivity.class.getName(), "Logged activity");
        if( !utenteLoggato() ){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_fragment);
    }


    private boolean utenteLoggato(){
        UtenteSessionManager manager = UtenteSessionManager.get(this);
        return manager.getUtente() != null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LoggedActivity.class.getName(),"save");
        UtenteSessionManager manager = UtenteSessionManager.get(this);
        manager.saveUtente();
        manager.saveIscrizioni();
    }
}
