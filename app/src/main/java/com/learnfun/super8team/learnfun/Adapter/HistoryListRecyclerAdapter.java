package com.learnfun.super8team.learnfun.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnfun.super8team.learnfun.Activity.BeforeHistoryActivity;
import com.learnfun.super8team.learnfun.Bin.HistoryListRecycleItem;
import com.learnfun.super8team.learnfun.R;

import java.util.ArrayList;

/**
 * Created by cho on 2017-07-24.
 */


public class HistoryListRecyclerAdapter extends RecyclerView.Adapter<HistoryListRecyclerAdapter.ViewHolder>{
    private ArrayList<HistoryListRecycleItem> mDataset;
    Intent intent;
    private Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public TextView noTextView;
        public TextView titleTextView;
        public TextView dateTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            noTextView = (TextView)view.findViewById(R.id.planNo);
            titleTextView = (TextView)view.findViewById(R.id.planName);
            dateTextView = (TextView)view.findViewById(R.id.planDate);
        }
        @Override
        public void onClick(View v) {
            System.out.println("position"+getAdapterPosition());
            System.out.println("position"+getLayoutPosition());
            System.out.println("position"+getOldPosition());
//            Intent intent = new Intent(v.getContext() , HistoryDetailActivity.class);
//            v.getContext().startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryListRecyclerAdapter(ArrayList<HistoryListRecycleItem> myDataset,Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historylist_recycle_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.noTextView.setText(String.valueOf(mDataset.get(position).no));
        holder.titleTextView.setText(mDataset.get(position).title);
        holder.dateTextView.setText(mDataset.get(position).date);

        final HistoryListRecycleItem item = mDataset.get(position);
        holder.titleTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                System.out.println("아이템@@@@@@@@@@@@@@"+item.no);
                intent = new Intent(context,BeforeHistoryActivity.class);
                intent.putExtra("planNo", item.no);
                context.startActivity(intent);
            }
        });



    }



    // Replace the contents of a view (invoked by the layout manager)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}



