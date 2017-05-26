package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Dialog extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    private ImageView imageView;
    private String src;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);

        findViewById(R.id.dialog_exit).setOnClickListener(this);

        textView  = (TextView)  findViewById(R.id.dialog_txt);
        imageView = (ImageView) findViewById(R.id.dialog_img);

        //넘어오는 인텐트값 확인후 뷰 표시
        Intent intent = getIntent();
        if(intent.hasExtra("text")){
            textView.setText(intent.getExtras().getString("text"));
            textView.setVisibility(View.VISIBLE);
        }
        if(intent.hasExtra("image")){
            src = intent.getExtras().getString("image");
            Thread imgThread =  new Thread() {

                @Override
                public void run() {
                    try {
                        URL url = new URL(src);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            imgThread.start();

            try {
                imgThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(bitmap);

            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_exit:
                this.finish();
                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                break;
        }
    }
}
