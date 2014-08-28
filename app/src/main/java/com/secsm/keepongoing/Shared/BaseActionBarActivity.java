package com.secsm.keepongoing.Shared;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by JinS on 2014. 8. 28..
 */
public class BaseActionBarActivity extends ActionBarActivity {
    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()) {

                /* if the first running */
        } else {
            Toast.makeText(getBaseContext(), "네트워크 연결이 불안정합니다.\n애플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }
}
