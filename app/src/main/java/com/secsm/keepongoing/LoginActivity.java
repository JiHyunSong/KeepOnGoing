package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private static String LOG_TAG = "Login Activity";
    // UI references.
    private CheckBox login_auto_login_cb;
    private EditText mNicknameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RequestQueue vQueue;
    private int status_code;
    private String rMessage;
    private Intent intent;
    private String savedNick;
    private Button mSignUpButton;
    private Button mSignInButton;
    private BootstrapButton easterEggButton;
    private FrameLayout login_fl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        intent = getIntent();
        savedNick = intent.getStringExtra("nickname");

        vQueue = Volley.newRequestQueue(this);
        // fragment
        login_fl = (FrameLayout) findViewById(R.id.login_fl);

        // Set up the login form.
        mNicknameView = (EditText) findViewById(R.id.nickname);
        mPasswordView = (EditText) findViewById(R.id.password);

        login_auto_login_cb = (CheckBox) findViewById(R.id.login_auto_login_cb);

        // Sign In button
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidProfile()) {
                    UserLogin(mNicknameView.getText().toString(), mPasswordView.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(), "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign up (Register) button
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        // go to Register page
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AuthActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (KogPreference.isAutoLogin(LoginActivity.this)) {
            mNicknameView.setText(KogPreference.getNickName(LoginActivity.this));
            mPasswordView.setText(KogPreference.getPassword(LoginActivity.this));

            UserLogin(KogPreference.getNickName(LoginActivity.this), KogPreference.getPassword(LoginActivity.this));
        } else if (!TextUtils.isEmpty(savedNick)) {
            mNicknameView.setText(savedNick);
        } else {
            savedNick = KogPreference.getNickName(LoginActivity.this);
            mNicknameView.setText(savedNick);
        }

        if (KogPreference.DEBUG_MODE) {
            easterEggButton = (BootstrapButton) findViewById(R.id.easter_egg_button);
            easterEggButton.setVisibility(View.VISIBLE);
            // go to Register page
            easterEggButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String easter_msg = "안녕";
//                    SimpleDialogFragment.createBuilder(LoginActivity.this, android.support.v4.app.FragmentManager())
//                            .setMessage(easter_msg).show();
                    GoNextPage(mNicknameView.getText().toString(), mPasswordView.getText().toString());


                }
            });

        }

    }

    private void GoNextPage(String nickname, String password) {
        if (login_auto_login_cb.isChecked()) {
            KogPreference.setAutoLogin(LoginActivity.this, true);
            KogPreference.setPassword(LoginActivity.this, password);
        } else {
            KogPreference.setAutoLogin(LoginActivity.this, false);
            KogPreference.setPassword(LoginActivity.this, "");
        }

        Toast.makeText(getBaseContext(), "로그인이 되었습니다.", Toast.LENGTH_SHORT).show();

        KogPreference.setLogin(LoginActivity.this, true);
        //KogPreference.setString(LoginActivity.this, "nickName", nickname);
        KogPreference.setNickName(LoginActivity.this, nickname);
//        Intent intent = new Intent(this, MainmenuActivity.class);
        Intent intent = new Intent(LoginActivity.this, TabActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }


    private void UserLogin(final String nickName, final String password) {
//        login_fl.setVisibility(View.VISIBLE);

        String get_url = KogPreference.REST_URL +
                "User" +
                "?nickname=" + nickName +
                "&password=" + Encrypt.encodingMsg(password) +
                "&gcmid=" + KogPreference.getRegId(LoginActivity.this);
        Log.i(LOG_TAG, "URL : " + get_url);

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
//                                login_fl.setVisibility(View.GONE);
                                GoNextPage(nickName, password);
                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "아이디와 패스워드를 확인해주세요", Toast.LENGTH_SHORT).show();
                            } else {
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
//                login_fl.setVisibility(View.GONE);

                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }

    private boolean isValidProfile() {
        String rNickName = mNicknameView.getText().toString();
        String rPassWord = mPasswordView.getText().toString();

        return !TextUtils.isEmpty(rPassWord) && isPasswordValid(rPassWord) && !TextUtils.isEmpty(rNickName) && isNicknameValid(rNickName);
    }

    private boolean isNicknameValid(String nickName) {
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 4) && (password.length() <= 12);
    }


}



