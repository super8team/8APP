package com.learnfun.super8team.learnfun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity {
    private ArrayList<JSONObject> jsons = new ArrayList();
    private ArrayList<Content> contents = new ArrayList();
    private JSONArray json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        //권한받기
        permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCheck(Manifest.permission.ACCESS_COARSE_LOCATION);

        json = call(); //컨텐츠 명세 불러오기
        //DB에서 명세 뽑아오는 걸로 수정할 것 - 메인 액티비티에서 명세 찾을 조건을 받아야됨

        Log.i("컨텐츠 길이","asdadsads"+String.valueOf(json.length()));

        try {
            for (int i = 0; i < json.length(); i++) {
                //전체 컨텐츠 갯수 뽑아내고 분리
                jsons.add(json.getJSONObject(i));
                Log.i("컨텐츠 명",jsons.get(i).getString("name"));
                Content con = new Content(jsons.get(i), this);
                contents.add(con);
            }
//        Log.i("컨텐츠 길이","asdadsads"+String.valueOf(contents.size()));
            //테스트용 실행
//            contents.get(0).setContentView();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //GPS 값 받아오기

//        final TextView tv = (TextView) findViewById(R.id.bottom_text);
//
//        tv.setText("GPS가 잡혀야 좌표가 구해짐");

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Log.i("리스너 작동하는가","작동함");
                double lat = location.getLatitude();
                double lng = location.getLongitude();
//                double lat = 35.896497;
//                double lng = 128.620664;
                //이곳에서 각컨텐츠 조건함수 호출
                for (int i=0;i<contents.size();i++){
                    try {
                        //각콘텐츠 반경과 현재 좌표를 비교하고 컨텐츠 실행중이 아니면 컨텐츠 표시
                        Log.i(contents.get(i).getContentName()+"디세이블 상황 ", String.valueOf(contents.get(i).getContentDisable()));
                        if(contents.get(i).checkCondition(lat,lng) && !Content.CONTENT_USED && !contents.get(i).getContentDisable()){
                            contents.get(i).setContentView();
                            //AR 화면 끄는 코드 추가할 것
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

//                tv.setText("latitude: " + lat + ", longitude: " + lng);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
//                tv.setText("onStatusChanged :"+provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
//                tv.setText("onProviderEnabled :"+provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
//                tv.setText("onProviderDisabled :"+provider);
            }
        };

        Log.i("정상작동 하는가", ": 한다");
        try{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,ll);
        }catch (SecurityException se){
            se.printStackTrace();
        }

    }

    //제이슨 파일을 읽어오는 코드
    public JSONArray call(){
        AssetManager am = getResources().getAssets();

        JSONArray jobj = null;
        try{

            AssetManager.AssetInputStream ais = (AssetManager.AssetInputStream)am.open("json/bb.json");

            BufferedReader br = new BufferedReader(new InputStreamReader(ais));

            StringBuilder sb = new StringBuilder();

            int bufferSize = 1024 * 1024;

            char readBuf [] = new char[bufferSize];
            int resultSize = 0;

            while((resultSize = br.read(readBuf)) != -1){
                if(resultSize == bufferSize){
                    sb.append(readBuf);
                }else{
                    for(int i = 0;i< resultSize; i++){
                        sb.append(readBuf[i]);
                    }
                }
            }

            String jString = sb.toString();

             jobj = new JSONArray(jString);


        }catch (JSONException je){
            Log.e("jsonerr","제이슨에러",je);
        }catch (Exception e){
            Log.e("jsonerr","파일이없음",e);
        }
        return jobj;
    }

    //권한 받는 코드
    public void permissionCheck(final String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            final Activity context = this;

            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);

            if(permissionCheck == PackageManager.PERMISSION_DENIED){

                //권한이 없을경우
                if(context.shouldShowRequestPermissionRationale(permission)){
                    Log.i("권한이 없다 ", permission);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Permission Request")
                            .setMessage("Permission to use features is required")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        context.requestPermissions(new String[]{permission},20000);
                                    }
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }else {
                    context.requestPermissions(new String[]{permission}, 20000);
                }
            }
        }
    }

//    콘텐츠 종료 확인 코드, 액션스크립트에 end블록을 사용했을경우 리절트 반환
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("리절트 실행", "===================================");
        if(resultCode == 3203){
            Log.i("코드 일치", String.valueOf(resultCode));
            String contentName = data.getStringExtra("name");

            for(int i=0;i<contents.size();i++){
                //반환값의 이름과 같은 이름의 컨텐츠를 찾는다.
                if(contents.get(i).getContentName().equals(contentName)){
                    //찾아서 종료,
                    contents.get(i).unsetContentView();

                    //AR 다시 작동 추가할 것
                }
            }
        }
    }
}
