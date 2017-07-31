package com.learnfun.super8team.learnfun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import android.widget.TextView;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class BeforeHistoryActivity extends FragmentActivity implements OnMapReadyCallback{


    private GoogleMap mMap;

    UserPreferences userPreferences;
    LatLng DEAGU = new LatLng(35.896953, 128.621367);
    NetworkAsync requestNetwork;
    JSONObject sendData,planGPS;
    Animation translateLeftAnim;
    Animation translateRightAnim;
    LinearLayout slidingPage01;
    ScrollView scrollPage;
    private Button slidingPageClose,logBtn;

    String placeNum="";
    boolean isPageOpen = false;
    AppCompatDialog progressDialog;


    //히스토리에 이미지를 뿌려주기 위한 변수들
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MyData> myDataset;

    int planNo=0;
    String[] historyContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "온크리에이트 실행");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_history);

        //Intent 가져오기
        Intent intent = getIntent();
        //MainActivity 에서 msg 이름으로 저장했던 값을 msg 변수 로 저장
        planNo = intent.getExtras().getInt("planNo");
        System.out.println("플랜번호@@@@@@@@@@@@@@@@@@@@@@@@@"+planNo);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userPreferences = UserPreferences.getUserPreferences(this);

        slidingPageClose = (Button)findViewById(R.id.slidingPageClose);
        //writeHistory = (Button)findViewById(R.id.writeHistory);

        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);
        scrollPage = (ScrollView) findViewById(R.id.scrollPage);


        translateLeftAnim = AnimationUtils.loadAnimation(this,R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this,R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        logBtn = (Button)findViewById(R.id.logBtn);
        logBtn.setOnClickListener(logListener);



        slidingPageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPage01.startAnimation(translateRightAnim);
                scrollPage.startAnimation(translateRightAnim);
            }
        });


    }//oncreate function end


    //로그를 보여주기 위한 dialog
    private View.OnClickListener logListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BeforeHistoryActivity.this); // 빌더 얻기

            // 제목 설정
            alertDialogBuilder.setTitle("로그");

            // 다이얼로그 메세지 생성 setMessage에 서버에서 로그 기록을 가지고 와서 뿌려줘야함
            alertDialogBuilder
                    .setMessage(getLog())
                    .setCancelable(false)
                    .setNegativeButton("취소", //Negative 버튼 기능 작성
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel(); // 다이얼로그 취소
                                }
                            });

            // 다이럴로그 객체 얻어오기
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();

        }
    };


    private class SlidingPageAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isPageOpen){
                slidingPage01.setVisibility(View.GONE);
                scrollPage.setVisibility(View.GONE);
                isPageOpen = false;

            }else{

                isPageOpen=true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }



    public void getPlanGPS(){ // 히스토리부분

        sendData = new JSONObject();

        try {

            //recentDate.put("date",getDate());
            sendData.put("planNo",planNo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestNetwork = new NetworkAsync(this, "getBeforePlanHistory",  NetworkAsync.POST, sendData);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            planGPS = new JSONObject(returnString);

            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            JSONObject placeGPS = new JSONObject(planGPS.getString("gps"));
            PolygonOptions rectOptions = new PolygonOptions();
            Log.e("planResult", "result is "+placeGPS);

            for(int i = 0 ; i < placeGPS.length();i++){

                //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                String placeNum = "place" + (i+1);
                JSONObject dataJsonObject = placeGPS.getJSONObject(placeNum);
                //JSONObject placeData = new JSONObject(dataJsonObject);
                Log.d("TAG", dataJsonObject.getString("name"));
                LatLng planGPS = new LatLng(dataJsonObject.getDouble("lat"), dataJsonObject.getDouble("lng"));

                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(planGPS);

                //markerOptions.icon(getMarkerIcon(markerColor)); // change the color of marker
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.placemarker));
                markerOptions.title(dataJsonObject.getString("no"));

                Marker planMarker = mMap.addMarker(markerOptions);
                //planMarker.setOnClickListener(onButton1Clicked); //마커에 클릭이벤트를 달아 오른쪽에 창이 나오도록 해야함
                rectOptions.add(planGPS);

            }

            // Get back the mutable Polygon
            Polygon polygon = mMap.addPolygon(rectOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEAGU));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));




        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                getPlanGPS();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Log.d("markerTitle = ",marker.getTitle());


                            Log.d("김봉춘 체크 = ","김봉춘체크");
                            placeNum = marker.getTitle();
                            if (isPageOpen) {
                                //slidingPage01.startAnimation(translateRightAnim);
                            } else {
                                startProgress();//다이얼로그 실행 함수
                                slidingPage01.setVisibility(View.VISIBLE);
                                slidingPage01.startAnimation(translateLeftAnim);
                                scrollPage.setVisibility(View.VISIBLE);
                                scrollPage.startAnimation(translateLeftAnim);

                                //디비에서 히스토리 정보를 가져와서 슬라이드창에 글을 뿌려준다
                                sendData = new JSONObject();

                                try {

                                    //recentDate.put("date",getDate());
                                    sendData.put("userId",userPreferences.getUserId());
                                    sendData.put("placeNum",placeNum);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                requestNetwork = new NetworkAsync(BeforeHistoryActivity.this, "getHistoryContent",  NetworkAsync.POST, sendData);

                                try {
                                    // 네트워크 통신 후 json 획득
                                    String returnString = requestNetwork.execute().get();
                                    Log.e("planResult", "result is "+returnString);
                                    JSONObject place = new JSONObject(returnString);

                                    //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
                                    JSONObject contentList = new JSONObject(place.getString("place"));
                                    String sumContent = "";
                                    //cardView를 만들기위한 코드
                                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    mRecyclerView.setHasFixedSize(true);

                                    // use a linear layout manager
                                    mLayoutManager = new LinearLayoutManager(BeforeHistoryActivity.this);
                                    mRecyclerView.setLayoutManager(mLayoutManager);

                                    // specify an adapter (see also next example)
                                    myDataset = new ArrayList<>();
                                    mAdapter = new MyAdapter(myDataset);
                                    mRecyclerView.setAdapter(mAdapter);
                                    historyContent = new String[contentList.length()];
                                    String[] imageUrl,content;
                                    imageUrl = new String[contentList.length()];
                                    content = new String[contentList.length()];

                                    for(int i = 0 ; i < contentList.length();i++){

                                        //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                                        String contentNum = "content" + (i+1);
                                        JSONObject dataJsonObject = contentList.getJSONObject(contentNum);
                                        //JSONObject placeData = new JSONObject(dataJsonObject);sumContent

                                        //sumContent += "content : " + dataJsonObject.getString("content") + "\nweather : " +dataJsonObject.getString("weather")+ "\n";
                                        //contentView.setText("content : " + dataJsonObject.getString("content") + "\nweather : " +dataJsonObject.getString("weather"));
                                        //getImageFromURL(dataJsonObject.getString("url")),dataJsonObject.getString("content")

//                                        myDataset.add(new MyData(dataJsonObject.getString("content"),getImageFromURL("http://163.44.166.91/LEARnFUN/storage/app/historyImgs/1-1.png")));
//                                        imageUrl[i] = dataJsonObject.getString("url");
//                                        content[i] = dataJsonObject.getString("content");
                                        myDataset.add(new MyData(dataJsonObject.getString("content"),dataJsonObject.getString("url"),BeforeHistoryActivity.this));
                                    }
//                                    ImageAsync getImage = new ImageAsync(HistoryDetailActivity.this,imageUrl);
//                                    Bitmap[] imageBit = getImage.execute().get();
//                                        myDataset.add(new MyData(dataJsonObject.getString("content"),bitmap));
//                                        final String finalImageUrl = imageUrl;
//                                        Thread mThread = new Thread(){
//                                            @Override
//                                            public void run() {
//                                                try{//baseShoppingURL
//
//                                                    URL url = new URL(finalImageUrl); //URL주소를 이용해서 URL객체를 생성
//                                                    //아래 코드는 웹에서 이미지를 가져온뒤
//                                                    //이미지 뷰에 지정할 Bitmap을 생성하는 과정
//                                                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                                                    conn.setDoInput(true);
//                                                    conn.connect();
//
//                                                    InputStream is = conn.getInputStream();
//                                                    bitmap = BitmapFactory.decodeStream(is);
//
//                                                }catch (IOException ex){
//
//                                                }
//                                            }
//                                        };
//
//                                        mThread.start();
//
//                                        try{
//                                            mThread.join();
//                                    for(int i = 0 ; i < contentList.length();i++){
//                                        myDataset.add(new MyData(content[i],imageBit[i]));
//                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        return false;
                    }
                });
                //  addImageMarker(); // 새로운 이미지 마커를 박음
            }


        });

    }//onMapReady function end



    protected void onStop() {
        //mGoogleApiClient.disconnect();
//        socket.disconnect();
//        socket.on(Socket.EVENT_DISCONNECT, listenDisconnectPerson);
        super.onStop();
    }


    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


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

        progressON(BeforeHistoryActivity.this,"Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }
    public String getLog(){
        String userNo = userPreferences.getUserNo();
        String returnString="";

        sendData = new JSONObject();

        try {

            //recentDate.put("date",getDate());
            sendData.put("userNo",userNo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestNetwork = new NetworkAsync(this, "getLog",  NetworkAsync.POST, sendData);
        try {
            // 네트워크 통신 후 json 획득
            returnString = requestNetwork.execute().get();

            JSONObject result = new JSONObject(returnString);
            returnString = result.getString("log");
        }catch (Exception e){

        }
        return returnString;
    }
}
