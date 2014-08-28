package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.R;
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
import java.util.Calendar;
import java.util.Date;

public class ScoreViewActivity extends Activity {
    private static String LOG_TAG = "ScoreViewActivity";
    private RequestQueue vQueue;
    private int status_code;
    private String[] FriendNicks;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_view);
        vQueue = MyVolley.getRequestQueue(ScoreViewActivity.this);


        intent = getIntent();
        FriendNicks = intent.getStringArrayExtra("Friends");

        if(KogPreference.DEBUG_MODE)
        {
            for(int i=0; i<FriendNicks.length; i++)
            {
                Log.i(LOG_TAG, "get Friends nickname " + i + " : " + FriendNicks[i]);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFriendsScoreRequest();

    }


    private String getPrevMonday() {
        Calendar now = Calendar.getInstance();
        int weekday = now.get(Calendar.DAY_OF_WEEK);
//        Log.i("getMonday1", "now.toString : " + now.toString());
//        Log.i("getMonday1", "weekday : " + weekday);
        if (weekday != Calendar.MONDAY) {
            int days = (Calendar.SUNDAY - weekday + 1) % 7;
            now.add(Calendar.DAY_OF_YEAR, days);
            now.add(Calendar.DAY_OF_YEAR, -7);
        }
        Date date = now.getTime();

        String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return format;
    }

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

    private void getFriendsScoreRequest() {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "Timeset";
//                "?rid=" + KogPreference.getRid(StudyRoomActivity.this) +
//                "&fromdate=" + getThisMonday() +
//                "&todate=" + getRealDate();

        JSONObject sendBody = new JSONObject();
        try{
            sendBody.put("rid", KogPreference.getRid(ScoreViewActivity.this));
            sendBody.put("fromdate", getPrevMonday());
            sendBody.put("todate", getRealDate());
            Log.i(LOG_TAG, "sendbody : " + sendBody.toString());
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, "getFriendsScoreRequest error : " + e.toString());
        }

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Encrypt.encodeIfNeed(get_url), sendBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            int status_code = response.getInt("status");
                            if (status_code == 200) {
                                JSONArray rMessage;
                                rMessage = response.getJSONArray("message");
                                //////// real action ////////
//                                mFriends = new ArrayList<FriendNameAndIcon>();
//                                JSONObject rObj;
//                                mFriends.add(mFriendsFristToAdd);
//                                //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
//
//                                if(KogPreference.ROOM_TYPE_LIFE.equals(type)) {
//
//                                    for (int i = 0; i < rMessage.length(); i++) {
//                                        rObj = rMessage.getJSONObject(i);
//                                        if (!"null".equals(rObj.getString("nickname"))) {
//                                            Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime") + "|" + rObj.getString("isMaster"));
//                                            mFriends.add(new FriendNameAndIcon(
//                                                    URLDecoder.decode(rObj.getString("image"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("targetTime"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("isMaster"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("score"), "UTF-8")));
//                                        }
//                                    }
//                                    mFriends.add(mFriendsLastToShowScoreDetail);
//                                }else
//                                {
//                                    for (int i = 0; i < rMessage.length(); i++) {
//                                        rObj = rMessage.getJSONObject(i);
//                                        if (!"null".equals(rObj.getString("nickname"))) {
//                                            Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime") + "|" + rObj.getString("isMaster"));
//                                            mFriends.add(new FriendNameAndIcon(
//                                                    URLDecoder.decode(rObj.getString("image"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("targetTime"), "UTF-8"),
//                                                    URLDecoder.decode(rObj.getString("isMaster"), "UTF-8")));
//                                        }
//                                    }
//
//                                }
//                                /////////////////////////////
//                                friendArrayAdapter = new FriendsArrayAdapters(StudyRoomActivity.this, R.layout.friend_list_item, mFriends);
//                                friendList.setAdapter(friendArrayAdapter);
//
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
                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
        vQueue.start();
    }



}
