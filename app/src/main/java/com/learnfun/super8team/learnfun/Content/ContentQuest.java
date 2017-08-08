package com.learnfun.super8team.learnfun.Content;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.learnfun.super8team.learnfun.R;

import static android.view.Window.FEATURE_NO_TITLE;

public class ContentQuest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_quest);
        Intent intent = getIntent();

        TextView tv = (TextView) findViewById(R.id.questMessage);
        tv.setText(intent.getStringExtra("message"));

        setResult(7732);
    }
}
