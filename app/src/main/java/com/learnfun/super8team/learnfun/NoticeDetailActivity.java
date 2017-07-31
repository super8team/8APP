package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class NoticeDetailActivity extends AppCompatActivity {
    final static String TAG = "NoticeDetailActivity";
    final String DETAIL_URI = "noticeDetail";
    final String ANSWER_URI = "respondStore";
    final String ANSWER_UPDATE_URI = "respondUpdate";

    TextView title, date, writer, respond;
    Button confirmBtn;
    Boolean confirmFlag = true;

    UserPreferences user;
    NetworkAsync request;
    String noticeNo, resultString;
    Context context;
    JSONObject json, noticeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        title = (TextView) findViewById(R.id.title_value);
        writer = (TextView) findViewById(R.id.writer_value);
        date = (TextView) findViewById(R.id.date_value);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);
        respond = (TextView) findViewById(R.id.respond_value);

        context = this;

        user = UserPreferences.getUserPreferences(context);
        noticeNo = getIntent().getStringExtra("notice");

        try {
            json = new JSONObject();
            json.put("notice", noticeNo);
            json.put("no", user.getUserNo());

            request = new NetworkAsync(context, DETAIL_URI, NetworkAsync.POST, json);
            resultString = request.execute().get();
            Log.i(TAG, "result: "+resultString);
            noticeInfo = new JSONObject(resultString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    json = new JSONObject();
                    json.put("no", user.getUserNo());
                    json.put("notice", noticeNo);

                    String uri;
                    if (respond.getText().equals(new String(""))) {
                        uri = ANSWER_URI;
                    } else {
                        uri = ANSWER_UPDATE_URI;
                    }
                    if(confirmFlag) { // true, 동의상태 -> 비동의로 수정
                        Log.i(TAG, "비동의함");
                        json.put("respond", "비동의");
                        request = new NetworkAsync(context, uri, NetworkAsync.POST, json);
                        respond.setText("비동의");
                        confirmBtn.setText("동의");
                        confirmFlag = false;
                    } else { // false, 비동의상태 -> 동의로 수정
                        Log.i(TAG, "동의함");
                        json.put("respond", "동의");
                        respond.setText("동의");
                        confirmBtn.setText("비동의");
                        request = new NetworkAsync(context, uri, NetworkAsync.POST, json);
                        confirmFlag = true;
                    }

                    request.execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        inputValue(noticeInfo);
    }

    private void inputValue(JSONObject notice) {
        // notice, title, substance, writer, date, respond, responddate
        try {
            title.setText((String) notice.get("title"));
            writer.setText((String) notice.get("writer"));
            date.setText((String) notice.get("date"));
            respond.setText((String) notice.get("respond"));
            if(respond.getText().equals(new String("동의"))) {
                confirmBtn.setText("비동의");
                confirmFlag = true;
            } else {
                confirmBtn.setText("동의");
                confirmFlag = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
