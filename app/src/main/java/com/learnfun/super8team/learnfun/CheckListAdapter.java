package com.learnfun.super8team.learnfun;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cho on 2017-07-31.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder>{
    private ArrayList<CheckListItem> mDataset;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public TextView dateTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            dateTextView = (TextView)view.findViewById(R.id.substance);
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
    public CheckListAdapter(ArrayList<CheckListItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist_recycle_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.dateTextView.setText(mDataset.get(position).substance);


    }



    // Replace the contents of a view (invoked by the layout manager)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

class CheckListItem {

    public String substance;

    public CheckListItem(String substance){

        this.substance = substance;
    }


}