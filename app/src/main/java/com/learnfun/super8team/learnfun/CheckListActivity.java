package com.learnfun.super8team.learnfun;

import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.support.v7.app.ActionBar.*;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.TableLayout.*;

import android.widget.TableLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckListActivity extends AppCompatActivity {

    UserPreferences userPreferences;
    NetworkAsync requestNetwork;

    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
    TableLayout table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.checkList);

        userPreferences = UserPreferences.getUserPreferences(this);

        requestNetwork = new NetworkAsync(CheckListActivity.this, "getCheckList",  NetworkAsync.POST);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            JSONObject checkList = new JSONObject(returnString);

            table = new TableLayout(this); // 테이블 생성
            TableRow row[] = new TableRow[checkList.length()];     // 테이블 ROW 생성
            TextView text[][] = new TextView[checkList.length()][3]; // 데이터


            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            for(int tr=0; tr < checkList.length() ; tr++ ){
                String checkName = "check" + (tr+1);
                JSONObject check = new JSONObject(checkList.getString(checkName));
//                check.getString("title");
//                check.getString("bigsort");
//                check.getString("substance");

                row[tr] = new TableRow(this);


                for (int td = 0; td < 3; td++) {              // for문을 이용한 칸수 (TD)

                    text[tr][td] = new TextView(this);
                    if(td==0){
                        text[tr][td].setText(check.getString("title"));                   // 데이터삽입
                    }else if(td==1){
                        text[tr][td].setText(check.getString("bigsort"));                   // 데이터삽입
                    }else{
                        text[tr][td].setText(check.getString("substance"));                   // 데이터삽입
                    }
                    text[tr][td].setTextSize(15);                     // 폰트사이즈

                        text[tr][td].setTextColor(Color.BLACK);         // 폰트컬러



                    text[tr][td].setGravity(Gravity.CENTER);    // 폰트정렬

                    text[tr][td].setBackgroundColor(Color.WHITE);

                    text[tr][td].setWidth(450);    // 크기
                    row[tr].addView(text[tr][td]);


                } // td for end
                table.addView(row[tr], rowLayout);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }





        mlayout.addView(table,rowLayout);
    }

    public void tableGrid(int trCt, int tdCt) {

        table = new TableLayout(this); // 테이블 생성
        TableRow row[] = new TableRow[trCt];     // 테이블 ROW 생성
        TextView text[][] = new TextView[trCt][tdCt]; // 데이터

        for (int tr = 0; tr < trCt; tr++) {                  // for문을 이용한 줄수 (TR)

            row[tr] = new TableRow(this);

            for (int td = 0; td < tdCt; td++) {              // for문을 이용한 칸수 (TD)

                text[tr][td] = new TextView(this);
                text[tr][td].setText("데이터");                   // 데이터삽입
                text[tr][td].setTextSize(20);                     // 폰트사이즈
                if((td+tr)%2 == 0 ){
                    text[tr][td].setTextColor(Color.WHITE);         // 폰트컬러
                    text[tr][td].setBackgroundColor(Color.BLACK);     // 배경컬러
                }else{
                    text[tr][td].setTextColor(Color.BLACK);         // 폰트컬러
                    text[tr][td].setBackgroundColor(Color.WHITE);     // 배경컬러
                }

                text[tr][td].setGravity(Gravity.CENTER);    // 폰트정렬
                text[tr][td].setHeight(200);    // 크기
                text[tr][td].setWidth(200);    // 크기

                row[tr].addView(text[tr][td]);

            } // td for end
            table.addView(row[tr], rowLayout);

        } // tr for end

    }
}
