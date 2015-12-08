package com.example.send;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by root on 15-12-8.
 */
public class MyFragment extends Fragment{
    private Button mBtLoginOut = null;
    private Runnable mLoginOutRun = null;
    private Handler mHandler = null;
    Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_page, container, false);
        mBtLoginOut = (Button)view.findViewById(R.id.login_out);
        /*
        mBtLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(mLoginOutRun).start();
            }
        });*/
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 0:
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        String message = (String)msg.obj;
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
                    default:
                        break;
                }
            }
        };

        mLoginOutRun = new Runnable() {
            @Override
            public void run() {
                String url = KeyWord.DOMAIN + KeyWord.LOGIN_OUT_URL;
                String token = MainApplication.getInstance().getShared(KeyWord.SESSION_TOKEN);
                JSONObject jsonSend = new JSONObject();
                try{
                    jsonSend.put(KeyWord.SESSION_TOKEN, token);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("shanlihou", jsonSend.toString());
                String ret = HandleUrl.getInstance().urlPostHttp(url, jsonSend.toString());
                JSONTokener jsonTokener = new JSONTokener(ret);
                JSONObject jsonRet = null;
                try{
                    jsonRet = (JSONObject)jsonTokener.nextValue();
                    if (jsonRet.has(KeyWord.ERROR)){
                        Message message = new Message();
                        message.what = 1;
                        message.obj = jsonRet.getString(KeyWord.ERROR);
                        mHandler.sendMessage(message);
                    }else{
                        MainApplication.getInstance().setShared(KeyWord.SESSION_TOKEN, null);
                        mHandler.sendEmptyMessage(0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        return view;
    }
}
