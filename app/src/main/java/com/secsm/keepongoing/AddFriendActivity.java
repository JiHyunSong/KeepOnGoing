package com.secsm.keepongoing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddFriendActivity extends BaseActivity {

    private BootstrapButton add_friend_add_btn;
    private BootstrapButton add_friend_go_back_btn;
    private EditText add_friend_name_et;
    String f_nickName;

    private static String LOG_TAG = "AddFriendActivity";
    private RequestQueue vQueue;
//    private int status_code;
    private String rMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

//        vQueue = Volley.newRequestQueue(this);
        vQueue = MyVolley.getRequestQueue(AddFriendActivity.this);

        add_friend_add_btn = (BootstrapButton) findViewById(R.id.add_friend_add_btn);
        add_friend_go_back_btn = (BootstrapButton) findViewById(R.id.add_friend_go_back_btn);
        add_friend_name_et = (EditText) findViewById(R.id.add_friend_name_et);

        add_friend_name_et.setPrivateImeOptions("defaultInputmode=english;");

        add_friend_add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                f_nickName = add_friend_name_et.getText().toString();
                if (isNicknameValid(f_nickName)) {

                    addFriendRequest(f_nickName);

                } else {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_friend_go_back_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GoBackPage();
            }
        });

    }

    private boolean isNicknameValid(String nickName) {
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
//                TODO : implement setAllEnable()
//                setAllEnable();
            }
            else if(msg.what == -1){
//                TODO : implement setAllDisable()
//                setAllDisable();
            }
        }
    };

    /** getMyInfoRequest
     * statusCode == 200 => get My info, Update UI
     */
    Handler addFriendRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    rMessage = result.getString("message");
                    // real action

                    Toast.makeText(getBaseContext(), "친구로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    private void addFriendRequest(final String mf_nickName) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase addFriendRequest = HttpAPIs.addFriendPost(
                    KogPreference.getNickName(AddFriendActivity.this),
                    mf_nickName);
            HttpAPIs.background(addFriendRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = addFriendRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        addFriendRequestHandler.sendMessage(msg);
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
//        String get_url = KogPreference.REST_URL +
//                "Friend";// +
////                "?nickname=" + KogPreference.getNickName(AddFriendActivity.this) +
////                "&nickname_f=" + mf_nickName;
//
//        JSONObject sendBody = new JSONObject();
//
//        try{
//            sendBody.put("nickname", KogPreference.getNickName(AddFriendActivity.this));
//            sendBody.put("nickname_f", mf_nickName);
//            Log.i(LOG_TAG, "sendBody : " + sendBody.toString() );
//        }catch (JSONException e)
//        {
//            Log.e(LOG_TAG, " sendBody e : " + e.toString());
//        }
//
//
//        Log.i(LOG_TAG, "URL : " + get_url);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Encrypt.encodeIfNeed(get_url), sendBody,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject");
//                        Log.i(LOG_TAG, response.toString());
//
//                        try {
//                            status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                rMessage = response.getString("message");
//                                // real action
//
//                                Toast.makeText(getBaseContext(), "친구로 등록되었습니다.", Toast.LENGTH_SHORT).show();
//                            } else if (status_code == 9001) {
//                                Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
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
//                Log.i(LOG_TAG, "Response Error");
//                if (KogPreference.DEBUG_MODE) {
//                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }
//        );
//        vQueue.add(jsObjRequest);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            GoBackPage();
        }
        return false;
    }

    private void GoBackPage() {
//        Intent intent = new Intent(AddFriendActivity.this, TabActivity.class);
//        startActivity(intent);
        AddFriendActivity.this.finish();
    }
}
