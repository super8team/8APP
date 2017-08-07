package com.learnfun.super8team.learnfun;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PlanTableActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PlanListItem> myDataset;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;
    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
    TableLayout table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_table);



        userPreferences = UserPreferences.getUserPreferences(this);
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("userId",userPreferences.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestNetwork = new NetworkAsync(PlanTableActivity.this, "getPlanDetail",  NetworkAsync.POST,sendData);

        try {

            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            JSONObject planList = new JSONObject(returnString);
            mRecyclerView = (RecyclerView) findViewById(R.id.planDetail_recycler_view);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(PlanTableActivity.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            myDataset = new ArrayList<>();
            mAdapter = new PlanTableAdapter(myDataset);
            mRecyclerView.setAdapter(mAdapter);

            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            for(int tr=0; tr < planList.length() ; tr++ ){
                String planName = "plan" + (tr+1);
                JSONObject plan = new JSONObject(planList.getString(planName));
                plan.getString("place");
                plan.getString("at");


                    //JSONObject placeData = new JSONObject(dataJsonObject);sumContent

                    myDataset.add(new PlanListItem(tr+1,plan.getString("place"),plan.getString("at")));


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
