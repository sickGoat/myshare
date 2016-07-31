package com.shareyour.antonio.sdcloud.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.callbacksinterface.AmbitoClickListener;

/**
 * Created by antonio on 20/06/15.
 */
public class AmbitoHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,View.OnLongClickListener {
    
    private TextView mAmbitoName;

    private TextView mAmbitoCategoria;

    private AmbitoProxy mAmbito;

    private AmbitoClickListener mListener;

    public AmbitoHolder(View itemView) {
        super(itemView);

        mAmbitoName = (TextView) itemView.findViewById(R.id.hall_testo);
        mAmbitoCategoria = (TextView) itemView.findViewById(R.id.hall_categoria);

        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void bind(AmbitoProxy ambito,AmbitoClickListener listener){
        mAmbito = ambito;

        mAmbitoName.setText(ambito.getTesto());
        mAmbitoCategoria.setText(ambito.getCategoriaTesto());
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        //send intent to ambito activity
        if( mAmbito != null ){
            mListener.onAmbitoSelected(mAmbito);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if( mAmbito != null ){
            mListener.onAmbitoLongClicked(mAmbito);
            return true;
        }
        return false;
    }
}
