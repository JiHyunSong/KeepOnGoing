package com.secsm.keepongoing.Shared;

/**
 * Created by JinS on 2014. 8. 9..
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.secsm.keepongoing.Adapters.InviteRoom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

public final class KogPreference {
    private static String LOG_TAG = "Preference LOG";
    public static Boolean DEBUG_MODE = true;
    public static Boolean NO_AUTH = false;
    public static Boolean NO_SOCKET = false;
    private static final String PREF_NAME = "KeepOnGoingPreference";
    private static String LOGIN_TAG = "LOGIN";
    private static String AUTOLOGIN_TAG = "AUTO_LOGIN";
    private static String NICKNAME_TAG = "NICK_NAME";
    private static String PASSWORD_TAG = "PASSWORD";
    private static String RID_TAG = "RID";
    private static String GCM_TAG = "GCMID";
    private static String QUIZ_NUM_TAG = "QUIZNUM";
    private static String INVITE_ROOM = "INVITE_ROOM";
    public static String REST_URL = "http://210.118.74.195:8080/KOG_Server_Rest/rest/";
    public static String UPLOAD_PROFILE_URL = "http://210.118.74.195:8080/KOG_Server_Rest/rest/UserImage";
    public static String DOWNLOAD_PROFILE_URL = "http://210.118.74.195:8080/KOG_Server_Rest/upload/UserImage/";
    public static String UPLOAD_CHAT_IMAGE_URL = "http://210.118.74.195:8080/KOG_Server_Rest/rest/ChatImage";
    public static String DOWNLOAD_CHAT_IMAGE_URL = "http://210.118.74.195:8080/KOG_Server_Rest/upload/ChatImage/";
    public static String MESSAGE_TYPE_TEXT = "text";
    public static String MESSAGE_TYPE_IMAGE = "image";
    public static String MESSAGE_TYPE_DUMMY = "dummy";
    public static String MESSAGE_TYPE_QUIZ = "quiz";
    public static String MESSAGE_TYPE_LOGOUT="logout";
    public static String ROOM_TYPE_LIFE="liferoom";
    public static final String CHAT_IP = "210.118.74.195";
    public static final int CHAT_PORT = 9090;

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

    public static boolean isLogin(final Context context) {
        try {
            Boolean v = getBoolean(context, LOGIN_TAG);
            return v;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static void setLogin(final Context context, final boolean value) {
        try {
            setBoolean(context, LOGIN_TAG, value);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "set Login ClassCaseException : " + e.getStackTrace());
        }
    }

    public static boolean isAutoLogin(final Context context) {
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

    public static String getNickName(final Context context) {
        try {
            return getString(context, NICKNAME_TAG);
        } catch (ClassCastException e) {
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


    public static String getPassword(final Context context) {
        try {
            return getString(context, PASSWORD_TAG);
        } catch (ClassCastException e) {
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

    public static String getRid(final Context context) {
        try {
            return getString(context, RID_TAG);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "getPassword fail, ClassCaseException : " + e.getStackTrace());
            return "";
        }
    }

    public static void setRid(final Context context, final String value) {
        try {
            setString(context, RID_TAG, value);
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

    public static String getRegId(final Context context) {
        try {
            Log.i(LOG_TAG, "GCM : " +getString(context, GCM_TAG));
            return getString(context, GCM_TAG);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "get Registration ID fail, ClassCaseException : " + e.getStackTrace());
            return "";
        }
    }


    public static String getQuizNum(final Context context) {
        try {
            return getString(context, QUIZ_NUM_TAG);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "getQuizNum fail, ClassCaseException : " + e.getStackTrace());
            return "";
        }
    }

    public static void setQuizNum(final Context context, final String value) {
        try {
            setString(context, QUIZ_NUM_TAG, value);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "setQuizNum ClassCaseException : " + e.getStackTrace());
        }
    }

    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }


    public static void setInviteRooms(Context context, ArrayList<InviteRoom> values) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            String input = "{" +
                    "\"" + "roomID" + "\"" + ":"  + "\"" + values.get(i).getRoomID() +  "\"" + "," +
                    "\"" + "roomName" + "\"" + ":"  + "\"" + values.get(i).getRoomName() +   "\"" + "," +
                    "\"" + "message" + "\"" + ":"  + "\"" + values.get(i).getMessage() +   "\""
                    + "}";

            Log.i(LOG_TAG, "put : " + input);

            a.put(input);
        }
        if (!values.isEmpty()) {
            editor.putString(INVITE_ROOM, a.toString());
        } else {
            editor.putString(INVITE_ROOM, null);
        }
        editor.commit();
    }


    public static ArrayList<InviteRoom> getInviteRooms(Context context) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        Set<String> set = prefs.getStringSet(INVITE_ROOM, null);
        String jsonArray = prefs.getString(INVITE_ROOM, null);
        Log.i(LOG_TAG, "jsonArray : " + jsonArray);
        ArrayList<InviteRoom> items = new ArrayList<InviteRoom>();
        if (jsonArray != null) {
            try {
                JSONArray a = new JSONArray(jsonArray);
                for (int i = 0; i < a.length(); i++) {

                    String jsonOjectStr = a.getString(i);
                    JSONObject jsonObject = new JSONObject(jsonOjectStr);
                    String id = jsonObject.getString("roomID");
                    String name = jsonObject.getString("roomName");
                    String message = jsonObject.getString("message");
                    InviteRoom myclass = new InviteRoom(id, name, message);

                    items.add(myclass);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return items;
    }
}
