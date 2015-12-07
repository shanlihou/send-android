package com.example.send;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by root on 15-11-16.
 */
public class LoginActivity extends Activity{
    private Button mBtSingUp = null;
    private Button mBtLogin = null;
    private EditText mEtUserName = null;
    private EditText mEtPassword = null;
    private TextView mTxtRestPass = null;
    private TextView mBtNotLoginContact = null;
    private Runnable mRunLogin = null;
    private Handler mHandler = null;

    private String mSendData = null;
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String DOMAIN = "http://192.168.0.109";
    private final String LOGIN_URL = "/login";
    private final String SESSION_TOKEN = "sessionToken";
    private final String ERROR = "error";
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mBtSingUp = (Button)findViewById(R.id.bt_sign_up);
        mBtLogin = (Button)findViewById(R.id.login_btnLogin);
        mEtUserName = (EditText)findViewById(R.id.login_edtId);
        mEtPassword = (EditText)findViewById(R.id.login_edtPwd);
        mTxtRestPass = (TextView)findViewById(R.id.login_txtForgotPwd);
        mBtNotLoginContact = (TextView)findViewById(R.id.not_login_but_use);
        mEtUserName.setText("dazhonglian");
        mEtPassword.setText("889914");
        mBtSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        mTxtRestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
                startActivity(intent);
            }
        });
        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mEtUserName.getText().toString();
                String password = mEtPassword.getText().toString();
                JSONObject jsonSend = new JSONObject();
                try{
                    jsonSend.put(USERNAME, userName);
                    jsonSend.put(PASSWORD, password);

                }catch (Exception e){
                    e.printStackTrace();
                }
                mSendData = jsonSend.toString();
                mBtLogin.setEnabled(false);
                new Thread(mRunLogin).start();
            }
        });
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        mBtLogin.setEnabled(true);
                        if (MainApplication.getInstance().getShared(SESSION_TOKEN) != null){
                            Intent intent = new Intent(LoginActivity.this, MyActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                        break;
                    case 1:
                        String message = (String)msg.obj;
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        };
        mBtNotLoginContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
        mRunLogin = new Runnable() {
            @Override
            public void run() {
                Log.d("shanlihou", mSendData);
                String ret = HandleUrl.getInstance().urlPostHttp(DOMAIN + LOGIN_URL, mSendData);
                JSONTokener jsonTokener = new JSONTokener(ret);
                JSONObject jsonRet = null;
                try{
                    jsonRet = (JSONObject)jsonTokener.nextValue();
                    if (jsonRet.has(SESSION_TOKEN)){
                        MainApplication.getInstance().setShared(SESSION_TOKEN, jsonRet.getString(SESSION_TOKEN));
                    }else if (jsonRet.has(ERROR)){
                        Message message = new Message();
                        message.what = 1;
                        message.obj = jsonRet.get(ERROR);
                        mHandler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        };
    }
}
