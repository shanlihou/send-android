package com.example.send;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 15-12-7.
 */
public class TrueMainActivity extends FragmentActivity{
    private List<Fragment> fragments;
    private SharedPreferences mShared;
    private RadioGroup mRgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.true_main);
        mRgs = (RadioGroup)findViewById(R.id.liner_bottom_radio);
        fragments = new ArrayList<>();
        fragments.add(new ContactsFragment());
        fragments.add(new MyFragment());
        startService(new Intent(ContactsService.ACTION_NAME));
        init();
        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.true_main_frame, mRgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener());
    }


    private void init(){
        mShared = getSharedPreferences(KeyWord.SHARED_MAIN, Context.MODE_PRIVATE);
        boolean hasFirst = mShared.getBoolean(KeyWord.IS_FIRST, false);
        HandleContact.getInstance().init(this.getApplicationContext());
        if (!hasFirst){
            HandleContact.getInstance().getContactListsFromOrigin(this.getApplicationContext());
            HandleContact.getInstance().saveContactsList();
            SharedPreferences.Editor editor = mShared.edit();
            editor.putBoolean(KeyWord.IS_FIRST, true);
            editor.commit();
            Log.d("shanlihou", "first");
        }else{
            Log.d("shanlihou", "not first");
            HandleContact.getInstance().getContactsListFromDB();
        }
    }

}
