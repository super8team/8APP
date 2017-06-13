package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StudentMainActivity extends AppCompatActivity {
    private Button goToHistoryDetail , goToContens , goToHistory , goToSurvey ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        goToHistoryDetail = (Button)findViewById(R.id.goToHistoryDetail);
        goToHistory = (Button)findViewById(R.id.goToHistory);
        goToSurvey = (Button)findViewById(R.id.goToSurvey);
        goToContens = (Button)findViewById(R.id.goToContens);


        goToHistoryDetail.setOnClickListener(mainListener);
        goToHistory.setOnClickListener(mainListener);
        goToSurvey.setOnClickListener(mainListener);
        goToContens.setOnClickListener(mainListener);

    }

    private View.OnClickListener mainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.goToHistoryDetail:

                    Intent intent = new Intent(StudentMainActivity.this, HistoryDetailActivity.class);

                    startActivity(intent);

                    break;

                case R.id.goToHistory:
                    intent = new Intent(StudentMainActivity.this, PlanTableActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToSurvey:
                    intent = new Intent(StudentMainActivity.this, StudentListActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToContens:
                    intent = new Intent(StudentMainActivity.this, ContentActivity.class);
                    startActivity(intent);

                    break;



            }

        }
    };
}
