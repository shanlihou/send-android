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

    private final static String TABLE_CONTACTS_INFO = "send_contact_info";

    public final static String FIELD_ID = "id";

    public final static String FIELD_LASTNAME = "last_name";

    public final static String FIELD_FIRSTNAME = "first_name";

    public final static String FIELD_PHONENUMBER = "phone_number";

    public final static String FIELD_DISPLAYNAME = "display_name";
    public HandleDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_CONTACTS_INFO + "("
                + FIELD_ID + " long primary key, "
                + FIELD_DISPLAYNAME + " text, "
                + FIELD_PHONENUMBER + " text );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + TABLE_CONTACTS_INFO;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor query() {
        SQLiteDatabase db;
        Cursor cursor;
        db = this.getReadableDatabase();
        cursor = db.query(TABLE_CONTACTS_INFO, null, null, null, null, null, null);
        return cursor;
    }

    public String getByKey(String key){
        String ret = null;
        SQLiteDatabase db;
        db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_CONTACTS_INFO + " where " + FIELD_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{key});
        while(cursor.moveToNext()){
            ret = cursor.getString(cursor.getColumnIndex(FIELD_PHONENUMBER));
        }
        return ret;
    }

    public long insert(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLE_CONTACTS_INFO, null, createValues(key, value));
    }

    public void delete(String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_ID + "=?";
        String[] whereValue = {
                location
        };
        db.delete(TABLE_CONTACTS_INFO, where, whereValue);
    }
    public void deleteAll(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS_INFO, null, null);
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
        cv.put(FIELD_DISPLAYNAME, title);
        cv.put(FIELD_PHONENUMBER, location);
        return cv;
    }


}
