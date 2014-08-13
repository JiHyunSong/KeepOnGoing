package com.secsm.keepongoing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.secsm.keepongoing.Shared.KogPreference;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity{

    private static String LOG_TAG="Login Activity";
    // UI references.
    private EditText mNicknameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RequestQueue vQueue;
    private int status_code;
    private String rMessage;
    private Intent intent;
    private String savedNick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intent = getIntent();
        savedNick = intent.getStringExtra("nickname");

        vQueue = Volley.newRequestQueue(this);
        // Set up the login form.
        mNicknameView = (EditText) findViewById(R.id.nickname);
        mPasswordView = (EditText) findViewById(R.id.password);

        // Sign In button
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO : REST apply
                //UserLogin(mNicknameView.getText().toString(), mPasswordView.getText().toString());
                GoNextPage();
            }
        });

        // Sign up (Register) button
        Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        // go to Register page
        mSignUpButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AuthActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(!TextUtils.isEmpty(savedNick))
        {
            mNicknameView.setText(savedNick);
        }

    }

    private void GoNextPage() {
        Toast.makeText(getBaseContext(), "로그인이 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainmenuActivity.class);
        startActivity(intent);
        this.finish();
    }


    private void UserLogin(String nickName, String password) {
        String get_url = KogPreference.REST_URL +
                "User" +
                "?nickname=" + nickName +
                "&password=" + password;

        Log.i(LOG_TAG, "post btn event trigger");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, get_url, null,
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
                                GoNextPage();

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

    private boolean isNicknameValid(String nickName) {
        //TODO: Replace this with your own logic
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return (password.length() >= 4) && (password.length() <= 12);
    }


}



