package com.secsm.keepongoing;

import android.app.ActionBar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Adapters.RoomNaming;
import com.secsm.keepongoing.Adapters.RoomsArrayAdapters;
import com.secsm.keepongoing.Alarm.Contact;
import com.secsm.keepongoing.Alarm.DBContactHelper;
import com.secsm.keepongoing.Alarm.Preference;
import com.secsm.keepongoing.Alarm.alram_list;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Quiz.Quiz_Set_Search;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TabActivity extends BaseActivity {
    private String rMessage;


    final static int TAB_DELAY = 10;

    public static final int ROOMNAME_REQUEST_CODE = 1;
    public static final int FRIENDNAME_REQUEST_CODE = 2;
    public static final int CHATROOM_REQUEST_CODE = 3;
    public static final int FRIENDS_REQUEST_CODE = 4;
    private static final String LOG_TAG = "MainmenuActivity";
    private final int ADDROOM = 100;
    private final int MANAGEFRIENDS = 200;
    private TextView ahcieve, _current_time_text, _current_time_text2,_goal_time;
    private TextView _text2;
    MenuInflater inflater;
    protected int i = 0, minute = 0, diff_hour, diff_min;
    boolean a = false;
    long mills = 0;
    private RequestQueue vQueue;
    private int status_code;

    private DBHelper mDBHelper;
    private ListView roomList, friendList, settingList;

    private ImageButton tabStopwatch, tabFriends, tabRooms, tabSettings;
    private RelativeLayout layoutStopwatch, layoutFriends, layoutRooms, layoutSettings;
    private LinearLayout llStopwatch, llFriends, llRooms, llSettings;
    private MenuItem actionBarFirstBtn, actionBarSecondBtn;

    private ProgressBar tab_progress;
    ArrayList<FriendNameAndIcon> mFriends;
    ArrayList<RoomNaming> mRooms;
    ArrayList<String> arGeneral3;


    private void setAllEnable() {
        tab_progress.setVisibility(View.GONE);
        ahcieve.setEnabled(true);
        _current_time_text.setEnabled(true);
        _current_time_text2.setEnabled(true);
        _goal_time.setEnabled(true);
        _text2.setEnabled(true);
        roomList.setEnabled(true);
        friendList.setEnabled(true);
        settingList.setEnabled(true);
        tabStopwatch.setEnabled(true);
        tabFriends.setEnabled(true);
        tabRooms.setEnabled(true);
        tabSettings.setEnabled(true);
    }

    private void setAllDisable() {
        tab_progress.setVisibility(View.VISIBLE);
        ahcieve.setEnabled(false);
        _current_time_text.setEnabled(false);
        _current_time_text2.setEnabled(false);
        _goal_time.setEnabled(false);
        _text2.setEnabled(false);
        roomList.setEnabled(false);
        friendList.setEnabled(false);
        settingList.setEnabled(false);
        tabStopwatch.setEnabled(false);
        tabFriends.setEnabled(false);
        tabRooms.setEnabled(false);
        tabSettings.setEnabled(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

//        MyVolley.init(TabActivity.this);
//        vQueue = Volley.newRequestQueue(this);
        vQueue = MyVolley.getRequestQueue(TabActivity.this);

        tab_progress = (ProgressBar)findViewById(R.id.tab_progress);

        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO | ActionBar.NAVIGATION_MODE_STANDARD);


//        getFriendsRequest();
//
//        getStudyRoomsRequest();

        // layout (when the tab image button clicked, visibility change
        layoutStopwatch = (RelativeLayout) findViewById(R.id.tab_stopwatch_layout);
        layoutFriends = (RelativeLayout) findViewById(R.id.tab_friends_layout);
        layoutRooms = (RelativeLayout) findViewById(R.id.tab_rooms_layout);
        layoutSettings = (RelativeLayout) findViewById(R.id.tab_settings_layout);

        // tab layouts
        llStopwatch = (LinearLayout) findViewById(R.id.tab_stopwatch_ll);
        llFriends = (LinearLayout) findViewById(R.id.tab_friends_ll);
        llRooms = (LinearLayout) findViewById(R.id.tab_rooms_ll);
        llSettings = (LinearLayout) findViewById(R.id.tab_settings_ll);

        // tab image button
        tabStopwatch = (ImageButton) findViewById(R.id.imgBtn_tab_stopwatch);
        tabFriends = (ImageButton) findViewById(R.id.imgBtn_tab_friends);
        tabRooms = (ImageButton) findViewById(R.id.imgBtn_tab_rooms);
        tabSettings = (ImageButton) findViewById(R.id.imgBtn_tab_settings);

        Log.i(LOG_TAG, "onCreate");
        Log.i(LOG_TAG, "onCreate nickname : " + KogPreference.getNickName(TabActivity.this));
        Log.i(LOG_TAG, "onCreate rid : " + KogPreference.getRid(TabActivity.this));
        Log.i(LOG_TAG, "onCreate regid : " + KogPreference.getRegId(TabActivity.this));

        mDBHelper = new DBHelper(this);

        roomList = (ListView) findViewById(R.id.room_list);
        friendList = (ListView) findViewById(R.id.friend_list);

        // add list item onClickListener
        roomList.setOnItemClickListener(itemClickListener);
        friendList.setOnItemClickListener(itemClickListener);

        roomList.setOnItemLongClickListener(itemLongClickListener);

        // setup tab_settings
        arGeneral3 = new ArrayList<String>();
        arGeneral3.add("내 프로필");
        arGeneral3.add("알람 / 목표시간 설정");
        arGeneral3.add("퀴즈 모음");
        if (!KogPreference.isLogin(TabActivity.this)) {
            arGeneral3.add("로그인");
        }else{
            arGeneral3.add("로그아웃");
        }
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
        ahcieve = (TextView) findViewById(R.id.tvMsg);
        _text2 = (TextView) findViewById(R.id.goal);
        ahcieve.setText("00:00:00");

       //@민수 삽입



        _current_time_text = (TextView) findViewById(R.id.currenttime);
        _current_time_text2 = (TextView) findViewById(R.id.currenttime2);
        _goal_time = (TextView) findViewById(R.id.goal);


        //@민수 테스트


        //@민수 타이머 선언
        TimerTask adTast2 = new TimerTask() {
            public void run() {
                replace_current_time_Handler.sendEmptyMessage(0);
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(adTast2, 0, 1000); // 0초후 첫실행, 1초마다 계속실행


        Contact contact3 = helper.getContact(2);
        _text2.setText(
                (contact3.gethour() / 10 == 0 ? "0" + contact3.gethour() : contact3.gethour())
                        + ":" + (contact3.getminute() / 10 == 0 ? "0" + contact3.getminute() : contact3.getminute()) + ":" + "00"
        );
        Preference.setString(TabActivity.this, "goal_time", _text2.getText().toString());


        final ToggleButton play_pause = (ToggleButton) findViewById(R.id.toggleButton2);
        play_pause.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                if (play_pause.isChecked()) {

                    Preference.putBoolean(TabActivity.this, "toggle", true);

                    play_pause.setBackgroundResource(R.drawable.selector_tab_pause_button);
                    if (timer == null) {

                        Date start = new Date();
                        if(Preference.getBoolean(TabActivity.this, "first_start")==false) {
                            Preference.putBoolean(TabActivity.this, "first_start", true);
                            Preference.setString(TabActivity.this, "start_date",(start.getYear() + 1900) + "/" + (start.getMonth() + 1) + "/" + start.getDate());
                        }
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

                    play_pause.setBackgroundResource(R.drawable.selector_tab_play_button);
                    Preference.putBoolean(TabActivity.this, "toggle", false);
                    if (timer != null) {
                        timer.cancel();
                        Log.i(LOG_TAG, "타이머 스탑");
                        timer = null;
                    }
                }

            }
        });

        //@민수 타이머 완료
        Button finish_btn = (Button) findViewById(R.id.finish);

        finish_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                play_pause.setChecked(false);
                play_pause.setBackgroundResource(R.drawable.selector_tab_play_button);
               Preference.setString(TabActivity.this, "Resumetimer", "");
                Preference.putBoolean(TabActivity.this, "toggle", false);
                if (timer != null) {
                    timer.cancel();
                    Log.i(LOG_TAG, "타이머 완료");
                    timer = null;
                }



                Preference.setLong(TabActivity.this, "diff", 0);
       /*         Toast.makeText(TabActivity.this,
                        "@SERVER : \n"+start.toString()+"|달성시간"+_text.getText(),2).show();*/
                try {
                    if (Preference.getBoolean(TabActivity.this, "first_start") == true) {
                        acheivetimeRegisterRequest(_goal_time.getText().toString(), ahcieve.getText().toString(), Preference.getString(TabActivity.this, "start_date"));
                        Preference.putBoolean(TabActivity.this, "first_start", false);
                        ahcieve.setText("00:00:00");
                        Preference.setLong(TabActivity.this, "diff",0);
                    }

                }catch (Exception e)
                {

                }

            }


        });

        if(KogPreference.DEBUG_MODE && KogPreference.NO_AUTH)
        {
            mFriends = new ArrayList<FriendNameAndIcon>();
            for(int i=0; i < 3; i++)
            {
                mFriends.add(new FriendNameAndIcon( "default.png", "nickname" + i , null));
            }

            FriendsArrayAdapters mockFriendArrayAdapter;
            mockFriendArrayAdapter = new FriendsArrayAdapters(TabActivity.this, R.layout.friend_list_item, mFriends);
            friendList.setAdapter(mockFriendArrayAdapter);

            mRooms = new ArrayList<RoomNaming>();
            for(int i=0; i< 3; i++) {
                mRooms.add(new RoomNaming("subjectroom", "2" + i, "meet 6days", "KOG STUDY", "2", null, null, null, null, null));
            }

            RoomsArrayAdapters roomsArrayAdapter;
            roomsArrayAdapter = new RoomsArrayAdapters(TabActivity.this, R.layout.room_list_item, mRooms);
            roomList.setAdapter(roomsArrayAdapter);

        }

    }


    //@민수 통신
    private void acheivetimeRegisterRequest(String target_time,String accomplished_time,String date) {
        target_time = target_time.trim().replace(" ", "%20");
        accomplished_time = accomplished_time.trim().replace(" ", "%20");
        date = date.trim().replace(" ", "%20");

        final String temp_target_time=target_time;
        final String temp_accomplished_time=accomplished_time;
        final String temp_date=date;
        String get_url = KogPreference.REST_URL +
                "Time"; // +
//                "?nickname=" + KogPreference.getNickName(TabActivity.this) +
//                "&target_time=" + target_time +
//                "&accomplished_time=" + accomplished_time+
//                "&date=" + date;

        JSONObject sendBody = new JSONObject();
        try{
            sendBody.put("nickname", KogPreference.getNickName(TabActivity.this));
            sendBody.put("target_time", target_time);
            sendBody.put("accomplished_time", accomplished_time);
            sendBody.put("date", date);
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, " sendBody e : " + e.toString());
        }



//http://210.118.74.195:8080/KOG_Server_Rest/rest/Time?nickname=jins&target_time=10:00:00&accomplished_time=00:00:00&date=2014/8/25

        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, sendBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "POST JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                rMessage = response.getString("message");
                                // real action
                               // GoNextPage();
                                     Toast.makeText(getBaseContext(),"시간등록 완료"+temp_accomplished_time, Toast.LENGTH_SHORT).show();
                            } else if (status_code == 9001) {

                                    Toast.makeText(getBaseContext(), "시간등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                            else if(status_code ==1001) {
                                acheivetimeputRequest(temp_target_time, temp_accomplished_time, temp_date);
                            }
                            else {
                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, "Response Error");
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        vQueue.add(jsObjRequest);

    }
    //@통신
    private void acheivetimeputRequest(String target_time,String accomplished_time,String date) {
        final String temp_accomplished_time=accomplished_time;
        String get_url = KogPreference.REST_URL +
                "Time"; // +
//                "?nickname=" + KogPreference.getNickName(TabActivity.this) +
//                "&target_time=" + target_time +
//                "&accomplished_time=" + accomplished_time+
//                "&date=" + date;

        JSONObject sendBody = new JSONObject();
        try{
            sendBody.put("nickname", KogPreference.getNickName(TabActivity.this));
            sendBody.put("target_time", target_time);
            sendBody.put("accomplished_time", accomplished_time);
            sendBody.put("date", date);
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, " sendBody e : " + e.toString());
        }


        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, get_url, sendBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                rMessage = response.getString("message");
                                // real action
                                // GoNextPage();
                                Toast.makeText(getBaseContext(),"시간등록 완료"+temp_accomplished_time, Toast.LENGTH_SHORT).show();
                            //    Toast.makeText(getBaseContext(), LOG_TAG +rMessage, Toast.LENGTH_SHORT).show();
                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "시간등록이 불가능합니다. PUT", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, "Response Error");
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        vQueue.add(jsObjRequest);
    }








    private void setInvisibleBody() {
        layoutStopwatch.setVisibility(View.INVISIBLE);
        layoutFriends.setVisibility(View.INVISIBLE);
        layoutRooms.setVisibility(View.INVISIBLE);
        layoutSettings.setVisibility(View.INVISIBLE);
        actionBarFirstBtn.setVisible(false);
        actionBarSecondBtn.setVisible(false);
        llStopwatch.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        llFriends.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        llRooms.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        llSettings.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        tabStopwatch.setBackgroundResource(R.drawable.tab_stopwatch_icon);
        tabFriends.setBackgroundResource(R.drawable.tab_friends_icon);
        tabRooms.setBackgroundResource(R.drawable.tab_chatroom_icon);
        tabSettings.setBackgroundResource(R.drawable.tab_option_icon);
//        actionBarRoomTabNotifyBtn.setVisibility(View.INVISIBLE);
//        actionBarRoomTabAddBtn.setVisibility(View.INVISIBLE);
    }

    // tab setOnClickListener
    View.OnClickListener tabListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBtn_tab_stopwatch:
//                    Log.i(LOG_TAG, "stopwatch tab");
                    setInvisibleBody();
                    layoutStopwatch.setVisibility(View.VISIBLE);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_stopwatchTab_settings_listener);
                    llStopwatch.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabStopwatch.setBackgroundResource(R.drawable.tab_stopwatch_icon_p);
                    break;

                case R.id.imgBtn_tab_friends:
//                    Log.i(LOG_TAG, "friends tab");
                    setInvisibleBody();
                    layoutFriends.setVisibility(View.VISIBLE);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_add_person);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_friends_add_listener);

                    llFriends.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabFriends.setBackgroundResource(R.drawable.tab_friends_icon_p);

                    break;

                case R.id.imgBtn_tab_rooms:
//                    Log.i(LOG_TAG, "rooms tab");
                    setInvisibleBody();
//                    actionBarRoomTabNotifyBtn.setVisibility(View.VISIBLE);
//                    actionBarRoomTabAddBtn.setVisibility(View.VISIBLE);
                    layoutRooms.setVisibility(View.VISIBLE);
//                    actionBarFirstBtn.setIcon(R.drawable.ic_action_web_site);
//                    actionBarFirstBtn.setVisible(true);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                    actionBarSecondBtn.setVisible(true);
//                    actionBarFirstBtn.setOnMenuItemClickListener(ab_rooms_notify_listener);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_rooms_add_listener);
                    llRooms.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabRooms.setBackgroundResource(R.drawable.tab_chatroom_icon_p);
                    break;

                case R.id.imgBtn_tab_settings:
//                    Log.i(LOG_TAG, "settings tab");
                    setInvisibleBody();
                    layoutSettings.setVisibility(View.VISIBLE);
                    llSettings.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabSettings.setBackgroundResource(R.drawable.tab_option_icon_p);
                    break;
            }
        }
    };


    void getImageFromURL(String img_name, ImageView imgView) {

        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL+img_name;
        // TODO R.drawable.error_image
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.no_image,
                        R.drawable.no_image)
        );
    }

    ListView.OnItemLongClickListener itemLongClickListener = new ListView.OnItemLongClickListener() {
        int selectedPosition = 0;

        public boolean onItemLongClick(AdapterView<?> adapterView, View v,
                                       int pos, long arg3) {
            if(adapterView.getId() == R.id.friend_list)
            {

            }else if(adapterView.getId() == R.id.room_list)
            {
                Log.i(LOG_TAG, "tab3, rooms long Clicked");
                mDialog = outRoomDialog(mRooms.get(pos).getRoomname(), mRooms.get(pos).getRid());
                mDialog.show();
            }

            return false;
        }
    };
    private AlertDialog mOutRoomDialog;
    TextView simple_dialog_text;

    private AlertDialog outRoomDialog(final String room_name, final String room_id) {
        final View innerView = getLayoutInflater().inflate(R.layout.simple_dialog_layout, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        simple_dialog_text = (TextView) innerView.findViewById(R.id.simple_dialog_text);

//        info_iconFriend.setBackgroundResource(R.drawable.ic_action_add_group);
        simple_dialog_text.setText(room_name + "를 나오시겠습니까?");
        ab.setTitle("방 나가기");
        ab.setView(innerView);

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setAllDisable();
                outRoomRequest(room_id);
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

    /* click listener for setting tab */
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.friend_list) {
                Log.i(LOG_TAG, "tab2, friends Clicked");
                mDialog = createInflaterDialog(mFriends.get(position).getProfile_path(), mFriends.get(position).getName(), mFriends.get(position).getTargetTime());
                mDialog.show();


            } else if (adapterView.getId() == R.id.room_list) {
                Log.i(LOG_TAG, "tab3, rooms Clicked");
                Log.i(LOG_TAG, "position : " + position);
                Intent intent = new Intent(TabActivity.this, StudyRoomActivity.class);
                intent.putExtra("type", mRooms.get(position).getType());
                intent.putExtra("rule", mRooms.get(position).getRule());
                KogPreference.setRid(TabActivity.this, mRooms.get(position).getRid());
                KogPreference.setQuizNum(TabActivity.this, mRooms.get(position).getQuiz_num());
                Log.i(LOG_TAG, "RID (mRooms.get(position).getRid()): " + mRooms.get(position).getRid());
                Log.i(LOG_TAG, "RID (KogPreference.getRid(TabActivity.this)): " + KogPreference.getRid(TabActivity.this));

                startActivity(intent);
//                TabActivity.this.finish();


            } else if (adapterView.getId() == R.id.setting_list) {
                Log.i(LOG_TAG, "tab4, settings Clicked");
                Log.i(LOG_TAG, "position : " + position);
                switch (position) {
                    case 0: // 내 프로필
                        Log.i(LOG_TAG, "tab4, settings Clicked");
                        Intent intent_my_profile = new Intent(TabActivity.this, MyProfileActivity.class);
                        startActivity(intent_my_profile);

                        break;
                    case 1: // 알람 / 목표시간 설정
                        Log.i(LOG_TAG, "tab4, settings Clicked");
                        Intent intent_alarm = new Intent(TabActivity.this, alram_list.class);
                        startActivity(intent_alarm);
//                        Intent intent_alarm = new Intent(TabActivity.this, NoticeActivity.class);
//                        startActivity(intent_notice);
                        break;
//                    case 2: // 목표 시간 설정
//                        Log.i(LOG_TAG, "tab4, settings Clicked");
//
//                        break;
                    case 2: // 퀴즈 모음
                        Intent intent = new Intent(TabActivity.this, Quiz_Set_Search.class);
                        startActivity(intent);
                        //@민수 ㅋㅋ
                        break;

                    case 3:
                        if(arGeneral3.get(position).toString().equals("로그아웃"))
                        {
                            Log.i(LOG_TAG, "tab4, 로그아웃");
                            logout();
                        }
                        break;
                }
            }
        }
    };

    private void logout() {
        logoutRequest(KogPreference.getNickName(this));
        KogPreference.setLogin(this, false);
        KogPreference.setNickName(this, "");
        KogPreference.setPassword(this, "");
        KogPreference.setRid(this, "");
//        KogPreference.setRegId(this, "");
        KogPreference.setQuizNum(this, "");
        KogPreference.setAutoLogin(this, false);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

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

//        info_iconFriend.setBackgroundResource(R.drawable.ic_action_add_group);
        Log.i(LOG_TAG, "profileUrl : " + profileUrl);
        getImageFromURL(profileUrl, info_iconFriend);

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
            ahcieve.setText("00:00:00");
        else
            ahcieve.setText(Preference.getString(TabActivity.this, "Resumetimer"));


        if(!KogPreference.NO_AUTH) {
            getFriendsRequest();
            getStudyRoomsRequest();
        }

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
        actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
        actionBarSecondBtn.setVisible(true);
        actionBarSecondBtn.setOnMenuItemClickListener(ab_stopwatchTab_settings_listener);
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
//            TabActivity.this.();


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
            ahcieve.setText(timediff(TabActivity.this));
            Preference.setString(TabActivity.this, "achieve_time", ahcieve.getText().toString());
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
//@민수
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

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }
    //////////////////////////////////
    // REST API //
    //////////////////////////////////

    private void getFriendsRequest() {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Friend" +
                "?nickname=" + KogPreference.getNickName(TabActivity.this) +
                "&date=" + getRealDate();

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                JSONArray rMessage;
                                rMessage = response.getJSONArray("message");
                                //////// real action ////////
                                mFriends = new ArrayList<FriendNameAndIcon>();
                                JSONObject rObj;

                                //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
                                for(int i=0; i< rMessage.length(); i++)
                                {
                                    rObj = rMessage.getJSONObject(i);
                                    if (!"null".equals(rObj.getString("nickname"))) {
                                        Log.i(LOG_TAG, "add Friends : " + Encrypt.encodeIfNeed(rObj.getString("image")) + "|" +
                                                Encrypt.encodeIfNeed(rObj.getString("nickname")) + "|" + Encrypt.encodeIfNeed(rObj.getString("targetTime")));


                                        mFriends.add(new FriendNameAndIcon(
//                                                URLDecoder.decode(rObj.getString("type"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("image"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("targetTime"), "UTF-8")));
                                    }
                                }

                                FriendsArrayAdapters mockFriendArrayAdapter;
                                mockFriendArrayAdapter = new FriendsArrayAdapters(TabActivity.this, R.layout.friend_list_item, mFriends);
                                friendList.setAdapter(mockFriendArrayAdapter);


                                /////////////////////////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
//                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }


    private void getStudyRoomsRequest() {
        String get_url = KogPreference.REST_URL +
                "Rooms" +
                "?nickname=" + KogPreference.getNickName(TabActivity.this);

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        //{"message":[
                        // {"startTime":null,"roomname":"KOF STUDY","rid":"23","meetDays":null,"rule":"meet 6days","maxHolidayCount":"2","showupTime":null,"type":"liferoom","durationTime":null},
                        // {"startTime":null,"roomname":"liferoom","rid":"29","meetDays":null,"rule":"tt","maxHolidayCount":"1","showupTime":null,"type":"liferoom","durationTime":null},
                        // {"startTime":null,"roomname":"lif","rid":"30","meetDays":null,"rule":"134","maxHolidayCount":"1","showupTime":null,"type":"liferoom","durationTime":null},
                        // {"startTime":null,"roomname":"??","rid":"31","meetDays":null,"rule":"???","maxHolidayCount":"1","showupTime":null,"type":"liferoom","durationTime":null},
                        // {"startTime":null,"roomname":"??2?","rid":"34","meetDays":null,"rule":"??","maxHolidayCount":"5","showupTime":null,"type":"liferoom","durationTime":null},
                        // {"startTime":"04:26:09","roomname":"tesmpSub","rid":"33","meetDays":"mon|tue|wed|fri|sun","rule":"???","maxHolidayCount":null,"showupTime":"18","type":"subjectroom","durationTime":"16"}
                        // ],"status":"200"}

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                JSONArray rMessage;
                                rMessage = response.getJSONArray("message");
                                //////// real action ////////
                                mRooms = new ArrayList<RoomNaming>();
                                JSONObject rObj;

                                //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
                                Log.i(LOG_TAG, "room size : " + rMessage.length());
                                for(int i=0; i< rMessage.length(); i++) {
//                                    Log.i(LOG_TAG, "index : " + i);
//                                    Log.i(LOG_TAG, "rMessage.getJSONObject(i) : " + rMessage.getJSONObject(i));
                                    rObj = rMessage.getJSONObject(i);
                                    //rMessage.getJSONObject(i) : {"startTime":"04:26:09","roomname":"tesmpSub","rid":"33","meetDays":"mon|tue|wed|fri|sun","rule":"???","maxHolidayCount":null,"showupTime":"18","type":"subjectroom","durationTime":"16"}
                                    //add Rooms : subjectroom|33|???|tesmpSub|null|04:26:09|16|18|mon|tue|wed|fri|sun
                                    //add Rooms : liferoom|23|meet 6days|KOF STUDY|2|null|null|null|null
//                                    Log.i(LOG_TAG, "add Rooms : " + rObj.getString("type") + "|" + rObj.getString("rid") + "|" + rObj.getString("rule") + "|" +rObj.getString("roomname")
//                                            + "|" +rObj.getString("maxHolidayCount")+ "|" +rObj.getString("startTime")+ "|" +rObj.getString("durationTime")
//                                            + "|" +rObj.getString("showupTime")+ "|" +rObj.getString("meetDays"));
                                    if (!"null".equals(rObj.getString("rid"))){
                                        mRooms.add(new RoomNaming(
                                                URLDecoder.decode(rObj.getString("type"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("rid"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("rule"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("roomname"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("maxHolidayCount"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("startTime"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("durationTime"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("showupTime"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("meetDays"),"UTF-8"),
                                                URLDecoder.decode(rObj.getString("num"),"UTF-8")
                                            ));
                                        Log.i(LOG_TAG, "num"+ URLDecoder.decode(rObj.getString("num"),"UTF-8"));
                                    }
                                }

                                RoomsArrayAdapters roomsArrayAdapter;
                                roomsArrayAdapter = new RoomsArrayAdapters(TabActivity.this, R.layout.room_list_item, mRooms);
                                roomList.setAdapter(roomsArrayAdapter);


                                /////////////////////////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러 : \n스터디 방 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "통신 에러 : \n스터디 방 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);

    }


    private void outRoomRequest(String room_id) {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Room/User" +
                "?rid=" + room_id +
                "&nickname=" + KogPreference.getNickName(TabActivity.this);

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, Encrypt.encodeIfNeed(get_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, " kickOffMemberRequest get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            int status_code = response.getInt("status");
                            if (status_code == 200) {
//                                JSONArray rMessage;
//                                rMessage = response.getJSONArray("message");
                                //////// real action ////////

                                Toast.makeText(getBaseContext(), "방을 나왔습니다.", Toast.LENGTH_SHORT).show();
                                setAllEnable();

                                refreshActivity();

                                //////// real action ////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                                setAllEnable();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                            setAllEnable();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setAllEnable();
                Toast.makeText(getBaseContext(), "통신 에러 : \n방을 나올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }

    private void logoutRequest(String nickname) {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "LoginSession";
        Log.i(LOG_TAG, "URL : " + get_url);

        Map<String, String> map = new HashMap<String, String>();
        map.put("nickname", nickname);
            Log.i(LOG_TAG, "nickname : " + nickname);

        JSONObject sendBody = new JSONObject(map);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, Encrypt.encodeIfNeed(get_url), sendBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, " kickOffMemberRequest get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            int status_code = response.getInt("status");
                            if (status_code == 200) {
//                                JSONArray rMessage;
//                                rMessage = response.getJSONArray("message");
                                //////// real action ////////

                                Toast.makeText(getBaseContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                                //////// real action ////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                                setAllEnable();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                            setAllEnable();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setAllEnable();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }

    private void refreshActivity() {
        Intent _intent = new Intent(this, TabActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(_intent);
    }


}
