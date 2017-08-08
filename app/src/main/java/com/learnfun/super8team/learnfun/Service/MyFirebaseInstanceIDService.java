package com.learnfun.super8team.learnfun.Service;

/**
 * Created by cho on 2017-08-02.
 */


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private Socket socket=null;
    private static final String TAG = "MyFirebaseIIDService";
    UserPreferences userPreferences;
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.

        userPreferences = UserPreferences.getUserPreferences(this);

            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + token);

            // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
            sendRegistrationToServer(token);

    }

    private void sendRegistrationToServer(String token) {

//        // Add custom implementation, as needed.
//        if(Common.getBooleanPerf(context,Constants.isTokenSentToServer,false) ||
//                Common.getStringPref(context,Constants.userId,"").isEmpty()){
//
//            return;
//        }
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token", token)
//                .build();
//
//        //request
//        Request request = new Request.Builder()
//                .url("http://서버주소/fcm/register.php")
//                .post(body)
//                .build();
//
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try{

            socket = IO.socket("http://163.44.166.91:8000");
            //socket.on(Socket.EVENT_CONNECT, listenStartPerson);
            //.on("getclass1", listen_start_person)
            socket.connect();
        } catch(Exception e){
            Log.i("ERROR", "ERROR : Socket connection failed");
        }



        if(socket.connected()){
            Log.i("SocketCheck", "Socket connection successful");
        }
        else{
            Log.i("SocketCheck", "Socket connection failed");
        }

        JSONObject tokenObj = new JSONObject();

        try {

            tokenObj.put("token",token);
            tokenObj.put("name",userPreferences.getUserName());
            tokenObj.put("school",userPreferences.getUserSchool());
            tokenObj.put("grade",userPreferences.getUserGrade());
            tokenObj.put("class",userPreferences.getUserClass());
            tokenObj.put("userType",userPreferences.getUserType());

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

       // socket.emit("saveTheToken",tokenObj);

    }
}
