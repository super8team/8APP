package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class WriteHistoryActivity extends AppCompatActivity {
    NetworkAsync requestNetwork;
    private Button closeWriteHistory , writeContentHistory;
    Intent intent;
    String placeNum="";
    EditText contentHistory;
    UserPreferences userPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_history);

        closeWriteHistory = (Button)findViewById(R.id.closeWriteHistory);
        writeContentHistory = (Button)findViewById(R.id.writeContentHistory);

        closeWriteHistory.setOnClickListener(mainListener);
        writeContentHistory.setOnClickListener(mainListener);

        contentHistory = (EditText)findViewById(R.id.contentHistory);
        userPreferences = UserPreferences.getUserPreferences(this);
        Intent getIntent = getIntent();
        Bundle myBundle = getIntent.getExtras();
        placeNum = myBundle.getString("placeNum");

    }

    private View.OnClickListener mainListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.writeContentHistory:

                    JSONObject sendData = new JSONObject();
                    try {

                        sendData.put("userId",userPreferences.getUserId());
                        sendData.put("placeNum",placeNum);
                        sendData.put("content",contentHistory.getText().toString()+" ");
                        sendData.put("weather","sunny");
                        Log.d("sendData = ", String.valueOf(sendData));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestNetwork = new NetworkAsync(WriteHistoryActivity.this, "writeHistoryContent",  NetworkAsync.POST, sendData);
                    requestNetwork.execute();
                    intent = new Intent();
                    setResult(0,intent);
                    finish();
                    break;

                case R.id.closeWriteHistory:

                    intent = new Intent();
                    setResult(0,intent);
                    finish();
                    break;



            }

        }
    };

}
