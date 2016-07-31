package com.shareyour.antonio.sdcloud.model;

import android.content.Context;
import android.util.Log;

import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.shareyour.antonio.sdcloud.helpers.Serializer;

import java.util.ArrayList;

/**
 * Created by antonio on 26/06/15.
 */
public class UtenteSessionManager {

    private static UtenteSessionManager mManager;

    private static Context mAppContext;

    private UtenteProxy mUtente;

    private ArrayList<AmbitoProxy> mIscrizioni;

    private boolean mIscUptodate = true;

    private boolean mUteUptodate = true;

    private UtenteSessionManager(Context appContext){
        mAppContext = appContext;
        mIscrizioni = Serializer.loadIscrizioni(appContext);
        mUtente = Serializer.loadUtente(appContext);
    }

    public static UtenteSessionManager get(Context context){
        if( mAppContext == null )
            mManager = new UtenteSessionManager(context.getApplicationContext());

        return mManager;
    }

    public ArrayList<AmbitoProxy> getIscrizioni(){
        Log.v(UtenteSessionManager.class.getName()," Iscrizioni salvate: "+mIscrizioni.size());
        return mIscrizioni;
    }

    public void addIscrizione(AmbitoProxy ambito){
        Log.v(UtenteSessionManager.class.getName(), "addIscrizione " + mIscrizioni.size());
        boolean toAdd = true;
        for( AmbitoProxy ambitoProxy : mIscrizioni ){
            if( ambitoProxy.getId().equals(ambito.getId())){
                toAdd = false;
                break;
            }
        }
        if(toAdd) {
            mIscrizioni.add(ambito);
            mIscUptodate = false;
        }
    }

    public void deleteIscrizione(AmbitoProxy ambito){ mIscrizioni.remove(ambito); mIscUptodate = false; }

    public void deleteIscrizioneById(Long id){
        for( int i = 0 ; i < mIscrizioni.size() ; i++ ){
            AmbitoProxy ambito = mIscrizioni.get(i);
            if( ambito.getId().equals(id)) {
                mIscrizioni.remove(ambito);
                mIscUptodate = false;
                break;
            }
        }
    }

    public void addIscrizioni(ArrayList<AmbitoProxy> ambiti){
        mIscrizioni = ambiti;
    }

    public AmbitoProxy getAmbito(Long idAmbito){
        for(AmbitoProxy ambito : mIscrizioni )
            if( ambito.getId().equals(idAmbito) )
                return ambito;

        return null;
    }

    public UtenteProxy getUtente(){ return mUtente; }

    public void setUtente(UtenteProxy utente){ this.mUtente = utente; mUteUptodate = false; }

    public void saveIscrizioni(){
        if( !mIscUptodate ) {
            Serializer.saveIscrizioni(mIscrizioni, mAppContext);
            mIscUptodate = true;
        }
    }

    public void saveUtente(){
        if( !mUteUptodate ){
            Log.v(UtenteSessionManager.class.getName(),"Utente salvato");
            Serializer.saveUtente(mUtente, mAppContext);
            mUteUptodate = true;
        }
    }

    public UtenteProxy loadUtente(){
        return Serializer.loadUtente(mAppContext);
    }
    /**
     * Metodo chiamato quando l'utente effettua il logout
     * cancella tutte le informazioni dai file
     */
    public void logoutUtente(){
        mIscrizioni.clear();
        mUtente = null;
        Serializer.saveUtente(mUtente,mAppContext);
        Serializer.saveIscrizioni(mIscrizioni,mAppContext);
    }

}
