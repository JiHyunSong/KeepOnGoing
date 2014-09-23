package com.secsm.keepongoing.Shared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.secsm.keepongoing.LoginActivity;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.SnowWiFiMonitor;

public class BaseActivity extends Activity {
    protected SnowWiFiMonitor m_SnowWifiMonitor = null;
    private static String LOG_TAG = "BaseActivity";
    private int previous_state=-5;
    protected SnowWiFiMonitor.OnChangeNetworkStatusListener SnowChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void registerWifiBroadcastReceiver() {
        m_SnowWifiMonitor = new SnowWiFiMonitor(this);
        m_SnowWifiMonitor.setOnChangeNetworkStatusListener(SnowChangedListener);
        Log.i(LOG_TAG, "register m_SnowWifiMonitor in baseActivity");
        registerReceiver(m_SnowWifiMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(m_SnowWifiMonitor, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        registerReceiver(m_SnowWifiMonitor, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        SnowChangedListener = new SnowWiFiMonitor.OnChangeNetworkStatusListener()
        {
            @Override
            public void OnChanged(int status)
            {
                switch(status)
                {
                    case SnowWiFiMonitor.WIFI_STATE_DISABLED:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_DISABLED");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_DISABLING:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_DISABLING");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_ENABLED:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_ENABLED");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_ENABLING:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_ENABLING");
                        break;

                    case SnowWiFiMonitor.WIFI_STATE_UNKNOWN:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_UNKNOWN");
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_CONNECTED:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_CONNECTED");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_CONNECTED;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_CONNECTED) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_CONNECTED;
                                Toast.makeText(getBaseContext(), "네트워크 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_CONNECTING:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_CONNECTING");
                        if(previous_state==-5) {
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_CONNECTING;
                        }
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_CONNECTING) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_CONNECTING;
                                Toast.makeText(getBaseContext(), "네트워크 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_DISCONNECTED");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_DISCONNECTED;
                                Toast.makeText(getBaseContext(), "네트워크 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_DISCONNECTING:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_DISCONNECTING");
                        if(previous_state==-5) {
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_DISCONNECTING;
                        }
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_DISCONNECTING) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_DISCONNECTING;
                                Toast.makeText(getBaseContext(), "네트워크 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_SUSPENDED:
                        Log.i(LOG_TAG, "[WifiMonitor] NETWORK_STATE_SUSPENDED");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_SUSPENDED;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_SUSPENDED) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_SUSPENDED;
                            }
                        }
                        break;

                    case SnowWiFiMonitor.NETWORK_STATE_UNKNOWN:
                        Log.i(LOG_TAG, "[WifiMonitor] WIFI_STATE_UNKNOWN");
                        if(previous_state==-5)
                            previous_state = SnowWiFiMonitor.NETWORK_STATE_UNKNOWN;
                        else {
                            if(previous_state!=SnowWiFiMonitor.NETWORK_STATE_UNKNOWN) {
                                previous_state = SnowWiFiMonitor.NETWORK_STATE_UNKNOWN;
                            }
                        }

                        break;

                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerWifiBroadcastReceiver();

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()) {

                /* if the first running */
        } else {
            Toast.makeText(getBaseContext(), "네트워크 연결이 불안정합니다.\n애플리케이션을 종료합니다.", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(m_SnowWifiMonitor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
