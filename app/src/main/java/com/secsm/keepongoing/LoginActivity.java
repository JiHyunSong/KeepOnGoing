package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private static String LOG_TAG = "Login Activity";
    // UI references.
    private CheckBox login_auto_login_cb;
    private EditText mNicknameView, mPasswordView;
    private View mProgressView, mLoginFormView;
    private String rMessage;
    private Intent intent;
    private Button mSignUpButton, mSignInButton;
    private BootstrapButton easterEggButton;
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    /** 리소스 초기화 */
    private void init() {
        mNicknameView = (EditText) findViewById(R.id.nickname);
        mPasswordView = (EditText) findViewById(R.id.password);

        mNicknameView.setPrivateImeOptions("defaultInputmode=english;");
        mPasswordView.setPrivateImeOptions("defaultInputmode=english;");

        login_auto_login_cb = (CheckBox) findViewById(R.id.login_auto_login_cb);

        loginProgressBar = (ProgressBar) findViewById(R.id.login_progress);

        // Sign In button
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidProfile()) {
                    setAllDisable();
                    UserLogin(mNicknameView.getText().toString(), mPasswordView.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(), "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign up (Register) button
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AuthActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        //   if (KogPreference.DEBUG_MODE) {
        easterEggButton = (BootstrapButton) findViewById(R.id.easter_egg_button);
        easterEggButton.setVisibility(View.GONE);
        // go to Register page
        easterEggButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String easter_msg = "안녕";
                GoNextPage(mNicknameView.getText().toString(), mPasswordView.getText().toString());
            }
        });

        //}

        if (KogPreference.isAutoLogin(LoginActivity.this)) {
            mNicknameView.setText(KogPreference.getNickName(LoginActivity.this));
            mPasswordView.setText(KogPreference.getPassword(LoginActivity.this));
            setAllDisable();
            UserLogin(KogPreference.getNickName(LoginActivity.this), KogPreference.getPassword(LoginActivity.this));
        } else {
            String savedNick = KogPreference.getNickName(LoginActivity.this);
            mNicknameView.setText(savedNick);
        }
    }

    /** 다음화면으로 */
    private void GoNextPage(String nickname, String password) {
        if (login_auto_login_cb.isChecked()) {
            KogPreference.setAutoLogin(LoginActivity.this, true);
            KogPreference.setPassword(LoginActivity.this, password);
        } else {
            KogPreference.setAutoLogin(LoginActivity.this, false);
            KogPreference.setPassword(LoginActivity.this, "");
        }

//        Toast.makeText(LoginActivity.this, "로그인이 되었습니다.", Toast.LENGTH_SHORT).show();

        KogPreference.setLogin(LoginActivity.this, true);
        KogPreference.setNickName(LoginActivity.this, nickname.trim());

        Intent intent = new Intent(LoginActivity.this, TabActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    Handler UserLoginSuccessHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    //rMessage = result.getString("message");
                    GoNextPage(mNicknameView.getText().toString(), mPasswordView.getText().toString());
                }
                else if (statusCode == 1001) {
                    GoNextPage(mNicknameView.getText().toString(), mPasswordView.getText().toString());
                    Toast.makeText(getBaseContext(), "다른기기에서 로그아웃 됩니다", Toast.LENGTH_SHORT).show();
                }
                else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "아이디와 패스워드를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                    }                }
            }catch (JSONException e)
            {
                e.printStackTrace();
                errorHandler.sendEmptyMessage(1);
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

    /** 로그인 요청 */
    private void UserLogin(final String nickName, final String password) {

        try {
            HttpRequestBase requestLogin = HttpAPIs.login(
                    nickName.trim()
                    , Encrypt.encodingMsg(password)
                    , KogPreference.getRegId(LoginActivity.this));

            HttpAPIs.background(requestLogin, new CallbackResponse() {
                public void success(HttpResponse response) {

                    loginHandler.sendEmptyMessage(1);

                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    try {
                        if(result != null){
                            Message msg = UserLoginSuccessHandler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("JSONData", result.toString());
                            msg.setData(b);
                            UserLoginSuccessHandler.sendMessage(msg);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorHandler.sendEmptyMessage(1);
                    }
                }

                public void error(Exception e) {
                    loginHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler loginHandler = new Handler(){

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

    /** ID / PW 유효성 체크 */
    private boolean isValidProfile() {
        String rNickName = mNicknameView.getText().toString();
        String rPassWord = mPasswordView.getText().toString();

        return !TextUtils.isEmpty(rPassWord) && isPasswordValid(rPassWord) && !TextUtils.isEmpty(rNickName) && isNicknameValid(rNickName);
    }

    /** ID 유효성 체크 */
    private boolean isNicknameValid(String nickName) {
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    /** PW  유효성 체크 */
    private boolean isPasswordValid(String password) {
        return (password.length() >= 4) && (password.length() <= 12);
    }

    /** 리소스 활성화 */
    private void setAllEnable() {
        login_auto_login_cb.setEnabled(true);
        mNicknameView.setEnabled(true);
        mPasswordView.setEnabled(true);
        mSignInButton.setEnabled(true);
        mSignUpButton.setEnabled(true);
        easterEggButton.setEnabled(true);
        loginProgressBar.setVisibility(View.GONE);
    }

    /** 리소스 비활성화 */
    private void setAllDisable() {
        login_auto_login_cb.setEnabled(false);
        mNicknameView.setEnabled(false);
        mPasswordView.setEnabled(false);
        mSignInButton.setEnabled(false);
        mSignUpButton.setEnabled(false);
        easterEggButton.setEnabled(false);
        loginProgressBar.setVisibility(View.VISIBLE);
    }
}



