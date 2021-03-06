package com.secsm.keepongoing;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Adapters.ListLockHelper;
import com.secsm.keepongoing.Adapters.MessageAdapter;
import com.secsm.keepongoing.Adapters.Msg;
import com.secsm.keepongoing.Alarm.Preference;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Quiz.Quiz_Main;
import com.secsm.keepongoing.Quiz.Solve_Main;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.SocketListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StudyRoomActivity extends BaseActivity {

    //    private String previous_ip=null;
//    private SnowWiFiMonitor m_SnowWifiMonitor = null;
    private int previous_state=-5;
    LinearLayout all_in_chat;
    private Intent intent;
    private static String LOG_TAG = "StudyRoom Activity";
    private Button sendMsgBtn;
    private Button additionalBtn; // send Picture
    private MessageAdapter messageHistoryMAdaptor;
    private EditText messageTxt;
    private String message;
    private DBHelper mDBHelper;
    private String rID;
    private boolean a,b;
    //    private int myID;
    private ArrayList<Msg> mTexts = new ArrayList<Msg>();
    private ListView messageList;
    private MenuItem actionBarFirstBtn, actionBarSecondBtn;
    private MenuItem actionBarThirdBtn, actionBarFourthBtn;
    private MenuItem actionBarFifthBtn;

    private ArrayList<FriendNameAndIcon> mFriends;
    private FriendNameAndIcon mFriendsFristToAdd;
    private FriendNameAndIcon mFriendsLastToShowScoreDetail;
    private FriendsArrayAdapters friendArrayAdapter;
private int originalheight=1557;

    private ListLockHelper listLockHelper;
    private static int loadSize = 10;
//    private RequestQueue vQueue;

//    private Socket client = null;
//    private BufferedReader br = null;
//    private BufferedWriter bw = null;

    private View activityRootView;
    private Animation translateLeftAnim;
    private Animation translateRightAnim;
    private LinearLayout slidingPage01;
    private LinearLayout study_room_additional_page;
    private LinearLayout study_room_additional_ll1_camera;
    private LinearLayout study_room_additional_ll2_album;
    private LinearLayout study_room_additional_ll3_my_time;
    private LinearLayout study_room_below_layout;
    private FrameLayout study_room_fl1;
    private boolean isPageOpen = false;
    private boolean isAdditionalPageOpen = false;
    private boolean BACK_MODE=false;
    private ListView friendList;
    private TextView room_rule_tv;
    private Handler mainHandler;
    private SocketListener sl;
    SocketAsyncTask_Reader soc_reader = null;
    SocketAsyncTask_Writer soc_writer = null;
    String type;
    String rule;
    String num;

    int rootHeight = 0;
    int actionBarHeight = 0;
    int statusBarHeight = 0;
    private RelativeLayout.LayoutParams study_room_fl1_lp = null;
    private RelativeLayout.LayoutParams study_room_below_layout_lp = null;
    private RelativeLayout.LayoutParams study_room_additional_page_lp = null;
    private void setAllEnable() {
        study_room_progress.setVisibility(View.GONE);
        sendMsgBtn.setEnabled(true);
        additionalBtn.setEnabled(true);
        messageTxt.setEnabled(true);
//        messageList.setEnabled(true);
        if (actionBarFirstBtn != null) {
            actionBarFirstBtn.setEnabled(true);
        }
        if (actionBarSecondBtn != null) {
            actionBarSecondBtn.setEnabled(true);
        }
        if (actionBarThirdBtn != null) {
            actionBarThirdBtn.setEnabled(true);
        }
        if (actionBarFourthBtn != null) {
            actionBarFourthBtn.setEnabled(true);
        }
//        actionBarFifthBtn.setEnabled(true);
//        study_room_additional_page.setEnabled(true);
//        study_room_additional_ll1_camera.setEnabled(true);
//        study_room_additional_ll2_album.setEnabled(true);
//        study_room_additional_ll3_my_time.setEnabled(true);
//        study_room_below_layout.setEnabled(true);
//        friendList.setEnabled(true);
    }

    private void setAllDisable() {
        study_room_progress.setVisibility(View.VISIBLE);
        sendMsgBtn.setEnabled(false);
        additionalBtn.setEnabled(false);
        messageTxt.setEnabled(false);
//        messageList.setEnabled(false);
        if (actionBarFirstBtn != null) {
            actionBarFirstBtn.setEnabled(false);
        }
        if (actionBarSecondBtn != null) {
            actionBarSecondBtn.setEnabled(false);
        }
        if (actionBarThirdBtn != null) {
            actionBarThirdBtn.setEnabled(false);
        }
        if (actionBarFourthBtn != null) {
            actionBarFourthBtn.setEnabled(false);
        }
//        if (actionBarFifthBtn != null) {
//            actionBarFifthBtn.setEnabled(false);
//        }
//        study_room_additional_page.setEnabled(false);
//        study_room_additional_ll1_camera.setEnabled(false);
//        study_room_additional_ll2_album.setEnabled(false);
//        study_room_additional_ll3_my_time.setEnabled(false);
//        study_room_below_layout.setEnabled(false);
//        friendList.setEnabled(false);
    }


    private ProgressBar study_room_progress;
    private View messageHeader;
    private Button messageHeaderButton;
    private TextView messageHeaderNoMessageToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GcmIntentService.setNewQuizHandler(newQuizHandler);
        GcmIntentService.setKickHandler(kickHandler);

        setContentView(R.layout.activity_study_room);
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.NAVIGATION_MODE_STANDARD);

        study_room_progress = (ProgressBar) findViewById(R.id.study_room_progress);
        all_in_chat=(LinearLayout)findViewById(R.id.all_in_chat);
//        MyVolley.init(StudyRoomActivity.this);
//        vQueue = MyVolley.getRequestQueue(StudyRoomActivity.this);

        mDBHelper = new DBHelper(this);
        intent = getIntent();
//        rID = (int) intent.getIntExtra("roomID", -1);
        rID = (String) intent.getStringExtra("rid");
        type = (String) intent.getStringExtra("type");
        rule = (String) intent.getStringExtra("rule");
        num = (String) intent.getStringExtra("num");

        Log.i(LOG_TAG, "onCreate nickname : " + KogPreference.getNickName(StudyRoomActivity.this));
        Log.i(LOG_TAG, "onCreate rid : " + KogPreference.getRid(StudyRoomActivity.this));
        Log.i(LOG_TAG, "onCreate regid : " + KogPreference.getRegId(StudyRoomActivity.this));

        if(!KogPreference.getRid(StudyRoomActivity.this).equals(rID))
        {
            KogPreference.setRid(StudyRoomActivity.this, rID);
        }

        if(!KogPreference.getQuizNum(StudyRoomActivity.this).equals(num))
        {
            KogPreference.setQuizNum(StudyRoomActivity.this, num);
        }

        if ("-1".equals(rID)) {
            // TODO : 잘못된 접근, 되돌아가기
        }

        Log.i(LOG_TAG, "type" + type);

//        myID = KogPreference.getInt(StudyRoomActivity.this, "uid");

        /* initial UI */
        activityRootView = (RelativeLayout) findViewById(R.id.activityRoot);
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        study_room_below_layout = (LinearLayout) findViewById(R.id.study_room_below_layout);
//        study_room_fl1 = (FrameLayout) findViewById(R.id.study_room_fl1);

        sendMsgBtn = (Button) findViewById(R.id.study_room_sendMsgBtn);
        messageTxt = (EditText) findViewById(R.id.study_room_messageTxtView);
        additionalBtn = (Button) findViewById(R.id.study_room_additional);

        messageHistoryMAdaptor = new MessageAdapter(StudyRoomActivity.this, R.layout.message_row, mTexts);
        messageList = (ListView) findViewById(R.id.study_room_message_list);


        messageHeader = getLayoutInflater().inflate(R.layout.progress_layout, null, false);
        messageHeaderButton = (Button) messageHeader.findViewById(R.id.listHeaderLoadMore);
        messageHeaderNoMessageToLoad = (TextView) messageHeader.findViewById(R.id.listHeaderNoMessageToLoad);

        messageList.addHeaderView(messageHeader);
        messageHeader.setVisibility(View.GONE);
        messageHeaderButton.setVisibility(View.GONE);
        messageHeaderNoMessageToLoad.setVisibility(View.VISIBLE);

        messageList.setAdapter(messageHistoryMAdaptor);

        messageHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "messageHeader");
                loadPreviousText(messageHistoryMAdaptor.getCount(), loadSize);
            }
        });


        //@민수
        all_in_chat=(LinearLayout)findViewById(R.id.all_in_chat);
        all_in_chat.setVisibility(View.GONE);
        all_in_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(getBaseContext(), "hi", Toast.LENGTH_SHORT).show();
                closesliding();
            }
        });


//        listLockHelper = new ListLockHelper(messageList);
//
//
//        listLockHelper.setRequest(new ListLockHelper.Request() {
//            @Override
//            public void request() {
//                messageHeader.setVisibility(View.GONE);
//                Log.i(LOG_TAG, "listLockHelper reqeust in");
//                Log.i(LOG_TAG, "messageHistoryMAdaptor.getCount()");
//                loadPreviousText(messageHistoryMAdaptor.getCount(), loadSize);
//                listLockHelper.setListLockOff();
//                messageHeader.setVisibility(View.VISIBLE);
//
//            }
//        });



        room_rule_tv = (TextView) findViewById(R.id.room_role_tv);
        room_rule_tv.setMovementMethod(new ScrollingMovementMethod());

        friendList = (ListView) findViewById(R.id.roomFriendList);
        friendList.setOnItemClickListener(itemClickListener);
        friendList.setOnItemLongClickListener(itemLongClickListener);

        // 슬라이딩으로 보여질 레이아웃 객체 참조
        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);
        study_room_additional_page = (LinearLayout) findViewById(R.id.study_room_additional_page);
        study_room_additional_ll1_camera = (LinearLayout) findViewById(R.id.study_room_additional_ll1_camera);
        study_room_additional_ll2_album = (LinearLayout) findViewById(R.id.study_room_additional_ll2_album);
        study_room_additional_ll3_my_time = (LinearLayout) findViewById(R.id.study_room_additional_ll3_my_time);

        // 애니메이션 객체 로딩
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);


        room_rule_tv.setText(rule);
        statusBarHeight = getStatusBarHeight();
        actionBarHeight = getActionBarHeight();
        study_room_below_layout_lp = (RelativeLayout.LayoutParams) study_room_below_layout.getLayoutParams();
//        study_room_fl1_lp = (RelativeLayout.LayoutParams) study_room_fl1.getLayoutParams();
        study_room_additional_page_lp = (RelativeLayout.LayoutParams) study_room_additional_page.getLayoutParams();
        /* IF there is and exists room, load the stored message */

        /* Init connection w/ server
        *
        * send my nickname! as a type json
        * */

        // 애니메이션 객체에 리스너 설정
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        mFriendsFristToAdd = new FriendNameAndIcon(getBaseContext(), refreshAdaptorHandler, "friend_btn_mypp_plus_press.png","친구 초대하기", "null", "null");
        mFriendsLastToShowScoreDetail = new FriendNameAndIcon(getBaseContext(), refreshAdaptorHandler, "talk_ico_menu_vote.png","스코어 상세보기", "null", "null");

//        init();
        /* at First, holding the focus */
//        messageTxt.requestFocus();

        /* when you click "send" */
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                messageList.smoothScrollToPosition(messageList.getCount() - 1);
                sendMessage();
            }
        });

        study_room_additional_ll1_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close();
                doTakePhotoAction();
            }
        });

        study_room_additional_ll2_album.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close();
                doTakeAlbumAction();
            }
        });

        study_room_additional_ll3_my_time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMyAcommplishedTime();
            }
        });

        Log.i(LOG_TAG, "additionalBtn onClick Listener set");
      //minsu todo
        additionalBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(LOG_TAG, "additionalBtn onClick with isAdditionalPageOpen : " + isAdditionalPageOpen);
                Log.i(LOG_TAG, "minsu : additionalBtn onClick with Height : " + slidingPage01.getHeight()+"original : " +originalheight);

                if(originalheight<slidingPage01.getHeight())
                    originalheight=slidingPage01.getHeight();
                  if (originalheight == slidingPage01.getHeight()) {

                      if (!isAdditionalPageOpen) {
                          Log.i(LOG_TAG, "minsu yes" );
                          setVisibleAdditionalPage();
                          /*getWindow().setSoftInputMode(
                                  WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/
//                getImage();
                      } else {

                          Log.i(LOG_TAG, "minsu no" );
                          setInvisibleAddtionalPage();
                     /*     getWindow().setSoftInputMode(
                                  WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);*/
                      }
                } else {


                    if (!isAdditionalPageOpen) {
                        Log.i(LOG_TAG, "minsu 1" );
                        setVisibleAdditionalPage();
                        getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                getImage();
                    } else {
                        Log.i(LOG_TAG, "minsu 2" );
                        setInvisibleAddtionalPage();
                        getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            }

        });

        messageTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(LOG_TAG, "setOnFocusChangeListener , has Focus : " + hasFocus);
            }
        });

        messageTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "setOnClickListener , ");
                if (isAdditionalPageOpen) {
                    setInvisibleAddtionalPage();
                }

            }
        });


        originalheight=slidingPage01.getHeight();

    }

    Handler newQuizHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                Bundle b = msg.getData();
                if(KogPreference.getRid(getBaseContext()).equals(b.getString("rid"))){
                    KogPreference.setQuizNum(getBaseContext(), b.getString("num"));
                    notifyQuiz.sendEmptyMessage(1);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    };

    Handler kickHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                Bundle b = msg.getData();
                if (KogPreference.getRid(getBaseContext()).equals(b.getString("rid"))) {
                    if (KogPreference.getNickName(getBaseContext()).equals(b.getString("nickname"))) {
                        Toast.makeText(getBaseContext(), "강퇴되었습니다.", Toast.LENGTH_SHORT).show();
                        GoBack();
                    } else {

                        for(int i=0; i<mFriends.size(); i++)
                        {
                            if(mFriends.get(i).getName().equals(b.getString("nickname"))){
                                mFriends.remove(i);
                                refreshAdaptorHandler.sendEmptyMessage(2);
                                Toast.makeText(getBaseContext(), b.getString("nickname") + "님이 강퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    Handler notifyQuiz = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                if(actionBarSecondBtn != null) {
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_read_new);
                }
            }else{
                if(actionBarSecondBtn != null) {
                    actionBarSecondBtn.setIcon(R.drawable.ic_action_read);
                }
            }
        }
    };

    @Override
    protected void registerWifiBroadcastReceiver() {
//        super.registerWifiBroadcastReceiver();
        SnowChangedListener = new SnowWiFiMonitor.OnChangeNetworkStatusListener()
        {
            @Override
            public void OnChanged(int status)
            {
                switch(status)
                {
                    case SnowWiFiMonitor.WIFI_STATE_DISABLED:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_DISABLED");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_DISABLING:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_DISABLING");
                        GoBack();
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_ENABLED:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_ENABLED");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_ENABLING:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_ENABLING");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_UNKNOWN:
                        disconnectConnection(disconnectConnectionHandler);
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_UNKNOWN");
                        GoBack();
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_CONNECTED:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_CONNECTED");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_CONNECTED;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_CONNECTED) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_CONNECTED;
                                disconnectConnection(disconnectConnectionHandler);
                                GoBack();
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_CONNECTING:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_CONNECTING");
                        GoBack();
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_DISCONNECTED");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED;
                                //disconnectConnection(disconnectConnectionHandler);
                                GoBack();
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_DISCONNECTING:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_DISCONNECTING");
                        GoBack();
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_SUSPENDED:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_SUSPENDED");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_SUSPENDED;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_SUSPENDED) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_SUSPENDED;
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_UNKNOWN:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_UNKNOWN");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_UNKNOWN;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_UNKNOWN) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_UNKNOWN;
                            }
                        }

                        break;

                }
            }
        };

        Log.i(LOG_TAG, "register m_SnowWifiMonitor");
        m_SnowWifiMonitor = new SnowWiFiMonitor(this);
        m_SnowWifiMonitor.setOnChangeNetworkStatusListener(SnowChangedListener);
        registerReceiver(m_SnowWifiMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(m_SnowWifiMonitor, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        registerReceiver(m_SnowWifiMonitor, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        Log.i(LOG_TAG, "register m_SnowWifiMonitor done");
    }

    private void setInvisibleAddtionalPage() {
        study_room_additional_page.setVisibility(View.INVISIBLE);
        study_room_additional_page_lp.height = 0;
        showSoftKeyboard();
        isAdditionalPageOpen = false;
    }

    private void setVisibleAdditionalPage() {
        hideSoftKeyboard(study_room_additional_page);
        study_room_additional_page_lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        study_room_additional_page.setVisibility(View.VISIBLE);
        activityRootView.invalidate();
        Log.e("minsu : )"," study_room_additional_page_lp.height"+study_room_additional_page_lp.height);
        isAdditionalPageOpen = true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return -1;
    }

// JSON
// nickname
// rid
// message

    public String getInitialMsg() {
        try {
            JSONObject jObj = new JSONObject();
            Log.i(LOG_TAG, "jObj.toString()");
            jObj.put("nickname", KogPreference.getNickName(StudyRoomActivity.this));
            jObj.put("rid", KogPreference.getRid(StudyRoomActivity.this));
            Log.i(LOG_TAG, "jObj.toString() " + jObj.toString() + "\n");
            jObj.put("rid", KogPreference.getRid(StudyRoomActivity.this));

            Log.i(LOG_TAG, "Log.i(LOG_TAG,  \"jObj.toString() \" + jObj.toString() + \"\\n\");");

            return jObj.toString();
        } catch (JSONException e) {
            Log.i(LOG_TAG, "Json Exception!\n" + e.toString());
            e.printStackTrace();
        }
        return "";
    }


    private void sendMyAcommplishedTime() {
        String msg1 = KogPreference.getNickName(StudyRoomActivity.this) + "의 현재 달성시간은 ";
        String accomplishedTime = "";
        String msg2 = " 입니다.";
        String target_time=Preference.getString(StudyRoomActivity.this,"goal_time");
        Date today = new Date();

        try {
            accomplishedTime = Preference.getString(StudyRoomActivity.this, "achieve_time");
            achievetimeRegisterRequest(target_time, accomplishedTime, Preference.getString(StudyRoomActivity.this, "start_date"));
            sendMsgToSvr(msg1 + accomplishedTime + msg2, KogPreference.MESSAGE_TYPE_TEXT);

        } catch (Exception ex) {
            Log.e(LOG_TAG, "sendAccomplishedTime error : " + ex.toString());
        }
    }

    /** 타이머 종료시 서버에 시간 전송 Post */
    private void achievetimeRegisterRequest(String target_time,String accomplished_time,String date) {
        target_time = target_time.trim().replace(" ", "%20");
        accomplished_time = accomplished_time.trim().replace(" ", "%20");
        date = date.trim().replace(" ", "%20");

        final String temp_target_time=target_time;
        final String temp_accomplished_time=accomplished_time;
        final String temp_date=date;

        try {
            HttpRequestBase requestTime = HttpAPIs.timePost(
                    KogPreference.getNickName(StudyRoomActivity.this)
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
                            String rMessage = result.getString("message");
                            // real action
                            // GoNextPage();
                            Toast.makeText(getBaseContext(), "시간등록 완료" + temp_accomplished_time, Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 9001) {

                            Toast.makeText(getBaseContext(), "시간등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 1001) {
//                            acheivetimeputRequest(temp_target_time, temp_accomplished_time, temp_date);
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
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 타이머 종료시 서버에 시간 전송 Put */
    private void achievetimeputRequest(String target_time,String accomplished_time,String date) {
        final String temp_accomplished_time=accomplished_time;
//        String get_url = KogPreference.REST_URL +
//                "Time";

        try {
            HttpRequestBase requestTime = HttpAPIs.timePut(
                    KogPreference.getNickName(StudyRoomActivity.this)
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
                            String rMessage = result.getString("message");
                            Toast.makeText(getBaseContext(),"시간등록 완료"+temp_accomplished_time, Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 9001) {
                            Toast.makeText(getBaseContext(), "시간등록이 불가능합니다. PUT", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        Log.i(LOG_TAG, "button Clicked");
        String data = "";
        String msg = messageTxt.getText().toString();

        if (msg != null && !msg.trim().equals("")) {
            message = msg;
            try {
                Log.i(LOG_TAG, "sendMessage() , msg : " + msg);
                sendMsgToSvr(msg, KogPreference.MESSAGE_TYPE_TEXT);
//                sendText(KogPreference.getNickName(StudyRoomActivity.this), KogPreference.getRid(StudyRoomActivity.this), msg, "plaintext");

                messageTxt.setText("");
            } catch (Exception ex) {
            }
        }
        if(msg.trim().equals("")){
            messageTxt.setText("");
        }
    }

    /* message info must need the time */
    public String getRealTime() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        // I/StudyRoom Activity﹕ long : 1408229086924
        // Log.i(LOG_TAG, "long : " + time);
        // I/StudyRoom Activity﹕ Timestamp : 2014-08-17 07:44:46.924
        // Log.i(LOG_TAG, "Timestamp : " + currentTimestamp);
        // I/StudyRoom Activity﹕ Timestamp.toString().substring(0, 10) : 2014-08-17
        // Log.i(LOG_TAG, "Timestamp.toString().substring(0, 19) : " + currentTimestamp.toString().substring(0, 19));
        // I/StudyRoom Activity﹕ Timestamp.getTime() : 1408229086924
        // Log.i(LOG_TAG, "Timestamp.getTime() : " + currentTimestamp.getTime());
        return currentTimestamp.toString().substring(0, 19);
    }

    public String getProfileImageName(String f_nick) {
//        Log.i(LOG_TAG, " mFriends.size() " + mFriends.size());
//        Log.i(LOG_TAG, " f_nick : " + f_nick);
        if (f_nick.equals("나"))
            f_nick = KogPreference.getNickName(StudyRoomActivity.this);

        try {
            for (int i = 0; i < mFriends.size(); i++) {
                if (f_nick.equals(mFriends.get(i).getName())) {
                    return mFriends.get(i).getProfile_path();
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            GoBack();
        }
        return "profile_default.png";
    }


    /* this is update the message from someone(include me) */
    public void sendText(String _senderNickname, String _rid, String _text, String _messageType) {
        String time;
        Msg m;
        time = getRealTime();
        String _profileImageName = getProfileImageName(_senderNickname);
        String Name;
        time = getRealTime();
        if (_senderNickname.equals(KogPreference.getNickName(StudyRoomActivity.this))) {
            m = new Msg(StudyRoomActivity.this, "나", _text, time, "true", _messageType, _profileImageName, refreshAdaptorHandler);
            insertIntoMsgInSQLite("나", _text, time, "true", _messageType);
            messageHistoryMAdaptor.add(m);
            messageList.smoothScrollToPosition(messageList.getCount() - 1);

        } else if ("".equals(_text)) {

        } else {
            m = new Msg(StudyRoomActivity.this, _senderNickname, _text, time, "false", _messageType, _profileImageName, refreshAdaptorHandler);
            Log.i("MSG", "Name : " + _senderNickname + "Text : " + _text + "Time : " + time);
            insertIntoMsgInSQLite(_senderNickname, _text, time, "false", _messageType);
            messageHistoryMAdaptor.add(m);
            messageList.smoothScrollToPosition(messageList.getCount() - 1);

        }
        refreshAdaptorHandler.sendEmptyMessage(1);
    }

    // message 1 : refresh message history adapter
    // message 2 : refresh friends adapter
    Handler refreshAdaptorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 1){
                messageHistoryMAdaptor.refresh();
            }else if(msg.what == 2){
                friendArrayAdapter.refresh();
            }
        }
    };


    ///////////////////
    // upload image  //
    ///////////////////
    private AlertDialog mDialog;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    private FileInputStream mFileInputStream = null;
    private URL connectUrl = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";


    void getImage() {
        mDialog = createDialog();
        mDialog.show();
    }

    /* create the dialog */
    private AlertDialog createDialog() {
        final View innerView = getLayoutInflater().inflate(R.layout.image_crop_row, null);

        Button camera = (Button) innerView.findViewById(R.id.btn_camera_crop);
        Button gellary = (Button) innerView.findViewById(R.id.btn_gellary_crop);
        Button cancel = (Button) innerView.findViewById(R.id.btn_cancel_crop);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close();
                doTakePhotoAction();
                setDismiss(mDialog);
            }
        });

        gellary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close();
                doTakeAlbumAction();
                setDismiss(mDialog);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDismiss(mDialog);
            }
        });

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("이미지 설정");
        ab.setView(innerView);

        return ab.create();
    }

    /* using camera */
    private void doTakePhotoAction() {
        Log.i(LOG_TAG, "doTakePhotoAction()");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		/* making the own path for cropped image */
        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /* open the gallery */
    private void doTakeAlbumAction() {
        Log.i(LOG_TAG, "doTakeAlbumAction()");
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /* dialog exit */
    private void setDismiss(AlertDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /* crop image makes the saved image */
    private Uri createSaveCropFile() {
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), url));
        Log.i(LOG_TAG, "createSaveCropFile : " + uri);
        return uri;
    }

    /* getting the image path by uri.
     * if uri is null, getting the last path */
    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    private void DoFileUpload(String filePath) throws IOException {
        Log.d("Test", "file path = " + filePath);
        imageUploadFlag = true;

        asyncFilePath = filePath;
        init();
//        VolleyUploadImage();
        ImageUpload(filePath);
    }

    String asyncFilePath;
    boolean imageUploadFlag = false;



    Handler ImageUploadHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    /////////////////////////////
                    String uploadedChatImgName = result.getString("message");
                    Log.i(LOG_TAG, "chat image rMessage : " + uploadedChatImgName);
                    sendMsgToSvr(uploadedChatImgName, KogPreference.MESSAGE_TYPE_IMAGE);
                    /////////////////////////////
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    void ImageUpload(final String filePath){
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.uploadImage(
                    KogPreference.UPLOAD_CHAT_IMAGE_URL,
                    KogPreference.getRid(StudyRoomActivity.this),
                    KogPreference.getNickName(StudyRoomActivity.this),
                    filePath);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
//                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = ImageUploadHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        ImageUploadHandler.sendMessage(msg);

                        File file = new File(filePath);
                        file.delete();
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* copy the file from srcFile to destFile */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /* Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed. */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResultX");
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                Log.d(LOG_TAG, "onActivityResult PICK_FROM_ALBUM");
                mImageCaptureUri = data.getData();
                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File copy_file = new File(mImageCaptureUri.getPath());
			/* copy the image for crop to SD card */
                copyFile(original_file, copy_file);
//                break;
            }

            case PICK_FROM_CAMERA: {
                Log.d(LOG_TAG, "onActivityResult PICK_FROM_CAMERA");

			/* setup the image resize after taking the image */
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

			/* the path for image */
                intent.putExtra("output", mImageCaptureUri);

                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }

            case CROP_FROM_CAMERA: {
                Log.w(LOG_TAG, "onActivityResult CROP_FROM_CAMERA");
                //mImageCaptureUri = file:///storage/sdcard0/Pictures/tmp_1408977926598.jpg
                Log.w(LOG_TAG, "mImageCaptureUri = " + mImageCaptureUri);
                String full_path = mImageCaptureUri.getPath();
//                String photo_path = full_path.substring(4, full_path.length());
                String photo_path = full_path;
                //비트맵 Image path = /storage/sdcard0/Pictures/tmp_1408977926598.jpg
                Log.w(LOG_TAG, "비트맵 Image path = " + photo_path);

                Bitmap photo = BitmapFactory.decodeFile(photo_path);
//                mPhotoImageView.setImageBitmap(photo);
//
//                insertImgInfoToSQLite(photo_path);
                try {
                    DoFileUpload(photo_path);
                } catch (Exception e) {
                    Log.i("img", e.toString());
                }
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, "title", "descripton");
			/* for media scanning */
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
                intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
                intentFilter.addDataScheme("file");
          /*      sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                        + Environment.getExternalStorageDirectory())));*/
//                refreshActivity();

                init();
                break;
            }
        }
    }

    ////////////////////
    // send my time   //
    ////////////////////

    int keyBoardHeight = 0;
    int screenHeight = 0;
    int editBoxHeight = 0;

    void getSoftKeyboardHeight() {
        if (keyBoardHeight <= 100) {
            Rect r = new Rect();
            View rootview = this.getWindow().getDecorView(); // this = activity
            rootview.getWindowVisibleDisplayFrame(r);

            screenHeight = rootview.getRootView().getHeight();
            int heightDifference = screenHeight - (r.bottom - r.top);
            int resourceId = getResources().getIdentifier("status_bar_height",
                    "dimen", "android");

            Log.i("Keyboard Size", "heightDifference : " + heightDifference);
            if (heightDifference < 100 && heightDifference > 10) {
                editBoxHeight = heightDifference;
                Log.i("Keyboard Size", "editBoxHeight : " + editBoxHeight);
            }
            if (resourceId > 0) {
                heightDifference -= getResources().getDimensionPixelSize(resourceId);
            }
            if (heightDifference > 100) {
                keyBoardHeight = heightDifference;
            }
            Log.i("Keyboard Size", "Size: " + heightDifference);
        }
    }


    ////////////////////
    // Action bar     //
    ////////////////////


    private String getThisMonday() {
        Calendar now = Calendar.getInstance();
        int weekday = now.get(Calendar.DAY_OF_WEEK);
        Log.i("getMonday1", "now.toString : " + now.toString());
        Log.i("getMonday1", "weekday : " + weekday);
        if (weekday != Calendar.MONDAY) {
            int days = (Calendar.SUNDAY - weekday + 1) % 7;
            now.add(Calendar.DAY_OF_YEAR, days);
        }
        Date date = now.getTime();

        String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return format;
    }

    /**
     * 애니메이션 리스너 정의
     */
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        /**
         * 애니메이션이 끝날 때 호출되는 메소드
         */
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                all_in_chat.setVisibility(View.GONE);
                //      Toast.makeText(getBaseContext(), "GONE1", Toast.LENGTH_SHORT).show();
                slidingPage01.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else {
                all_in_chat.setVisibility(View.VISIBLE);
                //       Toast.makeText(getBaseContext(), "VISIBLE1", Toast.LENGTH_SHORT).show();
                slidingPage01.setVisibility(View.VISIBLE);
                isPageOpen = true;
            }
        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }

    }

    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.roomFriendList) {
                Log.i(LOG_TAG, "friends Clicked");
                if(position == 0)
                {
                    // 친구 초대하기
                    if(mFriends != null) {
                        Intent intent = new Intent(StudyRoomActivity.this, AddMoreFriendActivity.class);

                        if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {
                            String[] FriendNicks = new String[mFriends.size() - 2];
                            for (int i = 1; i < mFriends.size() - 1; i++) {
                                FriendNicks[i - 1] = mFriends.get(i).getName();
                            }
                            intent.putExtra("Friends", FriendNicks);
                            startActivity(intent);
                        }else{
                            String[] FriendNicks = new String[mFriends.size() - 1];
                            for (int i = 1; i < mFriends.size(); i++) {
                                FriendNicks[i - 1] = mFriends.get(i).getName();
                            }
                            intent.putExtra("Friends", FriendNicks);
                            startActivity(intent);
                        }
                    }
                }else if(position == mFriends.size()-1)
                {
                    // 스코어 상세보기
                    if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {
                        if(mFriends != null) {
                            Intent intent = new Intent(StudyRoomActivity.this, ScoreViewActivity.class);
                            if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {
                                String[] FriendNicks = new String[mFriends.size() - 2];
                                for (int i = 1; i < mFriends.size() - 1; i++) {
                                    FriendNicks[i - 1] = mFriends.get(i).getName();
                                }
                                intent.putExtra("Friends", FriendNicks);
                                startActivity(intent);
                            }else{
                                String[] FriendNicks = new String[mFriends.size() - 1];
                                for (int i = 1; i < mFriends.size(); i++) {
                                    FriendNicks[i - 1] = mFriends.get(i).getName();
                                }
                                intent.putExtra("Friends", FriendNicks);
                                startActivity(intent);
                            }
                        }
                    }
                }

            }
        }
    };


    ListView.OnItemLongClickListener itemLongClickListener = new ListView.OnItemLongClickListener() {
        int selectedPosition = 0;

        public boolean onItemLongClick(AdapterView<?> adapterView, View v, int pos, long arg3) {

            if (adapterView.getId() == R.id.roomFriendList) {
                Log.i(LOG_TAG, "friends long Clicked pos " + pos + " type " + type);
                if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {
                    if (pos != 0 && pos != mFriends.size() - 1) {
                        if (isMasterAndNotMe(KogPreference.getNickName(StudyRoomActivity.this))) {
                            mDialog = createInflaterDialog(mFriends.get(pos).getName());
                            mDialog.show();
                        }
                    }
                }else{
                    Log.i(LOG_TAG, "not KogPreference.ROOM_TYPE_LIFE (" + KogPreference.ROOM_TYPE_LIFE + ")");
                    Log.i(LOG_TAG, "mFriends.get(pos).getName() : " + mFriends.get(pos).getName());
                    Log.i(LOG_TAG, "isMasterAndNotMe : " + isMasterAndNotMe(KogPreference.getNickName(StudyRoomActivity.this)));
                    if (pos != 0 && pos != mFriends.size()) {
                        if (isMasterAndNotMe(KogPreference.getNickName(StudyRoomActivity.this))) {
                            mDialog = createInflaterDialog(mFriends.get(pos).getName());
                            mDialog.show();
                        }
                    }
                }
            }
            return false;
        }
    };

    private Boolean isMasterAndNotMe(String f_nickname)
    {
        Log.i(LOG_TAG, "1 isMasterAndNotMe in with f_nickname : " + f_nickname);

        Log.i(LOG_TAG, "1 isMasterAndNotMe in with KogPreference.getNickName(getBaseContext()) : " + KogPreference.getNickName(getBaseContext()));
        Log.i(LOG_TAG, "1 f_nickname.equals(KogPreference.getNickName(getBaseContext())) : " + f_nickname.equals(KogPreference.getNickName(getBaseContext())));
        Log.i(LOG_TAG, "1 mFriends.size() " + mFriends.size());
        for(int i=0; i<mFriends.size(); i++)
        {
            Log.i(LOG_TAG, "1 mFriends.get(i).getIsMaster() : " + mFriends.get(i).getIsMaster());
            if(mFriends.get(i).getIsMaster() != null) {
                Log.i(LOG_TAG, "1 mFriends.get(i).getIsMaster().equals(\"true\") : " + mFriends.get(i).getIsMaster().equals("true"));
                Log.i(LOG_TAG, "1 f_nickname.equals(mFriends.get(i).getName()) : " + f_nickname.equals(mFriends.get(i).getName()) );
                if (mFriends.get(i).getIsMaster().equals("true")) {
                    if (f_nickname.equals(mFriends.get(i).getName())) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    TextView simple_dialog_text;

    private AlertDialog createInflaterDialog(final String f_nickname) {
        final View innerView = getLayoutInflater().inflate(R.layout.simple_dialog_layout, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        simple_dialog_text = (TextView) innerView.findViewById(R.id.simple_dialog_text);

//        info_iconFriend.setBackgroundResource(R.drawable.ic_action_add_group);
        simple_dialog_text.setText(f_nickname + "를 강퇴시키시겠습니까?");
        ab.setTitle("방장 권한");
        ab.setView(innerView);

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setAllDisable();
                kickOffMemberRequest(f_nickname);
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


    protected void showSoftKeyboard() {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.showSoftInput(StudyRoomActivity.this.getCurrentFocus(), InputMethodManager.SHOW_FORCED);

    }

    protected void hideSoftKeyboard(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    MenuItem.OnMenuItemClickListener ab_friend_list_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_friend_list_listener");

            // 애니메이션 적용
            if (isPageOpen) {
//                if(!isAdditionalPageOpen) {
//                    additionalBtn.setEnabled(true);
//                    setVisibleAdditionalPage();
//                }
                additionalBtn.setEnabled(true);
                study_room_additional_ll1_camera.setEnabled(true);
                study_room_additional_ll2_album.setEnabled(true);
                study_room_additional_ll3_my_time.setEnabled(true);
                messageTxt.setEnabled(true);
                messageTxt.requestFocus();
//@민수

                slidingPage01.startAnimation(translateLeftAnim);

                Log.e(LOG_TAG, "left");
                slidingPage01.setVisibility(View.VISIBLE);
                //       all_in_chat.setVisibility(View.GONE);
                showSoftKeyboard();
            } else {
//                if(isAdditionalPageOpen) {
//                    setInvisibleAddtionalPage();
//                }
                additionalBtn.setEnabled(false);
                study_room_additional_ll1_camera.setEnabled(false);
                study_room_additional_ll2_album.setEnabled(false);
                study_room_additional_ll3_my_time.setEnabled(false);
                messageTxt.setEnabled(false);


                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                //@민수
                all_in_chat.setVisibility(View.VISIBLE);
                slidingPage01.startAnimation(translateRightAnim);
                hideSoftKeyboard(slidingPage01);
                Log.e(LOG_TAG, "right");
            }
            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_solve_quiz_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_friends_add_listener");

            Intent intent = new Intent(StudyRoomActivity.this, Solve_Main.class);
            startActivity(intent);
//            StudyRoomActivity.this.finish();


            return true;
        }
    };
    MenuItem.OnMenuItemClickListener ab_add_quiz_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_add_quiz_listener");

            Intent intent = new Intent(StudyRoomActivity.this, Quiz_Main.class);
            startActivity(intent);
//            StudyRoomActivity.this.finish();

            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_invite_friend_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_invite_friend_listener");

            // 친구 초대하기
            if(mFriends != null) {
                Intent intent = new Intent(StudyRoomActivity.this, AddMoreFriendActivity.class);

                if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {
                    String[] FriendNicks = new String[mFriends.size() - 2];
                    for (int i = 1; i < mFriends.size() - 1; i++) {
                        FriendNicks[i - 1] = mFriends.get(i).getName();
                    }
                    intent.putExtra("Friends", FriendNicks);
                    startActivity(intent);
                }else{
                    String[] FriendNicks = new String[mFriends.size() - 1];
                    for (int i = 1; i < mFriends.size(); i++) {
                        FriendNicks[i - 1] = mFriends.get(i).getName();
                    }
                    intent.putExtra("Friends", FriendNicks);
                    startActivity(intent);
                }
            }

            return true;
        }
    };
    MenuItem.OnMenuItemClickListener ab_kick_off_friend_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_kick_off_friend_listener");
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_room, menu);
        actionBarFirstBtn = menu.findItem(R.id.actionBarFirstBtn);
        actionBarFirstBtn.setOnMenuItemClickListener(ab_friend_list_listener);

        actionBarSecondBtn = menu.findItem(R.id.actionBarSecondBtn);


        mDBHelper = new DBHelper(getBaseContext());
        if(mDBHelper.isQuizNew(KogPreference.getRid(getBaseContext())))
        {
            Log.i(LOG_TAG, "isQuizNew notify 1");
            notifyQuiz.sendEmptyMessage(1);
        }else
        {
            Log.i(LOG_TAG, "isQuizNew notify -1");
            notifyQuiz.sendEmptyMessage(-1);
        }
        mDBHelper.close();

//        actionBarSecondBtn.setIcon(R.drawable.ic_action_read);



        actionBarSecondBtn.setOnMenuItemClickListener(ab_solve_quiz_listener);

        actionBarThirdBtn = menu.findItem(R.id.actionBarThirdBtn);
        actionBarThirdBtn.setOnMenuItemClickListener(ab_add_quiz_listener);

        actionBarFourthBtn = menu.findItem(R.id.actionBarFourthBtn);
        actionBarFourthBtn.setOnMenuItemClickListener(ab_invite_friend_listener);

        actionBarFifthBtn = menu.findItem(R.id.actionBarFifthBtn);
        actionBarFifthBtn.setOnMenuItemClickListener(ab_kick_off_friend_listener);

        actionBarFourthBtn.setVisible(false);
        actionBarFifthBtn.setVisible(false);

        if ("liferoom".equals(type)) {
            actionBarThirdBtn.setVisible(false);
            actionBarSecondBtn.setVisible(false);
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

    static boolean socketEnable;

    synchronized void init() {

        Log.i(LOG_TAG, "synchronized init");
        if(!KogPreference.NO_SOCKET) {
            if (soc_writer == null) {
                Log.i(LOG_TAG, "soc=null");
                disconnectConnection(socketConnectionHandler);
//                soc_writer = new SocketAsyncTask_Writer();
//                soc_writer.execute();
//                baseHandler.sendEmptyMessage(2);
                mDBHelper = new DBHelper(getBaseContext());
                mDBHelper.UpdateChatNew(KogPreference.getRid(getBaseContext()), false);
                mDBHelper.close();
                getFriendsRequest();
            }
        }else{
            getFriendsRequest();
        }
    }

    synchronized void close() {
        Log.i(LOG_TAG, "synchronized close");
        if(!KogPreference.NO_SOCKET){
            if (soc_reader != null) {
                soc_reader.cancel(true);
                soc_reader = null;
            }
            if (soc_writer != null) {
                soc_writer.sendMsgToSvr("exit");
                soc_writer.cancel(true);
                soc_writer = null;
            }
        }
    }

    //////////////////////////////////////////////////
    // for exit()                                   //
    //////////////////////////////////////////////////


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
        Log.i(LOG_TAG, "onPause nickname : " + KogPreference.getNickName(StudyRoomActivity.this));
        Log.i(LOG_TAG, "onPause rid : " + KogPreference.getRid(StudyRoomActivity.this));
        Log.i(LOG_TAG, "onPause regid : " + KogPreference.getRegId(StudyRoomActivity.this));
        close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        Log.i(LOG_TAG, "onResume");
        if (isAdditionalPageOpen) {
            setInvisibleAddtionalPage();
        }
        if (isPageOpen) {
            additionalBtn.setEnabled(true);
            slidingPage01.startAnimation(translateLeftAnim);
        }
        additionalBtn.setEnabled(true);

//        if(savedInstanceState != null)
//        {
//
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onDestroy() {
        super.onDestroy();

        close();
        Log.d("info>> ", "unregisterReceiver()...");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            close();
            messageList.smoothScrollToPosition(messageList.getCount() - 1);
            sendMessage();

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(BACK_MODE) {
                if (isAdditionalPageOpen) {

                    setInvisibleAddtionalPage();
                } else if (!isAdditionalPageOpen && !isPageOpen) {
                    GoBack();
                }
                if (isPageOpen) {
                    additionalBtn.setEnabled(true);
                    messageTxt.setEnabled(true);
                    messageTxt.requestFocus();
//                showSoftKeyboard();
                    slidingPage01.startAnimation(translateLeftAnim);
//                slidingPage01.setVisibility(View.VISIBLE);
                }
//            Intent intent = new Intent(StudyRoomActivity.this, TabActivity.class);
//            startActivity(intent);
            }else{
                close();
            }
        }
        return false;
    }

    private void GoBack() {
        Log.i(LOG_TAG, "private void GoBack()");
        //close();
        try {
            soc_writer.sendMsgToSvr("exit");
            Log.i(LOG_TAG,"Boolean.toString(soc_reader.cancel(true) : " +  Boolean.toString(soc_reader.cancel(true)));
            soc_reader = null;
            Log.i(LOG_TAG, "Boolean.toString(soc_writer.cancel(true) : " + Boolean.toString(soc_reader.cancel(true)));
            soc_reader = null;
        }
        catch (Exception e)
        {
            Log.i(LOG_TAG, e.getMessage() + " Exception in private void GoBack()");
            disconnectConnection(disconnectConnectionHandler);
        }
        StudyRoomActivity.this.finish();
    }
    //////////////////////////////////////////////////
    // DB                                           //
    //////////////////////////////////////////////////
//            insertIntoMsgInSQLite("나", _text, time, "true", _messageType);
//    insertIntoMsgInSQLite(_senderNickname, _text, time, "false", _messageType);

    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me, String _messageType) {
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        Log.i(LOG_TAG, "insert msg");
//        Calendar c = Calendar.getInstance();
//        String year = Integer.toString(c.get(Calendar.YEAR));
//        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
//        String day = Integer.toString(c.get(Calendar.DATE));
        Log.i("day", "nickname : " + _senderID);
//        String query = "INSERT INTO Chat " +
//                //"(room_id, senderID, senderText, year, month, day, time, me) " +
//                "(rid, senderID, senderText, time, me, messageType) " +
//                "VALUES (" +
//                "'" + rID + "','"
//                + _senderID + "','"
//                + _senderText + "','"
//                + _time + "','"
//                + _me + "','"
//                + _messageType + "');";
//        // TODO : check _time
//        Log.i(LOG_TAG, "execSQL : " + query);
//        db.execSQL(query);

        ContentValues value = new ContentValues();
        value.put("rid", rID);
        value.put("senderID", _senderID);
        value.put("senderText", _senderText);
        value.put("time", _time);
        value.put("me", _me);
        value.put("messageType", _messageType);
        db.insert("Chat", null, value);

        db.close();
        mDBHelper.close();
    }


    /* load the message from the SQLite */
    public void loadText() {

        Log.i(LOG_TAG, "loadText");

        messageHistoryMAdaptor.clear();



        try {
            SQLiteDatabase db;
            Cursor cursor = null;
            db = mDBHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT " +
                    "senderID, senderText, time, me, messageType " +
                    "FROM Chat WHERE rid = '" + rID + "' ORDER BY time DESC " +
                    "LIMIT " + loadSize + " OFFSET " + 0 + ";", null);
//            Log.i(LOG_TAG, "Load Text From db");
//            Log.i(LOG_TAG, "curser.getCount() : " + cursor.getCount());
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToLast();
                String _senderID = cursor.getString(0);
                //                    Log.i("loadText", "sender ID : " + _senderID);
                Msg m = new Msg(StudyRoomActivity.this,
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        getProfileImageName(_senderID),
                        refreshAdaptorHandler
                );
                messageHistoryMAdaptor.add(m);
                while (cursor.moveToPrevious()) {
                    _senderID = cursor.getString(0);
//                    Log.i("loadText", "sender ID : " + _senderID);
                    m = new Msg(StudyRoomActivity.this,
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            getProfileImageName(_senderID),
                            refreshAdaptorHandler
                    );
                    messageHistoryMAdaptor.add(m);
                }

                if(count < loadSize)
                {
                    messageHeaderButton.setVisibility(View.GONE);
                    messageHeaderNoMessageToLoad.setVisibility(View.VISIBLE);
                    messageHeader.setVisibility(View.VISIBLE);
                }else
                {
                    messageHeaderButton.setVisibility(View.VISIBLE);
                    messageHeaderNoMessageToLoad.setVisibility(View.GONE);
                    messageHeader.setVisibility(View.VISIBLE);
                }
            }else{
                messageHeaderButton.setVisibility(View.GONE);
                messageHeaderNoMessageToLoad.setVisibility(View.VISIBLE);
                messageHeader.setVisibility(View.VISIBLE);

            }
            cursor.close();
            db.close();
            mDBHelper.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e.toString());
            e.printStackTrace();
        }
    }

    public void loadPreviousText(int from, int size) {

        Log.i(LOG_TAG, "loadText");

        try {
            SQLiteDatabase db;
            Cursor cursor = null;
            db = mDBHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT " +
                    "senderID, senderText, time, me, messageType " +
                    "FROM Chat WHERE rid = '" + rID + "' ORDER BY time DESC " +
                    "LIMIT " + size + " OFFSET " + from + ";", null);
//            Log.i(LOG_TAG, "Load Text From db");
            Log.i(LOG_TAG, "curser.getCount() : " + cursor.getCount());
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToLast();
                int i = 0;
                String _senderID = cursor.getString(0);
//                    Log.i("loadText", "sender ID : " + _senderID);
                Msg m = new Msg(StudyRoomActivity.this,
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        getProfileImageName(_senderID),
                        refreshAdaptorHandler
                );
                messageHistoryMAdaptor.insert(m, i++);
                while (cursor.moveToPrevious()) {
                    _senderID = cursor.getString(0);
//                    Log.i("loadText", "sender ID : " + _senderID);
                    m = new Msg(StudyRoomActivity.this,
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            getProfileImageName(_senderID),
                            refreshAdaptorHandler
                    );
                    messageHistoryMAdaptor.insert(m, i++);
                }
                if(count < loadSize)
                {
                    messageHeaderButton.setVisibility(View.GONE);
                    messageHeaderNoMessageToLoad.setVisibility(View.VISIBLE);
                    messageHeader.setVisibility(View.VISIBLE);
                }else
                {
                    messageHeaderButton.setVisibility(View.VISIBLE);
                    messageHeaderNoMessageToLoad.setVisibility(View.GONE);
                    messageHeader.setVisibility(View.VISIBLE);
                }

            }else{
                messageHeaderButton.setVisibility(View.GONE);
                messageHeaderNoMessageToLoad.setVisibility(View.VISIBLE);
                messageHeader.setVisibility(View.VISIBLE);
            }
            cursor.close();
            db.close();
            mDBHelper.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e.toString());
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////
    // Listening the message from the server        //
    //////////////////////////////////////////////////
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String f_nickname = null, rid = null, text = null, messageType;
            Bundle b = msg.getData();

            f_nickname = b.getString("nickname");
            text = b.getString("message");
            rid = b.getString("rid");
            messageType = b.getString("messageType");


            Log.i("handleMsg", "friend_id : " + f_nickname);
            Log.i("handleMsg", "text : " + text);

            sendText(f_nickname, rid, text, messageType);
        }
    };

    Handler errorBackHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==1)
            {
                Toast.makeText(getBaseContext(), "통신 장애로 인하여 돌아갑니다.", Toast.LENGTH_SHORT).show();
                GoBack();
            }
        }
    };

    public void sendMsgToSvr(String msg, String messageType) {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("nickname", KogPreference.getNickName(StudyRoomActivity.this));
            jObj.put("rid", KogPreference.getRid(StudyRoomActivity.this));
            jObj.put("message", msg);
            jObj.put("messageType", messageType);
            Log.i(LOG_TAG, "send msg : " + jObj.toString());
            soc_writer.sendMsgToSvr(jObj.toString());
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "메시지 전송 실패!", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Json Exception!\n" + e.toString());
        }
    }


    Socket client = null;

    class SocketAsyncTask_Reader extends AsyncTask<Void, Void, Void> {
        private BufferedReader br = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Log.i(LOG_TAG, "-----------------------------reader");
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String read;
                JSONObject rMsg;

                while (true) {

                    if (isCancelled())
                        break;

                    read = br.readLine();

                    if (read != null) {
                        Log.i("R: Received:", "R: Received:" + read);
                        rMsg = new JSONObject(read);

                        Message ms = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("nickname", rMsg.getString("nickname"));
                        b.putString("rid", rMsg.getString("rid"));
                        b.putString("message", rMsg.getString("message"));
                        b.putString("messageType", rMsg.getString("messageType"));
                        ms.setData(b);
                        handler.sendMessage(ms);
                        //Log.i(LOG_TAG, "this is it~! : " + messageList.getItemAtPosition(0).toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorBackHandler.sendEmptyMessage(1);
                if (KogPreference.DEBUG_MODE) {
                    Log.i(LOG_TAG, "소켓 에러!\n" + e.toString());
//                    Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();
                }
            }

            return (null);
        }

        @Override
        protected void onPostExecute(Void unused) {
            try {
                br.close();
                br = null;
                soc_writer.executeClose();
            } catch (Exception e) {
                errorBackHandler.sendEmptyMessage(1);
                Log.i(LOG_TAG, "소켓 에러!\n" + e.toString());
            }
        }
    }

    BufferedWriter bw = null;
    class SocketAsyncTask_Writer extends AsyncTask<Void, Void, Void> {

        //private BufferedWriter bw = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Log.i(LOG_TAG, "-----------------------------writer");
                client = new Socket(KogPreference.CHAT_IP, KogPreference.CHAT_PORT);
                bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                bw.write(Encrypt.encodeIfNeed(getInitialMsg()));
                bw.newLine();
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
                errorBackHandler.sendEmptyMessage(1);
                if (KogPreference.DEBUG_MODE) {
                    Log.e(LOG_TAG, "소켓 에러!\n" + e.toString());
                }
            }

            if (soc_reader == null) {
                soc_reader = new SocketAsyncTask_Reader();
                Log.i(LOG_TAG, "Socket Reader execute");
                soc_reader.execute();
            }

            return (null);
        }

        private void sendMsgToSvr(String msg) {
            try {
                if (bw == null) {
                    bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                }

                bw.write(msg);
                bw.newLine();
                bw.flush();
                Log.i(LOG_TAG, "client sent msg. now flushed");
            } catch (Exception e) {
                Log.i(LOG_TAG, "client send message??????? " + e.toString());
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
        }

        public void executeClose() {
            try {
                bw.close();
                bw = null;
                client.close();
                client = null;
            } catch (Exception e) {
                errorBackHandler.sendEmptyMessage(1);
                Log.i(LOG_TAG, "소켓 에러!\n" + e.toString());
            }
        }
    }


    ////////////////////////////////////
    // REST API                       //
    ////////////////////////////////////
    /** base Handler for Enable/Disable all UI components */
    static boolean isRestOK;
    static boolean isSocketOK;

    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.i(LOG_TAG, "baseHandelr msg,what : " + msg.what);

            if(msg.what == 1){
                isRestOK = true;
            }else if(msg.what == 2){
                isSocketOK = true;
            }
            else if(msg.what == -1){
                setAllDisable();
            }

            if(isRestOK && isSocketOK)
            {
                setAllEnable();
            }
        }
    };

    Handler errorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Toast.makeText(getBaseContext(), "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };




    /** getMyInfoRequest
     * statusCode == 200 => get My info, Update UI
     */
    Handler disconnectConnectionHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    Log.i(LOG_TAG, "Disconnection succeed");
                    baseHandler.sendEmptyMessage(1);
                }
                else {
                    Log.i(LOG_TAG, "Disconnection failed");
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
                baseHandler.sendEmptyMessage(1);
                errorHandler.sendEmptyMessage(1);
            }
        }
    };

    Handler socketConnectionHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    Log.i(LOG_TAG, "Disconnection succeed");
                    soc_writer = new SocketAsyncTask_Writer();
                    soc_writer.execute();
                    baseHandler.sendEmptyMessage(2);
                }
                else {
                    Log.i(LOG_TAG, "Disconnection failed");
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
                errorHandler.sendEmptyMessage(1);
            }
        }
    };

    private void disconnectConnection(final Handler connectionHandler){
        try {
            Log.i(LOG_TAG, "disconnectConnection() in synchronized init");
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.disconnectConnectionGet(Encrypt.encodeIfNeed(KogPreference.getNickName(StudyRoomActivity.this)));
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
//                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = connectionHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        connectionHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

    /** getMyInfoRequest
     * statusCode == 200 => get My info, Update UI
     */
    Handler getFriendsRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONArray rMessage;
                    rMessage = result.getJSONArray("message");
                    //////// real action ////////
                    mFriends = new ArrayList<FriendNameAndIcon>();
                    JSONObject rObj;
                    mFriends.add(mFriendsFristToAdd);
                    //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}

                    if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {

                        Log.i(LOG_TAG, "friends length : " +rMessage.length());
                        for (int i = 0; i < rMessage.length(); i++) {
                            rObj = rMessage.getJSONObject(i);
                            Log.i(LOG_TAG, "i : " + i );
                            Log.i(LOG_TAG, "rObj.toString() : " + rObj.toString());
                            if (!"null".equals(rObj.getString("nickname"))) {
                                Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + URLDecoder.decode(rObj.getString("nickname"), "UTF-8") + "|" + rObj.getString("targetTime") + "|" + rObj.getString("isMaster"));
                                mFriends.add(new FriendNameAndIcon(
                                        getBaseContext(),
                                        refreshAdaptorHandler,
                                        URLDecoder.decode(rObj.getString("image"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("targetTime"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("isMaster"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("accomplishedTime"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("score"), "UTF-8")));
                                Log.i(LOG_TAG, "add");
                            }
                        }
                        Log.i(LOG_TAG, "in liferoom, add all");
                        mFriends.add(mFriendsLastToShowScoreDetail);
                        Log.i(LOG_TAG, "in liferoom, add tail");
                    }else
                    {
                        for (int i = 0; i < rMessage.length(); i++) {
                            rObj = rMessage.getJSONObject(i);
                            if (!"null".equals(rObj.getString("nickname"))) {
                                Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" +  URLDecoder.decode(rObj.getString("nickname"), "UTF-8") + "|" + rObj.getString("targetTime") + "|" + rObj.getString("isMaster"));
                                mFriends.add(new FriendNameAndIcon(
                                        getBaseContext(),
                                        refreshAdaptorHandler,
                                        URLDecoder.decode(rObj.getString("image"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("targetTime"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("isMaster"), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("accomplishedTime"), "UTF-8")));
                            }
                        }
                        Log.i(LOG_TAG, "in studyroom, add all");

                    }
                    Log.i(LOG_TAG, "set friends adapter");
                    /////////////////////////////
                    friendArrayAdapter = new FriendsArrayAdapters(StudyRoomActivity.this, R.layout.friend_list_item, mFriends);
                    friendList.setAdapter(friendArrayAdapter);

                    baseHandler.sendEmptyMessage(1);
                    loadText();
                    BACK_MODE = true;
                } else {
                    Toast.makeText(getBaseContext(), "네트워크 연결이 불안정합니다.\n뒤로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                    GoBack();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));

                }
            }catch (JSONException e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }catch (UnsupportedEncodingException e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }

        }
    };
    private void getFriendsRequest() {

        try {
            baseHandler.sendEmptyMessage(-1);

            HttpAPIs.getFriendsInRoomGet(
                    KogPreference.getRid(StudyRoomActivity.this), getThisMonday(), getRealDate(), new CallbackResponse() {
                        @Override
                        public void success(HttpResponse httpResponse) {
//                            baseHandler.sendEmptyMessage(1);
                            JSONObject obj = HttpAPIs.getHttpResponseToJSON(httpResponse);
                            if(obj != null){
                                try {
                                    Message msg = getFriendsRequestHandler.obtainMessage();
                                    Bundle b = new Bundle();
                                    b.putString("JSONData", obj.toString());
                                    msg.setData(b);
                                    getFriendsRequestHandler.sendMessage(msg);

                                    BACK_MODE = true;
                                } catch (Exception e) {
                                    Log.e(LOG_TAG, "get friends error : " + e.toString());
                                    BACK_MODE = true;
                                }
                            }
                        }

                        @Override
                        public void error(Exception e) {
                            baseHandler.sendEmptyMessage(1);
                            errorHandler.sendEmptyMessage(1);
                            Log.e(LOG_TAG, "에러");
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** kickOffMemberRequestHandler
     * statusCode == 200 => kickoff member success
     */
    Handler kickOffMemberRequestHandler = new Handler(){

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

                    Toast.makeText(getBaseContext(), "강퇴시켰습니다.", Toast.LENGTH_SHORT).show();
                    setAllEnable();
                    baseHandler.sendEmptyMessage(1);

                    for(int i=0; i<mFriends.size(); i++)
                    {
                        if(mFriends.get(i).getName().equals(b.getString("KickName"))){
                            mFriends.remove(i);
                            refreshAdaptorHandler.sendEmptyMessage(2);
                            break;
                        }
                    }

//                    refreshActivity();
                    refreshAdaptorHandler.sendEmptyMessage(1);
                    refreshAdaptorHandler.sendEmptyMessage(2);


                    //////// real action ////////
                } else if (statusCode == 1001) {
                    Toast.makeText(getBaseContext(), "권한이 없습니다! \n방장만 가능합니다!", Toast.LENGTH_SHORT).show();
                    setAllEnable();
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                    setAllEnable();
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                    }                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    private void kickOffMemberRequest(final String f_nickname) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.outRoomRequest(
                    KogPreference.getRid(StudyRoomActivity.this),f_nickname);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
//                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = kickOffMemberRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        b.putString("KickName", f_nickname);
                        msg.setData(b);
                        kickOffMemberRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    private void refreshActivity() {
//        Intent _intent = new Intent(this, StudyRoomActivity.class);
//        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        _intent.putExtra("type", type);
//        _intent.putExtra("rule", rule);
//        _intent.putExtra("rid", rID);
//        _intent.putExtra("num", num);
//        startActivity(_intent);
//    }
    //@민수
    public void closesliding(){
        //  Toast.makeText(getBaseContext(), "hi", Toast.LENGTH_SHORT).show();
        if(isPageOpen)
        {

            additionalBtn.setEnabled(true);
            study_room_additional_ll1_camera.setEnabled(true);
            study_room_additional_ll2_album.setEnabled(true);
            study_room_additional_ll3_my_time.setEnabled(true);
            messageTxt.setEnabled(true);
            messageTxt.setFocusable(true);
            messageTxt.requestFocus();
            showSoftKeyboard();
            slidingPage01.startAnimation(translateLeftAnim);
            Log.e(LOG_TAG, "left");
            slidingPage01.setVisibility(View.VISIBLE);
            //Todo keyboardfocusout
            //  all_in_chat.setVisibility(View.GONE);
            //  messageTxt.setFocusable(false);
        }
    }
/*
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        try
        {
            if(!hasFocus)
            {
                Log.e(LOG_TAG, "onWindowFocusChanged--try");
                onPause();
            }
            else
            {
                Log.e(LOG_TAG, "onWindowFocusChanged--tryelse");
                if (soc_reader==null) {
                    onResume();
                }
            }
        }
        catch(Exception ex)
        {
            if(!hasFocus)
            {
                try {
                    Log.e(LOG_TAG, "onWindowFocusChanged--catch");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ex.printStackTrace();
            }
        }
    }*/
}
