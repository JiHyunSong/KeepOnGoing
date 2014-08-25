package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity {
    ShimmerTextView tv;
    Shimmer shimmer;
    private static String LOG_TAG = "MainActivity";
    private RequestQueue vQueue;
    private int status_code;
    private String rMessage;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "291082441007";

    private GoogleCloudMessaging _gcm;
    private String _regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        KogPreference.setString(MainActivity.this, "phoneNo", getPhoneNumber());
        Encrypt.initKey();
//        vQueue = Volley.newRequestQueue(this);
        MyVolley.init(MainActivity.this);
        vQueue = MyVolley.getRequestQueue();

        _gcm = GoogleCloudMessaging.getInstance(this);
        _regId = getRegistrationId();


        //@민수 수정
        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        shimmer = new Shimmer();
        shimmer.start(tv);


        if (TextUtils.isEmpty(_regId))
            registerInBackground();

        Handler handle = new Handler();
        handle.postDelayed(new splashHandler(), 2000);







    }
//@민수 수정
    public void toggleAnimation(View target) {
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.start(tv);
        }
    }


    class splashHandler implements Runnable {
        public void run() {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobile.isConnected() || wifi.isConnected()) {

                /* if the first running */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                Log.d(LOG_TAG, "Network connect success");
            } else {
                Toast.makeText(getBaseContext(), "네트워크 연결이 불안정합니다.\n애플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();

                Log.d(LOG_TAG, "Network connect fail");
            }
        }
    }

    private void GoTabPage() {
//        Toast.makeText(getBaseContext(), "로그인이 되었습니다.", Toast.LENGTH_SHORT).show();
//        KogPreference.setLogin(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, TabActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    // TODO : Auto Login
    private void AutoLogin(final String nickName, String password) {
        String get_url = KogPreference.REST_URL +
                "User" +
                "?nickname=" + nickName +
                "&password=" + Encrypt.encodingMsg(password);
        Log.i(LOG_TAG, "URL : " + get_url);

        Log.i(LOG_TAG, "post btn event trigger");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, null,
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
                                GoTabPage();

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
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }


    ///////////////////////////////////
    // GCM                           //
    ///////////////////////////////////
    // registration  id를 가져온다.
    private String getRegistrationId() {
//        String registrationId = PreferenceUtil.instance(getApplicationContext()).regId();
        String registrationId = KogPreference.getString(MainActivity.this, "GCMID");
        if (TextUtils.isEmpty(registrationId)) {
            Log.i("MainActivity.java | getRegistrationId", "|Registration not found.|");
            return "";
        }

        return registrationId;
    }

    // gcm 서버에 접속해서 registration id를 발급받는다.
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (_gcm == null) {
                        _gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    _regId = _gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + _regId;
                    Log.e("GCM", msg);
                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(_regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("MainActivity.java | onPostExecute", "|" + msg + "|");
            }
        }.execute(null, null, null);
    }


    // registraion id를 preference에 저장한다.
    private void storeRegistrationId(String regId) {
//                Log.i("MainActivity.java | storeRegistrationId", "|" + "Saving regId on app version " + appVersion + "|");
//        KogPreference.setString(MainActivity.this, regId, "");
        KogPreference.setRegId(MainActivity.this, regId);
        Log.i(LOG_TAG, "reg: " + regId);
//                PreferenceUtil.instance(getApplicationContext()).putRedId(regId);
//                PreferenceUtil.instance(getApplicationContext()).putAppVersion(appVersion);
    }


}