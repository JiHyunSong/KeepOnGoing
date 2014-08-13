package com.secsm.keepongoing.Shared;

/**
 * Created by JinS on 2014. 8. 9..
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public final class KogPreference {
    private static String LOG_TAG = "Preference LOG";
    public static Boolean DEBUG_MODE = true;
    private static final String PREF_NAME = "KeepOnGoingPreference";
    private static String LOGIN_TAG = "LOGIN";
    public static String REST_URL="http://210.118.74.195:8080/KOG_Server_Rest/rest/";
    public KogPreference() {
        // not called
    }


    public static void putBoolean(final Context context,
                                  final String key, final boolean value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);

        editor.apply();
    }

    public static void putFloat(final Context context,
                                final String key, final float value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);

        editor.apply();

    }

    public static void putInt(final Context context, final String key, final int value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);

        editor.apply();
    }

    public static void putLong(final Context context, final String key, final long value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);

        editor.apply();
    }

    public static void putString(final Context context, final String key, final String value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);

        editor.apply();
        //ObiLog.i(TAG, "putString() success - k:" + key + ", v:" + value);
    }

    public static boolean getBoolean(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        try {
            boolean v = prefs.getBoolean(key, false);
            return v;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static float getFloat(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            float v = prefs.getFloat(key, Float.NaN);
            return v;
        } catch (ClassCastException e) {
            return Float.NaN;
        }
    }

    public static int getInt(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        try {
            int v = prefs.getInt(key, 0);
            return v;
        } catch (ClassCastException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static long getLong(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        try {
            long v = prefs.getLong(key, Long.MIN_VALUE);
            return v;
        } catch (ClassCastException e) {
            return Long.MIN_VALUE;
        }
    }

    public static String getString(final Context context, final String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        try {
            String v = prefs.getString(key, "");
            return v;
        } catch (ClassCastException e) {

            return "";
        }
    }

    public static boolean isLogin(final Context context){

        try {
            Boolean v = getBoolean(context, LOGIN_TAG);
            return v;
        } catch (ClassCastException e) {

            return false;
        }

    }

    public static void setLogin(final Context context){
        try {
            putBoolean(context, LOGIN_TAG, true);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "set Login ClassCaseException : " + e.getStackTrace());
        }

    }

}
