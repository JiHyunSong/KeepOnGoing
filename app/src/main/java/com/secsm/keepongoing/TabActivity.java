package com.secsm.keepongoing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Alarm.Contact;
import com.secsm.keepongoing.Alarm.DBContactHelper;
import com.secsm.keepongoing.Alarm.Preference;
import com.secsm.keepongoing.Alarm.alram_list;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Quiz.Quiz_Main;
import com.secsm.keepongoing.Quiz.Solve_Main;
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
    private TextView _text, _current_time_text, _current_time_text2;
    private TextView _text2;
    MenuInflater inflater;
    protected int i = 0, minute = 0, diff_hour, diff_min;
    boolean a = false;
    long mills = 0;

    private DBHelper mDBHelper;
    private ListView roomList, friendList, settingList;

    private ImageButton tabStopwatch, tabFriends, tabRooms, tabSettings;
    private RelativeLayout layoutStopwatch, layoutFriends, layoutRooms, layoutSettings;
    private MenuItem actionBarFirstBtn, actionBarSecondBtn;

    ArrayList<FriendNameAndIcon> mockFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.NAVIGATION_MODE_STANDARD);

        // layout (when the tab image button clicked, visibility change
        layoutStopwatch = (RelativeLayout) findViewById(R.id.tab_stopwatch_layout);
        layoutFriends = (RelativeLayout) findViewById(R.id.tab_friends_layout);
        layoutRooms = (RelativeLayout) findViewById(R.id.tab_rooms_layout);
        layoutSettings = (RelativeLayout) findViewById(R.id.tab_settings_layout);

        // tab image button
        tabStopwatch = (ImageButton) findViewById(R.id.imgBtn_tab_stopwatch);
        tabFriends = (ImageButton) findViewById(R.id.imgBtn_tab_friends);
        tabRooms = (ImageButton) findViewById(R.id.imgBtn_tab_rooms);
        tabSettings = (ImageButton) findViewById(R.id.imgBtn_tab_settings);

        Log.i(LOG_TAG, "onCreate");
        mDBHelper = new DBHelper(this);

        // setup rooms
        if (KogPreference.DEBUG_MODE) {
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
            mockFriends = new ArrayList<FriendNameAndIcon>();
//            ArrayList<String> mockFriends = new ArrayList<String>();
//            mockFriends.add(String profile_path, String name, String targetTime);
            mockFriends.add(new FriendNameAndIcon("", "temp1", "2011-08-22 12:09:36"));
            mockFriends.add(new FriendNameAndIcon("", "temp1", "2011-08-22 12:09:36"));
            mockFriends.add(new FriendNameAndIcon("", "temp1", "2011-08-22 12:09:36"));
            mockFriends.add(new FriendNameAndIcon("", "temp1", "2011-08-22 12:09:36"));

            FriendsArrayAdapters mockFriendArrayAdapter;
//            mockFriendArrayAdapter = new ArrayAdapter<String>(this,
//                    R.layout.tab_list, mockFriends);
            mockFriendArrayAdapter = new FriendsArrayAdapters(TabActivity.this, R.layout.friend_list_item, mockFriends);
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
        if (KogPreference.isLogin(TabActivity.this))
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


//@민수 시작

        // Alarm OnCreate
        a = Preference.getBoolean(TabActivity.this, "testValue");
        Log.i(LOG_TAG, "a : " + a);
        DBContactHelper helper = new DBContactHelper(this);

        if (!a) {
            Preference.putBoolean(TabActivity.this, "testValue", true);
            Contact contact = new Contact(0, 00, 00);
            helper.addContact(contact);
            Contact contact2 = new Contact(1, 10, 00);
            helper.addContact(contact2);


        }
        _text = (TextView) findViewById(R.id.tvMsg);
        _text2 = (TextView) findViewById(R.id.goal);


        //@민수 삽입
        Button btnhi = (Button) findViewById(R.id.button2);
        btnhi.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //@preference로 flag 설정
                Intent intent = new Intent(TabActivity.this, Quiz_Main.class);
                startActivity(intent);
            }
        });
        Button btnbye = (Button) findViewById(R.id.button3);
        btnbye.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //@preference로 flag 설정
                Intent intent = new Intent(TabActivity.this, Solve_Main.class);
                startActivity(intent);
            }
        });


        _current_time_text = (TextView) findViewById(R.id.currenttime);
        _current_time_text2 = (TextView) findViewById(R.id.currenttime2);
        TimerTask adTast2 = new TimerTask() {
            public void run() {
                replace_current_time_Handler.sendEmptyMessage(0);
            }
        };

        Timer timer2 = new Timer();
        timer2.schedule(adTast2, 0, 1000); // 0초후 첫실행, 20초마다 계속실행


        Contact contact3 = helper.getContact(2);
        _text2.setText(
                (contact3.gethour() / 10 == 0 ? "0" + contact3.gethour() : contact3.gethour())
                        + ":" + (contact3.getminute() / 10 == 0 ? "0" + contact3.getminute() : contact3.getminute()) + ":" + "00"
        );


        final ToggleButton mtoggle = (ToggleButton) findViewById(R.id.toggleButton2);
        mtoggle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                if (mtoggle.isChecked()) {
                    Preference.putBoolean(TabActivity.this, "toggle", true);
                    mtoggle.setBackgroundResource(R.drawable.selector_tab_pause_button);
                    if (timer == null) {

                        Date start = new Date();
                        Preference.setLong(TabActivity.this, "start", start.getTime() - Preference.getLong(TabActivity.this, "diff"));
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

                    mtoggle.setBackgroundResource(R.drawable.selector_tab_play_button);
                    Preference.putBoolean(TabActivity.this, "toggle", false);
                    if (timer != null) {
                        timer.cancel();
                        Log.i(LOG_TAG, "타이머 스탑");
                        timer = null;
                    }
                }

            }
        });

//        Button go_to_alram = (Button) findViewById(R.id.button1);
//        go_to_alram.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                //@preference로 flag 설정
//                Preference.putBoolean(TabActivity.this,"Mflag", true);
//
//
//                //@preference를 불러와서 flag 확인후 set이 안되있으면 set으로 함
//
//                Intent intent = new Intent(TabActivity.this, alram_list.class);
//                //Intent intent = new Intent(MainActivity.this, Alarm_main.class);
//                startActivity(intent);
//
//
//            }
//        });
        //@민수 타이머 완료
        Button ring = (Button) findViewById(R.id.ringring);
        ring.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Preference.setLong(TabActivity.this, "diff", 0);
                _text.setText("00:00:00");
                mtoggle.setChecked(false);
                mtoggle.setBackgroundResource(R.drawable.selector_tab_play_button);
                Preference.putBoolean(TabActivity.this, "toggle", false);

                if (timer != null) {
                    timer.cancel();
                    Log.i(LOG_TAG, "타이머 완료");
                    timer = null;
                }
                //Intent intent = new Intent(MainActivity.this, Klaxon.class);
                //Intent intent = new Intent(MainActivity.this, Alarm_main.class);
                //startActivity(intent);
            }
        });
    }

    private void setInvisibleBody() {
        layoutStopwatch.setVisibility(View.INVISIBLE);
        layoutFriends.setVisibility(View.INVISIBLE);
        layoutRooms.setVisibility(View.INVISIBLE);
        layoutSettings.setVisibility(View.INVISIBLE);
        actionBarFirstBtn.setVisible(false);
        actionBarSecondBtn.setVisible(false);
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
            switch (v.getId()) {
                case R.id.imgBtn_tab_stopwatch:
                    Log.i(LOG_TAG, "stopwatch tab");
                    setInvisibleBody();
                    layoutStopwatch.setVisibility(View.VISIBLE);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_settings);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_stopwatchTab_settings_listener);
                    break;

                case R.id.imgBtn_tab_friends:
                    Log.i(LOG_TAG, "friends tab");
                    setInvisibleBody();
                    layoutFriends.setVisibility(View.VISIBLE);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_add_person);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_friends_add_listener);
                    break;

                case R.id.imgBtn_tab_rooms:
                    Log.i(LOG_TAG, "rooms tab");
                    setInvisibleBody();
//                    actionBarRoomTabNotifyBtn.setVisibility(View.VISIBLE);
//                    actionBarRoomTabAddBtn.setVisibility(View.VISIBLE);
                    layoutRooms.setVisibility(View.VISIBLE);
                    actionBarFirstBtn.setIcon(R.drawable.ic_action_web_site);
                    actionBarFirstBtn.setVisible(true);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                    actionBarSecondBtn.setVisible(true);
                    actionBarFirstBtn.setOnMenuItemClickListener(ab_rooms_notify_listener);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_rooms_add_listener);
                    break;

                case R.id.imgBtn_tab_settings:
                    Log.i(LOG_TAG, "settings tab");
                    setInvisibleBody();
                    layoutSettings.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    ListView.OnItemLongClickListener itemLongClickListener = new ListView.OnItemLongClickListener() {
        int selectedPosition = 0;

        public boolean onItemLongClick(AdapterView<?> adapterView, View v,
                                       int pos, long arg3) {

            return false;
        }
    };

    /* click listener for setting tab */
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.friend_list) {
                Log.i(LOG_TAG, "tab2, friends Clicked");
                Log.i(LOG_TAG, "position : " + position);

                mDialog = createInflaterDialog("", mockFriends.get(position - 1).getName(), mockFriends.get(position - 1).getTargetTime());
                mDialog.show();


            } else if (adapterView.getId() == R.id.room_list) {
                Log.i(LOG_TAG, "tab3, rooms Clicked");
                Log.i(LOG_TAG, "position : " + position);
                Intent intent = new Intent(TabActivity.this, StudyRoomActivity.class);
                if (KogPreference.DEBUG_MODE) {
                    //TODO : change rid, not position value
                    KogPreference.setRid(TabActivity.this, "" + position);
                    intent.putExtra("roomID", position);
                } else {
                    KogPreference.setRid(TabActivity.this, "" + (-1));
                    intent.putExtra("roomID", -1);
                }
                startActivity(intent);
//                TabActivity.this.finish();


            } else if (adapterView.getId() == R.id.setting_list) {
                Log.i(LOG_TAG, "tab4, settings Clicked");
                Log.i(LOG_TAG, "position : " + position);
                switch (position) {
                    case 1: // 알람 설정
                        Log.i(LOG_TAG, "tab4, settings Clicked");
                        Intent intent_notice = new Intent(TabActivity.this, NoticeActivity.class);
                        startActivity(intent_notice);
                        break;
                    case 2: // 목표 시간 설정
                        Log.i(LOG_TAG, "tab4, settings Clicked");

                        break;
                    case 3: // 퀴즈 모음
                        break;
                    case 4: // 로그인
                        break;
                }
            }
        }
    };

    /**
     * Infalter 다이얼로그
     *
     * @return ab
     */
    private AlertDialog mDialog = null;
    private ImageView info_iconFriend = null;
    private TextView info_txtFriendNickname = null;
    private TextView info_txtTargetTime = null;

    private AlertDialog createInflaterDialog(String profileUrl, String name, String targetTime) {
        final View innerView = getLayoutInflater().inflate(R.layout.friend_info_dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        info_iconFriend = (ImageView) innerView.findViewById(R.id.info_iconFriend);
        info_txtFriendNickname = (TextView) innerView.findViewById(R.id.info_txtFriendNickname);
        info_txtTargetTime = (TextView) innerView.findViewById(R.id.info_txtTargetTime);

        info_iconFriend.setBackgroundResource(R.drawable.ic_action_add_group);
        info_txtFriendNickname.setText(name);
        info_txtTargetTime.setText(targetTime);
        ab.setTitle("친구정보");
        ab.setView(innerView);

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);
            }
        });

        return ab.create();
    }


    /**
     * 다이얼로그 종료
     *
     * @param dialog
     */
    private void setDismiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onResume() {


        super.onResume();
        if (Preference.getString(TabActivity.this, "Resumetimer") == "")
            _text.setText("00:00:00");
        else
            _text.setText(Preference.getString(TabActivity.this, "Resumetimer"));


        DBContactHelper helper = new DBContactHelper(this);
        Contact contact3 = helper.getContact(2);
        _text2.setText(
                (contact3.gethour() / 10 == 0 ? "0" + contact3.gethour() : contact3.gethour())
                        + ":" + (contact3.getminute() / 10 == 0 ? "0" + contact3.getminute() : contact3.getminute()) + ":" + "00"
        );


        final ToggleButton mtoggle = (ToggleButton) findViewById(R.id.toggleButton2);
        mtoggle.setBackgroundResource(R.drawable.selector_tab_play_button);
        if (Preference.getBoolean(TabActivity.this, "toggle")) {
            mtoggle.setChecked(true);
            mtoggle.setBackgroundResource(R.drawable.selector_tab_pause_button);

            if (timer == null) {
                Date start = new Date();
                Preference.setLong(TabActivity.this, "start", start.getTime() - Preference.getLong(TabActivity.this, "diff"));
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

        inflater = getMenuInflater();
        inflater.inflate(R.menu.tab, menu);
        actionBarFirstBtn = menu.findItem(R.id.actionBarFirstBtn);
        actionBarSecondBtn = menu.findItem(R.id.actionBarSecondBtn);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.actionBarFirstBtn) {

            return true;
        }
        if (id == R.id.actionBarSecondBtn) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ////////////////////
    // Action bar     //
    ////////////////////

    MenuItem.OnMenuItemClickListener ab_stopwatchTab_settings_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_stopwatchTab_settings_listener");
            //@preference로 flag 설정
            Preference.putBoolean(TabActivity.this, "Mflag", true);


            //@preference를 불러와서 flag 확인후 set이 안되있으면 set으로 함

            Intent intent = new Intent(TabActivity.this, alram_list.class);
            //Intent intent = new Intent(MainActivity.this, Alarm_main.class);
            startActivity(intent);


            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_friends_add_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_friends_add_listener");

            Intent intent = new Intent(TabActivity.this, AddFriendActivity.class);
            startActivity(intent);
//            TabActivity.this.finish();


            return true;
        }
    };
    MenuItem.OnMenuItemClickListener ab_rooms_notify_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_rooms_notify_listener");
            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_rooms_add_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_rooms_add_listener");

            Intent intent = new Intent(TabActivity.this, AddStudyRoomActivity.class);
            startActivity(intent);

            return true;
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            _text.setText(timediff(TabActivity.this));
        }
    };
    Handler replace_current_time_Handler = new Handler() {

        public void handleMessage(Message msg) {
            Date today = new Date();
            _current_time_text.setText((today.getYear() + 1900) + "/" + (today.getMonth() + 1) + "/" + today.getDate());
            _current_time_text2.setText(
                    (today.getHours() / 10 == 0 ? "0" + today.getHours() : today.getHours())
                            + ":" + (today.getMinutes() / 10 == 0 ? "0" + today.getMinutes() : today.getMinutes())
                            + ":" + (today.getSeconds() / 10 == 0 ? "0" + today.getSeconds() : today.getSeconds())
            );
        }
    };

    private String timediff(Context context) {

        DBContactHelper helper = new DBContactHelper(context);
        Contact contact = helper.getContact(2);
        int id = contact.getID();
        int hour = contact.gethour();
        int min = contact.getminute();
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");


            Date Date1 = new Date(Preference.getLong(TabActivity.this, "start"));
            ///    Log.i(LOG_TAG, "DAte1 : "+ Date1.toString());
            Date today = new Date();
            //    Log.i(LOG_TAG, "today : "+ today.toString());
            mills = today.getTime() - Date1.getTime();
            Preference.setLong(TabActivity.this, "diff", mills);
            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;
            int Seconds = (int) (mills / 1000) % 60;


            String diff =
                    (Hours / 10 == 0 ? "0" + Hours : Hours)
                            + ":" + (Mins / 10 == 0 ? "0" + Mins : Mins) + ":" + (Seconds / 10 == 0 ? "0" + Seconds : Seconds); // updated value every1 second

            Preference.setString(TabActivity.this, "Resumetimer", diff);

            return diff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    Timer timer;
}
