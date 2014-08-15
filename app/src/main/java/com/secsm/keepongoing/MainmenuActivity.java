package com.secsm.keepongoing;

import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private ListView roomList, friendList, settingList;


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

        // setup rooms
        if (KogPreference.DEBUG_MODE)
        {
            // mock room
            ArrayList<String> mockRooms = new ArrayList<String>();
            mockRooms.add("tempRoom1");
            mockRooms.add("tempRoom2");
            mockRooms.add("tempRoom3");

            ArrayAdapter<String> mockRoomArrayAdapter;
            mockRoomArrayAdapter = new ArrayAdapter<String>(this,
                    R.layout.tab_list, mockRooms);
            roomList = (ListView) findViewById(R.id.tab_rooms);
            roomList.setAdapter(mockRoomArrayAdapter);

            // mock quiz
            ArrayList<String> mockFriends = new ArrayList<String>();
            mockFriends.add("tempFriend1");
            mockFriends.add("tempFriend2");
            mockFriends.add("tempFriend3");
            mockFriends.add("tempFriend4");

            ArrayAdapter<String> mockFriendArrayAdapter;
            mockFriendArrayAdapter = new ArrayAdapter<String>(this,
                    R.layout.tab_list, mockFriends);
            friendList = (ListView) findViewById(R.id.tab_friends);
            friendList.setAdapter(mockFriendArrayAdapter);

            // add list item onClickListener
            roomList.setOnItemClickListener(itemClickListener);
            friendList.setOnItemClickListener(itemClickListener);

        }

        // setup tab_settings
        ArrayList<String> arGeneral3 = new ArrayList<String>();
        arGeneral3.add("알람 설정");
        arGeneral3.add("목표 시간 설정");
        arGeneral3.add("퀴즈 모음");
        arGeneral3.add("로그인");
//        arGeneral3.add("회원가입");
        ArrayAdapter<String> optionArrayAdapter;
        optionArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.tab_list, arGeneral3);
        settingList = (ListView) findViewById(R.id.tab_settings);
//        View header_setting = getLayoutInflater().inflate(R.layout.tab_header_setting, null, false);
//        settingList.addHeaderView(header_setting);
        settingList.setAdapter(optionArrayAdapter);

        // add list item onClickListener
        settingList.setOnItemClickListener(itemClickListener);

    }

    /* click listener for setting tab */
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            if (adapterView.getId() == R.id.tab_friends) {
                Log.i(LOG_TAG,"tab2, friends Clicked");
            } else if (adapterView.getId() == R.id.tab_rooms) {
                Log.i(LOG_TAG,"tab3, rooms Clicked");

            } else if (adapterView.getId() == R.id.tab_settings) {
                Log.i(LOG_TAG,"tab4, settings Clicked");
                switch (position) {
                    case 1: // 알람 설정
                        Log.i(LOG_TAG,"tab4, settings Clicked");
                        Intent intent_notice = new Intent(MainmenuActivity.this, NoticeActivity.class);
                        startActivity(intent_notice);
                        break;
                    case 2: // 목표 시간 설정
                        Log.i(LOG_TAG,"tab4, settings Clicked");

                        break;
                    case 3: // 퀴즈 모음
                        break;
                    case 4: // 로그인
                        break;
                }
            }
        }
    };

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


    // sql lite
    public void setFriendInfoFromDB() {

        SQLiteDatabase db;
        Cursor cursor;

        db = mDBHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT nickname_fl, profile FROM Friend_l", null);

        while (cursor.moveToNext()) {
            String[] friendInfo = new String[2];
            friendInfo[0] = cursor.getString(cursor.getColumnIndex("nickname_fl")); // id
            friendInfo[1] = cursor.getString(cursor.getColumnIndex("profile")); // name

        }
        cursor.close();
        db.close();
        mDBHelper.close();
    }

}
