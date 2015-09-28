package com.example.send;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 15-9-24.
 */


public class HandleDB extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "Send.db";

    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_NAME_MUSIC = "send";

    public final static String FIELD_ID = "_id";

    public final static String FIELD_KEY = "key";

    public final static String FIELD_VALUE = "value";
    public HandleDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_NAME_MUSIC + "(" + FIELD_ID + " integer primary key autoincrement,"
                + FIELD_KEY + " text, " + FIELD_VALUE + " text );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + TABLE_NAME_MUSIC;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor query() {
        SQLiteDatabase db;
        Cursor cursor;
        db = this.getReadableDatabase();
        cursor = db.query(TABLE_NAME_MUSIC, null, null, null, null, null, null);
        return cursor;
    }

    public String getByKey(String key){
        String ret = null;
        SQLiteDatabase db;
        db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME_MUSIC + " where " + FIELD_KEY + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{key});
        while(cursor.moveToNext()){
            ret = cursor.getString(cursor.getColumnIndex(FIELD_VALUE));
        }
        return ret;
    }

    public long insert(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLE_NAME_MUSIC, null, createValues(key, value));
    }

    public void delete(String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_KEY + "=?";
        String[] whereValue = {
                location
        };
        db.delete(TABLE_NAME_MUSIC, where, whereValue);
    }
    public void deleteAll(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_MUSIC, null, null);
    }



    public void showDB(){
        Cursor cursor = query();
        if (cursor.moveToFirst()){
            do{
                String key = cursor.getString(1);
                String value = cursor.getString(2);
                Log.d("shanlihou", key + ":" + value);
            }while(cursor.moveToNext());
        }
    }


    private ContentValues createValues(String title, String location) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_KEY, title);
        cv.put(FIELD_VALUE, location);
        return cv;
    }


}
