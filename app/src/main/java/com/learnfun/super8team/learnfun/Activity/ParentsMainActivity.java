package com.learnfun.super8team.learnfun.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.learnfun.super8team.learnfun.R;
import com.learnfun.super8team.learnfun.Service.UserPreferences;

public class ParentsMainActivity extends AppCompatActivity {
    private ImageButton goToHistoryDetail , goToSurvey , goToNoticeList,goToHistory ;
    private TextView welcome;
    UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_main);

        goToHistoryDetail = (ImageButton)findViewById(R.id.goToHistoryDetail);
        goToSurvey = (ImageButton)findViewById(R.id.goToSurvey);
        goToNoticeList = (ImageButton)findViewById(R.id.goToNoticeList);
        goToHistory = (ImageButton)findViewById(R.id.goToHistory);

        goToHistoryDetail.setOnClickListener(mainListener);
        goToSurvey.setOnClickListener(mainListener);
        goToNoticeList.setOnClickListener(mainListener);
        goToHistory.setOnClickListener(mainListener);

        userPreferences = UserPreferences.getUserPreferences(this);

        welcome = (TextView)findViewById(R.id.user_main_name);
        welcome.setText(userPreferences.getUserName()+" 학부모님 반갑습니다.");

        View view = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.parseColor("#f7f8f9"));
        }

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

                case R.id.goToNoticeList:
                    intent = new Intent(ParentsMainActivity.this, NoticeListActivity.class);
                    startActivity(intent);
                    break;

                case R.id.goToHistory:
                    intent = new Intent(ParentsMainActivity.this, HistoryListActivity.class);
                    startActivity(intent);

                    break;
            }

        }
    };
}
