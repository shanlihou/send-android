package com.example.send;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 15-12-1.
 */
public class MyActivity extends Activity{
    private Button mBtLoginOut = null;
    private final String DOMAIN = "http://192.168.0.109";
    private final String LOGIN_OUT_URL = "/logout";
    private final String SESSION_TOKEN = "sessionToken";
    private final String ERROR = "error";
    private Runnable mLoginOutRun = null;
    private ViewPager mPager = null;
    private Handler mHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info);
        mBtLoginOut = (Button)findViewById(R.id.login_out);
        mBtLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(mLoginOutRun).start();
            }
        });
        initViewPager();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 0:
                        Intent intent = new Intent(MyActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        String message = (String)msg.obj;
                        Toast.makeText(MyActivity.this, message, Toast.LENGTH_SHORT);
                    default:
                        break;
                }
            }
        };

        mLoginOutRun = new Runnable() {
            @Override
            public void run() {
                String url = DOMAIN + LOGIN_OUT_URL;
                String token = MainApplication.getInstance().getShared(SESSION_TOKEN);
                JSONObject jsonSend = new JSONObject();
                try{
                    jsonSend.put(SESSION_TOKEN, token);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("shanlihou", jsonSend.toString());
                String ret = HandleUrl.getInstance().urlPostHttp(url, jsonSend.toString());
                JSONTokener jsonTokener = new JSONTokener(ret);
                JSONObject jsonRet = null;
                try{
                    jsonRet = (JSONObject)jsonTokener.nextValue();
                    if (jsonRet.has(ERROR)){
                        Message message = new Message();
                        message.what = 1;
                        message.obj = jsonRet.getString(ERROR);
                        mHandler.sendMessage(message);
                    }else{
                        MainApplication.getInstance().setShared(SESSION_TOKEN, null);
                        mHandler.sendEmptyMessage(0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private void initViewPager(){
        mPager = (ViewPager) findViewById(R.id.vPager);
        List<View> listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.contacts, null));
        listViews.add(mInflater.inflate(R.layout.my_page, null));
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}