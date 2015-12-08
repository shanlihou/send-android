package com.example.send;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by root on 15-11-16.
 */
public class MainActivity extends Activity{
    private String SESSION_TOKEN = "sessionToken";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        startLoginAvtivity();
    }
    private void startLoginAvtivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String token = MainApplication.getInstance().getShared(SESSION_TOKEN);
                if (token == null){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, TrueMainActivity.class);
                    startActivity(intent);
                }
                finish();//结束本Activity
            }
        }, 2000);//设置执行时间
    }
}
