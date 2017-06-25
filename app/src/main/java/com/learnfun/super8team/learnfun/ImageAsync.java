package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by cho on 2017-06-25.
 */

public class ImageAsync extends AsyncTask<String, Void, Bitmap> {
    final static String TAG = "ImageAsync";

    final static String POST = "POST";
    final static String GET = "GET";

    final int MAX_BYTE = 1024; // 1024bytes
    final int MAX_DELAY = 10000; // 10seconds
    Bitmap bitmapImage=null;
    Context context = null;
    // 서비스도메인
    String urlAddr = "";
    JSONObject jsonObject = new JSONObject();
    String httpMethod;

    InputStream is = null;
    ByteArrayOutputStream baos = null;
    String stringData = "";

    public ImageAsync(Context context, String uri) {
        // 호출한 컨텍스트, 도메인 이후 uri, http메서드(상수NetwirdkAsync.POST), 전달할 json객체
        this.context = context;
        this.urlAddr += uri;
        Log.i(TAG, "result url: "+urlAddr);
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap image=null;
        // / 요청
        image = request();

        // 결과(json으로 변환 가능한 문자열)반환
        return image;
    }

    private Bitmap request() {


        try {
            URL url = new URL(urlAddr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if(conn != null) {
                // 메서드 방식에 따른 리퀘스트 프로퍼티 설정
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmapImage = BitmapFactory.decodeStream(is);

//                // 요청
//                int responseCode = conn.getResponseCode();
//                Log.d(TAG, "requestCode = "+responseCode);
//                if(responseCode == HttpURLConnection.HTTP_OK) {
//                    // 응답
//                    response(conn);
//                }
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

    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Bitmap s) {
        super.onPostExecute(s);
    }
}
