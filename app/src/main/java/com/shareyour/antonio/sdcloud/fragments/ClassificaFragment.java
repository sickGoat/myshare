package com.shareyour.antonio.sdcloud.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.InteresseProxy;
import com.appspot.sd_app_970.clientAPI.model.PreferenzaProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by antonio on 06/07/15.
 */
public class ClassificaFragment extends DialogFragment {

    private class InputArgs{
        String accessToken;
        Long idAmbito;
        Long idUtente;
    }

    private static final String BUNDLE_IDAMBITO = "ambitoId";

    private static final String BUNDLE_AMBITONAME = "ambitoName";

    private static final String BUNDLE_IDUTENTE = "utenteId";

    private static final String BUNDLE_NOMEUTENTE = "utenteNome";

    private Long mAmbitoId;

    private Long mUtenteId;

    private String mAmbitoName;


    private ListView mClassifica;

    private ArrayList<String> mItems = new ArrayList<>();

    public static ClassificaFragment newInstance(Long idAmbito,Long idUtente,String ambitoName){
        Bundle args = new Bundle();
        args.putLong(BUNDLE_IDAMBITO,idAmbito);
        args.putLong(BUNDLE_IDUTENTE,idUtente);
        args.putString(BUNDLE_AMBITONAME,ambitoName);
        ClassificaFragment fragment = new ClassificaFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if( savedInstanceState == null ){
            Bundle input = getArguments();
            mAmbitoId = input.getLong(BUNDLE_IDAMBITO);
            mUtenteId = input.getLong(BUNDLE_IDUTENTE);
            mAmbitoName = input.getString(BUNDLE_AMBITONAME);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            InputArgs inputArgs = new InputArgs();
            String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
            inputArgs.accessToken = accessToken;
            inputArgs.idAmbito = mAmbitoId;
            inputArgs.idUtente = mUtenteId;
            new FetchClassificaUtente(this).execute(inputArgs);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_classifica,null);

        mClassifica = (ListView) v.findViewById(R.id.classificaListView);
        mClassifica.setAdapter(new ClassificaAdapter(mItems));
        String titolo = getString(R.string.dialog_classifica,mAmbitoName);
        onViewCreated(v,savedInstanceState);
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(titolo).create();
    }

    public void updateUI(String[] interessi){
        for( int i = 0 ; i < interessi.length ; i++ ){
            mItems.add(interessi[i]);
        }
        mClassifica.setAdapter(new ClassificaAdapter(mItems));

    }

    private class ClassificaAdapter extends ArrayAdapter<String> {

        private ClassificaAdapter(ArrayList<String> items){
            super(getActivity(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_classifica,null);
            }

            TextView item = (TextView) convertView.findViewById(R.id.classifica_itemText);

            item.setText(getItem(position));

            return convertView;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }
    }

    private static class FetchClassificaUtente extends AsyncTask<InputArgs,Void,String[]>{

        private WeakReference<ClassificaFragment> mFragmentRef;

        private FetchClassificaUtente(ClassificaFragment fragment){
            mFragmentRef = new WeakReference<ClassificaFragment>(fragment);
        }

        @Override
        protected String[] doInBackground(InputArgs... params) {
            InputArgs input = params[0];
            String accessToken = input.accessToken;
            Long idAmbito = input.idAmbito;
            Long idUtente = input.idUtente;

            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            String[] interessi = null;
            try {
                ArrayList<PreferenzaProxy> preferenze = (ArrayList<PreferenzaProxy>) service.getClassificaUtente(accessToken, idAmbito, idUtente)
                        .execute().getItems();
                if(preferenze != null && !preferenze.isEmpty()){
                    interessi = new String[3];
                    for( PreferenzaProxy preferenza : preferenze ){
                        if( preferenza.getIdAmbito() != null )
                        if( preferenza.getIdAmbito().equals(idAmbito)){
                            int posizione = preferenza.getPosizione();
                            interessi[posizione] = preferenza.getTestoInteresse();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return interessi;
            }
            return interessi;
        }

        @Override
        protected void onPostExecute(String[] interesseProxies) {
            super.onPostExecute(interesseProxies);
            if( interesseProxies != null ){
                mFragmentRef.get().updateUI(interesseProxies);
            }
        }
    }

}
