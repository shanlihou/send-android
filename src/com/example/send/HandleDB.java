package com.example.send;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.*;

/**
 * Created by root on 15-9-24.
 */


public class HandleDB extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "Send.db";

    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_CONTACTS_INFO = "send_contact_info";
    private final static String TABLE_CONTACTS_DATA = "send_contact_data";

    public final static String FIELD_ID = "id";
    public final static String FIELD_CONTACTS_ID = "contacts_id";
    public final static String FIELD_LASTNAME = "last_name";
    public final static String FIELD_FIRSTNAME = "first_name";
    public final static String FIELD_LOOKUPKEY = "lookup_key";
    public final static String FIELD_PHONENUMBER = "phone_number";
    public final static String FIELD_DISPLAYNAME = "display_name";
    public final static String FIELD_SORTKEY = "sort_key";
    public final static String FIELD_VERSION = "data_version";
    public final static String FIELD_ISDELETED = "is_deleted";

    public final static int INDEX_LOOKUPKEY = 1;
    public final static int INDEX_CONTACTS_ID = 2;
    public final static int INDEX_DISPLAYNAME = 3;
    public final static int INDEX_VERSION = 4;
    public final static int INDEX_ISDELETED = 5;
    public final static int INDEX_SORTKEY = 6;

    public final static int DATA_LOOKUPKEY = 1;
    public final static int DATA_PHONENUMBER = 2;

    public HandleDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_CONTACTS_INFO + "("
                + FIELD_ID + " integer primary key autoincrement , "
                + FIELD_LOOKUPKEY + " text, "
                + FIELD_CONTACTS_ID + " long , "
                + FIELD_DISPLAYNAME + " text, "
                + FIELD_VERSION + " integer, "
                + FIELD_ISDELETED + " integer, "
                + FIELD_SORTKEY + " text );";
        db.execSQL(sql);
        sql = "Create table " + TABLE_CONTACTS_DATA + "("
                + FIELD_ID + " integer primary key autoincrement , "
                + FIELD_LOOKUPKEY + " text, "
                + FIELD_PHONENUMBER + " text );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + TABLE_CONTACTS_INFO;
        db.execSQL(sql);
        sql = " DROP TABLE IF EXISTS " + TABLE_CONTACTS_DATA;
        db.execSQL(sql);
        onCreate(db);
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = " DROP TABLE IF EXISTS " + TABLE_CONTACTS_INFO;
        db.execSQL(sql);
        sql = " DROP TABLE IF EXISTS " + TABLE_CONTACTS_DATA;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor queryInfo() {
        SQLiteDatabase db;
        Cursor cursor;
        db = this.getReadableDatabase();
        cursor = db.query(TABLE_CONTACTS_INFO, null, null, null, null, null, null);
        return cursor;
    }
    public Cursor queryData(){
        SQLiteDatabase db;
        Cursor cursor;
        db = this.getReadableDatabase();
        cursor = db.query(TABLE_CONTACTS_DATA, null, null, null, null, null, null);
        return cursor;
    }


    public long insert(Contacter contacter) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CONTACTS_INFO, null, createValuesInfo(contacter));
        int length = contacter.getPhoneNumberSize();
        for (int i = 0; i < length; i++){
            db.insert(TABLE_CONTACTS_DATA, null, createValuesData(contacter, i));
        }
        return 0;
    }

    public void delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_CONTACTS_ID + "=?";
        String[] whereValue = {
                id
        };
        db.delete(TABLE_CONTACTS_INFO, where, whereValue);
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS_INFO, null, null);
        db.delete(TABLE_CONTACTS_DATA, null, null);
    }

    private ContentValues createValuesInfo(Contacter contacter) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_DISPLAYNAME, contacter.getDisplayName());
        cv.put(FIELD_CONTACTS_ID, contacter.getContactId());
        cv.put(FIELD_LOOKUPKEY, contacter.getLookupKey());
        cv.put(FIELD_SORTKEY, contacter.getSortKey());
        cv.put(FIELD_VERSION, contacter.getVersion());
        cv.put(FIELD_ISDELETED, contacter.getIsDeleted());
        return cv;
    }

    private ContentValues createValuesData(Contacter contacter, int index){
        ContentValues cv = new ContentValues();
        cv.put(FIELD_LOOKUPKEY, contacter.getLookupKey());
        cv.put(FIELD_PHONENUMBER, contacter.getPhoneNumber(index));
        return cv;
    }

    public void getContactList(List<Contacter> contacterList, Map<String, Contacter> contacterMap){
        Cursor cursor = queryInfo();
        Contacter contacter;
        if (cursor.moveToFirst()){
            do{
                contacter = new Contacter();
                contacter.setLookupKey(cursor.getString(INDEX_LOOKUPKEY));
                contacter.setContactId(cursor.getString(INDEX_CONTACTS_ID));
                contacter.setSortKey(cursor.getString(INDEX_SORTKEY));
                contacter.setDisplayName(cursor.getString(INDEX_DISPLAYNAME));
                contacter.setVersion(cursor.getInt(INDEX_VERSION));
                contacter.setIsDeleted(cursor.getInt(INDEX_ISDELETED));
                contacterMap.put(contacter.getLookupKey(), contacter);
                contacterList.add(contacter);
            }while(cursor.moveToNext());
        }

        cursor = queryData();
        String tempLookup;
        if (cursor.moveToFirst()){
            do{
                tempLookup = cursor.getString(DATA_LOOKUPKEY);
                if (contacterMap.containsKey(tempLookup)){
                    contacterMap.get(tempLookup).addPhoneNumber(cursor.getString(DATA_PHONENUMBER));
                }
            }while (cursor.moveToNext());
        }
    }

    public void saveContactsList(List<Contacter> contactsList){
        Iterator<Contacter> iterator = contactsList.iterator();
        dropTable();
        deleteAll();
        while(iterator.hasNext()){
            Contacter contacter = iterator.next();
            insert(contacter);
        }
    }
}
