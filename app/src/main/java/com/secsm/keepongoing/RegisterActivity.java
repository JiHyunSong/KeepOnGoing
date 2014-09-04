package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.net.URLEncoder;
import java.sql.Timestamp;

public class RegisterActivity extends BaseActivity {

    private static String LOG_TAG = "Profile";
    private String rMessage;
    private RequestQueue vQueue;
    private EditText nickName, password1, password2, phoneNum;
    private Button btnRegister;
    private int status_code;
    private Intent intent;
    private String phoneNo;
    private String rNickName;
    private String rPassWord1;
    private String rPassWord2;
    private TextView alertPwd;
    private TextView alertNick;

    private void setAllEnable(){
        btnRegister.setEnabled(true);
        nickName.setEnabled(true);
        password1.setEnabled(true);
        password2.setEnabled(true);
        phoneNum.setEnabled(false);
    }

    private void setAllDisable(){
        btnRegister.setEnabled(false);
        nickName.setEnabled(false);
        password1.setEnabled(false);
        password2.setEnabled(false);
        phoneNum.setEnabled(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");
        btnRegister = (Button) findViewById(R.id.btnRegister);
        nickName = (EditText) findViewById(R.id.txtNickName);
        nickName.setNextFocusDownId(R.id.tvPassword1);
        password1 = (EditText) findViewById(R.id.txtPassword1);
        password1.setNextFocusDownId(R.id.txtPassword2);
        password2 = (EditText) findViewById(R.id.txtPassword2);
        phoneNum = (EditText) findViewById(R.id.txtPhoneNum);
        alertPwd = (TextView) findViewById(R.id.tvPwdAlert);
        alertNick = (TextView) findViewById(R.id.tvNickAlert);


        nickName.setPrivateImeOptions("defaultInputmode=english;");
        password1.setPrivateImeOptions("defaultInputmode=english;");
        password2.setPrivateImeOptions("defaultInputmode=english;");

        phoneNum.setText(phoneNo);
        phoneNum.setFocusable(false);
//        vQueue = Volley.newRequestQueue(this);
        vQueue = MyVolley.getRequestQueue(RegisterActivity.this);

        // TODO : register GCM


        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidProfile()) {
                    setAllDisable();
                    registerRequest(nickName.getText().toString(), password1.getText().toString(), null, phoneNum.getText().toString());
                } else {
                }

            }

        });
    }

    private void GoNextPage() {
        Toast.makeText(getBaseContext(), "회원가입이 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("nickname", rNickName);
        startActivity(intent);
        RegisterActivity.this.finish();
    }

    private void invisibleAlert() {
        alertNick.setVisibility(View.INVISIBLE);
        alertPwd.setVisibility(View.INVISIBLE);
    }

    private boolean isValidProfile() {
        rNickName = nickName.getText().toString();
        rPassWord1 = password1.getText().toString();
        rPassWord2 = password2.getText().toString();

        if (!rPassWord1.equals(rPassWord2)) {
            invisibleAlert();
            Toast.makeText(getBaseContext(), "패스워드 불일치", Toast.LENGTH_SHORT).show();
            alertPwd.setVisibility(View.VISIBLE);
        } else if (!isPasswordValid(rPassWord1)) {
            invisibleAlert();
            Toast.makeText(getBaseContext(), "패스워드 길이를 확인해주세요", Toast.LENGTH_SHORT).show();
            alertPwd.setVisibility(View.VISIBLE);
        } else if (!isNicknameValid(rNickName)) {
            invisibleAlert();
            Toast.makeText(getBaseContext(), "아이디 길이를 확인해주세요", Toast.LENGTH_SHORT).show();
            alertNick.setVisibility(View.VISIBLE);
        }

        return !TextUtils.isEmpty(rPassWord1) && !TextUtils.isEmpty(rPassWord2) && (rPassWord1.equals(rPassWord2)) && isPasswordValid(rPassWord1) && !TextUtils.isEmpty(rNickName) && isNicknameValid(rNickName);
    }

    private boolean isNicknameValid(String nickName) {
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 4) && (password.length() <= 12);
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

    /** AuthNumRegister
     * statusCode == 200 => send SMS to phone num
     * statusCode == 1001 => auth duplicate! go back to the back page */
    Handler registerRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("httpStatusCode"));
                if (statusCode == 200) {
                    rMessage = result.getString("message");
                    Log.i(LOG_TAG, "rMessage in RegisterRequest :" + rMessage);
                    // real action
//                    acheivetimeputRequest(_nickName,"10:00:00","00:00:00", getRealDate().replace('-', '/'));
                    GoNextPage();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "회원가입이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    private void registerRequest(final String _nickName, String password, String image, String phone) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.registerPost(
                    _nickName,
                    Encrypt.encodingMsg(password),
                    image,
                    phone,
                    KogPreference.getRegId(RegisterActivity.this));
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getJSONData(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = registerRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        registerRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " + e.toString());
                    e.printStackTrace();
                    //Toast.makeText(LoginActivity.this, "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
                    if (KogPreference.DEBUG_MODE) {
                        //  Toast.makeText(LoginActivity.this, LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        String get_url = KogPreference.REST_URL +
                "Register"; // +
//                "?nickname=" + _nickName +
//                "&password=" + Encrypt.encodingMsg(password) +
//                "&image=" + image +
//                "&phone=" + phone +
//                "&gcmid=" + KogPreference.getRegId(RegisterActivity.this);


//
//        JSONObject sendBody = new JSONObject();
//        try{
//            sendBody.put("nickname", _nickName);
//            sendBody.put("password", Encrypt.encodingMsg(password));
//            sendBody.put("image", image);
//            sendBody.put("phone", phone);
//            sendBody.put("gcmid", KogPreference.getRegId(RegisterActivity.this));
//            Log.i(LOG_TAG, "sendBody : " + sendBody.toString() );
//        }catch (JSONException e)
//        {
//            Log.e(LOG_TAG, " sendBody e : " + e.toString());
//        }
//
//        Log.i(LOG_TAG, "post btn event trigger");
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Encrypt.encodeIfNeed(get_url), sendBody,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject");
//                        Log.i(LOG_TAG, response.toString());
//
//                        try {
//                            int statusCode = Integer.parseInt(result.getString("httpStatusCode"));
//                            status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                rMessage = response.getString("message");
//                                // real action
//                                acheivetimeputRequest(_nickName,"10:00:00","00:00:00", getRealDate().replace('-', '/'));
//                                GoNextPage();
//                            } else if (status_code == 9001) {
//                                setAllEnable();
//                                Toast.makeText(getBaseContext(), "회원가입이 불가능합니다.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                setAllEnable();
//                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
//                                if (KogPreference.DEBUG_MODE) {
//                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                            setAllEnable();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(LOG_TAG, "Response Error");
//                setAllEnable();
//                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
//                if (KogPreference.DEBUG_MODE) {
//                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//        );
//        vQueue.add(jsObjRequest);
    }

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

//http://210.118.74.195:8080/KOG_Server_Rest/rest/Time?nickname=jins&target_time=10:00:00&accomplished_time=00:00:00&date=2014/8/25

    private void acheivetimeputRequest(final String __nickname, String target_time, String accomplished_time, String date) {
        String get_url = KogPreference.REST_URL +
                "Time"; // +
//                "?nickname=" + __nickname +
//                "&target_time=" + target_time +
//                "&accomplished_time=" + accomplished_time+
//                "&date=" + date;

        JSONObject sendBody = new JSONObject();
        try{
            sendBody.put("nickname", __nickname);
            sendBody.put("target_time", target_time);
            sendBody.put("accomplished_time", accomplished_time);
            sendBody.put("date", date);
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, " sendBody e : " + e.toString());
        }


        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, get_url, sendBody,
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
                                // GoNextPage();
//                                Toast.makeText(getBaseContext(), LOG_TAG +rMessage, Toast.LENGTH_SHORT).show();
                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "시간등록이 불가능합니다. PUT", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        vQueue.add(jsObjRequest);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.profile, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
