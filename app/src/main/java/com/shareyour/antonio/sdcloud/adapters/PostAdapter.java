package com.shareyour.antonio.sdcloud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.shareyour.antonio.sdcloud.R;
import com.shareyour.antonio.sdcloud.callbacksinterface.PostClickListener;
import com.shareyour.antonio.sdcloud.holders.PostHolder;

import java.util.ArrayList;

/**
 * Created by antonio on 20/06/15.
 */
public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

    private ArrayList<PostProxy> mPosts;

    private PostClickListener mListener;

    private Context mCtx;

    public PostAdapter(ArrayList<PostProxy> posts,PostClickListener listener,Context ctx){
        this.mPosts = posts;
        this.mListener = listener;
        this.mCtx = ctx;
    }


    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardview_post,parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        PostProxy post = mPosts.get(position);
        holder.bind(post,mListener,mCtx);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void addElements(ArrayList<PostProxy> posts){
        this.mPosts.addAll(posts);
        notifyDataSetChanged();
    }

    public void addElement(PostProxy post){
        this.mPosts.add(post);
        notifyDataSetChanged();
    }
}
