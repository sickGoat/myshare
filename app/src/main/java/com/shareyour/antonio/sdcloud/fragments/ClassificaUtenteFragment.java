package com.shareyour.antonio.sdcloud.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by antonio on 27/06/15.
 */
public class ClassificaUtenteFragment  extends DialogFragment {

    private static final String BUNDLE_IDAMBITO = "ambitoId";

    private static final String BUNDLE_AMBITONAME = "ambitoName";

    private static final String BUNDLE_IDUTENTE = "utenteId";

    private Long mAmbitoId;

    private Long mUtenteId;

    private String mAmbitoName;

    private ListView mClassifica;

    private ArrayList<String> mItems = new ArrayList<>();


    public static ClassificaUtenteFragment newInstance(Long idAmbito,Long idUtente,String ambitoName){
        Bundle args = new Bundle();
        args.putLong(BUNDLE_IDAMBITO,idAmbito);
        args.putLong(BUNDLE_IDUTENTE,idUtente);
        args.putString(BUNDLE_AMBITONAME,ambitoName);
        ClassificaUtenteFragment fragment = new ClassificaUtenteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if( savedInstanceState != null ){
            Bundle input = getArguments();
            mAmbitoId = input.getLong(BUNDLE_IDAMBITO);
            mUtenteId = input.getLong(BUNDLE_IDUTENTE);
            mAmbitoName = input.getString(BUNDLE_AMBITONAME);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_classifica,null);

        mClassifica = (ListView) v.findViewById(R.id.classificaListView);
        mClassifica.setAdapter(new ClassificaAdapter(mItems));

        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    public Long getAmbitoId(){ return this.mAmbitoId; }

    public Long getUtenteId(){ return this.mUtenteId; }

    public void addItems(ArrayList<String> items){
        ClassificaAdapter adapter = new ClassificaAdapter(items);
        mClassifica.setAdapter(adapter);
    }

    private class ClassificaAdapter extends ArrayAdapter<String>{

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

    private static class FetchClassificaTask extends AsyncTask<Void,Void,ArrayList<String>>{

        private WeakReference<ClassificaUtenteFragment> mFragmentRef;

        private FetchClassificaTask(ClassificaUtenteFragment fragment){
            mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Context ctx = mFragmentRef.get().getActivity();
            String accessToken = UtenteSessionManager.get(ctx).getUtente().getAccessToken();
            Long idUtente = mFragmentRef.get().getUtenteId();
            Long idAmbito = mFragmentRef.get().getAmbitoId();
            /*esegui chiamata*/

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            mFragmentRef.get().addItems(strings);
        }
    }
}
