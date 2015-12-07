package com.example.send;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by root on 15-12-7.
 */
public class TrueMainActivity extends Activity{
    ContactsFragment mFMContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFMContacts = new ContactsFragment();
        transaction.replace(R.id.true_main_frame, mFMContacts);
        transaction.commit();
    }
}
