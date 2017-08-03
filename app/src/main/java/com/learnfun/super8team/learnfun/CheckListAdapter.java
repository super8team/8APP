package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by cho on 2017-07-31.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder>{
    final ArrayList<Integer> checked = new ArrayList<>();
    final ArrayList<Integer> noChecked = new ArrayList<>();

    private ArrayList<CheckListItem> mDataset;
    private Button save;
    private Context context;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public TextView dateTextView;
        public CheckBox substanceCheckBox;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            dateTextView = (TextView)view.findViewById(R.id.substance);
            substanceCheckBox= (CheckBox) view.findViewById(R.id.checkList_checkBox);
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
    public CheckListAdapter(ArrayList<CheckListItem> myDataset,Button save,Context context) {
        mDataset = myDataset;
        this.save = save;
        this.context = context;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CheckListItem data = mDataset.get(position);
        boolean checkBox = false;
        holder.dateTextView.setText(mDataset.get(position).substance);
        if(mDataset.get(position).respond==1) checkBox=true;
        else checkBox = false;

        holder.substanceCheckBox.setOnCheckedChangeListener(null);
        holder.substanceCheckBox.setChecked(checkBox);
        holder.substanceCheckBox.setChecked(data.isSelected());

        holder.substanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//set your object's last status
                data.setSelected(isChecked);
                if(data.isSelected()){
                System.out.println("position : "+ mDataset.get(position).substance +"   no : " + mDataset.get(position).no );

                    checked.add(mDataset.get(position).no);
                    for(int i =0; i < noChecked.size();i++){
                        if(noChecked.get(i)==mDataset.get(position).no){
                            noChecked.remove(i);
                        }
                    }
                }else{

                    noChecked.add(mDataset.get(position).no);
                    for(int i =0; i < checked.size();i++){
                        if(checked.get(i)==mDataset.get(position).no){
                            checked.remove(i);
                        }
                    }
                }
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONArray checkedArray = new JSONArray(checked);
                        JSONArray noCheckedArray = new JSONArray(noChecked);

                        JSONObject sendData = new JSONObject();
                        userPreferences = UserPreferences.getUserPreferences(context);

                        try {
                            //recentDate.put("date",getDate());
                            sendData.put("userNo", userPreferences.getUserNo());
                            sendData.put("checked",checkedArray);
                            Log.i("checked: ", String.valueOf(checkedArray.length()));
                            Log.i("ChecklistAdapter", "nochecked");
                            sendData.put("noChecked",noCheckedArray);
                            Log.i("noChecked: ", String.valueOf(noCheckedArray.length()));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            requestNetwork = new NetworkAsync(context, "setCheckList",  NetworkAsync.POST, sendData);
                            String resultString = requestNetwork.execute().get();
                            Log.i("request result: ", resultString);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        //저장버튼 클릭시 저장되었습니다 표시 출력
                        String save = context.getString(R.string.saved);

                        Toast toast = Toast.makeText(context, save,
                                Toast.LENGTH_SHORT);
                        toast.show();

                    }


                });        save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONArray checkedArray = new JSONArray(checked);
                        JSONArray noCheckedArray = new JSONArray(noChecked);

                        JSONObject sendData = new JSONObject();
                        userPreferences = UserPreferences.getUserPreferences(context);

                        try {
                            //recentDate.put("date",getDate());
                            sendData.put("userNo", userPreferences.getUserNo());
                            sendData.put("checked",checkedArray);
                            Log.i("checked: ", String.valueOf(checkedArray.length()));
                            sendData.put("noChecked",noCheckedArray);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            requestNetwork = new NetworkAsync(context, "setCheckList",  NetworkAsync.POST, sendData);
                            String resultString = requestNetwork.execute().get();
                            Log.i("request result: ", resultString);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        //저장버튼 클릭시 저장되었습니다 표시 출력
                        String save = context.getString(R.string.saved);

                        Toast toast = Toast.makeText(context, save,
                                Toast.LENGTH_SHORT);
                        toast.show();

                    }


                });
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

class CheckListItem {

    public String substance;
    private boolean isSelected;
    public int no;
    public int respond;

    public CheckListItem(String substance, int no,int respond){

        this.substance = substance;
        this.no = no;
        this.respond = respond;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


}