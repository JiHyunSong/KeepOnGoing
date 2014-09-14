package com.secsm.keepongoing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class AuthNextActivity extends BaseActivity {
    Intent intent;
    Button btnOk;
    Button btnGoBack;
    EditText txtInputNo;
    int certiNo;
    String phoneNo;
    static String LOG_TAG = "AuthNext Activity";

    void setAllEnable(){
        btnOk.setEnabled(true);
        btnGoBack.setEnabled(true);
    }
    void setAllDisable(){
        btnOk.setEnabled(false);
        btnGoBack.setEnabled(false);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_next);

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
                    Toast.makeText(getBaseContext(), "인증번호가 틀렸습니다.\n다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, Integer.toString(certiNo));

                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
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
    Handler errorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Toast.makeText(getBaseContext(), "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /** AuthNumRegister
     * statusCode == 200 => send SMS to phone num
     * statusCode == 1001 => auth duplicate! go back to the back page */
    Handler AuthNumRegisterHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
//                int statusCode = Integer.parseInt(result.getString("httpStatusCode"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    Log.i(LOG_TAG, "receive 200 OK");
                    sendSMS(phoneNo);

                } else if (statusCode == 1002) {
                    Toast.makeText(getBaseContext(), "이미 가입된 번호입니다.\n중복가입 하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AuthNextActivity.this, LoginActivity.class);
                    startActivity(intent);
                    AuthNextActivity.this.finish();
                } else {
                    Toast.makeText(getBaseContext(), "잘못된 요청입니다", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    // register my phone number and random_generated_number
    private void AuthNumRegister(final String phone, final int random_num) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.authPost(URLEncoder.encode(phone, "UTF-8"), random_num);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
//                    JSONObject result = HttpAPIs.getJSONData(response);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                        if(result != null) {
                            Message msg = AuthNumRegisterHandler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("JSONData", result.toString());
                            msg.setData(b);
                            AuthNumRegisterHandler.sendMessage(msg);
                        }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                    //Toast.makeText(LoginActivity.this, "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Handler AuthConfirmHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
//                                rMessage = result.getString("message");
                    // real action
                    Log.i(LOG_TAG, "receive 200 OK");
                    GoNextPage();
                } else {
                    Toast.makeText(getBaseContext(), "인증번호가 틀렸습니다.\n다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (JSONException e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };

    // check my phone number and random_num from input by person
    private void AuthConfirm(String phone, int random_num) {

        try {
            HttpRequestBase requestAuthConfirm = HttpAPIs.authGet(URLEncoder.encode(phone, "UTF-8"), random_num);
            HttpAPIs.background(requestAuthConfirm, new CallbackResponse() {
                public void success(HttpResponse response) {

                    baseHandler.sendEmptyMessage(1);

                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null){
                        Message msg = AuthConfirmHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        AuthConfirmHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            errorHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }
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
