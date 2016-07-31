package com.shareyour.antonio.sdcloud.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.UtenteProfileActivity;
import com.shareyour.antonio.sdcloud.callbacksinterface.PostClickListener;
import com.shareyour.antonio.sdcloud.fragments.CommentiFragment;

/**
 * Created by antonio on 20/06/15.
 */
public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mUtenteText;

    private TextView mPostText;

    private PostProxy mPost;

    private PostClickListener mListener;

    private ImageView mImageview;



    public PostHolder(View itemView) {
        super(itemView);

        mUtenteText = (TextView) itemView.findViewById(R.id.post_utente);
        mPostText = (TextView) itemView.findViewById(R.id.post_testo);
        mImageview = (ImageView) itemView.findViewById(R.id.immagineUtente);

        itemView.setOnClickListener(this);
    }

    public void bind(final PostProxy post,PostClickListener listener, final Context ctx){
        mPost = post;
        mUtenteText.setText(post.getNomeUtente() +" "+ post.getCognomeUtente());
        mPostText.setText(post.getTesto());

        mImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, UtenteProfileActivity.class);
                intent.putExtra(UtenteProfileActivity.EXTRA_UTENTE,post.getIdUtente());
                ctx.startActivity(intent);
            }
        });
        this.mListener = listener;
    }


    @Override
    public void onClick(View v) {
        if(mPost != null ){
            this.mListener.onPostSelected(mPost);
        }
    }
}
