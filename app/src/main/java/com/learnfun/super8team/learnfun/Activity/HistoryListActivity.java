package com.learnfun.super8team.learnfun.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.learnfun.super8team.learnfun.Bin.HistoryListRecycleItem;
import com.learnfun.super8team.learnfun.Adapter.HistoryListRecyclerAdapter;
import com.learnfun.super8team.learnfun.Async.NetworkAsync;
import com.learnfun.super8team.learnfun.R;
import com.learnfun.super8team.learnfun.Service.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<HistoryListRecycleItem> myDataset;
    private JSONObject sendData;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

//디비에서 히스토리 정보를 가져와서 슬라이드창에 글을 뿌려준다
        sendData = new JSONObject();
        userPreferences = UserPreferences.getUserPreferences(this);

        try {
            //recentDate.put("date",getDate());
            sendData.put("userType",userPreferences.getUserType());
            sendData.put("userNo",userPreferences.getUserNo());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestNetwork = new NetworkAsync(HistoryListActivity.this, "getPlanList",  NetworkAsync.POST, sendData);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is " + returnString);
            //JSONObject place = new JSONObject(returnString);

            JSONArray planList = new JSONArray(returnString);
            //JSONObject contentList = new JSONObject(place.getString("place"));


            //cardView를 만들기위한 코드
            mRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(HistoryListActivity.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            myDataset = new ArrayList<>();
            mAdapter = new HistoryListRecyclerAdapter(myDataset,HistoryListActivity.this);
            mRecyclerView.setAdapter(mAdapter);


            for (int i = 0; i < planList.length(); i++) {

                //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬

                JSONObject dataJsonObject = planList.getJSONObject(i);
                //JSONObject placeData = new JSONObject(dataJsonObject);sumContent
                System.out.println(dataJsonObject);
                myDataset.add(new HistoryListRecycleItem(dataJsonObject.getInt("no"),dataJsonObject.getString("title"),dataJsonObject.getString("date")));
            }

        }catch (Exception e){

        }
    }



}
