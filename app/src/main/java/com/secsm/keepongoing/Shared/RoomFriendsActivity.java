package com.secsm.keepongoing.Shared;

import android.app.Activity;
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
import com.secsm.keepongoing.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

public class RoomFriendsActivity extends Activity {
    ArrayList<FriendNameAndIcon> mFriends;

    private RequestQueue vQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_friends);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_friends, menu);
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


//    private void getFriendsRequest() {
//
//        //TODO : check POST/GET METHOD and get_URL
//        String get_url = KogPreference.REST_URL +
//                "Room/User" +
//                "?rid=" + KogPreference.getRid(StudyRoomActivity.this) +
//                "&date=" + getRealDate();
//
//        Log.i(LOG_TAG, "URL : " + get_url);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject");
//                        Log.i(LOG_TAG, response.toString());
//
//                        try {
//                            int status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                JSONArray rMessage;
//                                rMessage = response.getJSONArray("message");
//                                //////// real action ////////
//                                mFriends = new ArrayList<FriendNameAndIcon>();
//                                JSONObject rObj;
//
//                                //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
//                                for(int i=0; i< rMessage.length(); i++)
//                                {
//                                    rObj = rMessage.getJSONObject(i);
//                                    if (!"null".equals(rObj.getString("nickname"))) {
//                                        Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime"));
//                                        mFriends.add(new FriendNameAndIcon(
//                                                URLDecoder.decode(rObj.getString("image"), "UTF-8"),
//                                                URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
//                                                URLDecoder.decode(rObj.getString("targetTime"), "UTF-8")));
//                                    }
//                                }
//                                /////////////////////////////
//                            } else {
//                                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                                if (KogPreference.DEBUG_MODE) {
//                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                Log.i(LOG_TAG, "Response Error");
//                if (KogPreference.DEBUG_MODE) {
//                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }
//        );
//        vQueue.add(jsObjRequest);
//    }
}
