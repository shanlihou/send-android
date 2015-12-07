package com.example.send;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.logging.LogRecord;

public class ContactsActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private ListView mContactView = null;
    private MyLetterListView myLetterListView;
    private TextView mOverlay;
    private WindowManager windowManager;
    private TextView mTopLetter;
    private SharedPreferences mShared = null;
    private Handler mHandler = null;
    private EditText mEditSearch = null;
    private ContactAdapter mContactAdapter = null;


    private final static String SHARED_MAIN = "contacts";

    private final static String KEY_FIRST = "first_init";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        init();
        mTopLetter = (TextView)findViewById(R.id.cur_top_letter);
        mTopLetter.setText("A");
        mEditSearch = (EditText)findViewById(R.id.etSearch);

        mContactView = (ListView)findViewById(R.id.contact_view);
        mContactAdapter = new ContactAdapter(this);
        mContactView.setAdapter(mContactAdapter);
        mContactView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                View view1 = mContactView.getChildAt(0);
                View view2 = mContactView.getChildAt(1);
                Contacter contacter1 = HandleContact.getInstance().getContact(i);
                Contacter contacter2 = HandleContact.getInstance().getContact(i + 1);
                Log.d("shanlihou", i + "");
                if (view1 != null && contacter1 != null) {
                    String alpha1 = HandleContact.getInstance().getAlpha(contacter1.getSortKey());
                    mTopLetter.setText(alpha1);
                    if (view2 != null && contacter2 != null) {
                        float high = view2.getY();
                        float hight = mTopLetter.getHeight();
                        String alpha2 = HandleContact.getInstance().getAlpha(contacter2.getSortKey());
                        Log.d("shanlihou", alpha1 + " " + alpha2);
                        if (alpha1.compareTo(alpha2) != 0) {
                            if (high > hight) {
                                mTopLetter.setY(0);
                            } else {
                                mTopLetter.setY(high - hight);
                            }
                        } else {
                            mTopLetter.setY(0);
                        }
                    } else {
                        mTopLetter.setY(0);
                    }
                }
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    HandleContact.getInstance().getSearchList(null);
                }else{
                    HandleContact.getInstance().getSearchList(s.toString());
                }
                mContactAdapter.notifyDataSetChanged();
            }
        });

        myLetterListView = (MyLetterListView)findViewById(R.id.letter_view);
        myLetterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        mTopLetter.setY(25);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
        //registerContentObservers();
        startService(new Intent(ContactsService.ACTION_NAME));
    }

    private void init(){
        mShared = getSharedPreferences(SHARED_MAIN, Context.MODE_PRIVATE);
        boolean hasFirst = mShared.getBoolean(KEY_FIRST, false);
        HandleContact.getInstance().init(this.getApplicationContext());
        if (!hasFirst){
            HandleContact.getInstance().getContactListsFromOrigin(this.getApplicationContext());
            HandleContact.getInstance().saveContactsList();
            SharedPreferences.Editor editor = mShared.edit();
            editor.putBoolean(KEY_FIRST, true);
            editor.commit();
            Log.d("shanlihou", "first");
        }else{
            Log.d("shanlihou", "not first");
            HandleContact.getInstance().getContactsListFromDB();
        }
        initOverlay();
    }


    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener
    {

        @Override
        public void onTouchingLetterChanged(final String s)
        {
            int pos = HandleContact.getInstance().getAlphaIndex(s);
            if (pos != -1){
                mContactView.setSelection(pos);
                mOverlay.setText(s);
                mOverlay.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTouchingLetterEnd() {
            mOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private void initOverlay()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        mOverlay = (TextView) inflater.inflate(R.layout.overlay, null);
        WindowManager.LayoutParams lp =
                new WindowManager.LayoutParams(
                        120,
                        120,
                        100,
                        0,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        PixelFormat.TRANSLUCENT);
        // WindowManager windowManager = (WindowManager)
        // this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mOverlay, lp);
/*
        floatLetter = (TextView) inflater.inflate(R.layout.float_letter, null);
        WindowManager.LayoutParams flp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0,
                0,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        flp.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(floatLetter, flp);
        floatLetter.setText("test");
        floatLetter.setVisibility(View.VISIBLE);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        windowManager.removeView(mOverlay);
    }
}
