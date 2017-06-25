package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bon on 2017-05-19.
 */

public class UserPreferences {
    private static String TAG = "UserPreferences";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private JSONObject user = new JSONObject();
    private static UserPreferences userPreferences = null;

    final static String USERID = "id";
    final static String USERNAME = "name";
    final static String USERTYPE = "type";
    final static String CHILDID = "childID";

    private UserPreferences(Context context) {
        // 단말 어플리케이션 설정을 저장하는 프리퍼런스 키(LEARnFUN) 세팅
        preferences = context.getSharedPreferences("LEARnFUN", MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static UserPreferences getUserPreferences(Context context) {
        // 싱글톤
        if(userPreferences == null) {
            userPreferences = new UserPreferences(context);
        }
        return userPreferences;
    }

    public JSONObject getUser() {
        // user객체를 항상 최신화
        setUser();
        return user;
    }

    public void setUser() {
        try {
            // 저장된 값 불러오기 (프리퍼런스 --> json객체)
            user.put(USERID, preferences.getString(USERID, ""));
            user.put(USERNAME, preferences.getString(USERID, ""));
            user.put(USERTYPE, preferences.getString(USERTYPE, ""));
            user.put(CHILDID, preferences.getString(CHILDID, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUser(JSONObject user) {
        // 프리퍼런스 갱신
        editor.clear();
        try {
            editor.putString(USERID, user.getString(USERID));
            editor.putString(USERNAME, user.getString(USERNAME));
            editor.putString(USERTYPE, user.getString(USERTYPE));
            editor.putString(CHILDID, user.getString(CHILDID));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        String userid = "";
        userid = preferences.getString(USERID, "");
        return userid;
    }

    public String getUserName() {
        String username = "";
        username = preferences.getString(USERNAME, "");
        return username;
    }

    public String getUserType() {
        String userType = "";
        userType = preferences.getString(USERTYPE, "");
        return userType;
    }

    public String getUserChild() {
        String userChild = "";
        userChild = preferences.getString(CHILDID, "");
        return userChild;
    }

    public void removeUser() {
        // 유저로그아웃, 프리퍼런스 삭제
        editor.clear();
        editor.commit();
        // user객체초기화
        user = new JSONObject();
        try {
            user.put(USERID, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
