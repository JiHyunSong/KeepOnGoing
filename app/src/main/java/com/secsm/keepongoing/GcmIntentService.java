package com.secsm.keepongoing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.secsm.keepongoing.Adapters.RoomNaming;
import com.secsm.keepongoing.Adapters.RoomsArrayAdapters;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String LOG_TAG = "GCM Intent Service";
    private static final int ROOM_INVITE = 0;
    private static final int FRIEND_INVITE = 1;
    private static final int CHAT_MESSAGE_CHAT = 2;
    private static final int CHAT_MESSAGE_IMAGE = 3;
    private RequestQueue vQueue;
    private DBHelper mDBHelper;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        mDBHelper = new DBHelper(this);
        vQueue = MyVolley.getRequestQueue(this);
        if (!extras.isEmpty()) { // has effect of unparcelling Bundle

//          * Filter messages based on message type. Since it is likely that GCM
//          * will be extended in the future with new message types, just ignore
//          * any message types you're not interested in, or that you don't
//          * recognize.

            Log.e("GCM", "GCMIntentService");
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String msg = intent.getStringExtra("message");
                int _messageType = -1;
                String m_type = intent.getExtras().getString("title");
                Log.i(LOG_TAG, "m_type : " + m_type);

                if(m_type=="room_invite"){

                }
                else if(m_type=="friend_invite"){
                }
                else if(m_type.equals(KogPreference.MESSAGE_TYPE_TEXT)) {
                    Log.i(LOG_TAG, "GCM get text type message");
//                    pushChatMessage(GcmIntentService.this, intent);
                    getStudyRoomsRequest(GcmIntentService.this, intent);
                }
                else if(m_type.equals(KogPreference.MESSAGE_TYPE_IMAGE)){
                    Log.i(LOG_TAG, "GCM get image type message");
                }else if(m_type.equals(KogPreference.MESSAGE_TYPE_LOGOUT))
                {
                    Log.i(LOG_TAG, "GCM get logout message");
                    KogPreference.setLogin(GcmIntentService.this, false);
                    KogPreference.setNickName(GcmIntentService.this, "");
                    KogPreference.setPassword(GcmIntentService.this, "");
                    KogPreference.setRid(GcmIntentService.this, "");
//                    KogPreference.setRegId(GcmIntentService.this, "");
                    KogPreference.setQuizNum(GcmIntentService.this, "");
                    KogPreference.setAutoLogin(GcmIntentService.this, false);
                }
                Log.i("GcmIntentService.java | onHandleIntent", "Received: " + extras.toString());

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void pushRoomInvite(Context context, Intent intent) {
        ArrayList<String> roomInvites = KogPreference.getStringArrayPref(context, "ROOM_INVITES");
        String inviteRoomID = intent.getExtras().getString("rid");
        String inviteRoomName = intent.getExtras().getString("roomName");
        String inviteRoomMessage = intent.getExtras().getString("message");
//        roomInvites.add();

    }

    private void pushFriendInvite(Context context, Intent intent) {
    }



    private void pushChatMessage(Context context, Intent intent) {
        String senderNickname;
        JSONObject intentMessage;
        String rid;
        String message;
        String messageType;
        PushWakeLock.acquireCpuWakeLock(context);
        Vibrator mVib = (Vibrator) context
                .getSystemService(context.VIBRATOR_SERVICE);
        mVib.vibrate(500);
        try {
            intentMessage = new JSONObject(intent.getExtras().getString("message"));
            senderNickname = intentMessage.getString("nickname");
            rid = intentMessage.getString("rid");
            message = intentMessage.getString("message");
            messageType = intentMessage.getString("messageType");


            Intent a = new Intent(context, PushWakeKogDialog.class);
            Bundle b2 = new Bundle();
            b2.putString("senderNickname", senderNickname);
            b2.putString("rid", rid);
            b2.putString("message", message);
            b2.putString("messageType", messageType);
            a.putExtras(b2);

            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(a);
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, "push Chat Message Json Exception : " + e.toString());
        }
    }


    private void pushChatMessageOnlyNotificationBar(Context context, Intent intent)
    {
        String senderNickname;
        JSONObject intentMessage;
        String rid;
        String message;
        String messageType;
        PushWakeLock.acquireCpuWakeLock(context);
        Vibrator mVib = (Vibrator) context
                .getSystemService(context.VIBRATOR_SERVICE);
        mVib.vibrate(500);
        try {
            intentMessage = new JSONObject(intent.getExtras().getString("message"));
            senderNickname = intentMessage.getString("nickname");
            rid = intentMessage.getString("rid");
            message = intentMessage.getString("message");
            messageType = intentMessage.getString("messageType");



            thisRoom = findRoomById(rid);

            if(thisRoom != null) {
                insertIntoMsgInSQLite(rid, senderNickname, message, getRealTime(), "false", messageType);
                if(messageType.equals(KogPreference.MESSAGE_TYPE_IMAGE))
                    message = "(사진)";

                if (message.length() > 15)
                    message = message.substring(0, 15);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Intent _intent = new Intent(getApplicationContext(), StudyRoomActivity.class);
                _intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                FLAG_ACTIVITY_NEW_TASK
//                FLAG_ACTIVITY_CLEAR_TOP
                //FLAG_ACTIVITY_SINGLE_TOP
//                _intent.putExtra("msg", msg);
                _intent.putExtra("type", thisRoom.getType());
                _intent.putExtra("rule", thisRoom.getRule());
                KogPreference.setRid(context, rid);
                KogPreference.setQuizNum(context, thisRoom.getQuiz_num());

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.kog_ico)
                        .setContentTitle(senderNickname)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0, 500});

                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }

        }catch (JSONException e)
        {
            Log.e(LOG_TAG, "push Chat Message Json Exception : " + e.toString());
        }
    }

    private RoomNaming findRoomById(String rid)
    {
        for(int i=0; i<mRooms.size(); i++)
        {
            if(mRooms.get(i).getRid().equals(rid))
            {
                return mRooms.get(i);
            }
        }
        return null;
    }


    private void insertIntoMsgInSQLite(String roomID, String _senderID, String _senderText, String _time, String _me, String _messageType) {
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        Log.i(LOG_TAG, "insert msg");
        Log.i("day", "nickname : " + _senderID);
        String query = "INSERT INTO Chat " +
                //"(room_id, senderID, senderText, year, month, day, time, me) " +
                "(rid, senderID, senderText, time, me, messageType) " +
                "VALUES (" +
                "'" + roomID + "','"
                + _senderID + "','"
                + _senderText + "','"
                + _time + "','"
                + _me + "','"
                + _messageType + "');";
        // TODO : check _time
        Log.i(LOG_TAG, "execSQL : " + query);
        db.execSQL(query);
        db.close();
        mDBHelper.close();
    }

    /* message info must need the time */
    public String getRealTime() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        // I/StudyRoom Activity﹕ long : 1408229086924
        // Log.i(LOG_TAG, "long : " + time);
        // I/StudyRoom Activity﹕ Timestamp : 2014-08-17 07:44:46.924
        // Log.i(LOG_TAG, "Timestamp : " + currentTimestamp);
        // I/StudyRoom Activity﹕ Timestamp.toString().substring(0, 10) : 2014-08-17
        // Log.i(LOG_TAG, "Timestamp.toString().substring(0, 19) : " + currentTimestamp.toString().substring(0, 19));
        // I/StudyRoom Activity﹕ Timestamp.getTime() : 1408229086924
        // Log.i(LOG_TAG, "Timestamp.getTime() : " + currentTimestamp.getTime());
        return currentTimestamp.toString().substring(0, 19);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500});

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private ArrayList<RoomNaming> mRooms;
    private RoomNaming thisRoom;

    private void getStudyRoomsRequest(Context context, final Intent intent) {
        try {
            HttpAPIs.getStudyRoomsRequest(KogPreference.getNickName(context), new CallbackResponse() {
                @Override
                public void success(HttpResponse httpResponse) {

                    JSONObject obj = HttpAPIs.getHttpResponseToJSON(httpResponse);

                    try {
                        int status_code = obj.getInt("status");
                        if (status_code == 200) {
                            JSONArray rMessage;
                            rMessage = obj.getJSONArray("message");
                            //////// real action ////////
                            mRooms = new ArrayList<RoomNaming>();
                            JSONObject rObj;

                            //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
                            Log.i(LOG_TAG, "room size : " + rMessage.length());
                            for (int i = 0; i < rMessage.length(); i++) {
                                rObj = rMessage.getJSONObject(i);
                                if (!"null".equals(rObj.getString("rid"))) {
                                    mRooms.add(new RoomNaming(
                                            URLDecoder.decode(rObj.getString("type"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("rid"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("rule"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("roomname"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("maxHolidayCount"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("startTime"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("durationTime"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("showupTime"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("meetDays"), "UTF-8"),
                                            URLDecoder.decode(rObj.getString("num"), "UTF-8")
                                    ));
                                    Log.i(LOG_TAG, "num" + URLDecoder.decode(rObj.getString("num"), "UTF-8"));
                                }
                            }

//                                RoomsArrayAdapters roomsArrayAdapter;
//                                roomsArrayAdapter = new RoomsArrayAdapters(TabActivity.this, R.layout.room_list_item, mRooms);
//                                roomList.setAdapter(roomsArrayAdapter);
                            /////////////////////////////

                            pushChatMessageOnlyNotificationBar(GcmIntentService.this, intent);

                        } else {
                            Log.e(LOG_TAG, "통신 에러 : " + obj.getString("message"));

//                                Toast.makeText(getBaseContext(), "통신 에러 : \n스터디 방 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                            if (KogPreference.DEBUG_MODE) {
//                                Toast.makeText(getBaseContext(), LOG_TAG + obj.getString("message"), Toast.LENGTH_SHORT).show();
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



//        String get_url = KogPreference.REST_URL +
//                "Rooms" +
//                "?nickname=" + KogPreference.getNickName(context);
//
//        Log.i(LOG_TAG, "URL : " + get_url);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject");
//                        Log.i(LOG_TAG, response.toString());
//                        try {
//                            int status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                JSONArray rMessage;
//                                rMessage = response.getJSONArray("message");
//                                //////// real action ////////
//                                mRooms = new ArrayList<RoomNaming>();
//                                JSONObject rObj;
//
//                                //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
//                                Log.i(LOG_TAG, "room size : " + rMessage.length());
//                                for(int i=0; i< rMessage.length(); i++) {
//                                    rObj = rMessage.getJSONObject(i);
//                                    if (!"null".equals(rObj.getString("rid"))){
//                                        mRooms.add(new RoomNaming(
//                                                URLDecoder.decode(rObj.getString("type"), "UTF-8"),
//                                                URLDecoder.decode(rObj.getString("rid"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("rule"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("roomname"), "UTF-8"),
//                                                URLDecoder.decode(rObj.getString("maxHolidayCount"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("startTime"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("durationTime"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("showupTime"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("meetDays"),"UTF-8"),
//                                                URLDecoder.decode(rObj.getString("num"),"UTF-8")
//                                        ));
//                                        Log.i(LOG_TAG, "num"+ URLDecoder.decode(rObj.getString("num"),"UTF-8"));
//                                    }
//                                }
//
////                                RoomsArrayAdapters roomsArrayAdapter;
////                                roomsArrayAdapter = new RoomsArrayAdapters(TabActivity.this, R.layout.room_list_item, mRooms);
////                                roomList.setAdapter(roomsArrayAdapter);
//                                /////////////////////////////
//
//                                pushChatMessageOnlyNotificationBar(GcmIntentService.this, intent);
//
//                            } else {
////                                Toast.makeText(getBaseContext(), "통신 에러 : \n스터디 방 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                                if (KogPreference.DEBUG_MODE) {
//                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                Toast.makeText(getBaseContext(), "통신 에러 : \n스터디 방 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                Log.i(LOG_TAG, "Response Error");
//                if (KogPreference.DEBUG_MODE) {
//                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }
//        );
//        vQueue.add(jsObjRequest);
    }

}