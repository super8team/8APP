package com.learnfun.super8team.learnfun;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PlanTableActivity extends AppCompatActivity {

    UserPreferences userPreferences;
    NetworkAsync requestNetwork;
    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
    TableLayout table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_table);

        RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.plan_list);

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

            table = new TableLayout(this); // 테이블 생성
            TableRow row[] = new TableRow[planList.length()];     // 테이블 ROW 생성
            TextView text[][] = new TextView[planList.length()][3]; // 데이터

            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            for(int tr=0; tr < planList.length() ; tr++ ){
                String planName = "plan" + (tr+1);
                JSONObject plan = new JSONObject(planList.getString(planName));
                plan.getString("place");
                plan.getString("at");
                row[tr] = new TableRow(this);


                for (int td = 0; td < 3; td++) {              // for문을 이용한 칸수 (TD)

                    text[tr][td] = new TextView(this);
                    if(td==0){
                        text[tr][td].setText((String.valueOf(tr+1)));                   // 데이터삽입
                    }else if(td==1){
                        text[tr][td].setText(plan.getString("place"));                   // 데이터삽입
                    }else{
                        text[tr][td].setText(plan.getString("at"));                   // 데이터삽입
                    }
                    text[tr][td].setTextSize(15);                     // 폰트사이즈

                    text[tr][td].setTextColor(Color.BLACK);         // 폰트컬러



                    text[tr][td].setGravity(Gravity.CENTER);    // 폰트정렬

                    text[tr][td].setBackgroundColor(Color.WHITE);

                    text[tr][td].setWidth(450);    // 크기

                    row[tr].addView(text[tr][td]);
                    table.addView(row[tr]);
                } // td for end

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mlayout.addView(table,rowLayout);
//        table.addView(row[tr]);
//        addView(row);
    }
}
