package com.learnfun.super8team.learnfun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckListActivity extends AppCompatActivity {
    TableLayout table;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        table = (TableLayout)findViewById(R.id.checkTable);
        userPreferences = UserPreferences.getUserPreferences(this);

        requestNetwork = new NetworkAsync(CheckListActivity.this, "getCheckList",  NetworkAsync.POST);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            JSONObject checkList = new JSONObject(returnString);

            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            for(int i=0; i < checkList.length() ; i++ ){
                String checkName = "check" + (i+1);
                JSONObject check = new JSONObject(checkList.getString(checkName));
                check.getString("title");
                check.getString("bigsort");
                check.getString("substance");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
