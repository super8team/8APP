package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KIM on 2017-06-09.
 * sqlite 사용하기 위한 매니저 클래스
 */

public class DBManager extends SQLiteOpenHelper{

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //DB가 존재하지않을경우 실행하는 메소드
        //명세를 저장할 수 있는 테이블을 만든다.
        Log.i("데이터베이스","를 만드는가");
        db.execSQL("CREATE TABLE JSON_CODE( json TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db가 존재하지만 버전이 다를경우 호출되는 메소드
    }

    public String init(String json){
        SQLiteDatabase db = getWritableDatabase();
        String temp = "";

        Cursor cr = db.rawQuery("select * from JSON_CODE",null);
        if(cr.getCount() >= 1){
            //데이터베이스에 내용이 있으면 거짓 -> 데이터베이스의 내용을 반환함
            while (cr.moveToNext()){
                temp = cr.getString(0);
                Log.i("기존값 있음",temp);
            }
            db.execSQL("delete from JSON_CODE");
        }else{
            //데이터베이스에 내용이 없으면 참 -> 명세를 디비에 저장하고 반환
            db.execSQL("insert into JSON_CODE values('"+json+"')");
            temp = json;
        }
//        return temp;
        return json;
    }

    public void update(String ContentName,boolean visionable,boolean clickable,boolean disable){
        SQLiteDatabase db = getWritableDatabase();
        String temp = "";

        Cursor cr = db.rawQuery("select * from JSON_CODE",null);

        while (cr.moveToNext()){
//            Log.i("기존 데이터!!!!!!",cr.getString(0));
            temp = cr.getString(0);
        }//로컬디비에서 명세를 뽑아옴
        JSONArray json = null;
        try {
            if(temp.equals("")) return;

            json = new JSONArray(temp);

            Log.i(ContentName+" 제이슨 변화 전!!!!",json.toString());
            for (int i=0;i<json.length();i++){
                //수정할 컨텐츠를 이름으로 찾는다
                if(json.getJSONObject(i).getString("name").equals(ContentName)){
                    json.getJSONObject(i).remove("disable");
                    json.getJSONObject(i).remove("visionable");
                    json.getJSONObject(i).remove("clickable");
                    json.getJSONObject(i).put("disable",disable);
                    json.getJSONObject(i).put("visionable",visionable);
                    json.getJSONObject(i).put("clickable",clickable);
                    Log.i("제이슨 변화 후!!!!!",json.toString());
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.execSQL("update JSON_CODE set json = '"+json.toString()+"'");
    }
}
