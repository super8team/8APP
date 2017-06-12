package com.learnfun.super8team.learnfun;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class HistoryDetailActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private static int MY_LOCATION_REQUEST_CODE = 2000;
    private LocationManager locationManager;
    MarkerOptions myMarker=null;
    private Socket socket=null;
    UserPreferences userPreferences;
    LatLng SEOUL = new LatLng(37.56, 126.97);
    NetworkAsync requestNetwork;
    JSONObject recentDate,planGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "온크리에이트 실행");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        chkGpsService();

        userPreferences = UserPreferences.getUserPreferences(this);
        switch(userPreferences.getUserType()) {
            case "parents":
                try{
                    socket = IO.socket("http://172.19.1.166:8000");


                    socket.on(Socket.EVENT_CONNECT, listenStartPerson);
                    //.on("getclass1", listen_start_person)
                    socket.on("getKidGPS", listenGetMessagePerson);

                    socket.connect();
                    socket.emit("kidGPS",userPreferences.getUserId());

                }
                catch(Exception e) {
                }

                    break;
            case "student":

                break;

            default:

                /*
                 다른 유저 예외처리
                 */
        }

        getPlanGPS();



    }//oncreate function end

    public static String getDate(){
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date date = new Date();
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public void getPlanGPS(){ // 히스토리부분
        recentDate = new JSONObject();
        try {
            recentDate.put("date",getDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestNetwork = new NetworkAsync(this, "getPlan",  NetworkAsync.POST, recentDate);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            planGPS = new JSONObject(returnString);
            JSONArray planGPSArray = new JSONArray(planGPS.getString("plan"));
            PolygonOptions rectOptions = new PolygonOptions();

            for(int i = 0 ; i < planGPSArray.length();i++){

                //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                JSONObject dataJsonObject = planGPSArray.getJSONObject(i);

                //Log.d("TAG", String.valueOf(dataJsonObject.getDouble("lat")));
                LatLng planGPS = new LatLng(dataJsonObject.getDouble("lat"), dataJsonObject.getDouble("lng"));

                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(planGPS);

                //markerOptions.icon(getMarkerIcon(markerColor)); // change the color of marker

                markerOptions.title(dataJsonObject.getString("name"));

                Marker planMarker = mMap.addMarker(markerOptions);

                rectOptions.add(planGPS);

            }

            // Get back the mutable Polygon
            Polygon polygon = mMap.addPolygon(rectOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "aaaa");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        socket.on(Socket.EVENT_DISCONNECT, listenDisconnectPerson);
        super.onDestroy();
    }

    private Emitter.Listener listenStartPerson = new Emitter.Listener() {

        public void call(Object... args) {

            socket.emit("Connection", "connected on server");
            //서버에서 보낸 JSON객체를 사용할 수 있습니다.

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //이곳에 ui 관련 작업을 할 수 있습니다.


                }
            });
        }
    };
    private Emitter.Listener listenGetMessagePerson = new Emitter.Listener() {

        public void call(Object... args) {
            Log.d("loglog", "get kidGPS");
            final JSONObject obj = (JSONObject)args[0];


            //서버에서 보낸 JSON객체를 사용할 수 있습니다.

            runOnUiThread(new Runnable() { //발생가능 문제점 : 1반이 선택된 상태에서 1반에 대한 객체가 들어올경우 마커가 중첩되어 찍힐수있음
                @Override
                public void run() {
                    try {
                        //위에서 오브젝트를 받은것을 다시 제이슨배열로 해체
                        JSONArray jsonArray = new JSONArray(obj.getString("class"));
                        //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //이곳에 ui 관련 작업을 할 수 있습니다.


                }
            });
        }
    };
    private Emitter.Listener listenDisconnectPerson = new Emitter.Listener() {

        public void call(Object... args) {
            Log.d("loglog", "연결 끊는다");
            socket.disconnect();


            //서버에서 보낸 JSON객체를 사용할 수 있습니다.

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //이곳에 ui 관련 작업을 할 수 있습니다.


                }
            });
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                Log.d("TAG", "onMapLoaded 체크퍼미션 실행했다");

                checkLocationPermission();
                //  addImageMarker(); // 새로운 이미지 마커를 박음
            }


        });

    }//onMapReady function end



    protected void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }




    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //퍼미션 요청을 위해 UI를 보여줘야 하는지 검사

            Log.d("TAG", "checkLocationPermission" + "이미 퍼미션 획득한 경우");
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);


        } else {
            Log.d("TAG", "checkLocationPermission" + "퍼미션 없는경우 권한신청");
            // Show rationale and request permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);



        }
        return true;
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult실행");
//        Log.d("permissions", permissions[0]);
//        Log.d("permissions.length", Integer.toString(permissions.length));
//        Log.d("grantResults", Integer.toString(grantResults[0]));
//        Log.d("ACCESS_FINE_LOCATION", Manifest.permission.ACCESS_FINE_LOCATION);
//        Log.d("PERMISSION_GRANTED", Integer.toString(PackageManager.PERMISSION_GRANTED));


        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            Log.d("TAG", "requestCode if문 안에 들어왔다");
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)  &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "퍼미션 획득시 마커를 추가 해준다");


                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                    locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);

                    //mMap.setMyLocationEnabled(true);
                    Log.d("TAG", "connect 실행했다");

                }
            } else {
                // Permission was denied. Display an error message.
                Log.d("TAG", "퍼미션취소");

            }
        }
    }//onRequestPermissionsResult end

//    private void setCustomMarkerView() {
//        marker_root_view = LayoutInflater.from(this).inflate(R.layout.activity_maps, null);
//        //tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
//        }


    private android.location.LocationListener locationListener = new android.location.LocationListener() {


        @Override
        public void onLocationChanged(Location location) {
            //setCustomMarkerView();
            Log.d("TAG", "onLocationChanged에 들어왔다");

            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            JSONObject gps = new JSONObject();
            try {
                gps.put("name",userPreferences.getUserId());
                gps.put("name",userPreferences.getUserName());
                gps.put("lat",location.getLatitude());
                gps.put("lng",location.getLongitude());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("personGPS",gps);

            if(myMarker==null) addMyMarker(myLatLng);
            else myMarker.position(myLatLng);

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


    };


    private void addMyMarker(LatLng latLng){
        Log.d("TAG", "마커생성!!!!!!!!!!!!!!!!!");
        //현재 위치에 마커 생성

        myMarker = new MarkerOptions();
        myMarker.position(latLng);
        myMarker.title("현재위치");
        mMap.addMarker(myMarker);

        //지도 상에서 보여주는 영역 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //mGoogleApiClient.connect();
    }


    //    private void addImageMarker(){
//
////        View marker = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.onbin_marker, null);
////        //현재 위치에 마커 생성
//        ArrayList<LatLng> a = new ArrayList();
//        LatLng latLng1 = new LatLng(35.897532, 128.622696);
//        LatLng latLng2 = new LatLng(35.896664, 128.619985);
//        LatLng latLng3 = new LatLng(35.891765, 128.614243);
//        LatLng latLng4 = new LatLng(35.897884, 128.608298);
//        LatLng latLng5 = new LatLng(35.895238, 128.622741);
//        LatLng latLng6 = new LatLng(35.893233, 128.624808);
//
//        a.add(latLng1); a.add(latLng2); a.add(latLng3);a.add(latLng4);a.add(latLng5);a.add(latLng6);
//
//
////
////        MarkerOptions markerOptions = new MarkerOptions();
////        markerOptions.position(latLng);
////        markerOptions.title("현재위치");
////        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
////        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
////        markerOptions.flat(true);
////        mMap.addMarker(markerOptions);
//        for(int i = 0 ; i < a.size();i++){
//            GroundOverlayOptions newarkMap = new GroundOverlayOptions()
//                    .image(BitmapDescriptorFactory.fromResource(R.drawable.onebin))
//                    .position(a.get(i), 86f, 65f);
//            mMap.addGroundOverlay(newarkMap);
//        }
//        mSydney = mMap.addMarker(new MarkerOptions()
//                .position(SYDNEY)
//                .title("Sydney");
//        mSydney.setTag(0);
//
//    }
    @Override
    public void onLocationChanged(Location location) {

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
