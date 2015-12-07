package com.example.send;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import org.json.JSONObject;

/**
 * Created by root on 15-11-25.
 */
public class ResetPassActivity extends Activity{
    private EditText mEtMailForReset = null;
    private ImageView mImRet = null;
    private Button mBtResetPass = null;
    private String mSendData = null;
    private Runnable mRunReset = null;
    private final String RESET_URL = "/passwordReset";
    private final String DOMAIN = "http://192.168.0.109";
    private final String MAIL = "mail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_pass);
        mEtMailForReset = (EditText)findViewById(R.id.etMailForReset);
        mBtResetPass = (Button)findViewById(R.id.bResetPass);
        mImRet = (ImageView)findViewById(R.id.resetPassRet);
        mBtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailSend = mEtMailForReset.getText().toString();
                JSONObject jsonSend = new JSONObject();
                try{
                    jsonSend.put(MAIL, mailSend);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("shanlihou", jsonSend.toString());
                mSendData = jsonSend.toString();
                new Thread(mRunReset).start();
            }
        });
        mRunReset = new Runnable() {
            @Override
            public void run() {
                String url = DOMAIN + RESET_URL;
                String ret = HandleUrl.getInstance().urlPostHttp(url, mSendData);
            }
        };
        mImRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassActivity.this.finish();
            }
        });
    }
}
