package com.secsm.keepongoing;

import java.sql.Timestamp;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;

/*
 * 메시지가 올 경우 보낸이와 해당 채팅방의 메시지를 띄워주는 푸시 메시지
 * */
public class PushWakeKogDialog extends Activity {
    private String senderNickname, roomID, message, messageType;
    private static final String LOG_TAG = "Push Dialog";
    private DBHelper mDBHelper;
    private NotificationManager notiManager;
    private static final int START_NOTI_ID = 101;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Dialog dialog = new Dialog(this);
        mDBHelper = new DBHelper(this);
        //		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        Intent intent = getIntent();
        senderNickname = intent.getStringExtra("senderNickname");
        roomID = intent.getStringExtra("rid");
        message = intent.getStringExtra("message");
        messageType = intent.getStringExtra("messageType");
        Log.i(LOG_TAG, "senderNickname : " + senderNickname);
        Log.i(LOG_TAG, "roomID : " + roomID);
        Log.i(LOG_TAG, "message : " + message);
        Log.i(LOG_TAG, "messageType : " + messageType);
        boolean isAfterNoti = intent.getBooleanExtra("afterNoti", false);
//        if ("SERVER".equals(roomName)) {
//            roomName = getRoomNameFromSQLite(rid);
//        }


        // TODO : insert Msg
//        insertIntoMsgInSQLite(senderNickname, message, getRealTime());

//    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me, String _messageType) {
        insertIntoMsgInSQLite(senderNickname, message, getRealTime(), "false", messageType);
        if (message.length() > 15)
            message = message.substring(0, 15);

        new AlertDialog.Builder(this)
                .setTitle("메시지가 도착했습니다.")
                .setMessage(senderNickname + " : " + "\n" + ": " + message + "...")
                .setPositiveButton("확인", dialogClickListener)
                .setNegativeButton("닫기", dialogClickListener)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        PushWakeKogDialog.this.finish();
                    }
                })
                .show();

        if (!isAfterNoti)
            setNotification();
    }

    private void setNotification() {
        //1. 먼저 선언
        //import android.app.Notification;
        //import android.app.NotificationManager;

        //1.Notification 매니저 얻기
        String ns = Context.NOTIFICATION_SERVICE;
        notiManager = (NotificationManager) getSystemService(ns);

        //2.Notification 객체 생성 (아이콘, 티커메시지, 발생시간)
        CharSequence ticker = "메시지가 도착했습니다";

//        Notification notification = new Notification(R.drawable.push_icon1, ticker, System.currentTimeMillis());
        Notification notification;

        //3.private static final int START_NOTI_ID = 101; Notification id설정

        //	if(notification==null)
        // TODO : icon change
        notification = new Notification(R.drawable.ic_launcher, ticker, System.currentTimeMillis());

        //4. 알림 이후 사용자가 해당 알림을 선택하면 Notification 리스트 윈도우가 뜨는데,
        // 해당 윈도우에 표시될 내용 설정을 한다.
        // 또한, 해당 항목이 처리할 Intent를 추가한다.
        Context context = getApplicationContext();
        CharSequence contentTitle = "KicTalk";
        CharSequence contentText = "메시지가 들어왔습니다.";
        Intent notificationIntent = new Intent(this, PushWakeKogDialog.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("senderNickname", senderNickname);
        notificationIntent.putExtra("rid", roomID);
        notificationIntent.putExtra("afterNoti", true);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        //	notification.number = NOTI_NUM++;
        //	notification.number = 3;

        //	int notiID = START_NOTI_ID + notification.number;
        //	mActiveIdList.add(notiID);

        //5. Notification 전달
        notiManager.notify(START_NOTI_ID, notification);
    }

    private void cancelNotification() {
//        	if(mActiveIdList.isEmpty())
//        		return ;
//        	int id = mActiveIdList.remove(0);
        notiManager.cancel(START_NOTI_ID);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface arg0, int arg1) {
            // TODO Auto-generated method stub
            switch (arg1) {
                case -1: // positive, 확인
//                    phoneNo = getPhoneNumber();
//                    myID = getIdByXmlParsing(phoneNo);
                    cancelNotification();
                    KogPreference.setRid(PushWakeKogDialog.this, roomID);
                    Intent intent = new Intent(PushWakeKogDialog.this, StudyRoomActivity.class);
//                    intent.putExtra("myID", myID);
//                    intent.putExtra("rid", rid);
//                    intent.putExtra("roomName", roomName);
//                    intent.putExtra("phoneNo", phoneNo);//민향추가
//                    //if 방이 없다면
//                    boolean roomExist = isRoomExist(rid);
//                    if (roomExist == true)
//                        intent.putExtra("which", "0");
//                    else
//                        intent.putExtra("which", "1");

                    PushWakeKogDialog.this.finish();
                    startActivity(intent);

                    break;
                case -2: // negative, 닫기
                    cancelNotification();
                    PushWakeKogDialog.this.finish();
            }
        }

    };


    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me, String _messageType) {
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


    private void insertIntoMsgInSQLite(String senderID, String senderText, String time) {
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        Log.i(LOG_TAG, "insert msg");
        Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String day = Integer.toString(c.get(Calendar.DATE));
        Log.i("day", "year : " + year + " month : " + month + " day : " + day);
        db.execSQL("INSERT INTO msg_ph (room_id, senderID, senderText, year, month, day, time) VALUES ('" + roomID + "','" + senderID + "','" + senderText + "','" + year + "','" + month + "','" + day + "','" + time + "');");
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

}