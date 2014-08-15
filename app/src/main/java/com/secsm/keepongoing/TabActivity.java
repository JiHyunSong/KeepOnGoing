package com.secsm.keepongoing;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.secsm.keepongoing.Alarm.Contact;
import com.secsm.keepongoing.Alarm.DBContactHelper;
import com.secsm.keepongoing.Alarm.Preference;
import com.secsm.keepongoing.Alarm.alram_list;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TabActivity extends Activity {
    final static int TAB_DELAY = 10;

    public static final int ROOMNAME_REQUEST_CODE = 1;
    public static final int FRIENDNAME_REQUEST_CODE = 2;
    public static final int CHATROOM_REQUEST_CODE = 3;
    public static final int FRIENDS_REQUEST_CODE = 4;
    private static final String LOG_TAG = "MainmenuActivity";
    private final int ADDROOM = 100;
    private final int MANAGEFRIENDS = 200;
    private TextView _text;
    private TextView _text2;

    protected int i=0,minute=0,diff_hour,diff_min;
    boolean a=false;
    long mills=0;

    private DBHelper mDBHelper;
    private ListView roomList, friendList, settingList;

    private ImageButton tabStopwatch, tabFriends, tabRooms, tabSettings;
    private RelativeLayout layoutStopwatch, layoutFriends,layoutRooms,layoutSettings;
    private LinearLayout linearTab = null;
    private ImageButton actionBarRoomTabNotifyBtn = null;
    private ImageButton actionBarRoomTabAddBtn = null;
    private Button tabRoomCreateBtn = null;
//    Handler initHandler = new Handler()		// 기본 Tab 선택용 함들러 - UI 생성 후 0.01초후 실행
//    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            if(linearTab == null)			// 레이아웃 가져오기
//                linearTab = (LinearLayout)findViewById(R.id.linearTab);
//
//            if(linearTab != null)			// null이 아닐경우 초기화
//            {
//                View child = null;
//                linearTab.removeAllViews();
//                child = getLayoutInflater().inflate(R.layout.actionbar_room_tab, null);
//                actionBarRoomTabNotifyBtn = (ImageButton) findViewById(R.id.action_bar_room_notify_btn);
//                actionBarRoomTabAddBtn = (ImageButton) findViewById(R.id.action_bar_room_add_btn);
//                linearTab.addView(child);
//                linearTab.invalidate();
//            }
//            else								// null일경우 다시 0.01초후를 기약
//                new InitThread().start();	// 0.01초후 InitHandler로 메시지 전송
//
//
//            super.handleMessage(msg);
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        // layout (when the tab image button clicked, visibility change
        layoutStopwatch = (RelativeLayout) findViewById(R.id.tab_stopwatch_layout);
        layoutFriends = (RelativeLayout) findViewById(R.id.tab_friends_layout);
        layoutRooms = (RelativeLayout) findViewById(R.id.tab_rooms_layout);
        layoutSettings = (RelativeLayout) findViewById(R.id.tab_settings_layout);

        // tab image button
        tabStopwatch = (ImageButton) findViewById(R.id.imgBtn_tab_stopwatch);
        tabFriends = (ImageButton) findViewById(R.id.imgBtn_tab_friends);
        tabRooms = (ImageButton) findViewById(R.id.imgBtn_tab_rooms);
        tabSettings= (ImageButton) findViewById(R.id.imgBtn_tab_settings);

        // action bar
//        tabRoomCreateBtn = (Button) findViewById(R.id.tab_room_create_btn);
//        new InitThread().start();
//        if(linearTab == null)
//            linearTab = (LinearLayout)findViewById(R.id.linearTab);
//
//        if(linearTab != null) {
//            View child = null;
//
//            linearTab.addView(child);
//            linearTab.addView(actionBarRoomTabAddBtn);
//        }
        Log.i(LOG_TAG, "onCreate");
        mDBHelper = new DBHelper(this);

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
            roomList = (ListView) findViewById(R.id.room_list);
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
            friendList = (ListView) findViewById(R.id.friend_list);
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

        ArrayAdapter<String> optionArrayAdapter;
        optionArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.tab_list, arGeneral3);
        settingList = (ListView) findViewById(R.id.setting_list);
//        View header_setting = getLayoutInflater().inflate(R.layout.tab_header_setting, null, false);
//        settingList.addHeaderView(header_setting);
        settingList.setAdapter(optionArrayAdapter);

        // add list item onClickListener
        settingList.setOnItemClickListener(itemClickListener);

        // add listener
        tabStopwatch.setOnClickListener(tabListener);
        tabFriends.setOnClickListener(tabListener);
        tabRooms.setOnClickListener(tabListener);
        tabSettings.setOnClickListener(tabListener);

        // add create room listener
//        tabRoomCreateBtn.setOnClickListener(roomCreateListener);



        // Alarm OnCreate
        a = Preference.getBoolean(TabActivity.this, "testValue");
        Log.i(LOG_TAG, "a : "+ a);
        DBContactHelper helper = new DBContactHelper(this);

        if(!a) {
            Preference.putBoolean(TabActivity.this, "testValue", true);
            Contact contact = new Contact(0,00,00);
            helper.addContact(contact);
            Contact contact2 = new Contact(1,10,00);
            helper.addContact(contact2);
        }
        _text = (TextView) findViewById(R.id.tvMsg);
        _text2 = (TextView) findViewById(R.id.goal);
        Contact contact3=helper.getContact(2);
//        _text2.setText(
//                (contact3.gethour()/10==0 ? "0"+contact3.gethour() : contact3.gethour())
//                        +" : "+ (contact3.getminute()/10 == 0 ? "0" + contact3.getminute() : contact3.getminute() )+" : "+"00");


        final ToggleButton mtoggle = (ToggleButton) findViewById(R.id.toggleButton2);
        mtoggle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                if (mtoggle.isChecked()) {
                    Preference.putBoolean(TabActivity.this, "toggle",true);
                    if(timer == null) {

                        Date start = new Date();
                        Preference.putLong(TabActivity.this, "start", start.getTime()-Preference.getLong(TabActivity.this,"diff"));
                        TimerTask adTast = new TimerTask() {
                            public void run() {
                                mHandler.sendEmptyMessage(0);
                            }
                        };

                        timer = new Timer();
                        timer.schedule(adTast, 0, 1000); // 0초후 첫실행, 20초마다 계속실행
                        Log.i(LOG_TAG, "타이머 시작");
                    }
                } else {
                    Preference.putBoolean(TabActivity.this, "toggle",false);
                    if(timer != null) {
                        timer.cancel();
                        Log.i(LOG_TAG, "타이머 스탑");
                        timer = null;
                    }
                }

            }
        });

        Button go_to_alram = (Button) findViewById(R.id.button1);
        go_to_alram.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //@preference로 flag 설정
                Preference.putBoolean(TabActivity.this,"Mflag", true);


                //@preference를 불러와서 flag 확인후 set이 안되있으면 set으로 함

                Intent intent = new Intent(TabActivity.this, alram_list.class);
                //Intent intent = new Intent(MainActivity.this, Alarm_main.class);
                startActivity(intent);


            }
        });
        Button ring = (Button) findViewById(R.id.ringring);
        ring.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Preference.putLong(TabActivity.this, "diff",0);
                _text.setText("00:00:00");

                if(timer != null) {
                    timer.cancel();
                    Log.i(LOG_TAG, "타이머 스탑");
                    timer = null;
                }
                //Intent intent = new Intent(MainActivity.this, Klaxon.class);
                //Intent intent = new Intent(MainActivity.this, Alarm_main.class);
                //startActivity(intent);
            }
        });
    }
    private void setInvisibleBody(){
        layoutStopwatch.setVisibility(View.INVISIBLE);
        layoutFriends.setVisibility(View.INVISIBLE);
        layoutRooms.setVisibility(View.INVISIBLE);
        layoutSettings.setVisibility(View.INVISIBLE);
//        actionBarRoomTabNotifyBtn.setVisibility(View.INVISIBLE);
//        actionBarRoomTabAddBtn.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener roomCreateListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(TabActivity.this, CreateRoomActivity.class);
            startActivity(intent);
        }
    };

    // tab setOnClickListener
    View.OnClickListener tabListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.imgBtn_tab_stopwatch:
                    Log.i(LOG_TAG, "stopwatch tab");
                    setInvisibleBody();
                    layoutStopwatch.setVisibility(View.VISIBLE);
                    break;

                case R.id.imgBtn_tab_friends:
                    Log.i(LOG_TAG, "friends tab");
                    setInvisibleBody();
                    layoutFriends.setVisibility(View.VISIBLE);
                    break;

                case R.id.imgBtn_tab_rooms:
                    Log.i(LOG_TAG, "rooms tab");
                    setInvisibleBody();
//                    actionBarRoomTabNotifyBtn.setVisibility(View.VISIBLE);
//                    actionBarRoomTabAddBtn.setVisibility(View.VISIBLE);
                    layoutRooms.setVisibility(View.VISIBLE);
                    break;

                case R.id.imgBtn_tab_settings:
                    Log.i(LOG_TAG, "settings tab");
                    setInvisibleBody();
                    layoutSettings.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };




    /* click listener for setting tab */
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.friend_list) {
                Log.i(LOG_TAG,"tab2, friends Clicked");
                Log.i(LOG_TAG,"position : " + position);


            } else if (adapterView.getId() == R.id.room_list) {
                Log.i(LOG_TAG,"tab3, rooms Clicked");
                Log.i(LOG_TAG,"position : " + position);
                Intent intent = new Intent(TabActivity.this, StudyRoomActivity.class);
                if(KogPreference.DEBUG_MODE) {
                    intent.putExtra("roomID", position);
                }else{
git                     intent.putExtra("roomID", "-1");
                }
                startActivity(intent);



            } else if (adapterView.getId() == R.id.setting_list) {
                Log.i(LOG_TAG,"tab4, settings Clicked");
                Log.i(LOG_TAG,"position : " + position);
                switch (position) {
                    case 1: // 알람 설정
                        Log.i(LOG_TAG,"tab4, settings Clicked");
                        Intent intent_notice = new Intent(TabActivity.this, NoticeActivity.class);
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
    protected void onResume() {


        super.onResume();
        if(Preference.getString(TabActivity.this, "Resumetimer")=="")
            _text.setText("00:00:00");
        else
            _text.setText(Preference.getString(TabActivity.this,"Resumetimer"));


        DBContactHelper helper = new DBContactHelper(this);
        Contact contact3=helper.getContact(2);
        _text2.setText(
                (contact3.gethour()/10==0 ? "0"+contact3.gethour() : contact3.gethour())
                        +" : "+ (contact3.getminute()/10 == 0 ? "0" + contact3.getminute() : contact3.getminute() )+" : "+"00");



        final ToggleButton mtoggle = (ToggleButton) findViewById(R.id.toggleButton2);
        if ( Preference.getBoolean(TabActivity.this, "toggle")) {
            mtoggle.setChecked(true);

            if (timer == null) {
                Date start = new Date();
                Preference.putLong(TabActivity.this, "start", start.getTime() - Preference.getLong(TabActivity.this, "diff"));
                TimerTask adTast = new TimerTask() {
                    public void run() {
                        mHandler.sendEmptyMessage(0);
                    }
                };
                timer = new Timer();
                timer.schedule(adTast, 0, 1000); // 0초후 첫실행, 1초마다 계속실행
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.tab, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab, menu);
        return super.onCreateOptionsMenu(menu);
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

    ////////////////////
    // DB             //
//    ////////////////////
//    public void getFriendInfoFromSQLite() {
//
//        SQLiteDatabase db;
//        Cursor cursor;
//
//        db = mDBHelper.getReadableDatabase();
//
//        cursor = db.rawQuery("SELECT * FROM friendList_ph", null);
//
//        while (cursor.moveToNext()) {
//            String[] friendInfo = new String[3];
//            friendInfo[0] = cursor.getString(0); // id
//            friendInfo[1] = cursor.getString(1); // name
//            friendInfo[2] = cursor.getString(2); // phone number
//            friendNameArray.add(cursor.getString(1));
//            friendInfoArray.add(friendInfo);
//        }
//        cursor.close();
//        db.close();
//        mDBHelper.close();
//    }
//    public void setRoomIDAndNameArrayFromSQLite() {
//
//        SQLiteDatabase db;
//        Cursor cursor;
//
//        db = mDBHelper.getReadableDatabase();
//        cursor = db.rawQuery("SELECT * FROM roomlist_tbl", null);
//        while (cursor.moveToNext()) {
//            roomIDArrayFromSQLite.add(cursor.getString(0));
//            roomNameArray.add(cursor.getString(1));
//            //Log.i("RoomInfo MainMenu", cursor.getString(0) + "+" + cursor.getString(1) );
//        }
//        cursor.close();
//        db.close();
//        mDBHelper.close();
//    }



//    public class InitThread extends Thread
//    {
//        @Override
//        public void run()
//        {
//            try {
//                sleep(TabActivity.TAB_DELAY);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            initHandler.sendEmptyMessage(0);
//        }
//    };

    // alarm


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            //@preference flag가 set되어 있으면
            //@현재 시간-preference호출
            //@preference flag가 set되어 있지 않으면
            //@현재 시간을 preference로 저장
            _text.setText(timediff(TabActivity.this));
        }
    };

    private String timediff(Context context) {

        DBContactHelper helper = new DBContactHelper(context);
        Contact contact=helper.getContact(2);
        int id=contact.getID();
        int hour=contact.gethour();
        int min=contact.getminute();
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");


            Date Date1=new Date(Preference.getLong(TabActivity.this, "start"));
            Log.i(LOG_TAG, "DAte1 : "+ Date1.toString());
            Date today = new Date();
            Log.i(LOG_TAG, "today : "+ today.toString());
            mills = today.getTime()-Date1.getTime();
            Preference.putLong(TabActivity.this, "diff",mills);
            int Hours = (int) (mills/(1000 * 60 * 60));
            int Mins = (int) (mills/(1000*60)) % 60;
            int Seconds = (int) (mills/1000)%60;



            String diff =
                    (Hours/10==0 ? "0"+Hours:Hours)
                            + ":" + (Mins/10==0 ? "0"+Mins:Mins)+":"+(Seconds/10==0 ? "0"+Seconds:Seconds); // updated value every1 second

            Preference.putString(TabActivity.this,"Resumetimer",diff);

            return diff;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "error";
    }



    Timer timer;
}
