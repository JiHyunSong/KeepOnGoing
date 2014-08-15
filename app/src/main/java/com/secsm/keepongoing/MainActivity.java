package com.secsm.keepongoing;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.secsm.keepongoing.Shared.KogPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends Activity {

    private static String LOG_TAG="MainActivity";
    private RequestQueue vQueue;
    private int status_code;
    private String rMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        KogPreference.setString(MainActivity.this, "phoneNo", getPhoneNumber());

        vQueue = Volley.newRequestQueue(this);

        Handler handle = new Handler();
        handle.postDelayed(new splashHandler(), 2000);
    }



    class splashHandler implements Runnable {
        public void run() {
            ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobile.isConnected() || wifi.isConnected()){

                if(KogPreference.getBoolean(MainActivity.this, "firstStart") || !KogPreference.DEBUG_MODE) {
                    Intent intent = new Intent(MainActivity.this, MainmenuActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                else {
				/* if the first running */
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                Log.d(LOG_TAG, "Network connect success");
            }else{
                Toast.makeText(getBaseContext(), "네트워크 연결이 불안정합니다.\n애플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();

                Log.d(LOG_TAG, "Network connect fail");
            }
        }
    }


    private void GoNextPage(String nickname) {
        Toast.makeText(getBaseContext(), "로그인이 되었습니다.", Toast.LENGTH_SHORT).show();

        KogPreference.setLogin(MainActivity.this);
        KogPreference.setString(MainActivity.this, "nickName", nickname);
//        Intent intent = new Intent(this, MainmenuActivity.class);
        Intent intent = new Intent(MainActivity.this, TabActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    // TODO : Auto Login
    private void AutoLogin(final String nickName, String password) {
        String get_url = KogPreference.REST_URL +
                "User" +
                "?nickname=" + nickName +
                "&password=" + password;
        Log.i(LOG_TAG, "URL : " + get_url);

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
                                rMessage = response.getString("message");
                                // real action
                                GoNextPage(nickName);

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
                Log.i(LOG_TAG, "Response Error");
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
        vQueue.add(jsObjRequest);
    }

}