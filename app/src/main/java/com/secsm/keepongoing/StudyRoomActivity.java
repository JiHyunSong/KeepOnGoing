package com.secsm.keepongoing;

import android.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Adapters.MessageAdapter;
import com.secsm.keepongoing.Adapters.Msg;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Quiz.Quiz_Main;
import com.secsm.keepongoing.Quiz.Solve_Main;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class StudyRoomActivity extends Activity {

    private Intent intent;
    private static String LOG_TAG = "StudyRoom Activity";
    private Button sendMsgBtn;
    private Button additionalBtn; // send Picture
    private MessageAdapter messageHistoryMAdaptor;
    private EditText messageTxt;
    private String message;
    private DBHelper mDBHelper;
    private int rID;
//    private int myID;
    private ArrayList<Msg> mTexts = new ArrayList<Msg>();
    private ListView messageList;
    private MenuItem actionBarFirstBtn, actionBarSecondBtn;
    private MenuItem actionBarThirdBtn, actionBarFourthBtn;
    private MenuItem actionBarFifthBtn;
    ArrayList<FriendNameAndIcon> mFriends;

    private RequestQueue vQueue;

    private Socket client = null;
    private BufferedReader br = null;
    private BufferedWriter bw = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.NAVIGATION_MODE_STANDARD);

        MyVolley.init(StudyRoomActivity.this);
        vQueue = Volley.newRequestQueue(this);


        mDBHelper = new DBHelper(this);
        intent = getIntent();
        rID = (int) intent.getIntExtra("roomID", -1);
        rID = Integer.parseInt(KogPreference.getRid(StudyRoomActivity.this));
        if (rID == -1) {
            // TODO : 잘못된 접근, 되돌아가기
        }

//        myID = KogPreference.getInt(StudyRoomActivity.this, "uid");

        /* initial UI */
        sendMsgBtn = (Button) findViewById(R.id.study_room_sendMsgBtn);
        messageTxt = (EditText) findViewById(R.id.study_room_messageTxtView);


        messageHistoryMAdaptor = new MessageAdapter(StudyRoomActivity.this, R.layout.message_row, mTexts);
        messageList = (ListView) findViewById(R.id.study_room_message_list);
        messageList.setAdapter(messageHistoryMAdaptor);

		/* IF there is and exists room, load the stored message */
        loadText();

        /* Init connection w/ server
        *
        * send my nickname! as a type json
        * */
//        initConnection();

        /* at First, holding the focus */
        messageTxt.requestFocus();

        /* when you click "send" */
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sendMessage();
            }

        });
    }

// JSON
// nickname
// rid
// message


    public String getInitialMsg(){
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("id", KogPreference.getNickName(StudyRoomActivity.this));
            return jObj.toString();
        }catch (JSONException e) {
            if(KogPreference.DEBUG_MODE)
            {
                Toast.makeText(getBaseContext(), "Json Exception!\n" + e.toString() , Toast.LENGTH_SHORT).show();

            }
        }
        return "";
    }
    public void initConnection() {
        try{

            client = new Socket(KogPreference.CHAT_IP, KogPreference.CHAT_PORT);
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            Log.i(LOG_TAG, "id : " + getInitialMsg());

            bw.write(getInitialMsg());
            bw.newLine();
            bw.flush();

        }catch (IOException e) {
            if(KogPreference.DEBUG_MODE)
            {
                Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();

            }
        }
    }



    private void sendMessage() {
        Log.i(LOG_TAG, "button Clicked");
        String data = "";
        String msg = messageTxt.getText().toString();
        msg = msg.trim().replace(':', ' ');

        if (msg != null && !msg.equals("")) {
            message = msg;
            try {
                sendText(KogPreference.getNickName(StudyRoomActivity.this), msg);

                messageTxt.setText("");
            } catch (Exception ex) {
            }
        }
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

    /* this is update the message from someone(include me) */
    public void sendText(String _senderID, String _text) {
        String time;
        Msg m;
        time = getRealTime();
        if (KogPreference.DEBUG_MODE) {
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
    void getImageFromURL(String img_name, ImageView imgView) {

        String ImgURL = KogPreference.MEDIA_URL + img_name;
        // TODO R.drawable.error_image
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.no_image,
                        R.drawable.no_image)
        );
    }




    ////////////////////
    // Action bar     //
    ////////////////////

    MenuItem.OnMenuItemClickListener ab_friend_list_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_stopwatchTab_settings_listener");
            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_solve_quiz_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_friends_add_listener");

            Intent intent = new Intent(StudyRoomActivity.this, Solve_Main.class);
            startActivity(intent);
//            StudyRoomActivity.this.finish();


            return true;
        }
    };
    MenuItem.OnMenuItemClickListener ab_add_quiz_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_rooms_notify_listener");

            Intent intent = new Intent(StudyRoomActivity.this, Quiz_Main.class);
            startActivity(intent);
//            StudyRoomActivity.this.finish();

            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_invite_friend_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_rooms_add_listener");
            return true;
        }
    };
    MenuItem.OnMenuItemClickListener ab_kick_off_friend_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_rooms_add_listener");
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_room, menu);
        actionBarFirstBtn = menu.findItem(R.id.actionBarFirstBtn);
        actionBarFirstBtn.setOnMenuItemClickListener(ab_friend_list_listener);
        actionBarSecondBtn = menu.findItem(R.id.actionBarSecondBtn);
        actionBarSecondBtn.setOnMenuItemClickListener(ab_solve_quiz_listener);
        actionBarThirdBtn = menu.findItem(R.id.actionBarThirdBtn);
        actionBarThirdBtn.setOnMenuItemClickListener(ab_add_quiz_listener);
        actionBarFourthBtn = menu.findItem(R.id.actionBarFourthBtn);
        actionBarFourthBtn.setOnMenuItemClickListener(ab_invite_friend_listener);
        actionBarFifthBtn = menu.findItem(R.id.actionBarFifthBtn);
        actionBarFifthBtn.setOnMenuItemClickListener(ab_kick_off_friend_listener);
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
        Log.d("info>> ", "unregisterReceiver()...");
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            sendMessage();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            StudyRoomActivity.this.finish();

//            Intent intent = new Intent(StudyRoomActivity.this, TabActivity.class);
//            startActivity(intent);
        }
        return false;
    }
    //////////////////////////////////////////////////
    // DB                                           //
    //////////////////////////////////////////////////


    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me) {
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        //		Log.i(LOG_TAG, "insert msg");
        Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String day = Integer.toString(c.get(Calendar.DATE));
        //		Log.i("day", "me : " + me);

        // TODO : check _time
        db.execSQL("INSERT INTO Chat " +
                //"(room_id, senderID, senderText, year, month, day, time, me) " +
                "(rid, senderID, senderText, year, month, day, me) " +
                "VALUES (" +
                "'" + rID + "','"
                + _senderID + "','"
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
    public void loadText() {
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
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e.toString());
            e.printStackTrace();
        }
    }


    //////////////////////////////////////////////////
    // Listening the message from the server        //
    //////////////////////////////////////////////////
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String friend_id = null, text = null;
            Bundle b = msg.getData();

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


    ////////////////////////////////////
    // REST API                       //
    ////////////////////////////////////

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

    private void getFriendsRequest() {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "/Room/User" +
                "?nickname=" + KogPreference.getNickName(StudyRoomActivity.this) +
                "&date=" + getRealDate();

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            int status_code = response.getInt("status");
                            if (status_code == 200) {
                                JSONArray rMessage;
                                rMessage = response.getJSONArray("message");
                                //////// real action ////////
                                mFriends = new ArrayList<FriendNameAndIcon>();
                                JSONObject rObj;

                                //{"message":[{"targetTime":null,"image":"http:\/\/210.118.74.195:8080\/KOG_Server_Rest\/upload\/UserImage\/default.png","nickname":"jonghean"}],"status":"200"}
                                for(int i=0; i< rMessage.length(); i++)
                                {
                                    rObj = rMessage.getJSONObject(i);
                                    Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" +rObj.getString("targetTime"));
                                    mFriends.add(new FriendNameAndIcon(rObj.getString("image"),
                                            rObj.getString("nickname"),
                                            rObj.getString("targetTime")));
                                }

//                                FriendsArrayAdapters mockFriendArrayAdapter;
//                                mockFriendArrayAdapter = new FriendsArrayAdapters(TabActivity.this, R.layout.friend_list_item, mFriends);
//                                friendList.setAdapter(mockFriendArrayAdapter);


                                /////////////////////////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
    }
}
