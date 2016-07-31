package com.shareyour.antonio.sdcloud.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.sd_app_970.clientAPI.model.CommentoProxy;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.holders.CommentoHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 21/06/15.
 */
public class CommentoAdapter extends RecyclerView.Adapter<CommentoHolder> {

    private ArrayList<CommentoProxy> mCommenti;

    public CommentoAdapter(ArrayList<CommentoProxy> commenti) {
        this.mCommenti = commenti;
    }

    public void setCommenti(ArrayList<CommentoProxy> commenti) {
        this.mCommenti = commenti;
    }

    public void addCommenti(ArrayList<CommentoProxy> commenti){
        mCommenti.addAll(mCommenti.size()-1,commenti);
        this.notifyDataSetChanged();
    }

    @Override
    public CommentoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.commento_item,parent,false);

        return new CommentoHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentoHolder holder, int position) {
        CommentoProxy commento = mCommenti.get(position);
        holder.bind(commento);
    }

    @Override
    public int getItemCount() {
        return mCommenti.size();
    }

    public void addElements(List<CommentoProxy> commenti){
        mCommenti.addAll(commenti);
        notifyDataSetChanged();
    }
}
