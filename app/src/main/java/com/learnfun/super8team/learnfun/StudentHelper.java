package com.learnfun.super8team.learnfun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by cho on 2017-05-18.
 */
public class StudentHelper extends SQLiteOpenHelper {
    public StudentHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table student ( " +
                "num integer auto_increment , " +
                "name text , " +
                "id text , " +
                "color text , " +
                "className integer);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists student";
        db.execSQL(sql);
        onCreate(db);
    }

    public ArrayList<Student> selectAll(){
        ArrayList<Student> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("student", null, null, null, null, null, null);
        while(cursor.moveToNext()){
            Student item = new Student();
            item.num = cursor.getInt(cursor.getColumnIndex("num"));
            Log.d("num", String.valueOf(cursor.getInt(cursor.getColumnIndex("num"))));
            item.name = cursor.getString(cursor.getColumnIndex("name"));
            Log.d("name", cursor.getString(cursor.getColumnIndex("name")));
            item.id = cursor.getString(cursor.getColumnIndex("id"));
            Log.d("id", cursor.getString(cursor.getColumnIndex("id")));
            item.color = cursor.getString(cursor.getColumnIndex("color"));
            Log.d("color", cursor.getString(cursor.getColumnIndex("color")));
            item.className = cursor.getInt(cursor.getColumnIndex("className"));
            Log.d("className", String.valueOf(cursor.getInt(cursor.getColumnIndex("className"))));
            result.add(item);
        }
        return result;
    }

    public ArrayList<Integer> selectClasses(){
        ArrayList<Integer> result = new ArrayList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("student", null, null, null, "className", null, null);
        //String table,String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy,String limit
        while(cursor.moveToNext()){
            int className = cursor.getInt(cursor.getColumnIndex("className"));
            Log.d("className", String.valueOf(cursor.getInt(cursor.getColumnIndex("className"))));
            result.add(className);
        }
        return result;
    }

    public void delete(int num){
        SQLiteDatabase db = getWritableDatabase();
        if(num < 0)
            onUpgrade(db, db.getVersion(), db.getVersion()+1);
        else {
            db.delete("student", "num=?", new String[]{String.valueOf(num)});
        }
    }
    public void update(int num,String color){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("color",color);
        db.update("student",values, "num=?", new String[]{String.valueOf(num)});

    }
    public void insert(String id, String name,int className){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("color", "blue");
        values.put("className", className);
        db.insert("student", null, values);
    }

}
