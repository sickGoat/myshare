package com.shareyour.antonio.sdcloud.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.CategoriaProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.AmbitiActivity;
import com.shareyour.antonio.sdcloud.AmbitoActivity;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.abstractfragment.MenuFragmnet;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by antonio on 28/06/15.
 */
public class CategorieFragment extends MenuFragmnet {

    private ArrayList<CategoriaProxy> mCategorie = new ArrayList<>();

    private CategoriaAdapter mAdapter;

    private ListView mCategorieList;

    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categorie,container,false);

        mCategorieList = (ListView) view.findViewById(R.id.categoria_listView);
        mAdapter = new CategoriaAdapter(mCategorie);
        mCategorieList.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoading);
        //if(mCategorie.isEmpty())
            mProgressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            String accessToken = UtenteSessionManager.get(getActivity())
                    .getUtente().getAccessToken();
            new FetchCategorieTask(this).execute(accessToken);
        }
    }

    public void add(ArrayList<CategoriaProxy> categorie){
        mCategorie.addAll(categorie);
        mAdapter.notifyDataSetChanged();
    }

    public void updateUI(ArrayList<CategoriaProxy> categorie){
        add(categorie);
        mProgressBar.setVisibility(View.GONE);
    }

    private class CategoriaAdapter extends ArrayAdapter<CategoriaProxy>{


        public CategoriaAdapter(ArrayList<CategoriaProxy> categorie) {
            super(getActivity(), 0,categorie);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_categoria,null);
            }

            TextView text = (TextView) convertView.findViewById(R.id.categoria_testo);
            text.setText(mCategorie.get(position).getTesto());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AmbitiActivity.class);
                    intent.putExtra(AmbitiActivity.EXTRA_CATEGORIAID,mCategorie.get(position).getId());
                    intent.putExtra(AmbitiActivity.EXTRA_CATEGORIA_TEST,mCategorie.get(position).getTesto());
                    intent.putExtra(AmbitiActivity.EXTRA_CATEGORIA_DOMANDA,mCategorie.get(position).getTestoDomanda());
                    startActivity(intent);
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return mCategorie.size();
        }
    }


    private static class FetchCategorieTask extends AsyncTask<String,Void,ArrayList<CategoriaProxy>>{

        private WeakReference<CategorieFragment> mFragmnetRef;

        private FetchCategorieTask(CategorieFragment fragment){
            mFragmnetRef = new WeakReference<>(fragment);
        }


        @Override
        protected ArrayList<CategoriaProxy> doInBackground(String... params) {
            String accessToken = params[0];
            ArrayList<CategoriaProxy> categorie = new ArrayList<>();
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmnetRef.get().getResources().getString(R.string.app_name));
            builder.setRootUrl("https://sd-app-970.appspot.com/_ah/api/");
            ClientAPI service = builder.build();
            try {
                categorie.addAll(service.getCategorie(accessToken).execute().getItems());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return categorie;
        }

        @Override
        protected void onPostExecute(ArrayList<CategoriaProxy> categoriaProxies) {
            mFragmnetRef.get().updateUI(categoriaProxies);
        }
    }

}
