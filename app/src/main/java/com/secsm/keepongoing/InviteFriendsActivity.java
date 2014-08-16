package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONObject;

public class InviteFriendsActivity extends Activity {

    private Button invite_friend_add_btn;
    private Button invite_friend_go_back_btn;
    private EditText invite_friend_name_et;
    String f_nickName;

    private static String LOG_TAG="InviteFriendActivity";
    private RequestQueue vQueue;
    private int status_code;
    private String rMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        vQueue = Volley.newRequestQueue(this);

        invite_friend_add_btn = (Button)findViewById(R.id.invite_friend_add_btn);
        invite_friend_go_back_btn = (Button)findViewById(R.id.invite_friend_go_back_btn);
        invite_friend_name_et = (EditText) findViewById(R.id.invite_friend_name_et);

        invite_friend_add_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                f_nickName = invite_friend_name_et.getText().toString();
                if(isNicknameValid(f_nickName)) {

                    inviteFriendRequest(f_nickName);

                }else
                {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        invite_friend_go_back_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                GoBackPage();
            }
        });
    }


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

                        try{
                            status_code = response.getInt("status");
                            if(status_code == 200) {
                                rMessage = response.getString("message");
                                // real action

                                Toast.makeText(getBaseContext(), "방으로 초대되었습니다.", Toast.LENGTH_SHORT).show();
                            } else if(status_code == 9001){
                                Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                                if(KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i(LOG_TAG, "Response Error");
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
}
