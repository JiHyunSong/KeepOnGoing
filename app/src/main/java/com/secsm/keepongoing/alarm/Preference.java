package com.secsm.keepongoing.Alarm;

/**
 * Created by kim on 2014-08-13.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

public final class Preference {
    private static String TAG = "Preference";
    private static String mPrefName = "ObigoBaiduMusic";

    public static final String KEY_CALL_LEVEL = "key_call_level";
    public static final String KEY_SMS_LEVEL = "key_sms_level";
    public static final String KEY_SMS_KEYWORD = "key_sms_keyword";
    public static final String KEY_ALARM_LEVEL = "key_alarm_level";
    public static final String KEY_ALARM_LOCATION = "key_alarm_location";


    private Preference() {
        // not called
    }

    public static void putKeyword(final Context context, final ArrayList<String> _keyword) {
        if (_keyword.size() == 0)
            return;

        String str = "";
        for (int i=0; i<_keyword.size(); i++) {
            str = str + _keyword.get(i) + "/";
        }
        setString(context, KEY_SMS_KEYWORD, str);
    }

    public static void putBoolean(final Context context,
                                  final String key, final boolean value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);

        editor.apply();
    }

    public static void setFloat(final Context context,
                                final String key, final float value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);

        editor.apply();
    }

    public static void setInt(final Context context, final String key, final int value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);

        editor.apply();
    }

    public static void setLong(final Context context, final String key, final long value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);

        editor.apply();
    }

    public static void setString(final Context context, final String key, final String value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);

        editor.apply();
        //ObiLog.i(TAG, "setString() success - k:" + key + ", v:" + value);
    }

    public static boolean getBoolean(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            boolean v = prefs.getBoolean(key, false);
            Log.e("test", "v: " + v);
            return v;
        } catch (ClassCastException e) {
            Log.e("test", "e: " + e.toString());
            return false;
        }
    }

    public static float getFloat(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        try {
            float v = prefs.getFloat(key, Float.NaN);
            return v;
        } catch (ClassCastException e) {
            return Float.NaN;
        }
    }

    public static int getInt(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            int v = prefs.getInt(key, 0);
            return v;
        } catch (ClassCastException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static long getLong(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            long v = prefs.getLong(key, 0);
            return v;
        } catch (ClassCastException e) {
           return Long.MIN_VALUE;
           // return Long.MIN_VALUE;
        }
    }


    public static String getString(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            String v = prefs.getString(key, "");
            return v;
        } catch (ClassCastException e) {

            return "";
        }
    }

    public static String getPreferenceString(Context context, String key, String defValue) {
        return getPreferenceString(context, mPrefName, key, defValue);
    }
    public static String getPreferenceString(Context context, String name, String key, String defValue) {
        if (context == null) {
            return "";
        }

        SharedPreferences sharedPreferences = null;

        try {
            sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        if (sharedPreferences == null) {
            return "";
        }

        return sharedPreferences.getString(key, defValue);
    }

    public static boolean setPreferenceString(Context context, String key, String value) {
        return setPreferenceString(context, mPrefName, key, value);
    }
    public static boolean setPreferenceString(Context context, String name, String key, String value) {
        if (context == null) {
            return false;
        }
        SharedPreferences pref = null;
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        return editor.commit();
    }
}