package com.secsm.keepongoing;

import com.secsm.keepongoing.DB.DBHelper;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

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
    private ListView roomList, friendList, optionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Log.i(LOG_TAG, "onCreate");
        mDBHelper = new DBHelper(this);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        // register tab 1, 2, 3, 4, 5
        TabHost.TabSpec tab_stopwatch = tabHost.newTabSpec("tab1").setContent(R.id.tab_stopwatch)
                .setIndicator(getString(R.string.tab1));
        tabHost.addTab(tab_stopwatch);

        TabHost.TabSpec tab_friends = tabHost.newTabSpec("tab2").setContent(R.id.tab_friends)
                .setIndicator(getString(R.string.tab2));
        tabHost.addTab(tab_friends);

        TabHost.TabSpec tab_rooms = tabHost.newTabSpec("tab3").setContent(R.id.tab_rooms)
                .setIndicator(getString(R.string.tab3));
        tabHost.addTab(tab_rooms);

//        TabHost.TabSpec tab_quizs = tabHost.newTabSpec("tab4").setContent(R.id.tab4)
//                .setIndicator(getString(R.string.tab4));
//        tabHost.addTab(tab_quizs);

        TabHost.TabSpec tab_settings = tabHost.newTabSpec("tab5").setContent(R.id.tab_settings)
                .setIndicator(getString(R.string.tab5));
        tabHost.addTab(tab_settings);


        // add ListView
        		/* initial setting */
        ArrayList<String> arGeneral3 = new ArrayList<String>();
        arGeneral3.add("로그인");
        arGeneral3.add("회원가입");
        arGeneral3.add("알람 설정");
        arGeneral3.add("목표 시간 설정");
        arGeneral3.add("퀴즈 모음");
        ArrayAdapter<String> optionArrayAdapter;
        optionArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.tab_list, arGeneral3);
        optionList = (ListView) findViewById(R.id.tab_settings);
//        View header_setting = getLayoutInflater().inflate(R.layout.tab_header_setting, null, false);
//        optionList.addHeaderView(header_setting);
        optionList.setAdapter(optionArrayAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        int tabPosition = tabHost.getCurrentTab();
        Log.i(LOG_TAG, "onCreate Options Menu");
        switch (tabPosition)
        {
            case 1:
                Log.i(LOG_TAG, "tab 1");
                break;
            case 2:
                Log.i(LOG_TAG, "tab 2");
                break;
            case 3:
                Log.i(LOG_TAG, "tab 3");
                break;
            case 4:
                Log.i(LOG_TAG, "tab 4");
                break;
        }
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
