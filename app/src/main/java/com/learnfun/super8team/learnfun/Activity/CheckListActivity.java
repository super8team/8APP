package com.learnfun.super8team.learnfun.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.learnfun.super8team.learnfun.Adapter.CheckListAdapter;
import com.learnfun.super8team.learnfun.Bin.CheckListItem;
import com.learnfun.super8team.learnfun.Async.NetworkAsync;
import com.learnfun.super8team.learnfun.R;
import com.learnfun.super8team.learnfun.Service.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CheckListItem> myDataset;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;
    Button save;
    private JSONObject sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        save = (Button)findViewById(R.id.save_checkList);

        //save.setOnClickListener(mainListener);
        userPreferences = UserPreferences.getUserPreferences(this);

        sendData = new JSONObject();
            try {
                //recentDate.put("date",getDate());
                sendData.put("userNo",userPreferences.getUserNo());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        requestNetwork = new NetworkAsync(CheckListActivity.this, "getCheckList",  NetworkAsync.POST, sendData);
        //cardView를 만들기위한 코드
        mRecyclerView = (RecyclerView) findViewById(R.id.checkList_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(CheckListActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mAdapter = new CheckListAdapter(myDataset,save,CheckListActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            JSONObject checkList = new JSONObject(returnString);


            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            for(int tr=0; tr < checkList.length() ; tr++ ){
                String checkName = "check" + (tr+1);
                JSONObject check = new JSONObject(checkList.getString(checkName));
//                check.getString("title");
//                check.getString("bigsort");
//                check.getString("substance");

                        //JSONObject placeData = new JSONObject(dataJsonObject);sumContent

                        myDataset.add(new CheckListItem(check.getString("substance"),check.getInt("no"),check.getInt("respond"))); //respond
                    }

                }catch (Exception e){

                }

    }


}
