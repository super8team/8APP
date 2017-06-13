package com.learnfun.super8team.learnfun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class PlanTableActivity extends AppCompatActivity {
    TableLayout table;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;

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
        requestNetwork = new NetworkAsync(PlanTableActivity.this, "getCheckList",  NetworkAsync.POST,sendData);

        try {

            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            JSONObject planList = new JSONObject(returnString);

            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            for(int i=0; i < planList.length() ; i++ ){
                String planName = "plan" + (i+1);
                JSONObject check = new JSONObject(planList.getString(planName));
                check.getString("place");
                check.getString("at");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
