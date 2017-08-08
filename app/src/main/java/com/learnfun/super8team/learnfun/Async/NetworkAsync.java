package com.learnfun.super8team.learnfun.Async;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bon on 2017-05-18.
 */

// UI쓰레드와 백그라운드쓰레드를 같이 쓰기 쉽게 설계해 간단한 작업에 적합하게 만든 쓰레드
public class NetworkAsync extends AsyncTask<String, Integer, String> {
    final static String TAG = "NetworkAsync";

    public final static String POST = "POST";
    final static String GET = "GET";

    final int MAX_BYTE = 1024; // 1024bytes
    final int MAX_DELAY = 10000; // 10seconds

    Context context = null;
    // 서비스도메인
    String urlAddr = "http://163.44.166.91/LEARnFUN/public/app/";
    JSONObject jsonObject = new JSONObject();
    String httpMethod;

    InputStream is = null;
    ByteArrayOutputStream baos = null;
    String stringData = "";

    public NetworkAsync(Context context, String uri, String httpMethod, JSONObject jsonObject) {
        // 호출한 컨텍스트, 도메인 이후 uri, http메서드(상수NetwirdkAsync.POST), 전달할 json객체
        this.context = context;
        this.urlAddr += uri;
        this.jsonObject = jsonObject;
        this.httpMethod = httpMethod;
        Log.i(TAG, "result url: "+urlAddr);
    }
    public NetworkAsync(Context context, String uri, String httpMethod) {
        // 호출한 컨텍스트, 도메인 이후 uri, http메서드(상수NetwirdkAsync.GET)
        this.context = context;
        this.urlAddr += uri;
        this.httpMethod = httpMethod;
        Log.i(TAG, "result url: "+urlAddr);
    }

    @Override
    protected String doInBackground(String... params) {
        // 요청
        request();

        // 결과(json으로 변환 가능한 문자열)반환
        return stringData;
    }

    private String request() {
        String returnMessage = "fail";

        try {
            URL url = new URL(urlAddr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if(conn != null) {
                // 메서드 방식에 따른 리퀘스트 프로퍼티 설정
                switch (httpMethod) {
                    case POST:
                        setPost(conn);
                        break;
                    case GET:
                        setGet(conn);
                        break;
                    default:
                        /*
                        * 사용자 정의 에러, 없는 http메서드로 보내는 경우
                        * */
                }

                // 요청
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "requestCode = "+responseCode);
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    // 응답
                    response(conn);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMessage;
    }

    private void setPost(HttpURLConnection conn) {
        OutputStream os;
        try {
            // 리퀘스트 post 설정
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestMethod(POST);
            conn.setConnectTimeout(MAX_DELAY); // 응답 대기시간
            conn.setDoInput(true); // 읽기
            conn.setDoOutput(true); // 쓰기

            // 리퀘스트 프로퍼티 설정에 맞춰 보낼 자료를 스트림에 세팅
            if(jsonObject!=null) {
                os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.flush();
                os.close();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGet(HttpURLConnection conn) {
        Log.i(TAG, "get request");
        try {
            conn.setConnectTimeout(MAX_DELAY);
            conn.setRequestMethod(GET);
            conn.setDoInput(true);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private String response(HttpURLConnection conn) {
        Log.i(TAG, "in response");
        try {
            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();

            byte[] byteBuffer = new byte[MAX_BYTE];
            byte[] byteData = null;
            int currentLength = 0;
            Log.i(TAG, "start read");
            // 자료 읽기 시작
            while((currentLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, currentLength);
            }
            byteData = baos.toByteArray();

            // byte로 읽어들인 것을 string으로 변환
            stringData = new String(byteData);
            Log.i(TAG, "DATA response = "+stringData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringData;
    }


}
