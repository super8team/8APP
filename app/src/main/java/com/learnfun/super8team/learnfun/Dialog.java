package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.view.Window.FEATURE_NO_TITLE;

public class Dialog extends AppCompatActivity  {
    private TextView textView;
    private ImageView imageView;
    private String src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);

//        findViewById(R.id.dialog_exit).setOnClickListener(this);

        textView  = (TextView)  findViewById(R.id.dialog_txt);
        imageView = (ImageView) findViewById(R.id.dialog_img);

        //넘어오는 인텐트값 확인후 뷰 표시
        Intent intent = getIntent();
        Intent back = new Intent();

        if(intent.hasExtra("end") || intent.hasExtra("toast")){
            if(intent.hasExtra("number")){
                int  num  = intent.getExtras().getInt("number");
                back.putExtra("number", num);
            }
            if(intent.hasExtra("toast")){
                String message = intent.getExtras().getString("toast");
                back.putExtra("toast",message);
            }

            setResult(3203, back);
        }
        if(intent.hasExtra("text")){
            textView.setText(intent.getExtras().getString("text"));
            textView.setVisibility(View.VISIBLE);
//            setResult(1717);
//            finish();
        }
        if(intent.hasExtra("image")){
            src = intent.getExtras().getString("image");

            if(src.substring(src.length()-3,src.length()).equals("gif") || src.substring(src.length()-3,src.length()).equals("GIF")){
                Log.i("g.dialog이미지 종류 :::",src.substring(src.length()-3,src.length()));
                Glide.with(this).asGif().load(src).into(imageView);
            }else{
                Log.i("e.dialog이미지 종류 :::",src.substring(src.length()-3,src.length()));
                Glide.with(this).asBitmap().load(src).into(imageView);
            }

            imageView.setVisibility(View.VISIBLE);
        }

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.dialog_exit:
//                this.finish();
//                textView.setVisibility(View.GONE);
//                imageView.setVisibility(View.GONE);
//                break;
//        }
//    }
}
