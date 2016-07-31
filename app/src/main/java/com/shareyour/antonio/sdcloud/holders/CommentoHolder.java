package com.shareyour.antonio.sdcloud.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appspot.sd_app_970.clientAPI.model.CommentoProxy;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.UtenteProfileActivity;
import com.shareyour.antonio.sdcloud.fragments.UtenteProfileFragment;

/**
 * Created by antonio on 21/06/15.
 */
public class CommentoHolder extends RecyclerView.ViewHolder {

    private TextView mUtenteTextView;

    private TextView mCommentoTextView;

    private CommentoProxy mCommento;

    public CommentoHolder(View itemView) {
        super(itemView);

        mUtenteTextView = (TextView) itemView.findViewById(R.id.commento_utente);
        mCommentoTextView = (TextView) itemView.findViewById(R.id.commento_testo);
    }

    public void bind(final CommentoProxy commento){
        mCommento = commento;
        mUtenteTextView.setText(commento.getNomeUtente()+" "+commento.getCognomeUtente());
        mCommentoTextView.setText(commento.getTesto());
        mUtenteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = mUtenteTextView.getContext();
                Intent intent = new Intent(context,UtenteProfileActivity.class);
                intent.putExtra(UtenteProfileActivity.EXTRA_UTENTE,mCommento.getIdUtente());
                context.startActivity(intent);
            }
        });

    }
}
