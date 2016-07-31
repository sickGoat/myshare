package com.shareyour.antonio.sdcloud.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.callbacksinterface.AmbitoClickListener;
import com.shareyour.antonio.sdcloud.holders.AmbitoHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 16/06/15.
 */
public class AmbitoAdapter extends RecyclerView.Adapter<AmbitoHolder> {

    private List<AmbitoProxy> mAmbiti;

    private AmbitoClickListener mListener;

    public AmbitoAdapter(List<AmbitoProxy> mAmbiti,AmbitoClickListener listener) {
        this.mAmbiti = mAmbiti;
        this.mListener = listener;
    }


    @Override
    public AmbitoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardview_ambito,parent,false);
        AmbitoHolder holder = new AmbitoHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AmbitoHolder holder, int position) {
        AmbitoProxy ambito = mAmbiti.get(position);
        holder.bind(ambito,mListener);
    }

    @Override
    public int getItemCount() {
        return mAmbiti.size();
    }

    public void addElements(ArrayList<AmbitoProxy> ambiti){
        this.mAmbiti.addAll(ambiti);
        notifyDataSetChanged();
    }

    public void removeElement(AmbitoProxy ambito){
        this.mAmbiti.remove(ambito);
        notifyDataSetChanged();
    }
}
