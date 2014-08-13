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

public class AuthNextActivity extends Activity {
    Intent intent;
    Button btnOk;
    Button btnGoBack;
    EditText txtInputNo;
    int certiNo;
    String phoneNo;
    static String LOG_TAG="AuthNext Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_next);


        intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");
        sendSMS(phoneNo);

        btnOk = (Button)findViewById(R.id.btnOk);
        btnGoBack = (Button)findViewById(R.id.btnGoBackToAuth);
        txtInputNo = (EditText)findViewById(R.id.txtInputNo);

        btnOk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                if(Integer.toString(certiNo).equals(txtInputNo.getText().toString())) {

                    Toast.makeText(getBaseContext(), "인증이 완료됐습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AuthNextActivity.this, RegisterActivity.class);
                    intent.putExtra("phoneNo", phoneNo);
                    startActivity(intent);
                    AuthNextActivity.this.finish();
                }
                else {
                    Toast.makeText(getBaseContext(), "인증에 실패했습니다.\n다시 요청해 주세요.", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, Integer.toString(certiNo));

                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                sendSMS(phoneNo);
//                Toast.makeText(getBaseContext(), "재전송 했습니다.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "인증이 완료됐습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AuthNextActivity.this, AuthActivity.class);
                intent.putExtra("phoneNo", phoneNo);
                startActivity(intent);
                AuthNextActivity.this.finish();
            }
        });
    }

    /* sending SMS by self phone number */
    private void sendSMS(String phoneNumber){
        certiNo = setRandomNumber();
        Log.i(LOG_TAG, Integer.toString(certiNo));
        String message = "[KicTalk]인증번호는 " + certiNo +"입니다.";
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }

    /* making random number */
    private int setRandomNumber(){
        int randomNo = (int)(Math.random() * 9000) + 1000 ;
        Log.i(LOG_TAG, Integer.toString(randomNo));
        return randomNo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auth_next, menu);
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
