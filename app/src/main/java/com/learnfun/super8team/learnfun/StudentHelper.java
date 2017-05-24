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
                " id integer primary key , " +
                " name text , " +
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
            item.id = cursor.getInt(cursor.getColumnIndex("id"));
            Log.d("id", String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
            item.name = cursor.getString(cursor.getColumnIndex("name"));
            Log.d("name", cursor.getString(cursor.getColumnIndex("name")));
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

    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        if(id < 0)
            onUpgrade(db, db.getVersion(), db.getVersion()+1);
        else {
            db.delete("student", "_id=?", new String[]{String.valueOf(id)});
        }
    }

    public void insert(int id, String name,int className){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("className", className);
        db.insert("student", null, values);
    }

}
