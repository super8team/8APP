package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    final static String TAG = "LoginActivity";
    static String LOGINURI = "login";

    // 서버와의 통신을 위한 객체
    NetworkAsync requestNetwork;
    Context context;
    JSONObject userInputInfo, user;
    JSONObject resultJson;

    Button loginBtn;
    Button teacherBtn,parentsBtn,studentBtn;
    EditText idEdit, pwEdit;
    String inputID, inputPASS, returnString;

    // 로그인한 유저 정보를 담을 객체
    UserPreferences userPreferences;


    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "click the login button");
            // 값 유효성 검사, 있는지 없는지
            if(checkValidInput()) {
                // 네트워크 통신 모듈 생성
                requestNetwork = new NetworkAsync(context, LOGINURI,  NetworkAsync.POST, userInputInfo);

                try {
                    // 네트워크 통신 후 json 획득
                    returnString = requestNetwork.execute().get();
                    Log.e(TAG, "result is "+returnString);
                    user = new JSONObject(returnString);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    // 로그인이 성공했으면
                    if (user.get("loginSuccess").equals(true)) {
                        Log.i(TAG, "login success");
                        login();
                    } else { // 실패했으면 user객체 초기화
                        Log.i(TAG, "login Fail");
                        Toast.makeText(LoginActivity.this, "로그인에 실패했습니다! 아이디와 비밀번호를 확인해주세요!", Toast.LENGTH_LONG).show();
                        user = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    View.OnClickListener quickButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "quick login button");
            // 값 유효성 검사, 있는지 없는지
            switch (v.getId()) {

                case R.id.quickTeacher:

                    idEdit.setText("Illum");
                    pwEdit.setText("123456");

                    break;

                case R.id.quickParents:
                    idEdit.setText("desmond21");
                    pwEdit.setText("123456");

                    break;

                case R.id.quickStudent:
                    idEdit.setText("dicta");
                    pwEdit.setText("123456");

                    break;

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        userPreferences = UserPreferences.getUserPreferences(context);
        user = userPreferences.getUser();

        // 로그인 상태를 검사, 자동로그인하며 액티비티 이동
        //if (!userPreferences.getUserId().equals("")) { login(); }

        // 로그인 하지 않은 경우, 로그인 UI를 보여줌
        idEdit = (EditText)findViewById(R.id.idEdit);
        pwEdit = (EditText)findViewById(R.id.pwEdit);
        loginBtn = (Button)findViewById(R.id.loginBtn);

        //로그인을 빠르게 하기위한 버튼
        teacherBtn = (Button)findViewById(R.id.quickTeacher);
        parentsBtn = (Button)findViewById(R.id.quickParents);
        studentBtn = (Button)findViewById(R.id.quickStudent);

        teacherBtn.setOnClickListener(quickButtonListener);
        parentsBtn.setOnClickListener(quickButtonListener);
        studentBtn.setOnClickListener(quickButtonListener);

        // 로그인 버튼을 누른 이벤트처리
        loginBtn.setOnClickListener(loginListener);
    }

    private Boolean checkValidInput() {
        inputID = idEdit.getText().toString();
        inputPASS = pwEdit.getText().toString();

        // 아이디, 패스워드가 입력되지 않은 경우
        if(inputID.equals("")) {
            Toast.makeText(LoginActivity.this, "아이디를 입력해주세요!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(inputPASS.equals("")) {
            Toast.makeText(LoginActivity.this, "패스워드를 입력해주세요!", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*
        입력하지 않은 경우 외에 다른 여러 예외처리(몇 자리 이하, 무슨 특수문자 등등)
         */

        try {
            // 모든 예외사항에 해당되지 않는! 유효한 값을 json 객체로 만듦
            userInputInfo = new JSONObject();
            userInputInfo.put("inputID", inputID);
            userInputInfo.put("inputPASS", inputPASS);
            Log.i(TAG, "id = "+inputID+" pw = "+inputPASS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void login() {
        // 유저 설정, 무조건 자동로그인
        userPreferences.setUser(user);
        user = userPreferences.getUser();
        //Toast.makeText(LoginActivity.this, userPreferences.getUserId()+"님, 환영합니다!", Toast.LENGTH_SHORT).show();
        Intent intent = null;
        switch(userPreferences.getUserType()) {
            case "parents":
                    intent = new Intent(LoginActivity.this, ParentsMainActivity.class);
                //Toast.makeText(LoginActivity.this, user+"("+userPreferences.getUserType()+") 님, 환영합니다!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case "student":
                    intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                //Toast.makeText(LoginActivity.this, user+"("+userPreferences.getUserType()+") 님, 환영합니다!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case "teacher":
                    intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                //Toast.makeText(LoginActivity.this, user+"("+userPreferences.getUserType()+") 님, 환영합니다!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            default:

                /*
                 다른 유저 예외처리
                 */
        }
////            /* 로그인 테스트용 코드
//        intent = new Intent(LoginActivity.this, MainActivity.class);
////            * */
//        startActivity(intent);
//        finish();
    }
}
