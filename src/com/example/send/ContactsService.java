package com.example.send;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 15-11-5.
 */
public class ContactsService extends Service{
    public static final String ACTION_NAME = "ContactsServiceForObserver";
    private Handler mHandler;
    private static final int UPDATE_CONTACTS = 1;
    private HandleDB mContactDB;
    private Runnable updateRunnable = null;
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("shanlihou", "service onbind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("shanlihou", "service onCreate");
        super.onCreate();
        mContactDB = new HandleDB(this);
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    Log.d("shanlihou","start update");
                    HandleContact.getInstance().mergeContacts();
                }
            }
        };
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("shanlihou", "handle msg:" + msg.what);
                switch(msg.what){
                    case 1:
                        new Thread(updateRunnable).start();
                        break;
                    default:
                        break;
                }
            }
        };
        registerContentObservers();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("shanlihou", "service onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("shanlihou", "service start command");
        return super.onStartCommand(intent, flags, startId);
    }
    private void registerContentObservers(){
        //Uri uriContacts = ContactsContract.Contacts.CONTENT_URI;
        Uri uriContacts = ContactsContract.RawContacts.CONTENT_URI;
        getContentResolver().registerContentObserver(uriContacts, true, new ContactsContentObservers(mHandler));
        Log.d("shanlihou", "has registered");
    }

    private void updateContacts(){
        List<Contacter> contactersList = new ArrayList<>();
        Map<String, Contacter> contactersMap = new HashMap<>();
        mContactDB.getContactList(contactersList, contactersMap);

    }
}
