package com.secsm.keepongoing;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TabActivity extends BaseActivity implements View.OnClickListener {

    //@민수 viewpage 추가

    private boolean resumehelp = false;
    private boolean firsttogglebollean = false;
    private ViewPager mPager;
    private TextView tiger;
    private View viewtemp;
    private String rMessage;
    private int achieve_Hours;
    private int achieve_Mins;
    private int achieve_Seconds;
    private int s;
    private int  goal_hour;
    private int goal_minute;


    final static int TAB_DELAY = 10;
    FriendsArrayAdapters mockFriendArrayAdapter;

    public static final int ROOMNAME_REQUEST_CODE = 1;
    public static final int FRIENDNAME_REQUEST_CODE = 2;
    public static final int CHATROOM_REQUEST_CODE = 3;
    public static final int FRIENDS_REQUEST_CODE = 4;
    private static final String LOG_TAG = "MainmenuActivity";
    private final int ADDROOM = 100;
    private final int MANAGEFRIENDS = 200;
    private TextView ahcieve_time, _current_Day, currenttime2_sec,_current_Time,ahcieve_time_sec;
    private TextView goal_time,goal_sec;
    MenuInflater inflater;
    protected int i = 0, minute = 0, diff_hour, diff_min;
    boolean a = false;
    long mills = 0;

    private DBHelper mDBHelper;
    private ListView roomList, friendList, settingList;
    RoomsArrayAdapters roomsArrayAdapter;

    private ImageButton tabStopwatch, tabFriends, tabRooms, tabSettings;
    private RelativeLayout layoutStopwatch, layoutFriends, layoutRooms, layoutSettings;
    private LinearLayout llStopwatch, llFriends, llRooms, llSettings;
    private MenuItem actionBarFirstBtn, actionBarSecondBtn;

    private ProgressBar tab_progress;
    ArrayList<FriendNameAndIcon> mFriends;
    ArrayList<RoomNaming> mRooms;
    ArrayList<String> arGeneral3;

    private AlertDialog mOutRoomDialog;
    private TextView simple_dialog_text;

    private int pagerIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        //viewtemp=

        mDBHelper = new DBHelper(this);

        init();
//        initSettingsTab();
        //  initAlarm();


        //@민수 viewpage 추가
        initLayout();

    }

    /**
     * 리소스 초기화
     */
    public void init() {
        // TODO 리소스 초기화
        tab_progress = (ProgressBar) findViewById(R.id.tab_progress);

        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO | ActionBar.NAVIGATION_MODE_STANDARD);

        // layout (when the tab image button clicked, visibility change
//        layoutStopwatch = (RelativeLayout) findViewById(R.id.tab_stopwatch_layout);
//        layoutFriends = (RelativeLayout) findViewById(R.id.tab_friends_layout);
//        layoutRooms = (RelativeLayout) findViewById(R.id.tab_rooms_layout);
//        layoutSettings = (RelativeLayout) findViewById(R.id.tab_settings_layout);

        // tab layouts

    }

    /**
     * "스터디 목록" 초기화
     */
    public void initRoomsTab(View view) {
        roomList = (ListView) view.findViewById(R.id.room_list);

        if (KogPreference.DEBUG_MODE && KogPreference.NO_AUTH) {
            Log.i(LOG_TAG, "add mock list room items");

            mRooms = new ArrayList<RoomNaming>();
            for (int i = 0; i < 3; i++) {
                mRooms.add(new RoomNaming("subjectroom", "2" + i, "meet 6days", "KOG STUDY", "2", null, null, null, null, null, true));
            }

            RoomsArrayAdapters roomsArrayAdapter;
            roomsArrayAdapter = new RoomsArrayAdapters(TabActivity.this, R.layout.room_list_item, mRooms);
            roomList.setAdapter(roomsArrayAdapter);

        }
        roomList.setOnItemClickListener(itemClickListener);
        roomList.setOnItemLongClickListener(itemLongClickListener);
        getStudyRoomsRequest();

    }

    /**
     * "친구 목록" 초기화
     */
    public void initFriendsTab(View view) {
        friendList = (ListView) view.findViewById(R.id.friend_list);
        if (KogPreference.DEBUG_MODE && KogPreference.NO_AUTH) {
            Log.i(LOG_TAG, "add mock list friend items");
            mFriends = new ArrayList<FriendNameAndIcon>();
            for (int i = 0; i < 3; i++) {
                mFriends.add(new FriendNameAndIcon(getBaseContext(), refreshAdaptorHandler, "default.png", "nickname" + i, null));
            }

            FriendsArrayAdapters mockFriendArrayAdapter;
            mockFriendArrayAdapter = new FriendsArrayAdapters(TabActivity.this, R.layout.friend_list_item, mFriends);
            friendList.setAdapter(mockFriendArrayAdapter);
        }
        friendList.setOnItemClickListener(itemClickListener);
        getFriendsRequest();
    }

    /**
     * "더보기" 초기화
     */
    public void initSettingsTab(View view) {
        // setup tab_settings
        arGeneral3 = new ArrayList<String>();
        arGeneral3.add("내 프로필");
        arGeneral3.add("알람 / 목표시간 설정");
        arGeneral3.add("퀴즈 모음");
        if (!KogPreference.isLogin(TabActivity.this)) {
            arGeneral3.add("로그인");
        } else {
            arGeneral3.add("로그아웃");
        }

        ArrayAdapter<String> optionArrayAdapter;
        optionArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.tab_list, arGeneral3);
        settingList = (ListView) view.findViewById(R.id.setting_list);
        settingList.setAdapter(optionArrayAdapter);

        settingList.setOnItemClickListener(itemClickListener);

       /* tabStopwatch.setOnClickListener(tabListener);
        tabFriends.setOnClickListener(tabListener);
        tabRooms.setOnClickListener(tabListener);
        tabSettings.setOnClickListener(tabListener);*/
    }

    /**
     * 알람 초기화
     */
    public void initAlarm(View view) {
        // 맨처음 시작하는 경우



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


        ahcieve_time = (TextView) view.findViewById(R.id.tvMsg);
        ahcieve_time_sec=(TextView) view.findViewById(R.id.tvMsg_sec);
        goal_time = (TextView) view.findViewById(R.id.goal);
        goal_sec= (TextView) view.findViewById(R.id.goal_sec);
        //ahcieve_time.setText("00:00:00");
        _current_Day = (TextView) view.findViewById(R.id.currenttime);
        _current_Time = (TextView) view.findViewById(R.id.currenttime2);
        currenttime2_sec= (TextView) view.findViewById(R.id.currenttime2_sec);



        TimerTask adTast2 = new TimerTask() {
            public void run() {
                replace_current_time_Handler.sendEmptyMessage(0);
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(adTast2, 0, 1000); // 0초후 첫실행, 1초마다 계속실행
        Contact contact3 = helper.getContact(2);
        goal_time.setText(
                (contact3.gethour() / 10 == 0 ? "0" + contact3.gethour() : contact3.gethour())
                        + ":" + (contact3.getminute() / 10 == 0 ? "0" + contact3.getminute() : contact3.getminute())
        );
        goal_hour=contact3.gethour();
        goal_minute=contact3.getminute();




       // _current_Time.setText(goal_time.getText());
        Preference.setString(TabActivity.this, "goal_time", goal_time.getText().toString() + ":00");


        //@민수
        // 타이머 시작 버튼
        final ToggleButton play_pause = (ToggleButton) view.findViewById(R.id.toggleButton2);
        //시작버튼 초기화

        if (firsttogglebollean == false) {
            firsttogglebollean = true;
        } else if (Preference.getBoolean(TabActivity.this, "toggle")) {
            play_pause.setChecked(true);
            play_pause.setBackgroundResource(R.drawable.selector_tab_pause_button);
        }
        //성취시간 초기화
        Date start = new Date();
        Preference.setLong(TabActivity.this, "start", start.getTime() - Preference.getLong(TabActivity.this, "diff"));



        ahcieve_time.setText(timediff(TabActivity.this));
        ahcieve_time_sec.setText( "."   + (achieve_Seconds / 10 == 0 ? "0" + achieve_Seconds : achieve_Seconds));
        Preference.setString(TabActivity.this, "achieve_time", ahcieve_time.getText().toString()+":"+ahcieve_time_sec.getText().toString().substring(1,3));
        _current_Time.setText(minusgoalachieve());
        currenttime2_sec.setText( "." + (s/ 10 == 0 ? "0" + s : s));
        play_pause.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                if (play_pause.isChecked()) {

                    Preference.putBoolean(TabActivity.this, "toggle", true);

                    play_pause.setBackgroundResource(R.drawable.selector_tab_pause_button);
                    if (timer == null) {

                        Date start = new Date();
                        if (Preference.getBoolean(TabActivity.this, "first_start") == false) {
                            Preference.putBoolean(TabActivity.this, "first_start", true);
                            Preference.setString(TabActivity.this, "start_date", (start.getYear() + 1900) + "/" + (start.getMonth() + 1) + "/" + start.getDate());
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
        //@민수
        // 타이머 완료 버튼
        Button finish_btn = (Button) view.findViewById(R.id.finish);

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
                try {
                    if (Preference.getBoolean(TabActivity.this, "first_start") == true) {

                        achievetimeRegisterRequest(goal_time.getText().toString()+ ":00",
                                ahcieve_time.getText().toString()+":"+ahcieve_time_sec.getText().toString().substring(1,3),
                                Preference.getString(TabActivity.this, "start_date"));
                        Preference.putBoolean(TabActivity.this, "first_start", false);
                        ahcieve_time.setText("00:00");
                        ahcieve_time_sec.setText(".00");
                        _current_Time.setText(goal_time.getText());
                        currenttime2_sec.setText(".00");
                        Preference.setLong(TabActivity.this, "diff", 0);
                    }

                } catch (Exception e) {

                }

            }


        });

        if (KogPreference.DEBUG_MODE && KogPreference.NO_AUTH) {
            mFriends = new ArrayList<FriendNameAndIcon>();
            for (int i = 0; i < 3; i++) {
                mFriends.add(new FriendNameAndIcon(getBaseContext(), refreshAdaptorHandler, "default.png", "nickname" + i, null));
            }
        }
    }


    /**
     * 타이머 종료시 서버에 시간 전송 Post
     */
    private void achievetimeRegisterRequest(String target_time, String accomplished_time, String date) {
        target_time = target_time.trim().replace(" ", "%20");
        accomplished_time = accomplished_time.trim().replace(" ", "%20");
        date = date.trim().replace(" ", "%20");

        final String temp_target_time = target_time;
        final String temp_accomplished_time = accomplished_time;
        final String temp_date = date;

        try {
            HttpRequestBase requestTime = HttpAPIs.timePost(
                    KogPreference.getNickName(TabActivity.this)
                    , target_time
                    , accomplished_time
                    , date);

            HttpAPIs.background(requestTime, new CallbackResponse() {
                public void success(HttpResponse response) {

                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    try {
                        int statusCode = Integer.parseInt(result.getString("status"));
                        if (statusCode == 200) {
                            rMessage = result.getString("message");
                            // real action
                            // GoNextPage();
                            Toast.makeText(getBaseContext(), "시간등록 완료" + temp_accomplished_time, Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 9001) {

                            Toast.makeText(getBaseContext(), "시간등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 1001) {
                            achievetimeputRequest(temp_target_time, temp_accomplished_time, temp_date);
                        } else {
                            Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                            if (KogPreference.DEBUG_MODE) {
                                Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                public void error(Exception e) {
                    Log.i(LOG_TAG, "Response Error");
                    errorHandler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler achievetimeputRequestHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    rMessage = result.getString("message");
                    Log.i(LOG_TAG, "rMessage 200 " + rMessage);
                    Toast.makeText(getBaseContext(), "시간등록 완료", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "시간등록이 불가능합니다. PUT", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 타이머 종료시 서버에 시간 전송 Put
     */
    private void achievetimeputRequest(String target_time, String accomplished_time, String date) {
        final String temp_accomplished_time = accomplished_time;
        String get_url = KogPreference.REST_URL +
                "Time";

        try {
            HttpRequestBase requestTime = HttpAPIs.timePut(
                    KogPreference.getNickName(TabActivity.this)
                    , target_time
                    , accomplished_time
                    , date);

            HttpAPIs.background(requestTime, new CallbackResponse() {
                public void success(HttpResponse response) {

                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = achievetimeputRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        achievetimeputRequestHandler.sendMessage(msg);
                    }
//                    try {
//                        int statusCode = Integer.parseInt(result.getString("status"));
//                        if (statusCode == 200) {
//                            rMessage = result.getString("message");
//                            Toast.makeText(getBaseContext(),"시간등록 완료"+temp_accomplished_time, Toast.LENGTH_SHORT).show();
//                        } else if (statusCode == 9001) {
//                            Toast.makeText(getBaseContext(), "시간등록이 불가능합니다. PUT", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
//                            if (KogPreference.DEBUG_MODE) {
//                                Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    } catch (Exception e) {
//                    }
                }

                public void error(Exception e) {
                    Log.i(LOG_TAG, "Response Error");
                    e.printStackTrace();
                    errorHandler.sendEmptyMessage(1);
//                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
//                    if (KogPreference.DEBUG_MODE) {
//                        Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
//                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 리소스 비활성화
     */
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
    }

    private void setInvisibleTabMenu(){
        llStopwatch.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        llFriends.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        llRooms.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        llSettings.setBackgroundColor(getResources().getColor(R.color.keep_on_going_np));
        tabStopwatch.setBackgroundResource(R.drawable.tab_stopwatch_icon);
        tabFriends.setBackgroundResource(R.drawable.tab_friends_icon);
        tabRooms.setBackgroundResource(R.drawable.tab_chatroom_icon);
        tabSettings.setBackgroundResource(R.drawable.tab_option_icon);
    }

    /**
     * 탭 클릭시 이벤트
     */
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
                    layoutRooms.setVisibility(View.VISIBLE);
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_rooms_add_listener);
                    llRooms.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabRooms.setBackgroundResource(R.drawable.tab_chatroom_icon_p);
                    break;

                case R.id.imgBtn_tab_settings:
                    setInvisibleBody();
                    layoutSettings.setVisibility(View.VISIBLE);
                    llSettings.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabSettings.setBackgroundResource(R.drawable.tab_option_icon_p);
                    break;
            }
        }
    };

    /**
     * 리스트뷰 클릭시 이벤트
     */
    ListView.OnItemLongClickListener itemLongClickListener = new ListView.OnItemLongClickListener() {
        int selectedPosition = 0;

        public boolean onItemLongClick(AdapterView<?> adapterView, View v,
                                       int pos, long arg3) {
            if (adapterView.getId() == R.id.friend_list) {

            } else if (adapterView.getId() == R.id.room_list) {
                Log.i(LOG_TAG, "tab3, rooms long Clicked");
                mDialog = outRoomDialog(mRooms.get(pos).getRoomname(), mRooms.get(pos).getRid());
                mDialog.show();
            }

            return false;
        }
    };

    /**
     * 다이얼로그 생성
     */
    private AlertDialog outRoomDialog(final String room_name, final String room_id) {
        final View innerView = getLayoutInflater().inflate(R.layout.simple_dialog_layout, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        simple_dialog_text = (TextView) innerView.findViewById(R.id.simple_dialog_text);

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

    /**
     * 더보기 탭 리스트 클릭 리스너
     */
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            if (adapterView.getId() == R.id.friend_list) {
                Log.i(LOG_TAG, "tab2, friends Clicked");
                mDialog = createInflaterDialog(mFriends.get(position).getProfile_image(), mFriends.get(position).getName(), mFriends.get(position).getTargetTime());
                mDialog.show();
            } else if (adapterView.getId() == R.id.room_list) {
                Intent intent = new Intent(TabActivity.this, StudyRoomActivity.class);
                intent.putExtra("type", mRooms.get(position).getType());
                intent.putExtra("rule", mRooms.get(position).getRule());
                intent.putExtra("rid", mRooms.get(position).getRid());
                intent.putExtra("num", mRooms.get(position).getQuiz_num());
                KogPreference.setRid(TabActivity.this, mRooms.get(position).getRid());
                KogPreference.setQuizNum(TabActivity.this, mRooms.get(position).getQuiz_num());
                startActivity(intent);
            } else if (adapterView.getId() == R.id.setting_list) {
                switch (position) {
                    case 0: // 내 프로필
                        Intent intent_my_profile = new Intent(TabActivity.this, MyProfileActivity.class);
                        startActivity(intent_my_profile);

                        break;
                    case 1: // 알람 / 목표시간 설정
                        Intent intent_alarm = new Intent(TabActivity.this, alram_list.class);
                        startActivity(intent_alarm);
//                        Intent intent_alarm = new Intent(TabActivity.this, NoticeActivity.class);
//                        startActivity(intent_notice);
                        break;

                    case 2: // 퀴즈 모음
                        Intent intent = new Intent(TabActivity.this, Quiz_Set_Search.class);
                        startActivity(intent);
                        //@민수 ㅋㅋ
                        break;

                    case 3:
                        //TODO 이부분 왜 이런식으로 구현했는지 모르겠음, 그냥 로그아웃 하면 안되는거
                        if (arGeneral3.get(position).toString().equals("로그아웃")) {
                            logout();
                        }else if(arGeneral3.get(position).toString().equals("로그인")){
                            Intent _intent = new Intent(TabActivity.this, LoginActivity.class);
                            startActivity(_intent);
                            TabActivity.this.finish();
                        }
                        break;
                }
            }
        }
    };

    /**
     * 로그아웃
     */
    private void logout() {
        logoutRequest(KogPreference.getNickName(this));
        KogPreference.setLogin(this, false);
        KogPreference.setNickName(this, "");
        KogPreference.setPassword(this, "");
        KogPreference.setRid(this, "");
        KogPreference.setQuizNum(this, "");
        KogPreference.setAutoLogin(this, false);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * Infalter 다이얼로그
     */
    private AlertDialog mDialog = null;
    private ImageView info_iconFriend = null;
    private TextView info_txtFriendNickname = null;
    private TextView info_txtTargetTime = null;

    private AlertDialog createInflaterDialog(Bitmap profile_img, String name, String targetTime) {
        final View innerView = getLayoutInflater().inflate(R.layout.friend_info_dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        info_iconFriend = (ImageView) innerView.findViewById(R.id.info_iconFriend);
        info_txtFriendNickname = (TextView) innerView.findViewById(R.id.info_txtFriendNickname);
        info_txtTargetTime = (TextView) innerView.findViewById(R.id.info_txtTargetTime);

//        getImageFromURL(profileUrl, info_iconFriend);


        info_iconFriend.setImageBitmap(profile_img);
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
     */
    private void setDismiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*   if (Preference.getString(TabActivity.this, "Resumetimer") == "")
            ahcieve_time.setText("00:00:00");
        else
            ahcieve_time.setText(Preference.getString(TabActivity.this, "Resumetimer"));
             Preference.getInt(TabActivity.this, "Resumetimer_sec");
*/
        //      Toast.makeText(getBaseContext(), "tiger", Toast.LENGTH_SHORT).show();
        if (resumehelp) {

            Log.i(LOG_TAG, "mPager.getCurrentItem() : " + mPager.getCurrentItem());
            Log.i(LOG_TAG, "pagerIndex " + pagerIndex);
            switch (mPager.getCurrentItem()) {
                case 0:
                    DBContactHelper helper = new DBContactHelper(this);
                    Contact contact3 = helper.getContact(2);
                    goal_time = (TextView) viewtemp.findViewById(R.id.goal);
                    goal_time.setText(
                            (contact3.gethour() / 10 == 0 ? "0" + contact3.gethour() : contact3.gethour())
                                    + ":" + (contact3.getminute() / 10 == 0 ? "0" + contact3.getminute() : contact3.getminute())
                    );
                    goal_hour=contact3.gethour();
                    goal_minute=contact3.getminute();
                    Log.e("errorcheck","erroercheck : "+goal_hour+goal_minute);

                    if (Preference.getString(TabActivity.this, "Resumetimer") == "") {
                        ahcieve_time.setText("00:00");
                        ahcieve_time_sec.setText(".00");
                        achieve_Hours=0;
                        achieve_Mins=0;
                        achieve_Seconds=0;
                    }
                    else{
                        ahcieve_time.setText(Preference.getString(TabActivity.this, "Resumetimer"));
                        achieve_Seconds=Preference.getInt(TabActivity.this, "Resumetimer_sec");
                        ahcieve_time_sec.setText("." + (achieve_Seconds / 10 == 0 ? "0" + achieve_Seconds : achieve_Seconds));
                        /*
                        ahcieve_time.setText(timediff(TabActivity.this));
                        ahcieve_time_sec.setText("." + (achieve_Seconds / 10 == 0 ? "0" + achieve_Seconds : achieve_Seconds));
                        */
                    }
                    _current_Time.setText(minusgoalachieve());
                    currenttime2_sec.setText("." + (s / 10 == 0 ? "0" + s : s));
                    Preference.setString(TabActivity.this, "achieve_time", ahcieve_time.getText().toString()+":"+ahcieve_time_sec.getText().toString().substring(1,3));


                    final ToggleButton mtoggle = (ToggleButton) viewtemp.findViewById(R.id.toggleButton2);
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

                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                        actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                        actionBarSecondBtn.setVisible(true);
                        actionBarSecondBtn.setOnMenuItemClickListener(ab_stopwatchTab_settings_listener);
                    }
                    setInvisibleTabMenu();
                    llStopwatch.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabStopwatch.setBackgroundResource(R.drawable.tab_stopwatch_icon_p);
                    break;
                case 1:
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                        actionBarSecondBtn.setIcon(R.drawable.ic_action_add_person);
                        actionBarSecondBtn.setVisible(true);
                        actionBarSecondBtn.setOnMenuItemClickListener(ab_friends_add_listener);
                    }
                    getFriendsRequest();
                    setInvisibleTabMenu();
                    llFriends.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabFriends.setBackgroundResource(R.drawable.tab_friends_icon_p);
                    break;
                case 2:
                    GcmIntentService.setNewChatHandler(notifyNewChat);
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                        actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                        actionBarSecondBtn.setVisible(true);
                        actionBarSecondBtn.setOnMenuItemClickListener(ab_rooms_add_listener);
                    }
                    getStudyRoomsRequest();
                    setInvisibleTabMenu();
                    llRooms.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabRooms.setBackgroundResource(R.drawable.tab_chatroom_icon_p);
                    break;
                case 3:
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                    }
                    setInvisibleTabMenu();
                    llSettings.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabSettings.setBackgroundResource(R.drawable.tab_option_icon_p);
                    break;
                default:
                    break;
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
        int id = item.getItemId();
        if (id == R.id.actionBarFirstBtn) {
            return true;
        }
        if (id == R.id.actionBarSecondBtn) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO 메뉴부분 잘못되었음. 이런식으로 하는게 아니라 메뉴아이템을 가지고 하는것.
    //TODO 프리퍼런스는 저런 용도가 아님
    MenuItem.OnMenuItemClickListener ab_stopwatchTab_settings_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_stopwatchTab_settings_listener");
            //@preference로 flag 설정
            Preference.putBoolean(TabActivity.this, "Mflag", true);
            //@preference를 불러와서 flag 확인후 set이 안되있으면 set으로 함

            Intent intent = new Intent(TabActivity.this, alram_list.class);
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

    //TODO 무슨 핸들러인지 작성하기
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ahcieve_time.setText(timediff(TabActivity.this));
            ahcieve_time_sec.setText( "."   + (achieve_Seconds / 10 == 0 ? "0" + achieve_Seconds : achieve_Seconds));
            _current_Time.setText(minusgoalachieve());
            currenttime2_sec.setText( "." + (s/ 10 == 0 ? "0" + s : s));
            Preference.setString(TabActivity.this, "achieve_time", ahcieve_time.getText().toString()+":"+ahcieve_time_sec.getText().toString().substring(1,3));
        }
    };
    private String minusgoalachieve() {
        Date goal=new Date();
        Date achieve=new Date();
        goal.setHours(goal_hour);
        goal.setMinutes(goal_minute);
        goal.setSeconds(0);
        achieve.setHours(achieve_Hours);
        achieve.setMinutes(achieve_Mins);
        achieve.setSeconds(achieve_Seconds);
        //@민수 todo
//        Log.e("minsu","minsu : "+goal.toString() +" - "+achieve.toString());
       long minus = goal.getTime() - achieve.getTime();
        int h = (int) (minus / (1000 * 60 * 60));
        int m = (int) (minus / (1000 * 60)) % 60;
        s = (int) (minus / 1000) % 60;
String goalminusacheive =     (h / 10 == 0 ? "0" +h : h)
        + ":" + (m / 10 == 0 ? "0" + m: m)
        //+ ":" + (s/ 10 == 0 ? "0" + s : s);
        ;
        return goalminusacheive;


    }

    Handler replace_current_time_Handler = new Handler() {

        public void handleMessage(Message msg) {
            Date today = new Date();
            _current_Day.setText((today.getYear() + 1900) + "/" + (today.getMonth() + 1) + "/" + today.getDate());
          /*  _current_Time.setText(
                    (today.getHours() / 10 == 0 ? "0" + today.getHours() : today.getHours())
                            + ":" + (today.getMinutes() / 10 == 0 ? "0" + today.getMinutes() : today.getMinutes())
                            + ":" + (today.getSeconds() / 10 == 0 ? "0" + today.getSeconds() : today.getSeconds())
            );*/
            //todo 여기 지워야됨
        }
    };

    //@민수
    private String timediff(Context context) {

        DBContactHelper helper = new DBContactHelper(context);
        Contact contact = helper.getContact(2);
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");


            Date Date1 = new Date(Preference.getLong(TabActivity.this, "start"));
            ///    Log.i(LOG_TAG, "DAte1 : "+ Date1.toString());
            Date today = new Date();
            //    Log.i(LOG_TAG, "today : "+ today.toString());
            mills = today.getTime() - Date1.getTime();
            Preference.setLong(TabActivity.this, "diff", mills);
             achieve_Hours = (int) (mills / (1000 * 60 * 60));
             achieve_Mins = (int) (mills / (1000 * 60)) % 60;
             achieve_Seconds = (int) (mills / 1000) % 60;


            String diff =
                    (achieve_Hours / 10 == 0 ? "0" + achieve_Hours : achieve_Hours)
                            + ":" + (achieve_Mins / 10 == 0 ? "0" + achieve_Mins : achieve_Mins)
                    //        + ":"   + (achieve_Seconds / 10 == 0 ? "0" + achieve_Seconds : achieve_Seconds)
                    ; // updated value every1 second

            Preference.setString(TabActivity.this, "Resumetimer", diff);
            Preference.setInt(TabActivity.this, "Resumetimer_sec", achieve_Seconds);

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


    Handler getFriendsRequestHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONArray rMessage = result.getJSONArray("message");
                    mFriends = new ArrayList<FriendNameAndIcon>();

                    JSONObject rObj;

                    for (int i = 0; i < rMessage.length(); i++) {
                        rObj = rMessage.getJSONObject(i);
                        if (!"null".equals(rObj.getString("nickname"))) {
                            Log.i(LOG_TAG, "add Friends : " + Encrypt.encodeIfNeed(rObj.getString("image")) + "|" +
                                    Encrypt.encodeIfNeed(rObj.getString("nickname")) + "|" + Encrypt.encodeIfNeed(rObj.getString("targetTime")));


                            mFriends.add(new FriendNameAndIcon(
                                    getBaseContext(),
                                    refreshAdaptorHandler,
//                                                URLDecoder.decode(rObj.getString("type"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("image"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("targetTime"), "UTF-8")));
                        }
                    }

                    mockFriendArrayAdapter = new FriendsArrayAdapters(TabActivity.this, R.layout.friend_list_item, mFriends);
                    friendList.setAdapter(mockFriendArrayAdapter);

                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };

    Handler refreshAdaptorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                mockFriendArrayAdapter.refresh();
            }
        }
    };

    /**
     * 친구목록 가져오기
     */
    private void getFriendsRequest() {

        try {
            HttpAPIs.getFriendList(KogPreference.getNickName(TabActivity.this), new CallbackResponse() {
                @Override
                public void success(HttpResponse httpResponse) {

                    JSONObject obj = HttpAPIs.getHttpResponseToJSON(httpResponse);
                    if (obj != null) {
                        Message msg = getFriendsRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", obj.toString());
                        msg.setData(b);
                        getFriendsRequestHandler.sendMessage(msg);


                    }

                }

                @Override
                public void error(Exception e) {
                    Log.e(LOG_TAG, "에러");
                    errorHandler.sendEmptyMessage(1);
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler notifyNewChat = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if(mRooms!=null) {
                    for (int i = 0; i < mRooms.size(); i++) {
                        if (mRooms.get(i).getRid().equals(""+msg.what)) {
                            mRooms.get(i).setIsNew(true);
                            Log.i(LOG_TAG, "notifyNewChat : " + msg.what);
                            roomsArrayAdapter.refresh();
                        }
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    Handler getStudyRoomsRequestHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                DBHelper mDBhelper = new DBHelper(getBaseContext());
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONArray rMessage;
                    rMessage = result.getJSONArray("message");
                    mRooms = new ArrayList<RoomNaming>();
                    JSONObject rObj;

                    for (int i = 0; i < rMessage.length(); i++) {
                        rObj = rMessage.getJSONObject(i);
                        if (!"null".equals(rObj.getString("rid"))) {
                            mRooms.add(new RoomNaming(
                                    URLDecoder.decode(rObj.getString("type"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("rid"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("rule"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("roomname"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("maxHolidayCount"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("startTime"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("durationTime"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("showupTime"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("meetDays"), "UTF-8"),
                                    URLDecoder.decode(rObj.getString("num"), "UTF-8"),
                                    mDBhelper.isChatNew(rObj.getString("rid"))
                            ));
                        }
                    }

                    mDBhelper.close();
                    roomsArrayAdapter = new RoomsArrayAdapters(TabActivity.this, R.layout.room_list_item, mRooms);
                    roomList.setAdapter(roomsArrayAdapter);


                    /////////////////////////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n스터디 방 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 스터디룸 요청
     */
    private void getStudyRoomsRequest() {

        try {
            HttpAPIs.getStudyRoomsRequest(KogPreference.getNickName(TabActivity.this), new CallbackResponse() {
                @Override
                public void success(HttpResponse httpResponse) {

                    JSONObject obj = HttpAPIs.getHttpResponseToJSON(httpResponse);
                    Log.e(LOG_TAG, "응답: " + obj.toString());
                    if (obj != null) {
                        Message msg = getStudyRoomsRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", obj.toString());
                        msg.setData(b);
                        getStudyRoomsRequestHandler.sendMessage(msg);
                    }

                }

                @Override
                public void error(Exception e) {
                    e.printStackTrace();
                    errorHandler.sendEmptyMessage(1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 방 나갔을떄 요청에 대한 핸들링
     */
    public Handler outRoomRequestHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    Toast.makeText(getBaseContext(), "방을 나왔습니다.", Toast.LENGTH_SHORT).show();
                    setAllEnable();

                    refreshActivity();

                    //////// real action ////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void outRoomRequest(String room_id) {

        HttpRequestBase requestOutRoom = HttpAPIs.outRoomRequest(room_id, KogPreference.getNickName(TabActivity.this));
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpAPIs.background(requestOutRoom, new CallbackResponse() {
                public void success(HttpResponse response) {
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = outRoomRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        outRoomRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    outRoomRequestHandler.sendEmptyMessage(-1);
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            errorHandler.sendEmptyMessage(1);
        }
    }

    Handler logoutRequestHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
//                                JSONArray rMessage;
//                                rMessage = response.getJSONArray("message");
                    //////// real action ////////

                    Toast.makeText(getBaseContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                    //////// real action ////////
                } else {
                    Toast.makeText(getBaseContext(), "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void logoutRequest(String nickname) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase logoutRequest = HttpAPIs.logoutDelete(nickname);
            HttpAPIs.background(logoutRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = logoutRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        logoutRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " + e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void refreshActivity() {
        Intent _intent = new Intent(this, TabActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(_intent);
    }

    /**
     * base Handler for Enable/Disable all UI components
     */
    Handler baseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                setAllEnable();
            } else if (msg.what == -1) {
                setAllDisable();
            }
        }
    };

    Handler errorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(getBaseContext(), "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 리소스 활성화
     */
    private void setAllEnable() {
        mPager.setEnabled(true);
        tab_progress.setVisibility(View.GONE);
        ahcieve_time.setEnabled(true);
        _current_Day.setEnabled(true);
        _current_Time.setEnabled(true);
        goal_time.setEnabled(true);
        roomList.setEnabled(true);
        friendList.setEnabled(true);
        settingList.setEnabled(true);
        tabStopwatch.setEnabled(true);
        tabFriends.setEnabled(true);
        tabRooms.setEnabled(true);
        tabSettings.setEnabled(true);
    }

    /**
     * 리소스 비활성화
     */
    private void setAllDisable() {
        mPager.setEnabled(false);
        tab_progress.setVisibility(View.VISIBLE);
        ahcieve_time.setEnabled(false);
        _current_Day.setEnabled(false);
        _current_Time.setEnabled(false);
        goal_time.setEnabled(false);
        roomList.setEnabled(false);
        friendList.setEnabled(false);
        settingList.setEnabled(false);
        tabStopwatch.setEnabled(false);
        tabFriends.setEnabled(false);
        tabRooms.setEnabled(false);
        tabSettings.setEnabled(false);
    }

    //@민수 viewpage 추가
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_stopwatch_ll:
            case R.id.imgBtn_tab_stopwatch:
                setCurrentItem(0);
                break;
            case R.id.tab_friends_ll:
            case R.id.imgBtn_tab_friends:
                setCurrentItem(1);
                break;
            case R.id.tab_rooms_ll:
            case R.id.imgBtn_tab_rooms:
                setCurrentItem(2);
                break;
            case R.id.tab_settings_ll:
            case R.id.imgBtn_tab_settings:
                setCurrentItem(3);
                break;
        }
    }

    private void setCurrentItem(int index) {
        if (index == 0) {
            mPager.setCurrentItem(0);
        } else if (index == 1) {
            mPager.setCurrentItem(1);
        } else if (index == 2) {
            mPager.setCurrentItem(2);
        } else {
            mPager.setCurrentItem(3);
        }
    }

    //todo
    private void initLayout() {
//        mOne = (Button) findViewById(R.id.one);
//        mTwo = (Button) findViewById(R.id.two);
//        mThree = (Button) findViewById(R.id.three);

        tabStopwatch = (ImageButton) findViewById(R.id.imgBtn_tab_stopwatch);
        tabFriends = (ImageButton) findViewById(R.id.imgBtn_tab_friends);
        tabRooms = (ImageButton) findViewById(R.id.imgBtn_tab_rooms);
        tabSettings = (ImageButton) findViewById(R.id.imgBtn_tab_settings);

        llStopwatch = (LinearLayout) findViewById(R.id.tab_stopwatch_ll);
        llFriends = (LinearLayout) findViewById(R.id.tab_friends_ll);
        llRooms = (LinearLayout) findViewById(R.id.tab_rooms_ll);
        llSettings = (LinearLayout) findViewById(R.id.tab_settings_ll);

        llStopwatch.setOnClickListener(this);
        llFriends.setOnClickListener(this);
        llRooms.setOnClickListener(this);
        llSettings.setOnClickListener(this);

        tabStopwatch.setOnClickListener(this);
        tabFriends.setOnClickListener(this);
        tabRooms.setOnClickListener(this);
        tabSettings.setOnClickListener(this);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(new ViewPagerAdapter(this));

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                resumehelp = true;
                if (position == 0) {
                    Log.i(LOG_TAG, "view Pager Index : 0 ");
                    pagerIndex = position;
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                        actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                        actionBarSecondBtn.setVisible(true);
                        actionBarSecondBtn.setOnMenuItemClickListener(ab_stopwatchTab_settings_listener);
                    }
                    setInvisibleTabMenu();
                    llStopwatch.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabStopwatch.setBackgroundResource(R.drawable.tab_stopwatch_icon_p);

                } else if (position == 1) {
                    Log.i(LOG_TAG, "view Pager Index : 1 ");
                    pagerIndex = position;
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                        actionBarSecondBtn.setIcon(R.drawable.ic_action_add_person);
                        actionBarSecondBtn.setVisible(true);
                        actionBarSecondBtn.setOnMenuItemClickListener(ab_friends_add_listener);
                    }
                    setInvisibleTabMenu();
                    llFriends.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabFriends.setBackgroundResource(R.drawable.tab_friends_icon_p);

                } else if (position == 2) {
                    Log.i(LOG_TAG, "view Pager Index : 2 ");
                    GcmIntentService.setNewChatHandler(notifyNewChat);
                    pagerIndex = position;
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                        actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                        actionBarSecondBtn.setVisible(true);
                        actionBarSecondBtn.setOnMenuItemClickListener(ab_rooms_add_listener);
                    }
                    setInvisibleTabMenu();
                    llRooms.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabRooms.setBackgroundResource(R.drawable.tab_chatroom_icon_p);

                } else if (position == 3) {
                    Log.i(LOG_TAG, "view Pager Index : 3 ");
                    pagerIndex = position;
                    if (actionBarFirstBtn != null && actionBarSecondBtn != null) {
                        setInvisibleActionBar();
                    }
                    setInvisibleTabMenu();
                    llSettings.setBackgroundColor(getResources().getColor(R.color.keep_on_going));
                    tabSettings.setBackgroundResource(R.drawable.tab_option_icon_p);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    /**
     * 액션바 리소스 비활성화
     */
    private void setInvisibleActionBar() {
        actionBarFirstBtn.setVisible(false);
        actionBarSecondBtn.setVisible(false);
    }


    private class ViewPagerAdapter extends PagerAdapter {

        private LayoutInflater mLayoutInflater;

        public ViewPagerAdapter(Context context) {
            super();
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
//            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup pager, int index) {
            View view = null;
            viewtemp = pager;

            //@ view pager 1st menu

            Log.i(LOG_TAG, "instantiateItem, mPager.getCurrentItem : " + mPager.getCurrentItem());
            // TODO : to minsu, plz check index. It doesn't work make sense.
            if (index == 0) {
//                Log.i(LOG_TAG, "view Pager Index : 0 " + mPager.getCurrentItem());
                view = mLayoutInflater.inflate(R.layout.one, null);
                //todo 첫번쨰 메뉴
                initAlarm(view);
       /*         if(actionBarFirstBtn != null && actionBarSecondBtn != null) {
                    setInvisibleActionBar();
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_stopwatchTab_settings_listener);
                }*/
            }
            //@ view pager 2nd menu
            else if (index == 1) {

                //todo 두번쨰 메뉴
//                Log.i(LOG_TAG, "view Pager Index : 1 " + mPager.getCurrentItem());
                view = mLayoutInflater.inflate(R.layout.two, null);
                initFriendsTab(view);

              /*  if(actionBarFirstBtn != null && actionBarSecondBtn != null) {
                    setInvisibleActionBar();
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_add_person);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_friends_add_listener);
                }*/

                //@ view pager 3rd menu
            } else if (index == 2) {
                //todo 세번쨰 메뉴
//                Log.i(LOG_TAG, "view Pager Index : 2 " + mPager.getCurrentItem());
                view = mLayoutInflater.inflate(R.layout.three, null);
                initRoomsTab(view);
          /*      if(actionBarFirstBtn != null && actionBarSecondBtn != null) {
                    setInvisibleActionBar();
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_new);
                    actionBarSecondBtn.setVisible(true);
                    actionBarSecondBtn.setOnMenuItemClickListener(ab_rooms_add_listener);
                }*/
            }
            //@ view pager @4th menu
            else if (index == 3) {
                //todo 세번쨰 메뉴
//                Log.i(LOG_TAG, "view Pager Index : 3 " + mPager.getCurrentItem());
                view = mLayoutInflater.inflate(R.layout.four, null);
                initSettingsTab(view);
         /*       if(actionBarFirstBtn != null && actionBarSecondBtn != null) {
                    setInvisibleActionBar();
                }*/
            }
            ((ViewPager) pager).addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager) pager).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            resumehelp = true;
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void finishUpdate(View arg0) {
        }
    }

}
