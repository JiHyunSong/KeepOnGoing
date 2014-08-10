package com.secsm.keepongoing;

import com.secsm.keepongoing.DB.DBHelper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;

public class MainmenuActivity extends Activity {

    public static final int ROOMNAME_REQUEST_CODE = 1;
    public static final int FRIENDNAME_REQUEST_CODE = 2;
    public static final int CHATROOM_REQUEST_CODE = 3;
    public static final int FRIENDS_REQUEST_CODE = 4;
    private static final String LOG_TAG = "MainmenuActivity";
    private final int ADDROOM = 100;
    private final int MANAGEFRIENDS = 200;



    private TabHost tabHost;
    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Log.i(LOG_TAG, "onCreate");
        mDBHelper = new DBHelper(this);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        // register tab 1, 2, 3, 4, 5
        TabHost.TabSpec tab_stopwatch = tabHost.newTabSpec("tab1").setContent(R.id.tab1)
                .setIndicator(getString(R.string.tab1));
        tabHost.addTab(tab_stopwatch);

        TabHost.TabSpec tab_friends = tabHost.newTabSpec("tab2").setContent(R.id.tab2)
                .setIndicator(getString(R.string.tab2));
        tabHost.addTab(tab_friends);

        TabHost.TabSpec tab_rooms = tabHost.newTabSpec("tab3").setContent(R.id.tab3)
                .setIndicator(getString(R.string.tab3));
        tabHost.addTab(tab_rooms);

        TabHost.TabSpec tab_quizs = tabHost.newTabSpec("tab4").setContent(R.id.tab4)
                .setIndicator(getString(R.string.tab4));
        tabHost.addTab(tab_quizs);

        TabHost.TabSpec tab_settings = tabHost.newTabSpec("tab5").setContent(R.id.tab5)
                .setIndicator(getString(R.string.tab5));
        tabHost.addTab(tab_settings);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
