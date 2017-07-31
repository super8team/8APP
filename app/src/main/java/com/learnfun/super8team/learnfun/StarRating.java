package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import static android.R.attr.rating;
import static android.R.attr.ratingBarStyle;
import static android.view.Window.FEATURE_NO_TITLE;

public class StarRating extends AppCompatActivity {
    private RatingBar ratingBar;
    private TextView tv;
    private Button btn;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_star_rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tv = (TextView) findViewById(R.id.ratingText);
        btn = (Button) findViewById(R.id.ratingBtn);

        ratingBar.setStepSize((float) 0.5);
        ratingBar.setRating(3);
        ratingBar.setIsIndicator(false);
        tv.setText(ratingBar.getRating()*2+" 점");

        Intent getintent = getIntent();

        intent = new Intent();
        intent.putExtra("number",getintent.getIntExtra("number",-1));
        intent.putExtra("score",(int) (ratingBar.getRating()*2));
        setResult(9191,intent);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv.setText(rating*2+" 점");
                intent.removeExtra("score");
                intent.putExtra("score",(int) (rating*2));
                setResult(9191,intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.removeExtra("score");
                intent.putExtra("score",(int) (ratingBar.getRating()*2));
                setResult(9191,intent);
                finish();
            }
        });
    }
}
