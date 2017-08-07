package com.learnfun.super8team.learnfun;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cho on 2017-07-31.
 */

public class PlanTableAdapter extends RecyclerView.Adapter<PlanTableAdapter.ViewHolder>{
    private ArrayList<PlanListItem> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public TextView planDetailNo;
        public TextView planDetailName;
        public TextView planDetailDate;


        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            planDetailNo = (TextView)view.findViewById(R.id.plan_detail_no);
            planDetailName = (TextView)view.findViewById(R.id.plan_detail_name);
            planDetailDate = (TextView)view.findViewById(R.id.plan_detail_date);

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
    public PlanTableAdapter(ArrayList<PlanListItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlanTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_recycle_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.planDetailNo.setText(String.valueOf(mDataset.get(position).no));
        holder.planDetailName.setText(mDataset.get(position).name);
        holder.planDetailDate.setText(mDataset.get(position).date);


    }



    // Replace the contents of a view (invoked by the layout manager)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

class PlanListItem {

    public int no;
    public String name;
    public String date;

    public PlanListItem(int no,String name,String date){

        this.no = no;
        this.name = name;
        this.date = date;
    }


}