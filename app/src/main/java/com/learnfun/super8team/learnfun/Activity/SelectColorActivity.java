package com.learnfun.super8team.learnfun.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.learnfun.super8team.learnfun.R;

public class SelectColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_color);

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name","red");

                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }
}
