package com.example.send;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

/**
 * Created by root on 15-12-7.
 */
public class TrueMainActivity extends Activity{
    private ContactsFragment mFMContacts = null;
    private MyFragment mFMMine = null;
    private SharedPreferences mShared;
    private TabHost mTabHost = null;
    private Context mContext = null;
    private Button mShowContacts;
    private Button mShowMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.true_main);
        mContext = this;
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getMenuItem(R.drawable.ic_people_outline_black_18dp, "tab1")).setContent(R.id.tab1));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getMenuItem(R.drawable.ic_people_black_18dp, "tab2")).setContent(R.id.tab2));

        startService(new Intent(ContactsService.ACTION_NAME));
        init();
        setDefaultFragment();
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFMContacts = new ContactsFragment();
        transaction.replace(android.R.id.tabcontent, mFMContacts);
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

    public View getMenuItem(int imgID, String textID){
        LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.tab_item, null);
        ImageView imgView = (ImageView)ll.findViewById(R.id.icon);
        imgView.setBackgroundResource(imgID);
        TextView textView = (TextView)ll.findViewById(R.id.name);
        textView.setText(textID);
        menuItemList.add(ll);
        return ll;
    }


}
