package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SurveyDetailActivity extends AppCompatActivity {
    final static String TAG = "SurveyDetailActivity";
    final String DETAIL_URI = "getSurveyDetail";
    final String RESPOND_STORE_URI = "surveyRespondStore";
    final String RESPOND_UPDATE_URI = "surveyRespondUpdate";
    final JSONArray answers = new JSONArray();

    TextView title, date, question, answer;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    UserPreferences user;
    NetworkAsync request;
    String surveyNo, resultString;
    Context context;
    JSONObject json, article;
    JSONArray questions;

    RelativeLayout tableLayout;
    TableLayout table;
    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(MATCH_PARENT,WRAP_CONTENT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_detail);

        context = this;
        title = (TextView) findViewById(R.id.title_value);
        date = (TextView) findViewById(R.id.date_value);
        tableLayout = (RelativeLayout) findViewById(R.id.survey_list);

        user = UserPreferences.getUserPreferences(context);
        surveyNo = "1";
//        surveyNo = getIntent().getStringExtra("survey");
//        Log.i(TAG, "survey no: "+surveyNo);
        ((TextView) findViewById(R.id.title_value)).setText(getIntent().getStringExtra("title"));

        try {
            json = new JSONObject();
            json.put("survey", surveyNo);
            json.put("no", user.getUserNo());

            request = new NetworkAsync(context, DETAIL_URI, NetworkAsync.POST, json);
            resultString = request.execute().get();
            Log.i(TAG, "result: "+resultString);
            questions = new JSONArray(resultString);
//            surveyInfo = new JSONObject(resultString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inputValue();
    }

    private void inputValue() {
        try {
            table = new TableLayout(this);
            TableRow row[] = new TableRow[questions.length()];
            TextView text;

            for(int tr=0; tr<questions.length(); tr++) {
                article = (JSONObject) questions.get(tr);
                row[tr] = new TableRow(this);

                // 문제
                text = new TextView(this);
                text.setText((String)article.get("question"));
                text.setWidth(150);
                row[tr].addView(text);

                // 답안
                if (JSONArray.class == article.get("answers").getClass()) { // 객관식일 때 배치
                    JSONArray answers = (JSONArray) article.get("answers");
                    for (int i=0; i<answers.length(); i++) { // 답안 각각
                        text = new TextView(this);
                        text.setText((String) answers.get(i));
                        text.setWidth(200);
                        text.setGravity(Gravity.CENTER);
                        text.setBackgroundColor(Color.LTGRAY);

                        final TextView finalText = text;
                        text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Toast.makeText(SurveyDetailActivity.this, finalText.getText(), Toast.LENGTH_SHORT).show();

                            }
                        });

                        row[tr].addView(text);
                    }
                } else { // 주관식일때 배치
                    EditText subAnswer = new EditText(this);
                    subAnswer.setWidth(200);
                    row[tr].addView(subAnswer);
                } // end of if

                table.addView(row[tr]);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tableLayout.addView(table,rowLayout);
    }
}
