package com.learnfun.super8team.learnfun.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        // notice, title, substance, writer, date, respond, responddate
        try {
//            addSurveyTable((JSONArray)survey.get("question"));
            table = new TableLayout(this);
            Log.i(TAG, "length: "+questions.length());
            TableRow row[] = new TableRow[questions.length()];
            TextView text;

            for(int tr=0; tr<questions.length(); tr++) {
                article = (JSONObject) questions.get(tr);
                row[tr] = new TableRow(this);

                text = new TextView(this);
                text.setText((String) article.get("question"));
                text.setWidth(200);
                row[tr].addView(text);
                Log.i(TAG, "tr: "+tr+", question: "+article.get("question"));



                if (JSONArray.class == article.get("answers").getClass()) {
//                    if (!article.get("answers").equals(new String(""))) {
//                    final JSONArray answers = (JSONArray) article.get("answers");
                    for (int i=0; i<answers.length(); i++) {
                        text = new TextView(this);
                        text.setText((String) answers.get(i));
                        final String content = (String) answers.get(i);
                        text.setWidth(300);
                        row[tr].addView(text);

                        text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                answers.put();
//                                (TextView) v.getText();
//                                Log.i(TAG, "click: "+article.get());
                            }
                        });
                    }
                } // end of if


//                text[tr][td].setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.i(TAG, "click");
//                        Intent intent = new Intent(SurveyListActivity.this, SurveyDetailActivity.class);
//                        try {
//                            intent.putExtra("survey", (String)survey.get("no"));
//                            intent.putExtra("title", (String)survey.get("title"));
//                            startActivity(intent);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

                table.addView(row[tr]);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tableLayout.addView(table,rowLayout);
    }
}
