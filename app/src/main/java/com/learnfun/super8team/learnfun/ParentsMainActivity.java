package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParentsMainActivity extends AppCompatActivity {
    private Button goToHistoryDetail , goToSurvey ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_main);

        goToHistoryDetail = (Button)findViewById(R.id.goToHistoryDetail);
        goToSurvey = (Button)findViewById(R.id.goToSurvey);

        goToHistoryDetail.setOnClickListener(mainListener);
        goToSurvey.setOnClickListener(mainListener);


    }

    private View.OnClickListener mainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.goToHistoryDetail:

                    Intent intent = new Intent(ParentsMainActivity.this, HistoryDetailActivity.class);

                    startActivity(intent);

                    break;

                case R.id.goToSurvey:
                    intent = new Intent(ParentsMainActivity.this, SurveyListActivity.class);
                    startActivity(intent);

                    break;




            }

        }
    };
}
