package com.learnfun.super8team.learnfun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.learnfun.super8team.learnfun.AR.ARCamera;
import com.learnfun.super8team.learnfun.AR.AROverlayView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    final static String TAG = "ContentActivity";

    private SurfaceView surfaceView;
    private TextView tv;
    private LinearLayout cameraContainerLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;

    private SensorManager sensorManager;
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    private Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;

    private ArrayList<JSONObject> jsons = new ArrayList();
    private ArrayList<Content> contents = new ArrayList();
    private JSONArray json;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Log.e(TAG, "oncreated");

        // AR카메라를 위한 초기 설정
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        cameraContainerLayout = (LinearLayout) findViewById(R.id.camera_container_layout);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        arOverlayView = new AROverlayView(this);

        // 0607 22:00 여기 있던 권한 설정은 onResume으로 옮겼습니다 ㅎ.ㅎ 카메라 권한 따고 초기화 하는 거랑 같이 하기 위해서!
        // 0607 23:53 권한 획득에 자꾸 실패해서 진아코드로 대체

        // 여기 있던 json->call()은 onResume의 initContents()로 옮겼습니다! oncreate보다 resume이 늦게 시작하기 때문!

        // GPS 값 받아오기

        tv = (TextView) findViewById(R.id.bottom_text);
        tv.setText("GPS가 잡혀야 좌표가 구해짐");

        // 여기 있던 locationListener는 밑에 있는 리스너 함수들로 옮김!

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 권한 획득
        Log.d(TAG, "onResume");
        requestLocationPermission();
        requestCameraPermission();
        registerSensors();
        initContents();
        initAROverlayView();
    }

    public void onPause() {
        releaseCamera();
        super.onPause();
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
        Log.e(TAG, "requestCameraPermission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "시무룩");
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            Log.e(TAG, "camera_permission_OK");
            initARCameraView();
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            Log.e(TAG, "퍼미셔너어넌ㄴㄴ");
            initLocationService();
        }
    }

    private void initContents() {
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
    }

    private void initLocationService() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        Log.i(TAG, "locationService initial");

        try   {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//        final TextView tv = (TextView) findViewById(R.id.bottom_text);
//
//        tv.setText("GPS가 잡혀야 좌표가 구해짐");

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                Log.i(TAG, "네트워크 가능");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null)   {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) updateLatestLocation(location);
                }
            }

            if (isGPSEnabled)  {
                Log.i(TAG, "GPS 가능");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null)  {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) updateLatestLocation(location);
                }
            }
        } catch (Exception ex)  {
            Log.e(TAG, ex.getMessage());

        }
    }

    private void updateLatestLocation(Location curLocation) {
        Log.e(TAG, "updating..."+String.valueOf(curLocation.getLongitude()));
        if (arOverlayView !=null) {
            Log.i(TAG, "location update");
            arOverlayView.updateCurrentLocation(curLocation);
        }
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
        Log.i(TAG, "initAROverlayView");
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
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
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        //이곳에서 각컨텐츠 조건함수 호출
        for (int i=0;i<contents.size();i++){
            try {
                //각콘텐츠 반경과 현재 좌표를 비교하고 컨텐츠 실행중이 아니면 컨텐츠 표시
//                        Log.i("디세이블 상황 ", String.valueOf(contents.get(i).getContentDisable()));
                if(contents.get(i).checkCondition(lat,lng) && !Content.CONTENT_USED && !contents.get(i).getContentDisable()){
                    contents.get(i).setContentView();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        tv.setText("latitude: " + lat + ", longitude: " + lng);

        if (arOverlayView !=null) {
            Log.i(TAG, "location update");
            arOverlayView.updateCurrentLocation(location);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();



        return super.onTouchEvent(event);
    }
}
