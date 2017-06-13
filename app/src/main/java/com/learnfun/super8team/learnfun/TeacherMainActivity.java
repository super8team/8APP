package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeacherMainActivity extends AppCompatActivity {
    private Button goToToday , goToPlanTable , goToStudentList , goToContens , goToCheckList , goToRecommend ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        goToToday = (Button)findViewById(R.id.goToToday);
        goToPlanTable = (Button)findViewById(R.id.goToPlanTable);
        goToStudentList = (Button)findViewById(R.id.goToStudentList);
        goToContens = (Button)findViewById(R.id.goToContens);
        goToCheckList = (Button)findViewById(R.id.goToCheckList);
        goToRecommend = (Button)findViewById(R.id.goToRecommend);

        goToToday.setOnClickListener(mainListener);
        goToPlanTable.setOnClickListener(mainListener);
        goToStudentList.setOnClickListener(mainListener);
        goToContens.setOnClickListener(mainListener);
        goToCheckList.setOnClickListener(mainListener);
        goToRecommend.setOnClickListener(mainListener);

    }


    private View.OnClickListener mainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.goToToday:

                    Intent intent = new Intent(TeacherMainActivity.this, TodayActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToPlanTable:
                     intent = new Intent(TeacherMainActivity.this, PlanTableActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToStudentList:
                    intent = new Intent(TeacherMainActivity.this, StudentListActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToContens:
                    intent = new Intent(TeacherMainActivity.this, ContentActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToCheckList:
                    intent = new Intent(TeacherMainActivity.this, CheckListActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToRecommend:
                    intent = new Intent(TeacherMainActivity.this, RecommendActivity.class);
                    startActivity(intent);

                    break;

            }

        }
    };

}
