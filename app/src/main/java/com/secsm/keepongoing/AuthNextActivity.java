package com.secsm.keepongoing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class AuthNextActivity extends BaseActivity {
    Intent intent;
    Button btnOk;
    Button btnGoBack;
    EditText txtInputNo;
    int certiNo;
    String phoneNo;
    private RequestQueue vQueue;
    private int status_code;
    private String rMessage;
    static String LOG_TAG = "AuthNext Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_next);

//        vQueue = Volley.newRequestQueue(this);
        vQueue = MyVolley.getRequestQueue(AuthNextActivity.this);

        intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");

        certiNo = setRandomNumber();
        //sendSMS(phoneNo);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnGoBack = (Button) findViewById(R.id.btnGoBackToAuth);
        txtInputNo = (EditText) findViewById(R.id.txtInputNo);

        AuthNumRegister(phoneNo, certiNo);

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (Integer.toString(certiNo).equals(txtInputNo.getText().toString())) {
                    AuthConfirm(phoneNo, certiNo);
                } else {
                    Toast.makeText(getBaseContext(), "인증에 실패했습니다.\n다시 요청해 주세요.", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, Integer.toString(certiNo));

                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                sendSMS(phoneNo);
//                Toast.makeText(getBaseContext(), "재전송 했습니다.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "되돌아 갑니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AuthNextActivity.this, AuthActivity.class);
                intent.putExtra("phoneNo", phoneNo);
                startActivity(intent);
                AuthNextActivity.this.finish();
            }
        });
    }

    private void GoNextPage() {
        Toast.makeText(getBaseContext(), "인증이 완료됐습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AuthNextActivity.this, RegisterActivity.class);
        intent.putExtra("phoneNo", phoneNo);
        startActivity(intent);
        AuthNextActivity.this.finish();
    }

    /* sending SMS by self phone number */
    private void sendSMS(String phoneNumber) {
//        certiNo = setRandomNumber();
        // TODO : REST apply
        // AuthRegister(phoneNo, certiNo);
        Log.i(LOG_TAG, Integer.toString(certiNo));
        String message = "[KeepOnGoing]인증번호는 " + certiNo + "입니다.";
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


    // register my phone number and random_generated_number
    private void AuthNumRegister(String phone, int random_num) {
        String get_url = KogPreference.REST_URL +
                "Auth"; //+
//                "?phone=" + phone +
//                "&random_num=" + random_num;
        JSONObject sendBody = new JSONObject();
        try{
            sendBody.put("randomnum", random_num);
            sendBody.put("phone", URLEncoder.encode(phone, "UTF-8"));
            Log.i(LOG_TAG, "sendBody : " + sendBody.toString());
        }catch (Exception e)
        {
            Log.e(LOG_TAG, " sendBody e : " + e.toString());
        }

        Log.i(LOG_TAG, "post btn event trigger");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, sendBody,
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
                                Log.i(LOG_TAG, "receive 200 OK");
                                sendSMS(phoneNo);
                                // nothing to do
                            } else if (status_code == 1001) {
                                Toast.makeText(getBaseContext(), "이미 가입된 번호입니다.\n중복가입 하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AuthNextActivity.this, LoginActivity.class);
                                startActivity(intent);
                                AuthNextActivity.this.finish();
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
                Log.i(LOG_TAG, "Response Error : " + error.toString());

                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
        vQueue.start();
    }


    // check my phone number and random_num from input by person
    private void AuthConfirm(String phone, int random_num) {
        String get_url = KogPreference.REST_URL +
                "Auth" +
                "?phone=" + phone +
                "&random_num=" + random_num;

        Log.i(LOG_TAG, "post btn event trigger");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
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
                                Log.i(LOG_TAG, "receive 200 OK");

                                GoNextPage();
                            } else {

                                Toast.makeText(getBaseContext(), "인증에 실패했습니다.\n다시 요청해 주세요.", Toast.LENGTH_SHORT).show();
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

    /* making random number */
    private int setRandomNumber() {
        int randomNo = (int) (Math.random() * 9000) + 1000;
        Log.i(LOG_TAG, Integer.toString(randomNo));
        return randomNo;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.auth_next, menu);
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
