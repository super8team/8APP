package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KIM on 2017-05-12.
 */

public class ImgView extends ContentView {
    private String src;
    private int width;
    private int height;
    private ImageView imageView;
    private Bitmap bitmap;
    private ContentActivity contentActivity;
    ImgView(JSONObject jobj, View view, String name,ContentActivity contentActivity) throws JSONException, InterruptedException {
        //이미지컨텐츠 제이슨 받아서 값 분배
        this.id = jobj.getInt("id");
        this.ContentName = name;
        this.name = jobj.getString("name");
        this.src = jobj.getString("src");
        this.width = jobj.getInt("width");
        this.height = jobj.getInt("height");
        this.contentActivity = contentActivity;
        imageView = (ImageView) view;

        Log.i("이미지뷰 설정완료 -----", this.name);
    }

    public void setContentView() throws InterruptedException {

            //이미지 크기적용
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) imageView.getLayoutParams();
            params.width = this.width;
            params.height = this.height;
//            imageView.setMaxWidth(width);
//            imageView.setMaxHeight(height);

             //이미지 저장

            if(src.substring(src.length()-3,src.length()).equals("gif") || src.substring(src.length()-3,src.length()).equals("GIF")){
                Log.i("g이미지 종류 :::",src.substring(src.length()-3,src.length()));
                Glide.with(contentActivity).asGif().load(src).into(imageView);
            }else{
                Log.i("e이미지 종류 :::",src.substring(src.length()-3,src.length()));
                Glide.with(contentActivity).asBitmap().load(src).into(imageView);
            }
            //컨텐츠 활성화
            imageView.setVisibility(View.VISIBLE);
        }

    @Override
    public void unsetContentView() {
        if (this.id == 1){
            //이미지뷰의 id가 1인건 invisible로 할 것
            imageView.setVisibility(View.INVISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
        }

    }

    @Override
    public void setClickAction(final JSONObject code, final ContentActivity contentActivity) {
        Log.i("이미지뷰 클릭 리스너 설정 ","설정된 컨텐츠 "+ContentName);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("이미지뷰 클릭 ", ContentName);
                Intent intent = new Intent(contentActivity,Dialog.class);

                contentActivity.startActivityForResult(setActionScript(code,intent,ContentName),3203);
                contentActivity.overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_right);
            }
        });
    }

    @Override
    public void setCheckEditAction(final EditText editview, final String answer, final JSONObject ooo, final JSONObject xxx, final ContentActivity contentActivity) {
        Log.i("이미지뷰 에디트 리스너 설정 ","설정된 컨텐츠 "+ContentName);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("이미지뷰 에디트 ", ContentName);
                Intent intent = new Intent(contentActivity,Dialog.class);

                //정답을 맞췄을경우 출력화면
                if (editview.getText().toString().equals(answer)){
                    intent = setActionScript(ooo,intent,ContentName);
                }else { //틀렷을경우 출력화면
                    intent = setActionScript(xxx,intent,ContentName);
                }

                contentActivity.startActivityForResult(intent,3203);
                contentActivity.overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_right);
            }
        });
    }

    @Override
    public void actionClear() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아무것도 하지 않음
                Log.i("클리어 액션","아무것도 하지않는다");
            }
        });
    }
}
