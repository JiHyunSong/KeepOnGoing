package com.secsm.keepongoing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class AddMoreFriendActivity extends BaseActivity {

    private Intent intent;
    private Button add_more_friend_invite_room_btn;
    String f_nickName;

    private static String LOG_TAG = "addMoreFriendActivity";
    private int status_code;
    private String rMessage;
    FriendsArrayAdapters friendsArrayAdapters;
    FriendsArrayAdapters selected_friendsArrayAdapters;
    private ProgressBar add_more_friendProgressBar;

    private BootstrapEditText add_more_friend_search_et;
    ArrayList<FriendNameAndIcon> mFriends, selected_Friends;
    private ListView add_more_friend_list, to_add_more_friend_list;
    private String[] FriendNicks;

    private void setAllEnable(){
        add_more_friend_list.setEnabled(true);
        to_add_more_friend_list.setEnabled(true);
        add_more_friend_invite_room_btn.setEnabled(true);
        add_more_friendProgressBar.setVisibility(View.GONE);
    }

    private void setAllDisable(){
        add_more_friend_list.setEnabled(false);
        to_add_more_friend_list.setEnabled(false);
        add_more_friend_invite_room_btn.setEnabled(false);
        add_more_friendProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_friend);
//        MyVolley.init(InviteFriendsActivity.this);
        add_more_friendProgressBar = (ProgressBar) findViewById(R.id.add_more_progress);

        intent = getIntent();
        FriendNicks = intent.getStringArrayExtra("Friends");

        if(KogPreference.DEBUG_MODE)
        {
            for(int i=0; i<FriendNicks.length; i++)
            {
                Log.i(LOG_TAG, "get Friends nickname " + i +" : " + FriendNicks[i]);
            }
        }

        add_more_friend_search_et = (BootstrapEditText) findViewById(R.id.add_more_friend_search_et);
        add_more_friend_list = (ListView) findViewById(R.id.add_more_friend_list);
        to_add_more_friend_list = (ListView) findViewById(R.id.to_add_more_friend_list);

        add_more_friend_search_et.setPrivateImeOptions("defaultInputmode=english;");

        selected_Friends = new ArrayList<FriendNameAndIcon>();
        selected_friendsArrayAdapters = new FriendsArrayAdapters(AddMoreFriendActivity.this, R.layout.friend_list_item, selected_Friends);
        to_add_more_friend_list.setAdapter(selected_friendsArrayAdapters);

        to_add_more_friend_list.setOnItemClickListener(itemClickListener);
        add_more_friend_list.setOnItemClickListener(itemClickListener);
        add_more_friend_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if(add_more_friend_search_et.getText().toString() != null) {
                    String text = add_more_friend_search_et.getText().toString().toLowerCase(Locale.getDefault());
                    if (friendsArrayAdapters != null) {
                        friendsArrayAdapters.filter(text);
                    }
                }
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
        add_more_friend_invite_room_btn = (Button) findViewById(R.id.add_more_friend_invite_room_btn);
//        invite_friend_go_back_btn = (Button) findViewById(R.id.invite_friend_go_back_btn);
//        invite_friend_name_et = (EditText) findViewById(R.id.invite_friend_name_et);
//
        add_more_friend_invite_room_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAllDisable();
                for(int i=0; i<selected_Friends.size(); i++)
                {
                    inviteFriendToRoomRequest(KogPreference.getRid(AddMoreFriendActivity.this), selected_Friends.get(i).getName());
                }
                AddMoreFriendActivity.this.finish();
            }
        });
//
//        invite_friend_go_back_btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                GoBackPage();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFriendsRequest();
    }

    FriendNameAndIcon temp = null;
    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.to_add_more_friend_list) {
                Log.i(LOG_TAG, "tab2, friends Clicked");
                temp = selected_Friends.get(position);
                selected_Friends.remove(position);
                mFriends.add(temp);

                to_add_more_friend_list.setAdapter(selected_friendsArrayAdapters);
                add_more_friend_list.setAdapter(friendsArrayAdapters);
//                selected_friendsArrayAdapters.notifyDataSetChanged();
//                friendsArrayAdapters.notifyDataSetChanged();
            } else if (adapterView.getId() == R.id.add_more_friend_list) {
                Log.i(LOG_TAG, "friends Clicked");

                temp = mFriends.get(position);
                mFriends.remove(position);
                selected_Friends.add(temp);

                to_add_more_friend_list.setAdapter(selected_friendsArrayAdapters);
                add_more_friend_list.setAdapter(friendsArrayAdapters);


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
        AddMoreFriendActivity.this.finish();
    }

    private void GoTabPage() {
        setAllDisable();
        AddMoreFriendActivity.this.finish();
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


    private boolean isInRoom(String f_nickName)
    {
        Log.i(LOG_TAG, "isInRoom, Friends length : " + FriendNicks.length);
        Log.i(LOG_TAG, "isInRoom, Friends name : " + f_nickName);
        for(int j=0; j<FriendNicks.length; j++)
        {
            Log.i(LOG_TAG, "isInRoom, Friends : " + FriendNicks[j]);
            if(FriendNicks[j].equals(f_nickName))
            {
                return true;
            }
        }
        return false;
    }
    //////////////////////
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

                    Log.i(LOG_TAG, "rMessage  : " + rMessage);
                    Log.i(LOG_TAG, "rMessage.length() : " + rMessage.length());
                    //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
                    for(int i=0; i< rMessage.length(); i++)
                    {
                        rObj = rMessage.getJSONObject(i);
                        if (!"null".equals(rObj.getString("nickname")) && !isInRoom(URLDecoder.decode(rObj.getString("nickname"), "UTF-8"))) {
                            Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime"));
                            mFriends.add(new FriendNameAndIcon(getBaseContext(), refreshAdaptorHandler, rObj.getString("image"),
                                    URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
                                    rObj.getString("targetTime")));

                        }
                    }

                    friendsArrayAdapters = new FriendsArrayAdapters(AddMoreFriendActivity.this, R.layout.friend_list_item, mFriends);
                    add_more_friend_list.setAdapter(friendsArrayAdapters);


                    /////////////////////////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
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

    private void getFriendsRequest() {
        try {
            HttpAPIs.getFriendList(KogPreference.getNickName(AddMoreFriendActivity.this), new CallbackResponse() {
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
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                e.printStackTrace();
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
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                    errorHandler.sendEmptyMessage(1);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
