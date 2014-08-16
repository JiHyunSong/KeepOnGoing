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
    private static String AUTOLOGIN_TAG = "AUTO_LOGIN";
    private static String NICKNAME_TAG = "NICK_NAME";
    private static String PASSWORD_TAG = "PASSWORD";
    private static String GCM_TAG = "GCMID";
    public static String REST_URL="http://210.118.74.195:8080/KOG_Server_Rest/rest/";
    public KogPreference() {
        // not called
    }


    public static void setBoolean(final Context context,
                                  final String key, final boolean value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);

        editor.apply();
    }

    public static void setFloat(final Context context,
                                final String key, final float value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);

        editor.apply();

    }

    public static void setInt(final Context context, final String key, final int value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);

        editor.apply();
    }

    public static void setLong(final Context context, final String key, final long value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);

        editor.apply();
    }

    public static void setString(final Context context, final String key, final String value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);

        editor.apply();
        //ObiLog.i(TAG, "setString() success - k:" + key + ", v:" + value);
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

    /////////////////////////////
    // external method         //
    /////////////////////////////

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
            setBoolean(context, LOGIN_TAG, true);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "set Login ClassCaseException : " + e.getStackTrace());
        }
    }

    public static boolean isAutoLogin(final Context context){
        try {
            Boolean v = getBoolean(context, AUTOLOGIN_TAG);
            return v;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static void setAutoLogin(final Context context, final boolean value) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(AUTOLOGIN_TAG, value);
        editor.apply();
    }

    public static String getNickName(final Context context){
        try{
            return getString(context, NICKNAME_TAG);
        }catch (ClassCastException e){
            Log.e(LOG_TAG, "getNickName fail, ClassCaseException : " + e.getStackTrace());
            return "";
        }
    }

    public static void setNickName(final Context context, final String value) {
        try {
            setString(context, NICKNAME_TAG, value);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "setNickName ClassCaseException : " + e.getStackTrace());
        }
    }


    public static String getPassword(final Context context){
        try{
            return getString(context, PASSWORD_TAG);
        }catch (ClassCastException e){
            Log.e(LOG_TAG, "getPassword fail, ClassCaseException : " + e.getStackTrace());
            return "";
        }
    }

    public static void setPassword(final Context context, final String value) {
        try {
            setString(context, PASSWORD_TAG, value);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "setPassword ClassCaseException : " + e.getStackTrace());
        }
    }

    public static void setRegId(final Context context, final String value) {
        try {
            setString(context, GCM_TAG, value);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "setPassword ClassCaseException : " + e.getStackTrace());
        }
    }

    public static String getRegId(final Context context){
        try{
            return getString(context, GCM_TAG);
        }catch (ClassCastException e){
            Log.e(LOG_TAG, "get Registration ID fail, ClassCaseException : " + e.getStackTrace());
            return "";
        }
    }


}
