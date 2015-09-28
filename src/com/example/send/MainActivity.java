package com.example.send;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        init();

        contactView = (ListView)findViewById(R.id.contact_view);
        ContactAdapter contactAdapter = new ContactAdapter(this);
        contactView.setAdapter(contactAdapter);

        letterListView = (MyLetterListView)findViewById(R.id.letter_view);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
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
    }
}
