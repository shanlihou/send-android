package com.example.send;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private ListView contactView = null;
    private MyLetterListView letterListView;
    private TextView overlay;
    private WindowManager windowManager;
    private TextView floatLetter;
    private TextView TopLetter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        init();
        TopLetter = (TextView)findViewById(R.id.cur_top_letter);
        TopLetter.setText("A");

        contactView = (ListView)findViewById(R.id.contact_view);
        ContactAdapter contactAdapter = new ContactAdapter(this);
        contactView.setAdapter(contactAdapter);

        contactView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                View view1 = contactView.getChildAt(0);
                View view2 = contactView.getChildAt(1);
                Contacter contacter1 = HandleContact.getInstance().getContact(i);
                Contacter contacter2 = HandleContact.getInstance().getContact(i + 1);
                Log.d("shanlihou", i + "");
                if (view1 != null && contacter1 != null){
                    String alpha1 = HandleContact.getInstance().getAlpha(contacter1.getSortKey());
                    TopLetter.setText(alpha1);
                    if (view2 != null && contacter2 != null){
                        float high = view2.getY();
                        float hight = TopLetter.getHeight();
                        String alpha2 = HandleContact.getInstance().getAlpha(contacter2.getSortKey());
                        Log.d("shanlihou", alpha1 + " " + alpha2);
                        if (alpha1.compareTo(alpha2) != 0){
                            if (high > hight){
                                TopLetter.setY(0);
                            }
                            else{
                                TopLetter.setY(high - hight);
                            }
                        }
                        else{
                            TopLetter.setY(0);
                        }
                    }
                    else{
                        TopLetter.setY(0);
                    }
                }
            }
        });

        letterListView = (MyLetterListView)findViewById(R.id.letter_view);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        TopLetter.setY(25);
    }

    void init(){
        HandleContact.getInstance().getContactList(this);
        initOverlay();
    }


    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener
    {

        @Override
        public void onTouchingLetterChanged(final String s)
        {
            int pos = HandleContact.getInstance().getAlphaIndex(s);
            if (pos != -1){
                contactView.setSelection(pos);
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTouchingLetterEnd() {
            overlay.setVisibility(View.INVISIBLE);
        }
    }

    private void initOverlay()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        WindowManager.LayoutParams lp =
                new WindowManager.LayoutParams(
                        120,
                        120,
                        100,
                        0,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        PixelFormat.TRANSLUCENT);
        // WindowManager windowManager = (WindowManager)
        // this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);

        floatLetter = (TextView) inflater.inflate(R.layout.float_letter, null);
        WindowManager.LayoutParams flp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0,
                0,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        flp.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(floatLetter, flp);
        floatLetter.setText("test");
        floatLetter.setVisibility(View.VISIBLE);
    }
}
