package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
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
        //명세를 저장할 수 있는 테이블을 만든다. nosql
        db.execSQL("CREATE TABLE JSON_CODE( json TEXT );");
        db.execSQL("CREATE TABLE CONTENT( type TEXT, sql TEXT );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db가 존재하지만 버전이 다를경우 호출되는 메소드
        db.execSQL("drop table JSON_CODE");

        db.execSQL("CREATE TABLE JSON_CODE( json TEXT );");
        db.execSQL("CREATE TABLE CONTENT( type TEXT, sql TEXT );");
    }

    public void testClear(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from JSON_CODE");
        db.execSQL("delete from CONTENT");
        db.close();
    }

    public String init(String json){
        SQLiteDatabase db = getWritableDatabase();
        String temp = "";

        Cursor cr = db.rawQuery("select * from JSON_CODE",null);
        Cursor cr2= db.rawQuery("select * from CONTENT",null);

        if(cr.getCount() >= 1){
            //데이터베이스에 내용이 있으면 거짓 -> 데이터베이스의 내용을 반환함
            while (cr.moveToNext()){
                temp = cr.getString(0);
                Log.i("기존값 있음",temp);
            }
            while(cr2.moveToNext()){
                String temp2 = cr2.getString(0);
                Log.i("컨텐츠디비",": "+ temp2);
            }


        }else{
            //데이터베이스에 내용이 없으면 참 -> 명세를 디비에 저장하고 반환
            db.execSQL("insert into JSON_CODE values('"+json+"')");
            db.execSQL("insert into CONTENT values('quest'  , 'null')");
            db.execSQL("insert into CONTENT values('bingo'  , '/')");
            db.execSQL("insert into CONTENT values('collect', 'off')");
            db.execSQL("insert into CONTENT values('map'    , 'off')");
            temp = json;
        }

        db.close();
//        return temp;
        return json;
    }
    public String select(String type){
        SQLiteDatabase db = getWritableDatabase();
        String temp = "";

        Cursor cr = db.rawQuery("select * from CONTENT where type = '"+type+"'",null);
        while (cr.moveToNext()){
            temp = cr.getString(1);
            Log.i("서브 컨텐츠 내용물 ::",temp);
        }//현재 정보를 불러옴

        db.close();
        return temp;
    }

    public void insert(String type, String data){
        SQLiteDatabase db = getWritableDatabase();
        if(type.equals("bingo")){
            String temp = "";

            Cursor cr = db.rawQuery("select * from CONTENT where type = '"+type+"'",null);
            while (cr.moveToNext()){
                temp = cr.getString(1);
            }//현재 빙고 정보를 불러온다.


            String sql = "update CONTENT set sql = '"+temp+"/"+data+"' where type = '"+type+"'";
            db.execSQL(sql);
        }else{
            //빙고 이외의 컬럼
            String sql = "update CONTENT set sql = '"+data+"' where type = '"+type+"'";
            Log.i(type+" ::현재 데이터 저장",sql);
            db.execSQL(sql);
        }


        db.close();
    }

    public void reset(String type){
        SQLiteDatabase db = getWritableDatabase();
        switch (type){
            case "quest":
                db.execSQL("update CONTENT set sql = 'null' where type = 'quest'");
                break;
            case "bingo":
                db.execSQL("update CONTENT set sql =   '/' where type = 'bingo'");
                break;
            case "collect":
                db.execSQL("update CONTENT set sql = 'off' where type = 'collect'");
                break;
            case "map":
                db.execSQL("update CONTENT set sql = 'off' where type = 'map'");
                break;
        }
        db.close();
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

        db.close();
    }
}
