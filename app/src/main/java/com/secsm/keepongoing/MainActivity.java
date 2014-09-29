package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity {
    ShimmerTextView tv;
    Shimmer shimmer;
    private static String LOG_TAG = "MainActivity";
    private String rMessage;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "7537883841";

    private GoogleCloudMessaging _gcm;
    private String _regId;

    private static boolean isLoginDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Encrypt.initKey();

        _gcm = GoogleCloudMessaging.getInstance(this);
        _regId = getRegistrationId();


        //@민수 수정
        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        shimmer = new Shimmer();
        shimmer.start(tv);


        if (TextUtils.isEmpty(_regId))
            registerInBackground();

        isLoginDone = false;
        Handler handle = new Handler();
        handle.postDelayed(new splashHandler(), 2500);


        Log.i(LOG_TAG, "KogPreference.isLogin(MainActivity.this) : " + KogPreference.isLogin(MainActivity.this) );
        Log.i(LOG_TAG, "KogPreference.isAutoLogin(MainActivity.this) : " + KogPreference.isAutoLogin(MainActivity.this) );
        if (KogPreference.isAutoLogin(MainActivity.this) && KogPreference.isLogin(MainActivity.this)) {
            Log.i(LOG_TAG, "UserLogin try" );
            UserLogin(KogPreference.getNickName(MainActivity.this), KogPreference.getPassword(MainActivity.this));
        }
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
//                    GoLoginPage();
                    isLoginDone = true;
                }
                else {
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
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
                    , KogPreference.getRegId(MainActivity.this));

            HttpAPIs.background(requestLogin, new CallbackResponse() {
                public void success(HttpResponse response) {

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
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
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
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                MainActivity.this.finish();
//                Log.d(LOG_TAG, "Network connect success");

                Log.i(LOG_TAG, "isLoginDone : " + isLoginDone);
                if(isLoginDone) {
                    GoTabPage();
                }else{
                    GoLoginPage();
                }
            } else{
                Toast.makeText(getBaseContext(), "네트워크 연결이 불안정합니다.\n애플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();

                Log.d(LOG_TAG, "Network connect fail");
            }
        }
    }

    private void GoTabPage() {
        Intent intent = new Intent(MainActivity.this, TabActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void GoLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    ///////////////////////////////////
    // GCM                           //
    ///////////////////////////////////
    // registration  id를 가져온다.
    private String getRegistrationId() {
//        String registrationId = PreferenceUtil.instance(getApplicationContext()).regId();
        String registrationId = KogPreference.getString(MainActivity.this, "GCMID");
        Log.i("GCM", "registrationId : " + registrationId);
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
        KogPreference.setRegId(MainActivity.this, regId);
        Log.i(LOG_TAG, "reg: " + regId);
    }
}