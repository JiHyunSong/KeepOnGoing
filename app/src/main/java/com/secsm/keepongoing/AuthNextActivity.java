package com.secsm.keepongoing;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.KogPreference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthNextActivity extends BaseActivity {
    Intent intent;
    Button btnOk;
    Button btnGoBack;
    EditText txtInputNo;
    int certiNo;
    String phoneNo;
    static String LOG_TAG = "AuthNext Activity";
    private BroadcastReceiver msgReceiver;
    private Pattern p = Pattern.compile("(\\d{4})");
    private Matcher m;

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

        msgReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "BroadcastReceiver onReceive()");

                if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
                {
                    String sb = "";
                    Bundle b = intent.getExtras();

                    if(b != null){
                        Object[] pdusObj = (Object[]) b.get("pdus");

                        SmsMessage[] messages = new SmsMessage[pdusObj.length];
                        for(int i=0; i<pdusObj.length; i++)
                        {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        }


                        for(SmsMessage currentMessage : messages)
                        {

                            m = p.matcher(currentMessage.getMessageBody());
                            if(m.find()){
                                Log.i(LOG_TAG, "match!");
                                Log.i(LOG_TAG, "" + m.group(0));

                                if(receiveHandler != null) {
                                    Message msg = receiveHandler.obtainMessage();
                                    Bundle sendMsg = new Bundle();
                                    sendMsg.putString("receiveNum", m.group(0));
                                    msg.setData(sendMsg);
                                    receiveHandler.sendMessage(msg);
                                }

                            }
                            sb = sb + "문자열 수신되었습니다.1\n";
                            sb = sb + "[발신자전화번호].\n";
                            sb = sb + currentMessage.getOriginatingAddress();
                            sb = sb + "\n[수신메세지]\n";
                            sb = sb + currentMessage.getMessageBody(); //   [KeepOnGoing]인증번호는 9449입니다.
                        }

//                        Log.i(LOG_TAG, sb);
                    }
                }
            }
        };

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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(msgReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(msgReceiver);
    }

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

    public Handler receiveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                Bundle b = msg.getData();
                txtInputNo.setText(b.getString("receiveNum"));
            } catch (Exception e) {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };
}
