package com.learnfun.super8team.learnfun.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.learnfun.super8team.learnfun.Adapter.HistoryWritingAdapter;
import com.learnfun.super8team.learnfun.Bin.HistoryWritingItem;
import com.learnfun.super8team.learnfun.Async.NetworkAsync;
import com.learnfun.super8team.learnfun.R;
import com.learnfun.super8team.learnfun.Bin.Student;
import com.learnfun.super8team.learnfun.Connector.StudentHelper;
import com.learnfun.super8team.learnfun.Service.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class TodayActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private StudentHelper helper;
    private static int MY_LOCATION_REQUEST_CODE = 2000;
    public static final int REQUEST_CODE_WRITE = 1001;
    private LocationManager locationManager;
    private Socket socket=null;
    boolean isPageOpen = false;
    String placeNum="";
    Animation translateLeftAnim;
    Animation translateRightAnim;

    LinearLayout slidingPage01;
    ScrollView scrollPage;

    LatLng DEAGU = new LatLng(35.896687, 128.620512);
    LatLng GYUNGZOO = new LatLng(35.789932, 129.331438);
    Marker myMarker=null;
    JSONObject sendData,planGPS;
    UserPreferences userPreferences;
    NetworkAsync requestNetwork;

    ArrayList<Marker> classOneMarker = new ArrayList();
    ArrayList<Marker> classTwoMarker = new ArrayList();
    ArrayList<Marker> classThreeMarker = new ArrayList();
    ArrayList<String> studentMarkerArray = new ArrayList();
    private Button slidingPageClose,writeHistory,logBtn,noticeBtn;
    Spinner spinner;
    Switch histroySwitch;
    boolean mSwc = true;    // 스위치 상태를 기억할 변수
    boolean emitSwitch = false;
    boolean pauseCheck = false;
    Boolean placeInCheck=true;
    String logContent = "";
    int spinnerChoice=1;
    int   startPoint = 0;
    //히스토리에 이미지를 뿌려주기 위한 변수들
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<HistoryWritingItem> myDataset;
    JSONObject placeList = null;
    LatLng myLatLng=null;
    //dialog
    AppCompatDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "온크리에이트 실행");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        helper = new StudentHelper(this,"NameDB",null,7);
        slidingPageClose = (Button)findViewById(R.id.slidingPageClose);
        writeHistory = (Button)findViewById(R.id.writeHistory);
        logBtn = (Button)findViewById(R.id.logBtn);
        logBtn.setOnClickListener(logListener);
        noticeBtn = (Button)findViewById(R.id.noticeBtn);
        noticeBtn.setOnClickListener(noticeListener);

        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);
        scrollPage = (ScrollView) findViewById(R.id.scrollPage);

        //historyImage =(ImageView)this.findViewById(R.id.pictureImage);



        translateLeftAnim = AnimationUtils.loadAnimation(this,R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this,R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        spinner = (Spinner)findViewById(R.id.spinner);

        histroySwitch = (Switch)findViewById(R.id.historySwitch);
        histroySwitch.setOnCheckedChangeListener(SWITCH);




        userPreferences = UserPreferences.getUserPreferences(this);

        try{

            socket = IO.socket("http://163.44.166.91:8000");
            //socket.on(Socket.EVENT_CONNECT, listenStartPerson);
            //.on("getclass1", listen_start_person)
        } catch(Exception e){
            Log.i("ERROR", "ERROR : Socket connection failed");
        }

        socket.connect();

        if(socket.connected()){
            Log.i("SocketCheck", "Socket connection successful");
        }
        else{
            Log.i("SocketCheck", "Socket connection failed");
        }

        socket.on("studentGPSToTeacher",listenGetMessagePerson);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.class_Name,android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        chkGpsService(); //GPS sensor on / off check


        slidingPageClose.setOnClickListener(new View.OnClickListener() {//발자취창 눌렀을때 슬라이딩이벤트
            @Override
            public void onClick(View v) {
                slidingPage01.startAnimation(translateRightAnim);
                scrollPage.startAnimation(translateRightAnim);
            }
        });

        writeHistory.setOnClickListener(new View.OnClickListener() { //글쓰기 버튼 클릭시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteHistoryActivity.class);
                intent.putExtra("placeNum", placeNum);
                startActivityForResult(intent,REQUEST_CODE_WRITE);
            }
        });
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   // Toast.makeText(parent.getContext(),"선택한것은"+parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
                    mMap.clear();

                    if(parent.getItemAtPosition(position).equals("1반")){
                        Log.e("planResult", "1반 받았나!@");
                        classOneMarker.clear();

                        spinnerChoice = 1;
                        String name[] = {"김봉춘" , "이기춘" , "함초롬" , "강물맑음" ,"정수철"};

                        for(int i =0 ; i < name.length; i++){
                            createStudent(name[i],"1");
                        }


                    }else if(parent.getItemAtPosition(position).equals("2반")){
                        classTwoMarker.clear();

                        spinnerChoice=2;

                        spinnerChoice = 1;
                        String name[] = {"김봄", "이여름" , "박가을" , "정겨울" , "김소피아"};
                        for(int i =0 ; i < name.length; i++){
                            createStudent(name[i],"2");
                        }

                    }else{
                        classThreeMarker.clear();

                        spinnerChoice=3;
                        spinnerChoice = 1;
                        String name[] = { "이산" , "김바다" , "양초원" , "심들판" , "강사막"};

                        for(int i =0 ; i < name.length; i++){
                            createStudent(name[i],"3");
                        }


                    }
                    addMyMarker();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        placeList = getPlaceList();
    }//oncreate function end

    //로그를 보여주기 위한 dialog
    private View.OnClickListener logListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TodayActivity.this); // 빌더 얻기

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
//    private View.OnClickListener picListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TodayActivity.this); // 빌더 얻기
//
//            // 제목 설정
//            alertDialogBuilder.setTitle("사진");
//
//            // 다이얼로그 메세지 생성 setMessage에 서버에서 로그 기록을 가지고 와서 뿌려줘야함
//            alertDialogBuilder
//                    .setMessage(Glide.with(TodayActivity.this).asBitmap().load().into(ImageView))
//                    .setCancelable(false)
//                    .setNegativeButton("취소", //Negative 버튼 기능 작성
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel(); // 다이얼로그 취소
//                                }
//                            });
//
//            // 다이럴로그 객체 얻어오기
//            AlertDialog alertDialog = alertDialogBuilder.create();
//
//            // 다이얼로그 보여주기
//            alertDialog.show();
//
//        }
//    };

    //공지를 보내기위한 dialog
    private View.OnClickListener noticeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText etEdit = new EditText(TodayActivity.this);
            AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(TodayActivity.this); // 빌더 얻기


            // 제목 설정
            DialogBuilder.setTitle(getString(R.string.notice));
            DialogBuilder.setView(etEdit);

            // 다이얼로그 메세지 생성 setMessage에 서버에서 로그 기록을 가지고 와서 뿌려줘야함
            DialogBuilder
                    .setPositiveButton("전송", //Negative 버튼 기능 작성
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String inputValue = etEdit.getText().toString();
                                    //여기서 senData json오브젝트로 줄것
                                    JSONObject tokenObj = new JSONObject();

                                    try {


                                        tokenObj.put("name", userPreferences.getUserName());
                                        tokenObj.put("school", userPreferences.getUserSchool());
                                        tokenObj.put("grade", userPreferences.getUserGrade());
                                        tokenObj.put("class", userPreferences.getUserClass());
                                        tokenObj.put("userType", userPreferences.getUserType());
                                        tokenObj.put("msg", inputValue);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    socket.emit("sendMsg", tokenObj);

                                    dialog.cancel(); // 메세지전송 푸시처리
                                }
                            })
                    .setCancelable(false)
                    .setNegativeButton("취소", //Negative 버튼 기능 작성
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel(); // 다이얼로그 취소
                                }
                            });

            // 다이럴로그 객체 얻어오기
            AlertDialog alertDialog = DialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();

        }
    };

    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

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
    //스위치 눌렀을때 작동 on/off
    public Switch.OnCheckedChangeListener SWITCH = new Switch.OnCheckedChangeListener()

    {

        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

            mSwc = isChecked;
            if(isChecked == true){ //히스토리화면
                histroySwitch.setText("발자취");
                mMap.clear();

                spinner.setVisibility(View.INVISIBLE); // 화면에 안보임
                classOneMarker.clear();
                classTwoMarker.clear();
                classThreeMarker.clear();
                if(myMarker!=null) addMyMarker();
                getPlanGPS();


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { //발자취 눌렀을때 동작함수
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if(!marker.getTitle().equals("현재위치")) {
                            placeNum = marker.getTitle();
                            if (isPageOpen) {
                                //slidingPage01.startAnimation(translateRightAnim);
                            } else {
                               // startProgress();//다이얼로그 실행 함수
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

                                requestNetwork = new NetworkAsync(TodayActivity.this, "getHistoryContent",  NetworkAsync.POST, sendData);

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
                                    mLayoutManager = new LinearLayoutManager(TodayActivity.this);
                                    mRecyclerView.setLayoutManager(mLayoutManager);

                                    // specify an adapter (see also next example)
                                    myDataset = new ArrayList<>();
                                    mAdapter = new HistoryWritingAdapter(myDataset);
                                    mRecyclerView.setAdapter(mAdapter);


                                    for(int i = 0 ; i < contentList.length();i++){

                                        //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                                        String contentNum = "content" + (i+1);
                                        JSONObject dataJsonObject = contentList.getJSONObject(contentNum);
                                        //JSONObject placeData = new JSONObject(dataJsonObject);sumContent

                                        //sumContent += "content : " + dataJsonObject.getString("content") + "\nweather : " +dataJsonObject.getString("weather")+ "\n";
                                        //contentView.setText("content : " + dataJsonObject.getString("content") + "\nweather : " +dataJsonObject.getString("weather"));
                                        //getImageFromURL(dataJsonObject.getString("url")),dataJsonObject.getString("content")

//                                        myDataset.add(new HistoryWritingItem(dataJsonObject.getString("content"),getImageFromURL("http://163.44.166.91/LEARnFUN/storage/app/historyImgs/1-1.png")));
//                                        imageUrl[i] = dataJsonObject.getString("url");
//                                        content[i] = dataJsonObject.getString("content");
                                        myDataset.add(new HistoryWritingItem(dataJsonObject.getString("content"),dataJsonObject.getString("url"),TodayActivity.this));
                                    }
//                                        ImageAsync getImage = new ImageAsync(TodayActivity.this,imageUrl);
//                                        Bitmap[] imageBit = getImage.execute().get();
//                                        myDataset.add(new HistoryWritingItem(dataJsonObject.getString("content"),bitmap));
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
//
//                                        myDataset.add(new HistoryWritingItem(content[i],imageBit[i]));
//                                    }

//
//
//                                        }catch (InterruptedException e){
//
//                                        }



                                    //historyImage.setImageBitmap(bitmap);
                                    //contentView.setText(sumContent);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        return false;
                    }
                });


            } else{// 학생화면
                histroySwitch.setText("학생위치");
                mMap.clear();
                spinner.setVisibility(View.VISIBLE); // 화면에보임
                if(myMarker!=null) addMyMarker();
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        return false;
                    }
                });
            }


        }

    };



    @Override
    protected void onDestroy() {
//        socket.emit("disconnection");
        listenGetMessagePerson=null;
        pauseCheck=true;
        socket.on(Socket.EVENT_DISCONNECT, listenDisconnectPerson);
        super.onDestroy();
    }

    private Emitter.Listener listenStartPerson = new Emitter.Listener() {

        public void call(Object... args) {

            //socket.emit("Connection", "connected on server");
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
            if(pauseCheck) return;
            Log.d("loglog", "class 받았다@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            final JSONObject obj = (JSONObject)args[0];

            //서버에서 보낸 JSON객체를 사용할 수 있습니다.

            runOnUiThread(new Runnable() { //발생가능 문제점 : 1반이 선택된 상태에서 1반에 대한 객체가 들어올경우 마커가 중첩되어 찍힐수있음
                @Override
                public void run() {
                    try {
                        //위에서 오브젝트를 받은것을 다시 제이슨배열로 해체

                        //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                            //제이슨 객체안의 데이터를 빼온다
                            Log.d("studentname", obj.getString("class"));
                        int studentMarkerSize=0;
                        if(studentMarkerArray.isEmpty()){
                            studentMarkerSize = 0;
                        }else{
                            studentMarkerSize=studentMarkerArray.size();
                        }
                        boolean isStudentMarker=false;

                        for(int i =0; i < studentMarkerSize; i++){
                            Log.d("포문", "포문안에들어옴");
                            if(studentMarkerArray.get(i).equals(obj.getString("name"))){
                                isStudentMarker=true;

                            }else{
                                isStudentMarker=false;
                            }
                        }

                            if(isStudentMarker){
                                Log.d("이미있다", "이미마커가 있어");
                                LatLng latLng = new LatLng(obj.getDouble("lat"), obj.getDouble("lng"));


                                if(obj.getString("class").equals("1")){
                                    System.out.println("들어옴");
                                    for(int j=0; j<classOneMarker.size();j++){
                                        if(classOneMarker.get(j).getTitle().equals(obj.getString("name"))){
                                            System.out.println("이름이 같으니 위치변경!!!!!!!");
                                            classOneMarker.get(j).setPosition(latLng);
                                        }
                                    }
                                }else if(obj.getString("class").equals("2")){
                                    for(int j=0; j<classTwoMarker.size();j++){
                                        if(classTwoMarker.get(j).getTitle().equals(obj.getString("name"))){
                                            classTwoMarker.get(j).setPosition(latLng);
                                        }
                                    }
                                }else{
                                    for(int j=0; j<classThreeMarker.size();j++){
                                        if(classThreeMarker.get(j).getTitle().equals(obj.getString("name"))){
                                            classThreeMarker.get(j).setPosition(latLng);
                                        }
                                    }

                                }


                            }else{
                                Log.d("마커생", "마커를생성하려시도중");
                                if(obj.getString("class").equals("1") || spinnerChoice==1){
                                    addStudentMarker(obj, "1반");
                                }else if(obj.getString("class").equals("2") || spinnerChoice==2){
                                    addStudentMarker(obj,"2반");
                                }else{
                                    addStudentMarker(obj,"3반");

                                }
                            }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //이곳에 ui 관련 작업을 할 수 있습니다.


                }
            });
        }
    };
    @Override
    protected void onPause() {
        pauseCheck=true;
        locationListener=null;
        listenGetMessagePerson=null;
        super.onPause();
    }

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


    public void getPlanGPS(){ // 히스토리부분




        try {
            // 네트워크 통신 후 json 획득

            JSONObject placeGPS = getPlaceList();
            PolygonOptions rectOptions = new PolygonOptions();
            Log.e("planResult", "result is "+ placeGPS);

            for(int i = 0 ; i < placeGPS.length();i++){

                //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                String placeNum = "place" + (i+1);
                JSONObject dataJsonObject = placeGPS.getJSONObject(placeNum);
                //JSONObject placeData = new JSONObject(dataJsonObject);
                Log.d("TAG", dataJsonObject.getString("name"));
                LatLng planGPS = new LatLng(dataJsonObject.getDouble("lat"), dataJsonObject.getDouble("lng"));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(planGPS);
                //markerOptions.icon(R.drawable.placeMarker); // change the color of marker
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(GYUNGZOO));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                Log.d("TAG", "onMapLoaded 체크퍼미션 실행했다");

                checkLocationPermission();
                socket.emit("class1", "I want class1's GPS");
                emitSwitch = true;
                //  addImageMarker(); // 새로운 이미지 마커를 박음
            }


        });

    }//onMapReady function end



//
//    protected void onStart() {
//        if(mGoogleApiClient==null){
//            buildGoogleApiClient();
//
//        }
//
//        super.onStart();
//    }

    protected void onStop() {
        //mGoogleApiClient.disconnect();
        pauseCheck=true;
        listenGetMessagePerson=null;
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
//        marker_root_view = LayoutInflater.from(this).inflate(R.layout.activity_today, null);
//        //tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
//        }


    private android.location.LocationListener locationListener = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //setCustomMarkerView();
            //Log.d("TAG", "onLocationChanged에 들어왔다");

            //myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            myLatLng = new LatLng(35.789932, 129.331438); //시연용 경주 불국사

            if(myMarker==null){
                addMyMarker();

            }else{
                //myMarkers.get(0).position(myLatLng);

                myMarker.setPosition(myLatLng);

            }
            Boolean logChecked = true;
            try{ //현재 교사의 gps를 가지고 장소의 gps 와 비교하여 로그를 남김
//                for(int i = 0 ; i < placeList.length();i++){
//
//                    //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
//                    String placeNum = "place" + (i+1);
//                    JSONObject dataJsonObject = placeList.getJSONObject(placeNum);
//                    //JSONObject placeData = new JSONObject(dataJsonObject);
//                    Log.d("TAG", dataJsonObject.getString("name"));
//                    Double latDown = dataJsonObject.getDouble("lat")-0.0006;
//                    Double latUp = dataJsonObject.getDouble("lat")+0.0006;
//                    Double lngLeft = dataJsonObject.getDouble("lng")-0.0006;
//                    Double lngRight = dataJsonObject.getDouble("lng")+0.0006;
//
//
//                        if(  latDown < location.getLatitude() && latUp >location.getLatitude()    &&    lngLeft <location.getLongitude() && lngRight > location.getLongitude()   ){
//                            if(!placeInCheck) {
//                                logContent = dataJsonObject.getString("name") + "에 도착했습니다.";
//                                System.out.println(placeInCheck+"    "+logContent);
//                                setLog(logContent);
//                                placeInCheck = true;
//                            }
//
//                        }else{
//                            if(placeInCheck) {
//                                logContent = dataJsonObject.getString("name") + "에서 출발했습니다.";
//                                System.out.println(placeInCheck+"    "+logContent);
//                                setLog(logContent);
//                                placeInCheck = false;
//                            }
//                        }
//
//                    }

                String rLog  = "" ;
                Double rLatDown =0.0  ;
                Double rLatUp  =0.0 ;
                Double rLngLeft  =0.0 ;
                Double rLngRight=0.0;

                for(int i = startPoint ; i < placeList.length();i++){
                    String placeNum = "place" + (i+1);
                    JSONObject dataJsonObject = placeList.getJSONObject(placeNum);
                    //JSONObject placeData = new JSONObject(dataJsonObject);
                    //Log.d("TAG", dataJsonObject.getString("name"));
                    Double latDown = dataJsonObject.getDouble("lat")-0.002;
                    Double latUp = dataJsonObject.getDouble("lat")+0.002;
                    Double lngLeft = dataJsonObject.getDouble("lng")-0.002;
                    Double lngRight = dataJsonObject.getDouble("lng")+0.002;
                    if(i==startPoint){
                        rLog = dataJsonObject.getString("name");
                         rLatDown   = latDown;
                         rLatUp   = latUp;
                         rLngLeft   = lngLeft;
                         rLngRight= lngRight;
                    }
                    if(!placeInCheck){
                        if( i> 0 && latDown < location.getLatitude() && latUp >location.getLatitude()    &&    lngLeft <location.getLongitude() && lngRight > location.getLongitude() ){
                            logContent = dataJsonObject.getString("name") + "에 도착했습니다.";
                            System.out.println(placeInCheck+"    "+logContent);
                            setLog(logContent);
                            placeInCheck = true;
                            rLog = dataJsonObject.getString("name");
                             rLatDown   = latDown;
                             rLatUp   = latUp;
                             rLngLeft   = lngLeft;
                             rLngRight= lngRight;
                        }
                    }else{
                        if(!(rLatDown    < location.getLatitude() && rLatUp    >location.getLatitude()    &&    rLngLeft   <location.getLongitude() && rLngRight> location.getLongitude())){
                            logContent = rLog  + "에서 출발했습니다.";
                            System.out.println(placeInCheck+"    "+logContent);
                            setLog(logContent);
                            placeInCheck = false;
                            startPoint++;
                        }
                    }
                }



            }catch (Exception e){

            }

            //Toast.makeText(TodayActivity.this, (int) location.getLatitude()+" 좌표변경",Toast.LENGTH_SHORT).show();


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

    // View를 Bitmap으로 변환
//    private Bitmap createDrawableFromView(Context context, View view) {
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//
//        return bitmap;
//    }

    private void addMyMarker(){
        Log.d("TAG", "마커생성!!!!!!!!!!!!!!!!!");
        //현재 위치에 마커 생성

        if(myLatLng!=null) {
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarkerd)) // change the color of marker(red)
                    .title("현재위치"));

            //지도 상에서 보여주는 영역 이동
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            //mGoogleApiClient.connect();
        }
    }
    public void createStudent(String name,String classNo){
        JSONObject gps = new JSONObject();

        Double lat = random(35.788974,35.790645,6);
        Double lng = random(129.330215,129.332768,6);
        System.out.println("lat= " + lat+"lng = " + lng);
        try{
            gps.put("id", "dicta");
            gps.put("name", name);
            gps.put("schoolName","영진고등학교");
            gps.put("grade","1");
            gps.put("class",classNo);
            gps.put("lat",lat);
            gps.put("lng",lng);

            addStudentMarker(gps,"2");
        }catch (Exception e){

        }


    }
    public static int random(int min, int max)
    {
        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * 랜던 float 구하기
     *
     * @param min 최소값(이상)
     * @param max 최대값(이하)
     * @param count 소수점 이하 자릿수
     * @return
     */
    public static Double random(Double min, Double max, int count)
    {
        Double value = new Random().nextDouble() * (max - min) + min;
        return Double.valueOf(String.format("%." + count + "f", value));
    }
    private void addStudentMarker(JSONObject studentObj,String className) throws JSONException {
        Log.d("TAG", "학생마커 생성");
        ArrayList<Student> student = helper.selectAll();


//        myMarker = mMap.addMarker(new MarkerOptions()
//                .position(myLatLng)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarkerd)) // change the color of marker(red)
//                .title("현재위치"));

            LatLng studentGPS = new LatLng(studentObj.getDouble("lat"), studentObj.getDouble("lng"));



            String color="blue";
            for(int j =0; j < student.size();j++){
                if(studentObj.getString("name").equals(student.get(j).name)){
                    color=student.get(j).color; // change the color of marker

                }
            }
            //markerOptions.icon(getMarkerIcon("red")); // change the color of marker


            Marker studentMarker = mMap.addMarker( new MarkerOptions()
                    .position(studentGPS)
                    .title(studentObj.getString("name"))
                    .icon(getMarkerIcon(color))
            );

            studentMarkerArray.add(studentObj.getString("name"));
            if(className.equals("1반")){

                classOneMarker.add(studentMarker);
            }else if(className.equals("2반")){

                classTwoMarker.add(studentMarker);
            }else{

                classThreeMarker.add(studentMarker);
            }



    }

    // method definition //change the color of marker
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];

        //#ebf224 노랑   #0732f2 파랑  #ef1c09 빨강
        switch (color){
            case "red" :
                color = "#ef1c09";
                break;
            case "yellow" :
                color = "#ebf224";
                break;
            case "blue" :
                color = "#0732f2";
                break;
        }
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
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
////        mSydney = mMap.addMarker(new MarkerOptions()
////                .position(SYDNEY)
////                .title("Sydney");
////        mSydney.setTag(0);
//
//    }








    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this,"마커가 선택 되었습니다.",Toast.LENGTH_SHORT).show();
        return false;
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //색상을 클릭하고 다시 넘어올경우 색상을 저장해야함
        if(requestCode == REQUEST_CODE_WRITE){
            if(resultCode == RESULT_OK){
                String name = intent.getExtras().getString("name");
//                Toast toast = Toast.makeText(getBaseContext(),
//                        "응답으로 전달된 name: )" + name, Toast.LENGTH_LONG);
//                toast.show();

            }
        }
        slidingPage01.setVisibility(View.GONE);
        scrollPage.setVisibility(View.GONE);
        isPageOpen = false;
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

        progressON(TodayActivity.this,"Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }

    public JSONObject getPlaceList(){
        String userid = userPreferences.getUserId();

        sendData = new JSONObject();

        try {

            //recentDate.put("date",getDate());
            sendData.put("userID",userid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject placeGPS=null;
        requestNetwork = new NetworkAsync(this, "getPlan",  NetworkAsync.POST, sendData);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is " + returnString);
            planGPS = new JSONObject(returnString);

            //JSONArray planGPSArray = new JSONArray(planGPS.getString("gps"));
            placeGPS = new JSONObject(planGPS.getString("gps"));
            PolygonOptions rectOptions = new PolygonOptions();
            Log.e("planResult", "result is " + placeGPS);

        }catch (Exception e){

        }
        return placeGPS;
    }

    public void setLog(String log){
        String userNo = userPreferences.getUserNo();

        sendData = new JSONObject();

        try {

            //recentDate.put("date",getDate());
            sendData.put("userNo",userNo);
            sendData.put("log",log);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestNetwork = new NetworkAsync(this, "setLog",  NetworkAsync.POST, sendData);
        requestNetwork.execute();
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
