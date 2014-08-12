package com.secsm.keepongoing;

import com.secsm.keepongoing.Shared.KogPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        KogPreference.putString(MainActivity.this, "phoneNo", getPhoneNumber());

        Handler handle = new Handler();
        handle.postDelayed(new splashHandler(), 2000);
    }



    class splashHandler implements Runnable {
        public void run() {
            if(KogPreference.getBoolean(MainActivity.this, "firstStart") || !KogPreference.DEBUG_MODE) {
                Intent intent = new Intent(MainActivity.this, MainmenuActivity.class);
                startActivity(intent);
            }
            else {
				/* if the first running */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            MainActivity.this.finish();
        }
    }

}