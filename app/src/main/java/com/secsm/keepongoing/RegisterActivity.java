package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONObject;

public class RegisterActivity extends Activity {

    private static String LOG_TAG = "Profile";
    private String rMessage;
    private RequestQueue vQueue;
    private EditText nickName, password1, password2, phoneNum;
    private Button btnRegister;
    private int status_code;
    private Intent intent;
    private String phoneNo;
    String rNickName;
    String rPassWord1;
    String rPassWord2;
    TextView alertPwd;
    TextView alertNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");
        btnRegister = (Button) findViewById(R.id.btnRegister);
        nickName = (EditText) findViewById(R.id.txtNickName);
        password1 = (EditText) findViewById(R.id.txtPassword1);
        password2 = (EditText) findViewById(R.id.txtPassword2);
        phoneNum = (EditText) findViewById(R.id.txtPhoneNum);
        alertPwd = (TextView) findViewById(R.id.tvPwdAlert);
        alertNick = (TextView) findViewById(R.id.tvNickAlert);
        phoneNum.setText(phoneNo);
        phoneNum.setFocusable(false);
        vQueue = Volley.newRequestQueue(this);

        // TODO : register GCM


        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidProfile()) {
                    register(nickName.getText().toString(), password1.getText().toString(), "profile_default.png", phoneNum.getText().toString());
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


    private void register(String nickName, String password, String image, String phone) {
        String get_url = KogPreference.REST_URL +
                "Register" +
                "?nickname=" + nickName +
                "&password=" + Encrypt.encodingMsg(password) +
                "&image=" + image +
                "&phone=" + phone +
                "&gcmid=" + KogPreference.getRegId(RegisterActivity.this);

        Log.i(LOG_TAG, "post btn event trigger");

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
                                GoNextPage();
                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "회원가입이 불가능합니다.", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
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
