package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

public class InviteFriendsActivity extends Activity {

    private Intent intent;
    private Button invite_friend_create_room_btn;
    private Button invite_friend_go_back_btn;
    private EditText invite_friend_name_et;
    String f_nickName;

    private static String LOG_TAG = "InviteFriendActivity";
    private RequestQueue vQueue;
    private int status_code;
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
//        MyVolley.init(InviteFriendsActivity.this);
        vQueue = MyVolley.getRequestQueue(InviteFriendsActivity.this);
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

    private void inviteFriendRequest(final String mf_nickName) {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "User";
        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Encrypt.encodeIfNeed(get_url), null,
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

                                Toast.makeText(getBaseContext(), "방으로 초대되었습니다.", Toast.LENGTH_SHORT).show();
                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
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
                        if (KogPreference.DEBUG_MODE) {
                            Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );
        vQueue.add(jsObjRequest);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.invite_friends, menu);
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

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }


    //////////////////////

    private void getFriendsRequest() {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Friend" +
                "?nickname=" + KogPreference.getNickName(InviteFriendsActivity.this) +
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
                                        Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime"));
                                        mFriends.add(new FriendNameAndIcon(rObj.getString("image"),
                                                rObj.getString("nickname"),
                                                rObj.getString("targetTime")));
                                    }
                                }

                                friendsArrayAdapters = new FriendsArrayAdapters(InviteFriendsActivity.this, R.layout.friend_list_item, mFriends);
                                invite_friend_list.setAdapter(friendsArrayAdapters);


                                /////////////////////////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }


    // room create
    private void createLifeRoomRequest() {
//    String type, rule, max_holiday_count, start_time, duration_time, showup_time, meet_days;
        String _roomname = roomname.trim().replace(" ", "%20");
        _roomname = _roomname.trim().replace("\n", "%0D%0A");

        String _rule = rule.trim().replace(" ", "%20");
        _rule = _rule.trim().replace("\n", "%0D%0A");

        String get_url = KogPreference.REST_URL +
                "Room" +
                "?nickname=" + KogPreference.getNickName(InviteFriendsActivity.this)+
                "&type=" + type +
                "&rule=" + _rule +
                "&roomname=" + _roomname +
                "&max_holiday_count=" + max_holiday_count;

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Encrypt.encodeIfNeed(get_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                JSONObject rMessage;
                                rMessage = response.getJSONArray("message").getJSONObject(0);
                                rid = rMessage.getString("rid");
                                //////// real action ////////
                                for(int i=0; i<selected_Friends.size(); i++)
                                {
                                    inviteFriendToRoomRequest(rid, selected_Friends.get(i).getName());
                                }

                                GoTabPage();
                                /////////////////////////////
                            } else {
                                setAllDisable();

                                Toast.makeText(getBaseContext(), "통신 에러 : \n방을 생성할 수 없습니다", Toast.LENGTH_SHORT).show();
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
                setAllDisable();

                Log.i(LOG_TAG, "Response Error");
                Toast.makeText(getBaseContext(), "통신 에러 : \n방을 생성할 수 없습니다", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }

    private void createSubjectRoomRequest() {

//    String type, rule, max_holiday_count, start_time, duration_time, showup_time, meet_days;
        String _roomname = roomname.trim().replace(" ", "%20");
        _roomname = _roomname.trim().replace("\n", "%0D%0A");

        String _rule = rule.trim().replace(" ", "%20");
        _rule = _rule.trim().replace("\n", "%0D%0A");

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Room" +
                "?nickname=" + KogPreference.getNickName(InviteFriendsActivity.this)+
                "&type=" + type +
                "&rule=" + _rule +
                "&roomname=" + _roomname +
                "&start_time=" + start_time +
                "&duration_time=" + duration_time +
                "&showup_time=" + showup_time +
                "&meet_days=" + meet_days;

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Encrypt.encodeIfNeed(get_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                JSONObject rMessage;
                                rMessage = response.getJSONArray("message").getJSONObject(0);
                                rid = rMessage.getString("rid");
                                //////// real action ////////
                                for(int i=0; i<selected_Friends.size(); i++)
                                {
                                    inviteFriendToRoomRequest(rid, selected_Friends.get(i).getName());
                                }

                                GoTabPage();
                                /////////////////////////////
                            } else {
                                setAllDisable();
                                Toast.makeText(getBaseContext(), "통신 에러 : \n방을 생성할 수 없습니다", Toast.LENGTH_SHORT).show();
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
                setAllDisable();
                Toast.makeText(getBaseContext(), "통신 에러 : \n방을 생성할 수 없습니다", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }


    private void inviteFriendToRoomRequest(String rid, final String friendName) {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Room/User" +
                "?rid=" + rid +
                "&nickname=" + friendName;

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                //////// real action ////////

                                /////////////////////////////
                            } else {
                                setAllDisable();
                                Toast.makeText(getBaseContext(), "통신 에러 : \n" + friendName + "친구를 초대할 수 없습니다", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            setAllDisable();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, "Response Error");
                setAllDisable();
                Toast.makeText(getBaseContext(), "통신 에러 : \n" + friendName + "친구를 초대할 수 없습니다", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }


}
