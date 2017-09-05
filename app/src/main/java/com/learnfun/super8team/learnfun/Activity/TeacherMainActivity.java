package com.learnfun.super8team.learnfun.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.learnfun.super8team.learnfun.R;
import com.learnfun.super8team.learnfun.Service.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

public class TeacherMainActivity extends AppCompatActivity {
    private ImageButton goToToday , goToPlanTable , goToStudentList , goToContens , goToCheckList, goToHistory;
    private TextView welcome;
    private Socket socket=null;
    UserPreferences userPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        goToToday = (ImageButton)findViewById(R.id.goToToday);
        goToPlanTable = (ImageButton)findViewById(R.id.goToPlanTable);
        goToStudentList = (ImageButton)findViewById(R.id.goToStudentList);
        goToContens = (ImageButton)findViewById(R.id.goToContens);
        goToCheckList = (ImageButton)findViewById(R.id.goToCheckList);
        goToHistory = (ImageButton)findViewById(R.id.goToHistory);

        goToToday.setOnClickListener(mainListener);
        goToPlanTable.setOnClickListener(mainListener);
        goToStudentList.setOnClickListener(mainListener);
        goToContens.setOnClickListener(mainListener);
        goToCheckList.setOnClickListener(mainListener);
        goToHistory.setOnClickListener(mainListener);

        userPreferences = UserPreferences.getUserPreferences(this);

        welcome = (TextView)findViewById(R.id.user_main_name);
        welcome.setText(userPreferences.getUserName()+"선생님 반갑습니다.");

        View view = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.parseColor("#f7f8f9"));
        }


//        //추가한 라인
//        FirebaseMessaging.getInstance().subscribeToTopic("news");

            String token = FirebaseInstanceId.getInstance().getToken();
            System.out.println("token = " + token);
            saveToken(token);
//          FirebaseInstanceIdService fb = new MyFirebaseInstanceIDService();
//            fb.onTokenRefresh();

    }

    private void saveToken(String token) {

        System.out.println("토큰여부검사 : "+userPreferences.getToken());
        if(!userPreferences.getToken()) {

            try {

                socket = IO.socket("http://163.44.166.91:8000");
                //socket.on(Socket.EVENT_CONNECT, listenStartPerson);
                //.on("getclass1", listen_start_person)
            } catch (Exception e) {
                Log.i("ERROR", "ERROR : Socket connection failed");
            }

            socket.connect();

            if (socket.connected()) {
                Log.i("SocketCheck", "Socket connection successful");
            } else {
                Log.i("SocketCheck", "Socket connection failed");
            }

            JSONObject tokenObj = new JSONObject();

            try {

                tokenObj.put("token", token);
                tokenObj.put("name", userPreferences.getUserName());
                tokenObj.put("school", userPreferences.getUserSchool());
                tokenObj.put("grade", userPreferences.getUserGrade());
                tokenObj.put("class", userPreferences.getUserClass());
                tokenObj.put("userType", userPreferences.getUserType());

//            create table fcm(
//                    id int(20) not null auto_increment,
//                    token varchar(200) not null,
//                    name varchar(200) not null,
//                    school varchar(200),
//                    grade varchar(200),
//                    class varchar(200),
//                    userType varchar(200),
//
//                    primary key(id),
//                    unique key(token)
//              );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("saveTheToken", tokenObj);

            userPreferences.setToken(true);
            System.out.println("토큰여부검사@@@@@@@@@@@@@@@@@@@@@@@ : "+userPreferences.getToken());

        }

    }


    private View.OnClickListener mainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.goToToday:

                    Intent intent = new Intent(TeacherMainActivity.this, TodayActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToPlanTable:
                     intent = new Intent(TeacherMainActivity.this, PlanTableActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToStudentList:
                    intent = new Intent(TeacherMainActivity.this, StudentListActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToContens:
                    intent = new Intent(TeacherMainActivity.this, ContentActivity.class);
                    startActivity(intent);

                    break;

                case R.id.goToCheckList:
                    intent = new Intent(TeacherMainActivity.this, CheckListActivity.class);
                    startActivity(intent);

                    break;
                case R.id.goToHistory:
                    intent = new Intent(TeacherMainActivity.this, HistoryListActivity.class);
                    startActivity(intent);

                    break;
            }

        }
    };

}
