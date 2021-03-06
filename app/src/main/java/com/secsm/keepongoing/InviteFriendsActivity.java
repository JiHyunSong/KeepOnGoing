package com.secsm.keepongoing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Shared.BaseActivity;
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
import java.util.ArrayList;
import java.util.Locale;

public class InviteFriendsActivity extends BaseActivity {

    private Intent intent;
    private Button invite_friend_create_room_btn;
    private Button invite_friend_go_back_btn;
    private EditText invite_friend_name_et;
    String f_nickName;

    private static String LOG_TAG = "InviteFriendActivity";
    private String rMessage;
    FriendsArrayAdapters friendsArrayAdapters;
    FriendsArrayAdapters selected_friendsArrayAdapters;
    private ProgressBar inviteProgressBar;

    private BootstrapEditText invite_friend_search_et;
    ArrayList<FriendNameAndIcon> mFriends, selected_Friends;
    private ListView invite_friend_list, to_invite_friend_list;
//    private FrameLayout invite_friend_fl;
    String type, rule, roomname, max_holiday_count, start_time, duration_time, showup_time, meet_days;
    String rid = null;

    private void setAllEnable(){
        invite_friend_list.setEnabled(true);
        to_invite_friend_list.setEnabled(true);
        invite_friend_create_room_btn.setEnabled(true);
        inviteProgressBar.setVisibility(View.GONE);
    }

    private void setAllDisable(){
        invite_friend_list.setEnabled(false);
        to_invite_friend_list.setEnabled(false);
        invite_friend_create_room_btn.setEnabled(false);
        inviteProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        inviteProgressBar = (ProgressBar) findViewById(R.id.invite_progress);

        intent = getIntent();
        type = intent.getStringExtra("type");
        rule = intent.getStringExtra("rule");
        roomname = intent.getStringExtra("roomname");
        max_holiday_count = intent.getStringExtra("max_holiday_count");
        start_time = intent.getStringExtra("start_time");
        duration_time = intent.getStringExtra("duration_time");
        showup_time = intent.getStringExtra("showup_time");
        meet_days = intent.getStringExtra("meet_days");

        Log.i(LOG_TAG, " Activity enter ");
        Log.i(LOG_TAG, " type : " + type);
        Log.i(LOG_TAG, " roomname : " + roomname);
        Log.i(LOG_TAG, " max_holiday_count : " + max_holiday_count);
        Log.i(LOG_TAG, " start_time : " + start_time);
        Log.i(LOG_TAG, " duration_time : " + duration_time);
        Log.i(LOG_TAG, " showup_time : " + showup_time);
        Log.i(LOG_TAG, " meet_days : " + meet_days);


        invite_friend_search_et = (BootstrapEditText) findViewById(R.id.invite_friend_search_et);
        invite_friend_list = (ListView) findViewById(R.id.invite_friend_list);
        to_invite_friend_list = (ListView) findViewById(R.id.to_invite_friend_list);

        getFriendsRequest();
        selected_Friends = new ArrayList<FriendNameAndIcon>();
        selected_friendsArrayAdapters = new FriendsArrayAdapters(InviteFriendsActivity.this, R.layout.friend_list_item, selected_Friends);
        to_invite_friend_list.setAdapter(selected_friendsArrayAdapters);

        to_invite_friend_list.setOnItemClickListener(itemClickListener);
        invite_friend_list.setOnItemClickListener(itemClickListener);
        invite_friend_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = invite_friend_search_et.getText().toString().toLowerCase(Locale.getDefault());
                friendsArrayAdapters.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        invite_friend_create_room_btn = (Button) findViewById(R.id.invite_friend_create_room_btn);
//        invite_friend_go_back_btn = (Button) findViewById(R.id.invite_friend_go_back_btn);
//        invite_friend_name_et = (EditText) findViewById(R.id.invite_friend_name_et);
//
        invite_friend_create_room_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(LOG_TAG, "type : " + type);
                if ("liferoom".equals(type)) {
                    setAllDisable();
                    createLifeRoomRequest();
//                    inviteFriendRequest(f_nickName);
                } else if("subjectroom".equals(type))
                {
                    setAllDisable();
                    createSubjectRoomRequest();
                }
                else {
                    Toast.makeText(getBaseContext(), "방생성에 문제가 생겼습니다. \n첫화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                    GoTabPage();
                }
            }
        });
//
//        invite_friend_go_back_btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                GoBackPage();
//            }
//        });
    }


    FriendNameAndIcon temp = null;
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.to_invite_friend_list) {
                Log.i(LOG_TAG, "tab2, friends Clicked");
                temp = selected_Friends.get(position);
                selected_Friends.remove(position);
                mFriends.add(temp);

                to_invite_friend_list.setAdapter(selected_friendsArrayAdapters);
                invite_friend_list.setAdapter(friendsArrayAdapters);
//                selected_friendsArrayAdapters.notifyDataSetChanged();
//                friendsArrayAdapters.notifyDataSetChanged();
            } else if (adapterView.getId() == R.id.invite_friend_list) {
                Log.i(LOG_TAG, "friends Clicked");

                temp = mFriends.get(position);
                mFriends.remove(position);
                selected_Friends.add(temp);

                to_invite_friend_list.setAdapter(selected_friendsArrayAdapters);
                invite_friend_list.setAdapter(friendsArrayAdapters);


//                selected_friendsArrayAdapters.notifyDataSetChanged();
//                friendsArrayAdapters.notifyDataSetChanged();

            }
        }
    };

    private boolean isNicknameValid(String nickName) {
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            GoBackPage();
        }
        return false;
    }

    private void GoBackPage() {
        Intent intent = new Intent(InviteFriendsActivity.this, AddStudyRoomActivity.class);
        startActivity(intent);
        InviteFriendsActivity.this.finish();
    }

    private void GoTabPage() {
        setAllDisable();
        InviteFriendsActivity.this.finish();
    }

    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
                setAllEnable();
            }
            else if(msg.what == -1){
                setAllDisable();
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

                    //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
                    for(int i=0; i< rMessage.length(); i++)
                    {
                        rObj = rMessage.getJSONObject(i);
                        if (!"null".equals(rObj.getString("nickname"))) {
                            Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime"));
                            mFriends.add(new FriendNameAndIcon(getBaseContext(), refreshAdaptorHandler, rObj.getString("image"),
                                    rObj.getString("nickname"),
                                    rObj.getString("targetTime")));
                        }
                    }

                    friendsArrayAdapters = new FriendsArrayAdapters(InviteFriendsActivity.this, R.layout.friend_list_item, mFriends);
                    invite_friend_list.setAdapter(friendsArrayAdapters);
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 장애 : " + result.getString("message"));                }
            }catch (JSONException e)
            {
                e.printStackTrace();
                errorHandler.sendEmptyMessage(1);
            }
        }
    };

    Handler refreshAdaptorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 1){
                friendsArrayAdapters.refresh();
                selected_friendsArrayAdapters.refresh();
            }
        }
    };
    //////////////////////
    private void getFriendsRequest() {

        try {
            HttpAPIs.getFriendList(KogPreference.getNickName(InviteFriendsActivity.this), new CallbackResponse() {
                @Override
                public void success(HttpResponse httpResponse) {

                    JSONObject obj = HttpAPIs.getHttpResponseToJSON(httpResponse);
                    if(obj != null){
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
                    e.printStackTrace();
                    errorHandler.sendEmptyMessage(1);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            errorHandler.sendEmptyMessage(1);
        }
    }


    /** createLifeRoomRequest
     * statusCode == 200 => create room done and Request invite Friends
    */
    Handler createLifeRoomRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONObject rMessage;
                    rMessage = result.getJSONArray("message").getJSONObject(0);
                    rid = rMessage.getString("rid");
                    //////// real action ////////
                    for(int i=0; i<selected_Friends.size(); i++)
                    {
                        inviteFriendToRoomRequest(rid, selected_Friends.get(i).getName());
                    }

                    GoTabPage();
                    /////////////////////////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n방을 생성할 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : "+ result.getString("message"));
                }
            }catch (JSONException e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };

    // room create
    private void createLifeRoomRequest() {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase createLifeRoomRequest = HttpAPIs.createLifeRoomPost(
                    KogPreference.getNickName(InviteFriendsActivity.this),
                    type,
                    rule,
                    roomname,
                    max_holiday_count);
            HttpAPIs.background(createLifeRoomRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = createLifeRoomRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        createLifeRoomRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " + e.toString());
                    e.printStackTrace();
                    errorHandler.sendEmptyMessage(1);
                }
            });

        } catch (IOException e) {
            errorHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }
    }

    /** createSubjectRoomRequest
     * statusCode == 200 => create room done and Request invite Friends
     */
    Handler createSubjectRoomRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONObject rMessage;
                    rMessage = result.getJSONArray("message").getJSONObject(0);
                    rid = rMessage.getString("rid");
                    //////// real action ////////
                    for(int i=0; i<selected_Friends.size(); i++)
                    {
                        inviteFriendToRoomRequest(rid, selected_Friends.get(i).getName());
                    }

                    GoTabPage();
                    /////////////////////////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n방을 생성할 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : "+ result.getString("message"));
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
                errorHandler.sendEmptyMessage(1);
            }
        }
    };
    private void createSubjectRoomRequest() {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.createSubjectRoomPost(
                    KogPreference.getNickName(InviteFriendsActivity.this),
                    type,
                    rule,
                    roomname,
                    start_time,
                    duration_time,
                    showup_time,
                    meet_days);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = createSubjectRoomRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        createSubjectRoomRequestHandler.sendMessage(msg);

                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    errorHandler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            errorHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }
    }

    /** createSubjectRoomRequest
     * statusCode == 200 => create room done and Request invite Friends
     */
    Handler inviteFriendToRoomRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    //////// real action ////////

                    /////////////////////////////
                } else {
                    setAllDisable();
                    Toast.makeText(getBaseContext(), "통신 에러 : \n" + "친구를 초대할 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : "+ result.getString("message"));
                }
            }catch (JSONException e)
            {
                errorHandler.sendEmptyMessage(1);
            }
        }
    };

    private void inviteFriendToRoomRequest(String rid, final String friendName) {

        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase inviteFriendToRoomRequest = HttpAPIs.inviteFriendToRoomPost(rid, friendName);
            HttpAPIs.background(inviteFriendToRoomRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = inviteFriendToRoomRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        inviteFriendToRoomRequestHandler.sendMessage(msg);
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
            errorHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }
    }


}
