package com.example.send;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by root on 15-12-7.
 */
public class TrueMainActivity extends Activity implements View.OnClickListener{
    private ContactsFragment mFMContacts = null;
    private MyFragment mFMMine = null;
    private SharedPreferences mShared;
    private Button mShowContacts;
    private Button mShowMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.true_main);
        mShowContacts = (Button)findViewById(R.id.bt_show_contacts);
        mShowMine = (Button)findViewById(R.id.bt_show_my);

        mShowContacts.setOnClickListener(this);
        mShowMine.setOnClickListener(this);
        startService(new Intent(ContactsService.ACTION_NAME));
        init();
        setDefaultFragment();
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFMContacts = new ContactsFragment();
        transaction.replace(R.id.true_main_frame, mFMContacts);
        transaction.commit();
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

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch(v.getId()){
            case R.id.bt_show_contacts:
                if (mFMContacts == null){
                    mFMContacts = new ContactsFragment();
                }
                transaction.replace(R.id.true_main_frame, mFMContacts);
                break;
            case R.id.bt_show_my:
                if (mFMMine == null){
                    mFMMine = new MyFragment();
                }
                transaction.replace(R.id.true_main_frame, mFMMine);
                break;
            default:
                break;
        }
        transaction.commit();
    }
}
