package com.learnfun.super8team.learnfun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.learnfun.super8team.learnfun.AR.ARCamera;
import com.learnfun.super8team.learnfun.AR.AROverlayView;
import com.learnfun.super8team.learnfun.AR.LocationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ContentActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    final static String TAG = "ContentActivity";

    private SurfaceView surfaceView;
    private LinearLayout cameraContainerLayout;
    private RelativeLayout OverlayLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;

    private SensorManager sensorManager;
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 20;//1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    private Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
//    RocationPermissionThread permissionThread;

    private ArrayList<JSONObject> jsons = new ArrayList();
    private ArrayList<Content> contents = new ArrayList();
    private JSONArray json;

    private DBManager dbManager;
    private Context context = this;
    private NetworkAsync requestNetwork;
    private UserPreferences userPreferences = UserPreferences.getUserPreferences(context);


    private LinearLayout countParameter;
    private LinearLayout scoreParameter;
    private ImageButton quest;
    private ImageButton bingo;
    private ImageButton collection;
    private ImageButton contentMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //DB생성
        dbManager = new DBManager(getApplicationContext(),"content",null,1);
//        Log.i("db???",dbManager.toString());

        // AR카메라를 위한 초기 설정
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        cameraContainerLayout = (LinearLayout) findViewById(R.id.camera_container_layout);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        arOverlayView = new AROverlayView(this);

        OverlayLayout = (RelativeLayout) findViewById(R.id.overlay_layout);

        initContents();
        requestCameraPermission();
        // 0607 22:00 여기 있던 권한 설정은 onResume으로 옮겼습니다 ㅎ.ㅎ 카메라 권한 따고 초기화 하는 거랑 같이 하기 위해서!
        // 0607 23:53 권한 획득에 자꾸 실패해서 진아코드로 대체

        // 여기 있던 json->call()은 onResume의 initContents()로 옮겼습니다! oncreate보다 resume이 늦게 시작하기 때문!

        // GPS 값 받아오기
//        tvGPS = (TextView) findViewById(R.id.gps);
//        tvNAV = (TextView) findViewById(R.id.nav);
//        tv = (TextView) findViewById(R.id.bottom_text);
//        tv.setText("GPS가 잡혀야 좌표가 구해짐");

        // 여기 있던 locationListener는 밑에 있는 리스너 함수들로 옮김!

        //뒤로가기 버튼
        final ImageButton exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exitBtn.getVisibility() == View.VISIBLE) {
                    for (int i = 0; i < contents.size(); i++) {
                        contents.get(i).closeContent();
                        initAROverlayView();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 권한 획득
        requestLocationPermission();
        registerSensors();
        initAROverlayView();

    }

    public void onPause() {
//        releaseCamera();
        super.onPause();
        locationManager.removeUpdates(this);
    }

    private void releaseCamera() {
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            arCamera.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    public void requestCameraPermission() {
//        Log.e(TAG, "requestCameraPermission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            initARCameraView();
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e(TAG, "requestLocationPermission - if");
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
//            Log.e(TAG, "requestLocationPermission - else");
            initLocationService();
        }
    }

    private void initContents() {

        //상위 메뉴들 선언
        countParameter = (LinearLayout) findViewById(R.id.countParameter);
        scoreParameter = (LinearLayout) findViewById(R.id.scoreParameter);

        quest = (ImageButton) findViewById(R.id.questBtn);
        bingo = (ImageButton) findViewById(R.id.bingoBtn);
        contentMap = (ImageButton) findViewById(R.id.mapBtn);
        collection = (ImageButton) findViewById(R.id.collectBtn);

        try {
        JSONObject userInputInfo = new JSONObject();
            //유저 정보를 가져온다.
//            Log.i("유저정보   ",userPreferences.getUserId());
        userInputInfo.put("inputID",userPreferences.getUserId());

            // 서버에 유저아이디를 넘기고 명세를 넘겨받음
        requestNetwork = new NetworkAsync(context,"getContents",NetworkAsync.POST, userInputInfo);


        //로컬 디비에 명세가 없으면 명세를 저장, 명세가 있으면 로컬명세를 읽음
        String data = dbManager.init(call().toString());
//
//        실제실행 코드
//        String data = dbManager.init(requestNetwork.execute().get());



            //문자열을 제이슨 배열로 변환
            json = new JSONArray(data);

//            Log.i("컨텐츠 길이","asdadsads"+String.valueOf(json.length()));
            for (int i = 0; i < json.length(); i++) {
                //전체 컨텐츠 갯수 뽑아내고 분리
                jsons.add(json.getJSONObject(i));
//                Log.i("컨텐츠 명",jsons.get(i).getString("name"));
                Content con = new Content(jsons.get(i), this);
                contents.add(con);

                //진아 request  오버레이뷰에 컨텐츠 객체 추가
                arOverlayView.addOverlayContent(con);
            }
//        Log.i("컨텐츠 길이","asdadsads"+String.valueOf(contents.size()));
            //테스트용 실행
//            contents.get(0).setContentView();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    private void initLocationService() {

        // check for locationUpdate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // only check
            return ;
        }


        this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isNetworkEnabled && !isGPSEnabled) {
            this.locationServiceAvailable = false;
        } else {
            this.locationServiceAvailable = true;
        }

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
//            if(locationManager != null) {
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                Log.i(TAG, "gps init from NETWORK provider");
//            }
        } // end if network envables

        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
//            if (locationManager != null) {
//                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                Log.i(TAG, "gps init from GPS provider");
//            }
        } // end if gps enabled

//        if (location != null ) {
//            updateLatestLocation();
//        } // end if location is not null
    } // end function initLocationService

    private void updateLatestLocation() {
        Log.e(TAG, "updating..."+String.valueOf(location.getLongitude()));
        if (arOverlayView !=null) {
            Log.i(TAG, "initial location update");
            arOverlayView.updateCurrentLocation(location);
        }
        Log.e(TAG, String.valueOf(arOverlayView.getVisibility()));
    }

    private void initARCameraView() {
        Log.e(TAG, "initARCameraView");
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(this, surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    private void reloadSurfaceView() {
        Log.e(TAG, "reloadSurfaceView");
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open();
                camera.startPreview();
                arCamera.setCamera(camera);
            } catch (RuntimeException ex){
                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void registerSensors() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void initAROverlayView() {
//        contentView.setVisibility(View.GONE);
        Log.i("CONTENT_USED", String.valueOf(Content.CONTENT_USED));
        if (!Content.CONTENT_USED) {
            Log.i(TAG, "initAROverlayView");

            if (arOverlayView.getParent() != null) {
                ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
            }
            OverlayLayout.addView(arOverlayView);
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


    //    콘텐츠 종료 확인 코드, 액션스크립트에 end블록을 사용했을경우 리절트 반환
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("리절트 실행", "===================================");
        Log.i("토스트 내용물", "aaa"+String.valueOf(data));
        if(resultCode == 3203){
            Log.i("코드 일치", String.valueOf(resultCode));

            if(data.hasExtra("name")){
                String contentName = data.getStringExtra("name");
                Log.i("반환 이름?",contentName);
                for(int i=0;i<contents.size();i++){
                    //반환값의 이름과 같은 이름의 컨텐츠를 찾는다.
                    if(contents.get(i).getContentName().equals(contentName)){
                        //찾아서 종료,
                        contents.get(i).unsetContentView();
                    }
                }
            }

            if(data.hasExtra("toast")){
                Toast.makeText(this,data.getStringExtra("toast"),Toast.LENGTH_SHORT).show();
            }
        }else if( resultCode==7732){
            //한번 들어갔다가 나오면 아이콘 원상태로 복귀
            quest.setBackgroundResource(R.drawable.quest);
        }


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrixFromVector = new float[16];
            float[] projectionMatrix = new float[16];
            float[] rotatedProjectionMatrix = new float[16];

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);

            if (arCamera != null) {
                projectionMatrix = arCamera.getProjectionMatrix();
            }

            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
////        Log.i("위치위취취췿", "location updatelocation updatelocation updatelocation update");
////        Log.i("컨텐츠 사용중", String.valueOf(!Content.CONTENT_USED));
//        //이곳에서 각컨텐츠 조건함수 호출
////        contentsCheck(35.896480,128.620723);
//        contentsCheck(lat,lng);
//
//        tvGPS.setText("latitude: " + lat + ", longitude: " + lng);
//        Log.e(TAG, "GPS>> latitude: " + lat + ", longitude: " + lng);

        this.location = location;

        if (arOverlayView !=null) {
//            Log.i(TAG, "location update");
            this.updateLatestLocation();
            arOverlayView.updateCurrentLocation(location);
//            Log.e(TAG, String.valueOf(arOverlayView.getVisibility()));

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void stopAROverlay () {
        Log.e(TAG, "stop overlay!");
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY()-130;
        Location contentsLocation;

        Log.e(TAG, "onTouch: "+x+", "+y);
        Effect effect = new Effect(this,x,y);

        if (effect.getParent() != null) {
            ((ViewGroup) effect.getParent()).removeView(effect);
        }
        OverlayLayout.addView(effect);

        if (!locationServiceAvailable) return super.onTouchEvent(event);

        //컨텐츠 실행 부분을 이곳에 < contentsCheck(위도,경도)
        for (int i=0;i<contents.size();i++) {

            if(contents.get(i).getClickable() && contents.get(i).getVisionable()) {
                try {
                    contentsLocation = contents.get(i).getContentLocation();
                    List<Float> points = arOverlayView.getNavigationPoint(contentsLocation, i);

                    Float targetX = points.get(0);
                    Float targetY = points.get(1);
                    Log.i("contents", targetX+", "+targetY);


                    if(targetX - 100 < x && x < targetX + 100 && targetY - 100 < y && y < targetY +100) {
                          if (!Content.CONTENT_USED && !contents.get(i).getContentDisable()) {
                              Log.e(TAG, contents.get(i).getContentName());

                              //AR 비활성화
                              stopAROverlay();
                              contents.get(i).setContentView();
                          }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

            return super.onTouchEvent(event);
    }

    private void contentsCheck(double lat, double lng){
        if(!Content.CONTENT_USED) {
            for (int i = 0; i < contents.size(); i++) {
                try {
                    //각콘텐츠 반경과 현재 좌표를 비교하고 컨텐츠 실행중이 아니면 컨텐츠 표시
//                        Log.i("디세이블 상황 ", String.valueOf(contents.get(i).getContentDisable()));

                    if (contents.get(i).checkCondition(lat, lng) && !Content.CONTENT_USED && !contents.get(i).getContentDisable() && !contents.get(i).getClickable()) {

                        //AR 비활성화
                        stopAROverlay();
                        contents.get(i).setContentView();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setContentStatus(String contentName,boolean visionable, boolean clickable, boolean disable){
        for(int i =0;i<contents.size();i++){
            if(contents.get(i).getContentName().equals(contentName)){
                //로컬데이터베이스 명세 변경
                Log.i("컨텐츠명 :"+contents.get(i).getContentName(),"비교 명 :"+contentName);
                contents.get(i).setContentVisionable(visionable);
                contents.get(i).setContentClickable(clickable);
                contents.get(i).setContentDisable(disable);
                getDB().update(contentName,visionable,clickable,disable);
            }
        }
    }

    public DBManager getDB(){
        return dbManager;
    }
    public RelativeLayout getOverlayLayout() {return OverlayLayout; }

    public void onQuestButton(final String msg) {
        Log.i("뷰띄우는부분","체크됨");
        quest.setBackgroundResource(R.drawable.quest_mark);
        quest.setVisibility(View.VISIBLE);
        quest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this,ContentQuest.class);
                intent.putExtra("message",msg);
                startActivityForResult(intent,7732);
                overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_right);
            }
        });
    }
    public void closeQuestButton(){
        quest.setVisibility(View.GONE);
//        quest.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
}
