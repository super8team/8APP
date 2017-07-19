package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
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
    NetworkAsync requestNetwork;
    Context context;
    UserPreferences userPreferences;
    JSONObject user, notice;
    JSONArray noticeList;
    String resultString;
    TableLayout table;
    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);

    final String NOTICEURI = "noticeList";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);



        context = this;
        userPreferences = UserPreferences.getUserPreferences(context);
        user = userPreferences.getUser();

//        try {
            // 해당 학부모가 받은 가정통신문 리스트를 받아옴
//            requestNetwork = new NetworkAsync(context, NOTICEURI, NetworkAsync.POST, user);
//            resultString = requestNetwork.execute().get();
//            // [{no: (int), title: (string), answer:(string), answerDate: (string)}, {}, {} ...]
//            noticeList =  new JSONArray(resultString);

            // 테스트용 값
            JSONObject dummy = new JSONObject();
            try {
                dummy.put("no", "1");
                dummy.put("title", "타이틀");
                dummy.put("answer", "동의");
                dummy.put("answerDate", "170717");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            noticeList.put(dummy);
            noticeList.put(dummy);
            noticeList.put(dummy);

//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }


        table = new TableLayout(this); // 테이블 생성
        TableRow row[] = new TableRow[noticeList.length()];     // 테이블 ROW 생성
//        TextView text[][] = new TextView[noticeList.length()][3]; // 데이터
        for(int tr=0; tr < noticeList.length() ; tr++ ){
            try {
                notice = new JSONObject((String) noticeList.get(tr));
                row[tr] = new TableRow(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int tdLimit = notice.length();

            TextView text[] = new TextView[tdLimit];
            for(int td = 0; td < tdLimit; td++) {

            }

            Iterator<?> keys = notice.keys();
            int td = 0;
            while( keys.hasNext() ) {
                try {
                    String key = (String)keys.next();
                    text[td] = new TextView(this);
                    text[td].setText((String)notice.get(key));
                    text[td].setTextSize(15);
                    text[td].setTextColor(Color.BLACK);
                    text[td].setGravity(Gravity.CENTER);
                    text[td].setWidth(200);
                    row[tr].addView(text[td]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                td++;
            }

            table.addView(row[tr], rowLayout);
        }

    }
}
