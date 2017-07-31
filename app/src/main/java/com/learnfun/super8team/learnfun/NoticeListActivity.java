package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class NoticeListActivity extends AppCompatActivity {
    final static String TAG = "NoticeListActivity";
    final String NOTICEURI = "noticeList";

    RelativeLayout tableLayout;
    NetworkAsync requestNetwork;
    Context context;
    UserPreferences userPreferences;
    JSONObject user, notice;
    JSONArray noticeList;
    String resultString;
    TableLayout table;
    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        context = this;

        tableLayout = (RelativeLayout) findViewById(R.id.notice_list);

        userPreferences = UserPreferences.getUserPreferences(context);
        user = userPreferences.getUser();
        Log.i(TAG, "user get!");
        try {
//             해당 학부모가 받은 가정통신문 리스트를 받아옴
            requestNetwork = new NetworkAsync(context, NOTICEURI, NetworkAsync.POST, user);
            resultString = requestNetwork.execute().get();
            // [{no: (int), title: (string), answer:(string), answerDate: (string)}, {}, {} ...]
            Log.i(TAG, "result is "+resultString);
            noticeList = new JSONArray(resultString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }



        try {
            table = new TableLayout(this); // 테이블 생성
            TableRow row[] = new TableRow[noticeList.length()];     // 테이블 ROW 생성
            TextView text[][] = new TextView[noticeList.length()][4]; // 데이터

            for(int tr=0; tr < noticeList.length() ; tr++ ){
                notice = (JSONObject) noticeList.get(tr);
                row[tr] = new TableRow(this);

                Iterator<?> keys = notice.keys();
                Integer td = 0;
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    text[tr][td] = new TextView(this);
                    text[tr][td].setText((String) notice.get(key));
                    text[tr][td].setGravity(Gravity.CENTER);
                    text[tr][td].setWidth(200);
                    text[tr][td].setClickable(true);
                    if (key.equals(new String("title"))) {
                        Log.i(TAG, "button create");
                        text[tr][td].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "click");
                                Intent intent = new Intent(NoticeListActivity.this, NoticeDetailActivity.class);
                                try {
                                    intent.putExtra("notice", (String)notice.get("no"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    Log.i(TAG, "tr: "+tr+", td: "+td+", value: "+notice.get(key));
                    row[tr].addView(text[tr][td]);
                    td++;
                } // td for end

                table.addView(row[tr]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        tableLayout.addView(table,rowLayout);


    }
}
