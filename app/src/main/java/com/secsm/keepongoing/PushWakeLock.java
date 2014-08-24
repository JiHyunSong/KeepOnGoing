package com.secsm.keepongoing;

import android.content.Context;
import android.os.PowerManager;

//for the wake rock with vibrating
public class PushWakeLock {
    private static final String TAG = "wakelock";
    private static PowerManager.WakeLock mCpuWakeLock;

    static void acquireCpuWakeLock(Context context) {
        if(mCpuWakeLock != null) {
            return ;
        }

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG);
        mCpuWakeLock.acquire();
    }

    static void releaseCpuLock() {
        if(mCpuWakeLock != null) {
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }
}