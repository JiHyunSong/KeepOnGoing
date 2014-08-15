package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.secsm.keepongoing.Adapters.MessageAdapter;
import com.secsm.keepongoing.Adapters.Msg;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class StudyRoomActivity extends Activity {

    private Intent intent;
    private static String LOG_TAG="StudyRoom Activity";
    private Button sendMsgBtn;
    private MessageAdapter messageHistoryMAdaptor;
    private EditText messageTxt;
    private String message;
    private DBHelper mDBHelper;
    private int rID;
    private int myID;
    private ArrayList<Msg> mTexts = new ArrayList<Msg>();
    private ListView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);

        mDBHelper = new DBHelper(this);
        intent = getIntent();
        rID = (int)intent.getIntExtra("roomID", -1);
        if(rID == -1)
        {
            // TODO : 잘못된 접근, 되돌아가기
        }

        myID = KogPreference.getInt(StudyRoomActivity.this, "uid");

        /* initial UI */
        sendMsgBtn = (Button) findViewById(R.id.study_room_sendMsgBtn);
        messageTxt = (EditText) findViewById(R.id.study_room_messageTxtView);


        messageHistoryMAdaptor = new MessageAdapter(StudyRoomActivity.this, R.layout.message_row, mTexts);
        messageList = (ListView) findViewById(R.id.study_room_message_list);
        messageList.setAdapter(messageHistoryMAdaptor);
//        roomList = (ListView) findViewById(R.id.room_list);
//        roomList.setAdapter(mockRoomArrayAdapter);

		/* IF there is and exists room, load the stored message */
        loadText();

        /* at First, holding the focus */
        messageTxt.requestFocus();

        		/* when you click "send" */
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(LOG_TAG, "button Clicked");
                String data = "";
                String msg = messageTxt.getText().toString();
                msg = msg.trim().replace(':', ' ');
                if(msg != null && !msg.equals(""))
                {
                    message = msg;
					/* encoding the message before send */
                    try
                    {
                        sendText(Integer.toString(myID), msg);
                        messageTxt.setText("");
                    }catch(Exception ex){}
                }
            }
        });
    }


    /* message info must need the time */
    public String getRealTime()
    {
        Calendar c = Calendar.getInstance();
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String min = Integer.toString(c.get(Calendar.MINUTE));
        String sec = Integer.toString(c.get(Calendar.SECOND));

        return hour + ":" + min + ":" + sec;
    }

    /* this is update the message from someone(include me) */
    public void sendText(String _senderID, String _text)
    {
        String time;
        Msg m;
        time = getRealTime();
        if(KogPreference.DEBUG_MODE)
        {
            m = new Msg(StudyRoomActivity.this, "나", _text, time, "true");
            insertIntoMsgInSQLite("나", _text, time, "true");
            messageHistoryMAdaptor.add(m);

        }
//        String Name, time;
//        time = getRealTime();
//        Name = getNameFromSQLite(_senderID);
//        Msg m ;
//        if(_senderID.equals(myID)){
//            m = new Msg("나", _text, time, "true");
//            //        	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
//            insertIntoMsgInSQLite("나", _text, time, "true");
//            //show up
//            messageHistoryMAdaptor.add(m);
//        }else if(Name == null){
//            String NameFromServ = NameFromServ(_senderID);
//            m = new Msg(NameFromServ, _text, time, "false");
//            //       	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
//            insertIntoMsgInSQLite(NameFromServ, _text, time, "false");
//            messageHistoryMAdaptor.add(m);
//        }else if("".equals(_text)){
//
//        }else if(_text.equals("EXIT")){
//            m = new Msg("", Name + "님이 퇴장하셨습니다.", "", "false");
//            //      	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
//            insertIntoMsgInSQLite(Name, _text, time, "false");
//            messageHistoryMAdaptor.add(m);
//        }
//        else{
//            m = new Msg(Name, _text, time, "false");
//            //	    	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
//            insertIntoMsgInSQLite(Name, _text, time, "false");
//            messageHistoryMAdaptor.add(m);
//        }
    }
    /* getting the profile image from the server  */
	/* aURL is perfect URL like : http://203.252.195.122/files/tmp_1348736125550.jpg */
    public Bitmap getRemoteImage(final URL _aURL){
        Bitmap bm = null;
        try{
            final URLConnection conn = _aURL.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
        }catch(IOException e){
            Log.d("down", e.toString());
        }
        return bm;
    }
    /* before we send the message to server(to friend or me),
     * we have to make the protocol format.
     * at this time, use this method */
    private String getSendMsg() {
        Log.i("getSendMsg()", "msg :" + message + " myID :" + myID);
        if(!"".equals(myID)) {
//            return myID + ":" + message + ":" + myStatus + ":" + roomID + ":" + talking + ":false";

        }
        return null;

    }
    private String getSendMsg(String status) {
        if(!"".equals(myID)){
//            Log.i("getSendMsg", myID + ":" + null + ":" +  status + ":" + roomID + ":" + talking + ":false");
//            return myID + ":" + null + ":" +  status + ":" + roomID + ":" + talking + ":false";

        }
        return null;
    }
    private String getSendMsg(String _status, String _msgNull) {
        if(!"".equals(myID)) {
//            return myID + ":" + _msgNull + ":" + _status + ":" + roomID + ":" + talking + ":false";
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //////////////////////////////////////////////////
    // for exit()                                   //
    //////////////////////////////////////////////////
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            socket_.close();
//            Log.d("onDestroy", "socket close");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Log.d("info>> ", "unregisterReceiver()...");
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            try {
//                String data = getSendMsg("LOGOUT", null);
//                if(data!=null) {
//                    try{
////                        cipher.init(Cipher.ENCRYPT_MODE, key);
////                        byte[] encrypt = cipher.doFinal(data.getBytes());
////                        String encryptStr = ByteUtils.toHexString(encrypt);
////                        Log.i("send", "encrypt : " + ByteUtils.toHexString(encrypt));
////                        out_.println(encryptStr);
////                        out_.flush();
//                    }catch(Exception e){
//                        Log.e("write", e.toString());
//                    }
//
//                }
////                socket_.close();
//                Log.d("socket close", "socket close");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            // unregisterReceiver(mReceiver);
            // unregisterReceiver(receiver);
            setResult(RESULT_OK);
            finish();

            Intent intent = new Intent(StudyRoomActivity.this, TabActivity.class);
//            intent.putExtra("phoneNo", phoneNo);
            //intent.putExtra("roomName", roomNameArray.get(position));
            //intent.putExtra("roomID", roomIDArrayFromSQLite.get(position));
            //startActivityForResult(intent, CHATROOM_REQUEST_CODE);
            startActivity(intent);
        }
        return false;
    }
    //////////////////////////////////////////////////
    // DB                                           //
    //////////////////////////////////////////////////


    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me){
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        //		Log.i(LOG_TAG, "insert msg");
        Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH) +1);
        String day = Integer.toString(c.get(Calendar.DATE));
        //		Log.i("day", "me : " + me);

        // TODO : check _time
        db.execSQL("INSERT INTO Chat " +
                //"(room_id, senderID, senderText, year, month, day, time, me) " +
                "(rid, senderID, senderText, year, month, day, me) " +
                "VALUES (" +
                "'"+ rID + "','"
                +_senderID + "','"
                + _senderText + "','"
                + year + "','"
                + month + "','"
                + day + "','"
                //+ _time + "','"
                + _me + " ');");
        db.close();
        mDBHelper.close();
    }

    /* load the message from the SQLite */
    public void loadText()
    {
        try {
            SQLiteDatabase db;
            Cursor cursor = null;
            db = mDBHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT " +
                    "senderID, senderText, time, me " +
                    "FROM Chat WHERE rid = '" + rID + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Log.i("loadText", cursor.getString(cursor.getColumnIndex("senderID")) + " :"
                            + cursor.getString(cursor.getColumnIndex("senderText")));
                    Msg m = new Msg(StudyRoomActivity.this,
                            cursor.getString(cursor.getColumnIndex("senderID")),
                            cursor.getString(cursor.getColumnIndex("senderText")),
                            cursor.getString(cursor.getColumnIndex("time")),
                            cursor.getString(cursor.getColumnIndex("me")));
                    messageHistoryMAdaptor.add(m);
                }
            }
            cursor.close();
            db.close();
            mDBHelper.close();
        }catch (Exception e)
        {
            Log.e(LOG_TAG, "" + e.toString());
            e.printStackTrace();
        }
    }


    //////////////////////////////////////////////////
    // Listening the message from the server        //
    //////////////////////////////////////////////////
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            String friend_id = null, text = null;
            Bundle b= msg.getData();

            friend_id = b.getString("friend_id");
            text = b.getString("text");
            Log.i("handleMsg", "friend_id : " + friend_id);
            Log.i("handleMsg", "text : " + text);

            sendText(friend_id, text);
        }
    };

    private Thread updateThread = new Thread() {
        public void run() {
//            try {
//                while (true) {
//                    String read = reader_.readLine();
//                    Log.i("R: Received:", "R: Received before decrypt: " + read);
//
//                    cipher.init(Cipher.DECRYPT_MODE, key);
//                    byte[] decrypt = cipher.doFinal(ByteUtils.toBytes(read, 16));
//                    Log.i("복호", "복호 : " + ByteUtils.toHexString(decrypt));
//                    read = new String(decrypt, 0, decrypt.length, "EUC-KR");
//
//                    if (read != null) {
//                        Log.i("R: Received:", "R: Received:" + read);
//                    }
//                    StringTokenizer info = new StringTokenizer(read, "/");
//                    String roomInfo = info.nextToken();
//                    String [] roomData = roomInfo.split(":");
//
//                    if(roomData[0].equals(roomID)) {
//                        if(!roomData[2].equals("LOGOUT") && !roomData[2].equals("LOGIN")){
//                            Log.i(LOG_TAG, "senderID : " + roomData[2]);
//                            Message msg = handler.obtainMessage();
//                            Bundle b = new Bundle();
//                            b.putString("friend_id", roomData[2]);
//
//                            b.putString("text", roomData[3]);
//                            msg.setData(b);
//                            handler.sendMessage(msg);
//                        }
//                        if(roomData[2].equals("EXIT")){
//                            Message msg = handler.obtainMessage();
//                            Bundle b = new Bundle();
//                            b.putString("friend_id", roomData[2]);
//                            b.putString("text", "EXIT");
//                            msg.setData(b);
//                            handler.sendMessage(msg);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    };
}
