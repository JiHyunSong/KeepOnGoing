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
//    private String rMessage;
    private EditText nickName, password1, password2, phoneNum;
    private Button btnRegister;
    private Intent intent;
    private String phoneNo;
    private String rNickName;
    private String rPassWord1;
    private String rPassWord2;
    private TextView alertPwd;
    private TextView alertNick;

    private void setAllEnable() {
        btnRegister.setEnabled(true);
        nickName.setEnabled(true);
        password1.setEnabled(true);
        password2.setEnabled(true);
        phoneNum.setEnabled(false);
    }

    private void setAllDisable() {
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

        // TODO : register GCM


        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidProfile()) {
                    setAllDisable();
                    registerRequest(nickName.getText().toString(), password1.getText().toString(), "default.png", phoneNum.getText().toString());
                } else {
                }

            }

        });
    }

    private void GoNextPage() {
        Toast.makeText(getBaseContext(), "회원가입이 되었습니다.", Toast.LENGTH_SHORT).show();
        KogPreference.setNickName(RegisterActivity.this, rNickName);
        KogPreference.setPassword(RegisterActivity.this, null);
        Intent intent = new Intent(this, LoginActivity.class);
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
        return (nickName.trim().length() >= 4) && (nickName.trim().length() <= 10);
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 4) && (password.length() <= 12);
    }

    /**
     * base Handler for Enable/Disable all UI components
     */
    Handler baseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                setAllEnable();
            } else if (msg.what == -1) {
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

    /**
     * AuthNumRegister
     * statusCode == 200 => send SMS to phone num
     * statusCode == 1001 => auth duplicate! go back to the back page
     */
    Handler registerRequestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
//                int statusCode = Integer.parseInt(result.getString("httpStatusCode"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
//                    JSONObject rMessage = result.getJSONObject("message");
//                    Log.i(LOG_TAG, "rMessage in RegisterRequest :" + rMessage.toString());
                    // real action
                    achieveTimePutRequest(nickName.getText().toString(),"10:00:00","00:00:00", getRealDate());
                } else if (statusCode == 1002) {
                    Toast.makeText(getBaseContext(), "이미 등록된 번호입니다.\n다른 아이디를 사용해주세요.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 1003){
                    Toast.makeText(getBaseContext(), "이미 가입된 번호입니다.\n중복가입 하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "회원가입이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 장애 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                errorHandler.sendEmptyMessage(1);
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
//                    JSONObject result = HttpAPIs.getJSONData(response);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
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
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " + e.toString());
                    e.printStackTrace();
                    errorHandler.sendEmptyMessage(1);
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

    /**
     * AuthNumRegister
     * statusCode == 200 => send SMS to phone num
     * statusCode == 1001 => auth duplicate! go back to the back page
     */
    Handler achieveTimePutRequestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
//                int statusCode = Integer.parseInt(result.getString("httpStatusCode"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    String rMessage = result.getString("message");
                    // real action
                    GoNextPage();
//                                Toast.makeText(getBaseContext(), LOG_TAG +rMessage, Toast.LENGTH_SHORT).show();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "시간등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 장애 : " + result.getString("message"));
                }
            } catch (JSONException e) {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };

    //http://210.118.74.195:8080/KOG_Server_Rest/rest/Time?nickname=jins&target_time=10:00:00&accomplished_time=00:00:00&date=2014/8/25
    private void achieveTimePutRequest(String _nickname, String target_time, String accomplished_time, String date) {
        try {
            HttpRequestBase requestTime = HttpAPIs.timePut(
                    _nickname
                    , target_time
                    , accomplished_time
                    , date);

            HttpAPIs.background(requestTime, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "achieveTimePutRequest 응답: " + result.toString());
                    if(result != null) {
                        Message msg = achieveTimePutRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        achieveTimePutRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    Log.i(LOG_TAG, "Response Error");
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
