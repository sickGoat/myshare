package com.shareyour.antonio.sdcloud.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.sd_app_970.clientAPI.ClientAPI;
import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.InteresseProxy;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.abstractfragment.MenuFragmnet;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.helpers.Serializer;
import com.shareyour.antonio.sdcloud.model.UtenteSessionManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by antonio on 28/06/15.
 */
public class InteressiFragment extends MenuFragmnet {


    private static final String BUNDLE_AMBITO = "ambitoId";

    public static final int TO_FETCH = 6;

    public int lastEnd = 0;

    public boolean mLoading = true;

    private HashMap<InteresseProxy,Integer> mSelInt = new HashMap<>();

    private AmbitoProxy mAmbito;

    private ListView mInteressiListView;

    private EditText mInputSearch;

    private ImageButton mSaveButton;

    private ProgressBar mProgressBar;

    private ArrayList<InteresseProxy> mInteressi = new ArrayList<>();

    private InteressiAdapter mAdapter;

    private int itemCount = 0;

    private int nextPosition = 1;

    public int mFirstResult = 0;




    public static InteressiFragment newInstance(String ambito){
        Bundle args = new Bundle();
        args.putString(BUNDLE_AMBITO, ambito);
        InteressiFragment fragment = new InteressiFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if( savedInstanceState == null ){
            Bundle input = getArguments();
            String ambitoJson = input.getString(BUNDLE_AMBITO);
            mAmbito = Serializer.deserializeAmbito(ambitoJson);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( savedInstanceState == null ){
            String accessToken = UtenteSessionManager.get(getActivity())
                    .getUtente().getAccessToken();
            Pair<String,Long> inputPair = new Pair<>(accessToken,mAmbito.getId());

            new FetchInteressiTask(this).execute(inputPair);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interessi,container,false);

        mSaveButton = (ImageButton) view.findViewById(R.id.save_classificaButton);
        mInputSearch = (EditText) view.findViewById(R.id.search_view);
        mInteressiListView = (ListView) view.findViewById(R.id.interessi_listView);
        mInteressiListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!mLoading && (firstVisibleItem + visibleItemCount) >= (totalItemCount - visibleItemCount) ){
                    mLoading = true;
                    String accessToken = UtenteSessionManager.get(getActivity()).getUtente().getAccessToken();
                    Long idAmbito = mAmbito.getId();
                    Pair<String,Long> pair = new Pair<String, Long>(accessToken,idAmbito);
                    new FetchInteressiTask(InteressiFragment.this).execute(pair);
                    Toast.makeText(getActivity(),"Carica altro...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mProgressBar = (ProgressBar) view.findViewById(R.id.pbLoading);
        mProgressBar.setVisibility(View.VISIBLE);


        mAdapter = new InteressiAdapter(mInteressi);
        mInteressiListView.setAdapter(mAdapter);


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelInt.keySet().size() < 3) {
                    Toast.makeText(getActivity(), "Devi esprimere tre preferenze", Toast.LENGTH_SHORT).show();
                    return;
                }

                new SendClassificaTask(InteressiFragment.this).execute(mSelInt);
            }
        });


        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void updateUI(ArrayList<InteresseProxy> interessi){
        mProgressBar.setVisibility(View.GONE);
        mAdapter.addElements(interessi);
        lastEnd = interessi.size() + 1;
        if( interessi.size() == TO_FETCH ) {
            mLoading = false;
        }else{
            Toast.makeText(getActivity(),"Non c'è piu altro da caricare",Toast.LENGTH_SHORT).show();
        }
    }

    public Long getIdAmbito(){ return this.mAmbito.getId(); }


    public AmbitoProxy getAmbito(){ return this.mAmbito; }

    public ProgressBar getProgressBar(){ return this.mProgressBar; }

    private class InteressiAdapter extends ArrayAdapter<InteresseProxy> implements Filterable{

        private ArrayList<InteresseProxy> mFiltered;
        private ArrayList<InteresseProxy> mNotFiltered;
        private ItemFilter mFilter;

        private InteressiAdapter(ArrayList<InteresseProxy> interessi){
            super(getActivity(), 0, interessi);
            mFiltered = new ArrayList<>();
            mNotFiltered = new ArrayList<>();
            mFiltered.addAll(interessi);
            mNotFiltered.addAll(interessi);
            getFilter();
        }

        @Override
        public Filter getFilter() {
            if( mFilter == null )
                mFilter = new ItemFilter();

            return mFilter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_interessi,null);
            }
            TextView interesseText = (TextView) convertView.findViewById(R.id.interesse_text);
            final TextView interessePosition = (TextView) convertView.findViewById(R.id.interesse_position);

            final InteresseProxy interesse = getItem(position);
            interesseText.setText(interesse.getTesto());
            if( mSelInt.containsKey(interesse)){
                interessePosition.setText(Integer.toString(mSelInt.get(interesse)));
            }else{
                interessePosition.setText("");
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.getInteger(interessePosition.getText().toString(), -1);
                    if (position == -1 && itemCount == 3) {
                        Toast.makeText(getActivity(), "Non puoi scegliere altri interessi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if( mSelInt.get(interesse) == null ){
                        mSelInt.put(interesse,nextPosition);
                        interessePosition.setText(Integer.toString(nextPosition));
                        itemCount++;
                        nextPosition++;
                    }else{
                        Integer pos = mSelInt.get(interesse);
                        mSelInt.remove(interesse);
                        interessePosition.setText("");
                        nextPosition = pos;
                        itemCount--;
                    }
                    if( itemCount == 3 )
                        mSaveButton.setEnabled(true);
                }
            });

            return convertView;
        }

        private void addElements(ArrayList<InteresseProxy> interessi){
            this.mFiltered.addAll(interessi);
            this.mNotFiltered.addAll(interessi);
            mInteressi.addAll(interessi);
            notifyDataSetChanged();
        }

        private class ItemFilter extends Filter{

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults result = new FilterResults();
                if( constraint != null && constraint.toString().length() > 0 ){
                    ArrayList<InteresseProxy> filtered = new ArrayList<>();
                    for( InteresseProxy interesse : mInteressi ){
                        if( interesse.getTesto().toLowerCase().contains(constraint.toString().toLowerCase()))
                            filtered.add(interesse);
                    }
                    result.count = filtered.size();
                    result.values = filtered;
                }else{
                    synchronized (this){
                        result.values = mNotFiltered;
                        result.count = mNotFiltered.size();
                    }
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFiltered = (ArrayList<InteresseProxy>) results.values;
                notifyDataSetChanged();
                clear();
                for( int i = 0 ; i < mFiltered.size() ; i++ )
                    add(mFiltered.get(i));
                notifyDataSetInvalidated();
            }
        }
    }

    private static class FetchInteressiTask extends AsyncTask<Pair<String,Long>,Void,ArrayList<InteresseProxy>>{

        private WeakReference<InteressiFragment> mFragmentRef;

        private FetchInteressiTask(InteressiFragment fragment){
            this.mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        protected ArrayList<InteresseProxy> doInBackground(Pair<String, Long>... params) {
            Pair<String,Long> input = params[0];
            String accessToken = input.first;
            Long idAmbito = input.second;
            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            ArrayList<InteresseProxy> interessi = new ArrayList<>();
            try {
                ArrayList<InteresseProxy> interessiP = (ArrayList<InteresseProxy>) service
                        .getInteressiAmbito(accessToken,idAmbito)
                        .setStartResult(mFragmentRef.get().lastEnd).setEndResult(TO_FETCH).execute().getItems();
                interessi.addAll(interessiP);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return interessi;
        }

        @Override
        protected void onPostExecute(ArrayList<InteresseProxy> interesseProxies) {
            super.onPostExecute(interesseProxies);
            for( InteresseProxy in : interesseProxies)
                Log.v(InteressiFragment.class.getName(),in.getTesto());
            if( interesseProxies != null)
                mFragmentRef.get().updateUI(interesseProxies);
        }
    }

    private static class SendClassificaTask extends AsyncTask<HashMap<InteresseProxy,Integer>,Void,Boolean>{

        private WeakReference<InteressiFragment> mFragmentRef;

        private SendClassificaTask(InteressiFragment fragment){
            this.mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFragmentRef.get().getProgressBar().setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(HashMap<InteresseProxy,Integer>... params) {
            HashMap<InteresseProxy,Integer> preferenze = params[0];
            Long[] preferenzeArray = new Long[3];
            for( InteresseProxy interesse : preferenze.keySet() ){
                int position = preferenze.get(interesse);
                preferenzeArray[position-1] = interesse.getId();
            }

            String accessToken = UtenteSessionManager.get(mFragmentRef.get().getActivity())
                    .getUtente().getAccessToken();
            Long idAmbito = mFragmentRef.get().getIdAmbito();

            ClientAPI.Builder builder = new ClientAPI.Builder(AndroidHttp.newCompatibleTransport()
                    ,new AndroidJsonFactory(),null);
            builder.setApplicationName(mFragmentRef.get().getString(R.string.app_name));
            builder.setRootUrl(JSONConstants.BASE_URL);
            ClientAPI service = builder.build();
            try {
                service.addIscrizioneUtente(accessToken,idAmbito)
                        .setInteressi(Arrays.asList(preferenzeArray)).execute();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }



        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            InteressiFragment fragment = mFragmentRef.get();
            if( aBoolean ){
                Toast.makeText(fragment.getActivity(),"Iscrizione Effettuata",Toast.LENGTH_SHORT);
                UtenteSessionManager.get(fragment.getActivity()).addIscrizione(fragment.getAmbito());
                fragment.getActivity().finish();
            }else{
                Toast.makeText(fragment.getActivity(),"Qualcosa è andato storto",Toast.LENGTH_SHORT);
            }
        }
    }
}
