package com.example.send;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by root on 15-11-17.
 */
public class SignUpActivity extends Activity{
    private EditText mEtUserName = null;
    private EditText mEtMail = null;
    private EditText mEtPassword = null;
    private ImageView mSignUpRet = null;
    private Button mBSignUp = null;
    private final String DOMAIN = "http://192.168.0.109";
    private final String SIGN_UP_URL = "/signup";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String EMAIL = "email";
    private final String SESSION_TOKEN = "sessionToken";
    private final String ERROR = "error";
    private String sendData = null;
    private static Handler mHandler = null;

    private Runnable mRunSignUp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up);
        mEtUserName = (EditText)findViewById(R.id.etSignUserName);
        mEtMail = (EditText)findViewById(R.id.etSignMail);
        mEtPassword = (EditText)findViewById(R.id.etSignPassword);
        mBSignUp = (Button)findViewById(R.id.bSinUp);
        mSignUpRet = (ImageView)findViewById(R.id.signUpRet);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 0:
                        Intent intent = new Intent(SignUpActivity.this, MyActivity.class);
                        startActivity(intent);
                        SignUpActivity.this.finish();
                        break;
                    default:
                        String message = (String)msg.obj;
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT);
                        break;

                }
            }
        };
        mBSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEtUserName.getText().toString();
                String mail = mEtMail.getText().toString();
                String password = mEtPassword.getText().toString();
                JSONObject jsonSend = new JSONObject();
                try {
                    jsonSend.put(USERNAME, username);
                    jsonSend.put(PASSWORD, password);
                    jsonSend.put(EMAIL, mail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("shanlihou", jsonSend.toString());
                sendData = jsonSend.toString();
                new Thread(mRunSignUp).start();
            }
        });
        mRunSignUp = new Runnable() {
            @Override
            public void run() {
                String url = DOMAIN + SIGN_UP_URL;
                String ret = HandleUrl.getInstance().urlPostHttp(url, sendData);
                String token = "";
                Log.d("shanlihou", "ret:" + ret);
                JSONTokener jsonParser = new JSONTokener(ret);
                try{
                    JSONObject jsonRet = (JSONObject)jsonParser.nextValue();
                    if (jsonRet.has(SESSION_TOKEN)){
                        token = jsonRet.getString(SESSION_TOKEN);
                        MainApplication.getInstance().setShared(SESSION_TOKEN, token);
                        mHandler.sendEmptyMessage(0);
                    }else if(jsonRet.has(ERROR)){
                        Message message = new Message();
                        message.what = 1;
                        message.obj = jsonRet.getString(ERROR);
                        mHandler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        mSignUpRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.this.finish();
            }
        });
    }
}
