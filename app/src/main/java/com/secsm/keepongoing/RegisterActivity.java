package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONObject;

public class RegisterActivity extends Activity {

    private static String LOG_TAG = "Profile";
    private String rMessage;
    private EditText nickName, password1, password2, phoneNum;
    private Button btnRegister;
    private int status_code;
    private RequestQueue vQueue;
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


        // TODO : check valid nickname
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // check for valid nickname
                if(isValidProfile())
                {
                    Toast.makeText(getBaseContext(), "회원가입이 되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("nickname", rNickName);
                    startActivity(intent);

                    RegisterActivity.this.finish();

                    // TODO : REST apply
//                    register(nickName.getText().toString(), password1.getText().toString(), "defualt.png", phoneNum.getText().toString());
                }else
                {
                    alertNick.setVisibility(View.VISIBLE);
                    alertPwd.setVisibility(View.VISIBLE);
                    Toast.makeText(getBaseContext(), "입력란을 제대로 확인해주세요!", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    private boolean isValidProfile() {
        rNickName = nickName.getText().toString();
        rPassWord1 = password1.getText().toString();
        rPassWord2 = password2.getText().toString();

        return !TextUtils.isEmpty(rPassWord1) && !TextUtils.isEmpty(rPassWord2) && (rPassWord1.equals(rPassWord2)) && isPasswordValid(rPassWord1) && !TextUtils.isEmpty(rNickName) && isNicknameValid(rNickName) ;
    }

    private boolean isNicknameValid(String nickName) {
        //TODO: Replace this with your own logic
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return (password.length() >= 4) && (password.length() <= 12);
    }


    private void register(String nickName, String password, String image, String phone) {
        String get_url = KogPreference.REST_URL +
                "Register" +
                "?nickname=" + nickName +
                "&password=" + password +
                "&image=" + image +
                "&phone=" + phone;

        Log.i(LOG_TAG, "post btn event trigger");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try{
                            status_code = response.getInt("status");
                            if(status_code == 200){
                                // TODO : message is now OK  -> change
                                rMessage = response.getString("message");
                                // real action
                                Toast.makeText(getBaseContext(), "회원가입이 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainmenuActivity.class);
                                startActivity(intent);
                                RegisterActivity.this.finish();

                            }else {
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
                // TODO
                Log.i(LOG_TAG, "Response Error");
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
