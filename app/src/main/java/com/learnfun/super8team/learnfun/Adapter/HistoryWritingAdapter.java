package com.learnfun.super8team.learnfun.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.learnfun.super8team.learnfun.Bin.HistoryWritingItem;
import com.learnfun.super8team.learnfun.R;

import java.util.ArrayList;

/**
 * Created by cho on 2017-06-23.
 */

public class HistoryWritingAdapter extends RecyclerView.Adapter<HistoryWritingAdapter.ViewHolder>{
    private ArrayList<HistoryWritingItem> mDataset;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView)view.findViewById(R.id.image);
            mTextView = (TextView)view.findViewById(R.id.textview);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryWritingAdapter(ArrayList<HistoryWritingItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryWritingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).text);
        //holder.mImageView.setImageURI(mDataset.get(position).uri); //setImageBitmap(mDataset.get(position).img);
        String src = mDataset.get(position).uri;
        Log.i("src: ", src);
        if(src.substring(src.length()-3,src.length()).equals("gif") || src.substring(src.length()-3,src.length()).equals("GIF")){

            Glide.with(mDataset.get(position).cont).asGif().load(src).into(holder.mImageView);
        }else{

            Glide.with(mDataset.get(position).cont).asBitmap().load(src).into(holder.mImageView);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

