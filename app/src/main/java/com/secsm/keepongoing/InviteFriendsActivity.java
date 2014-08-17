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
import android.widget.ListView;
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
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

public class InviteFriendsActivity extends Activity {

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

    private BootstrapEditText invite_friend_search_et;
    ArrayList<FriendNameAndIcon> mFriends, selected_Friends;
    private ListView invite_friend_list, to_invite_friend_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        MyVolley.init(InviteFriendsActivity.this);
        vQueue = Volley.newRequestQueue(this);


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
                if (true) {

//                    inviteFriendRequest(f_nickName);

                } else {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
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

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, null,
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

    private void getFriendsRequest() {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Friend" +
                "?nickname=" + KogPreference.getNickName(InviteFriendsActivity.this) +
                "&date=" + getRealDate();

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, get_url, null,
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
                                    Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" +rObj.getString("targetTime"));
                                    mFriends.add(new FriendNameAndIcon(rObj.getString("image"),
                                            rObj.getString("nickname"),
                                            rObj.getString("targetTime")));
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
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }

}
