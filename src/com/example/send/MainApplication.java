package com.example.send;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Created by root on 15-11-13.
 */
public class MainApplication extends Application {
    public static final String SEND_PATH = "/mnt/sdcard/send/";
    private String SHARED_APP = "app";
    SharedPreferences mShared = null;
    private static MainApplication instance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        createSendDir();
        mShared = getSharedPreferences(SHARED_APP, Context.MODE_PRIVATE);
        //CrashHandler.instance(this).init();
        instance = this;
    }
    private void createSendDir(){
        File filePath = new File(SEND_PATH);
        if (filePath.exists()){
            if (!filePath.isDirectory()){
                filePath.mkdir();
            }
        }else{
            filePath.mkdir();
        }
    }

    public void setShared(String key, String value){
        SharedPreferences.Editor editor = mShared.edit();
        if (value == null){
            editor.remove(key);
        }
        else{
            editor.putString(key,value);
        }
        editor.commit();
    }
    public String getShared(String key){
        return mShared.getString(key, null);
    }
    public static MainApplication getInstance(){
        return instance;
    }
}
