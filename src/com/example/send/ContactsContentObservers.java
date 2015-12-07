package com.example.send;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by root on 15-11-5.
 */
public class ContactsContentObservers extends ContentObserver {

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    private Handler mHandler;

    private static final int UPDATE = 1;

    public ContactsContentObservers(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d("shanlihou", "your contacts changed");
        Message message = new Message();
        message.what = 1;
        mHandler.sendMessage(message);
    }
}
