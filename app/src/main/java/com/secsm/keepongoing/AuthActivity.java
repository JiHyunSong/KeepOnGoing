package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.secsm.keepongoing.R;

public class AuthActivity extends Activity {
    Spinner nationalNo;
    Button btnSendSMS;
    EditText txtPhoneNo;
    String countryNo = "82";
    String phoneNo;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        nationalNo = (Spinner)findViewById(R.id.nationalNumber);
        btnSendSMS = (Button)findViewById(R.id.button);
        txtPhoneNo = (EditText)findViewById(R.id.phone);

        //add Spinner select Event
        nationalNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                switch(position){
                    case 0:	//Korea
                        countryNo = "82";
                        break;
                    case 1:	//USA
                        countryNo = "1";
                        break;
                    case 2:	//Japan
                        countryNo = "81";
                        break;
                    case 3:	//Australia
                        countryNo = "61";
                        break;
                    case 4:	//Canada
                        countryNo = "1";
                        break;
                    case 5:	//UK
                        countryNo = "44";
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        // initial phone number setting
        txtPhoneNo.setText(getPhoneNumber());
        intent = getIntent();

        //add Button Click Event
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneNo = txtPhoneNo.getText().toString();
                if(phoneNo.length()>0) {
                    Intent intent = new Intent(AuthActivity.this, AuthNextActivity.class);
                    intent.putExtra("phoneNo", phoneNo);
                    startActivity(intent);
                    AuthActivity.this.finish();
                }
                else
                    Toast.makeText(getBaseContext(), "Please enter both phone number and message.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get phone number from activity
    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auth, menu);
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
