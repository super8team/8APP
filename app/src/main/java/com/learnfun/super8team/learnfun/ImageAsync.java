package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by cho on 2017-06-25.
 */

public class ImageAsync extends AsyncTask<Object, Object, Bitmap[]> {
    final static String TAG = "ImageAsync";

    Bitmap[] bitmapImage;
    Context context = null;
    // 서비스도메인
    String[] urlAddr;
    InputStream is = null;
    //dialog
    AppCompatDialog progressDialog;


    public ImageAsync(Context context, String[] uri) {
        // 호출한 컨텍스트, 도메인 이후 uri, http메서드(상수NetwirdkAsync.POST), 전달할 json객체
        this.context = context;
        this.urlAddr = uri;

        Log.i(TAG, "result url: "+ urlAddr[0]);
    }


    @Override
    protected Bitmap[] doInBackground(Object... params) {

        Bitmap[] image;
        // / 요청
//        image = request();

        // 결과(json으로 변환 가능한 문자열)반환
        return request();
    }

    private Bitmap[] request() {


        try {
            bitmapImage = new Bitmap[urlAddr.length];
            for(int i=0;i<urlAddr.length;i++){
                URL url = new URL(urlAddr[i]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if(conn != null) {
                    // 메서드 방식에 따른 리퀘스트 프로퍼티 설정
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmapImage[i] = BitmapFactory.decodeStream(is);
//                // 요청
//                int responseCode = conn.getResponseCode();
//                Log.d(TAG, "requestCode = "+responseCode);
//                if(responseCode == HttpURLConnection.HTTP_OK) {
//                    // 응답
//                    response(conn);
//                }
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapImage;
    }

    @Override
    protected void onPostExecute(Bitmap[] s) {
        startProgress();//다이얼로그 실행 함수
    }
    public void progressON(Context activity, String message) {

//        if (activity == null || activity.isFinishing()) {
//            return;
//        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }
    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }


        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void startProgress() {

        progressON(context,"Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }
}
