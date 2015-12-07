package com.example.send;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by root on 15-12-7.
 */
public class ContactsFragment extends Fragment{
    TextView mTopLetter = null;
    EditText mEditSearch = null;
    ListView mContactView = null;
    ContactAdapter mContactAdapter = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts, container, false);
        windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        init();
        mTopLetter = (TextView)view.findViewById(R.id.cur_top_letter);
        mTopLetter.setText("A");
        mEditSearch = (EditText)view.findViewById(R.id.etSearch);

        mContactView = (ListView)view.findViewById(R.id.contact_view);
        mContactAdapter = new ContactAdapter(this);
        mContactView.setAdapter(mContactAdapter);
        mContactView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                View view1 = mContactView.getChildAt(0);
                View view2 = mContactView.getChildAt(1);
                Contacter contacter1 = HandleContact.getInstance().getContact(i);
                Contacter contacter2 = HandleContact.getInstance().getContact(i + 1);
                Log.d("shanlihou", i + "");
                if (view1 != null && contacter1 != null) {
                    String alpha1 = HandleContact.getInstance().getAlpha(contacter1.getSortKey());
                    mTopLetter.setText(alpha1);
                    if (view2 != null && contacter2 != null) {
                        float high = view2.getY();
                        float hight = mTopLetter.getHeight();
                        String alpha2 = HandleContact.getInstance().getAlpha(contacter2.getSortKey());
                        Log.d("shanlihou", alpha1 + " " + alpha2);
                        if (alpha1.compareTo(alpha2) != 0) {
                            if (high > hight) {
                                mTopLetter.setY(0);
                            } else {
                                mTopLetter.setY(high - hight);
                            }
                        } else {
                            mTopLetter.setY(0);
                        }
                    } else {
                        mTopLetter.setY(0);
                    }
                }
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    HandleContact.getInstance().getSearchList(null);
                }else{
                    HandleContact.getInstance().getSearchList(s.toString());
                }
                mContactAdapter.notifyDataSetChanged();
            }
        });

        myLetterListView = (MyLetterListView)findViewById(R.id.letter_view);
        myLetterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        mTopLetter.setY(25);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
        //registerContentObservers();
        startService(new Intent(ContactsService.ACTION_NAME));
        return view;
    }
}