package com.learnfun.super8team.learnfun.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.learnfun.super8team.learnfun.Async.NetworkAsync;
import com.learnfun.super8team.learnfun.R;
import com.learnfun.super8team.learnfun.Service.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SurveyListActivity extends AppCompatActivity {
    final static String TAG = "SurveyListActivity";
    final String URI = "getSurveyList";

    RelativeLayout tableLayout;
    NetworkAsync requestNetwork;
    Context context;
    UserPreferences userPreferences;
    JSONObject user, survey;
    JSONArray surveyList;
    String resultString;
    TableLayout table;
    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);

        context = this;
        userPreferences = UserPreferences.getUserPreferences(context);
        user = userPreferences.getUser();

        tableLayout = (RelativeLayout) findViewById(R.id.survey_list);

        try {
            requestNetwork = new NetworkAsync(context, URI, NetworkAsync.POST, user);
            resultString = requestNetwork.execute().get();
            // [{no: (int), title: (string), answer:(string), answerDate: (string)}, {}, {} ...]
            Log.i(TAG, "result is "+resultString);
            surveyList = new JSONArray(resultString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            table = new TableLayout(this); // 테이블 생성
            TableRow row[] = new TableRow[surveyList.length()];     // 테이블 ROW 생성
            TextView text[][] = new TextView[surveyList.length()][2]; // 데이터

            for(int tr=0; tr < surveyList.length() ; tr++ ){
                survey = (JSONObject) surveyList.get(tr);
                row[tr] = new TableRow(this);

                Iterator<?> keys = survey.keys();
                Integer td = 0;
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    text[tr][td] = new TextView(this);
                    Log.i(TAG, key+": "+survey.get(key));
                    text[tr][td].setText((String) survey.get(key));
                    text[tr][td].setGravity(Gravity.CENTER);
                    if (td==2)
                        text[tr][td].setWidth(1300);
                    text[tr][td].setClickable(true);
                    if (key.equals(new String("title"))) {
                        Log.i(TAG, "button create");
                        final String surveyNo = (String) survey.get("no");
                        final String surveyTitle = (String)survey.get("title");
                        text[tr][td].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "click");
                                Intent intent = new Intent(SurveyListActivity.this, SurveyDetailActivity.class);
                                intent.putExtra("survey", surveyNo);
                                intent.putExtra("title", surveyTitle);
//                                    Toast.makeText(context, surveyNo, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        });
                    }
                    Log.i(TAG, "tr: "+tr+", td: "+td+", value: "+survey.get(key));
                    row[tr].addView(text[tr][td]);
                    td++;
                } // td for end

                table.addView(row[tr]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tableLayout.addView(table,rowLayout);
    }
}
