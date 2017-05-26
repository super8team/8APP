package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
    ImgView(JSONObject jobj, View view) throws JSONException, InterruptedException {
        //이미지컨텐츠 제이슨 받아서 값 분배
        this.id = jobj.getInt("id");
        this.name = jobj.getString("name");
        this.src = jobj.getString("src");
        this.width = jobj.getInt("width");
        this.height = jobj.getInt("height");

        imageView = (ImageView) view;

        Log.i("이미지뷰 설정완료 -----", this.name);
    }

    public void setContentView() throws InterruptedException {

            //이미지 크기적용
            imageView.setMaxWidth(width);
            imageView.setMaxHeight(height);
            //이미지 변경
            //네트워크작업은 메인 스레드가아닌 다른 스레드에서 작업해야함
            Thread imgThread = new Thread() {

                @Override
                public void run() {
                    try {
                        URL url = new URL(src);
                        //URL 주소를 이용해서 URL 객체 생성

                        //웹에서 이미지를 가져온뒤
                        //이미지 뷰에 지정할 Bitmap을 생성하는 과정

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            //웹에서 이미지를 가져오는 작업 스레드 실행
            //메인 스레드는 작업 스레드가 이미지 작업을 가져 올 때까지
            //대기 해야하므로 작업스레드의 join() 메소드를 호출해서
            //메인 스레드가 작업 스레드가 종료될때 까지 기다리도록 합니다.
            imgThread.start();

            imgThread.join();
             //이미지 저장
            imageView.setImageBitmap(bitmap);

            //컨텐츠 활성화
            imageView.setVisibility(View.VISIBLE);
        }

    @Override
    public void unsetContentView() {
        //이미지뷰의 id가 1인건 invisible로 할 것
    }

    @Override
    public void setClickAction(final JSONObject code, final ContentActivity contentActivity) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("리스너 클릭","버튼 클릭");
                Intent intent = new Intent(contentActivity,Dialog.class);
                try {
                    if(code.has("out_txt")){
                        //해당 액션 코드가 있는지 검사후 있으면 추출후 인텐트에 넣음
                        String text = code.getString("out_txt");
                        intent.putExtra("text",text);
                    }
                    if (code.has("out_img")){
                        String image= code.getString("out_img");
                        intent.putExtra("image",image);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                contentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void setCheckEditAction(final EditText editview, final String answer, final JSONObject ooo, final JSONObject xxx, final ContentActivity contentActivity) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contentActivity,Dialog.class);
                try{

                    if (editview.getText().toString().equals(answer)){
                        if(ooo.has("out_txt")){
                            String text = ooo.getString("out_txt");
                            intent.putExtra("text",text);
                        }
                        if (ooo.has("out_img")){
                            String image= ooo.getString("out_img");
                            intent.putExtra("image",image);
                        }
                    }else {
                        if(xxx.has("out_txt")){
                            String text = xxx.getString("out_txt");
                            intent.putExtra("text",text);
                        }
                        if (xxx.has("out_img")){
                            String image= xxx.getString("out_img");
                            intent.putExtra("image",image);
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                contentActivity.startActivity(intent);
            }
        });
    }
}
