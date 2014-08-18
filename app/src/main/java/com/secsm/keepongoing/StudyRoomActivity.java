package com.secsm.keepongoing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.secsm.keepongoing.Adapters.MessageAdapter;
import com.secsm.keepongoing.Adapters.Msg;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Quiz.Quiz_Main;
import com.secsm.keepongoing.Quiz.Solve_Main;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.KogSocketConnecter;
import com.secsm.keepongoing.Shared.MultipartRequest;
import com.secsm.keepongoing.Shared.MyVolley;
import com.secsm.keepongoing.Shared.SocketListener;
import com.secsm.keepongoing.Shared.SocketManager;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
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

//    private Socket client = null;
//    private BufferedReader br = null;
//    private BufferedWriter bw = null;

    private Handler mainHandler;
    private SocketListener sl;
    SocketAsyncTask soc=null;

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
        additionalBtn = (Button) findViewById(R.id.study_room_additional);

        messageHistoryMAdaptor = new MessageAdapter(StudyRoomActivity.this, R.layout.message_row, mTexts);
        messageList = (ListView) findViewById(R.id.study_room_message_list);
        messageList.setAdapter(messageHistoryMAdaptor);

		/* IF there is and exists room, load the stored message */
        loadText();

        /* Init connection w/ server
        *
        * send my nickname! as a type json
        * */


        init();
        /* at First, holding the focus */
        messageTxt.requestFocus();

        /* when you click "send" */
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sendMessage();
            }

        });

        additionalBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                getImage();
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
            jObj.put("nickname", KogPreference.getNickName(StudyRoomActivity.this));
            Log.i(LOG_TAG,  "jObj.toString() " + jObj.toString() + "\n");

            return jObj.toString();
        }catch (JSONException e) {
            Log.i(LOG_TAG,  "Json Exception!\n" + e.toString() );
            if(KogPreference.DEBUG_MODE)
            {
                Toast.makeText(getBaseContext(), "Json Exception!\n" + e.toString() , Toast.LENGTH_SHORT).show();

            }
        }
        return "";
    }





    private void sendMessage() {
        Log.i(LOG_TAG, "button Clicked");
        String data = "";
        String msg = messageTxt.getText().toString();

        if (msg != null && !msg.equals("")) {
            message = msg;
            try {
                Log.i(LOG_TAG, "sendMessage() , msg : " + msg);
                sendMsgToSvr(msg);
//                sendText(KogPreference.getNickName(StudyRoomActivity.this), msg);

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

    public String getProfileImageName(String f_nick)
    {
        Log.i(LOG_TAG, " mFriends.size() " + mFriends.size());
        for(int i=0 ; i<mFriends.size(); i++)
        {
            if(f_nick.equals(mFriends.get(i).getName()))
            {
                return mFriends.get(i).getProfile_path();
            }
        }
        return "";
    }


    /* this is update the message from someone(include me) */
    public void sendText(String _senderNickname, String _rid, String _text, String _messageType) {
        String time;
        Msg m;
        time = getRealTime();

        String _profileImageName = getProfileImageName(_senderNickname);

//        if (KogPreference.DEBUG_MODE) {
//            m = new Msg(StudyRoomActivity.this, "나", _text, time, "true", _messageType, _profileImageName);
//            insertIntoMsgInSQLite("나", _text, time, "true", _messageType);
//            messageHistoryMAdaptor.add(m);
//
//        }
        String Name;
        time = getRealTime();
        if(_senderNickname.equals(KogPreference.getNickName(StudyRoomActivity.this))){
            m = new Msg(StudyRoomActivity.this, "나", _text, time, "true", _messageType, _profileImageName);
//        	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
            insertIntoMsgInSQLite("나", _text, time, "true", _messageType);
            //show up
            messageHistoryMAdaptor.add(m);
        }else if("".equals(_text)){

        }else if(_text.equals("EXIT")){
            m = new Msg(StudyRoomActivity.this, "", _senderNickname + "님이 퇴장하셨습니다.", "", "false", _messageType, _profileImageName);
//      	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
            insertIntoMsgInSQLite(_senderNickname, _text, time, "false", _messageType);
            messageHistoryMAdaptor.add(m);
        }
        else{
            m = new Msg(StudyRoomActivity.this, _senderNickname, _text, time, "false", _messageType, _profileImageName);
//	    	Log.i("MSG", "Name : " + Name + "Text : " + text + "Time : " + time);
            insertIntoMsgInSQLite(_senderNickname, _text, time, "false", _messageType);
            messageHistoryMAdaptor.add(m);
        }
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


    ///////////////////
    // upload image  //
    ///////////////////
    private AlertDialog mDialog;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    private FileInputStream mFileInputStream = null;
    private URL connectUrl = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";


    void getImage()
    {
        mDialog = createDialog();
        mDialog.show();
    }

    /* create the dialog */
    private AlertDialog createDialog() {
        final View innerView = getLayoutInflater().inflate(R.layout.image_crop_row, null);

        Button camera = (Button)innerView.findViewById(R.id.btn_camera_crop);
//        Button gellary = (Button)innerView.findViewById(R.id.btn_gellary_crop);
        Button cancel = (Button)innerView.findViewById(R.id.btn_cancel_crop);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doTakePhotoAction();
                setDismiss(mDialog);
            }
        });

//        gellary.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                doTakeAlbumAction();
//                setDismiss(mDialog);
//            }
//        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDismiss(mDialog);
            }
        });

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("이미지 설정");
        ab.setView(innerView);

        return  ab.create();
    }

    /* using camera */
    private void doTakePhotoAction()
    {
        Log.i(LOG_TAG, "doTakePhotoAction()");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		/* making the own path for cropped image */
        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /* open the gallery */
    private void doTakeAlbumAction()
    {
        Log.i(LOG_TAG, "doTakeAlbumAction()");
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /* dialog exit */
    private void setDismiss(AlertDialog dialog){
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }

    /* crop image makes the saved image */
    private Uri createSaveCropFile(){
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), url));
        Log.i(LOG_TAG, "createSaveCropFile : " + uri);
        return uri;
    }

    /* getting the image path by uri.
     * if uri is null, getting the last path */
    private File getImageFile(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor !=null ) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    private void DoFileUpload(String filePath) throws IOException {
        Log.d("Test" , "file path = " + filePath);
        imageUploadFlag = true;
//        try {
//            soc.wait(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        ImageUploadAsyncTask imgUploadSync = new ImageUploadAsyncTask();
//
//        imgUploadSync.execute();


//        HttpFileUpload( KogPreference.UPLOAD_URL + "?rid=" + KogPreference.getRid(StudyRoomActivity.this) + "&nickname=" + KogPreference.getNickName(StudyRoomActivity.this)
//                , "", filePath);
        asyncFilePath = filePath;

        VolleyUploadImage();

    }
    String asyncFilePath;
    boolean imageUploadFlag = false;

    void VolleyUploadImage()
    {
        Charset c = Charset.forName("utf-8");
        String URL = KogPreference.UPLOAD_URL + "?rid=" + KogPreference.getRid(StudyRoomActivity.this) + "&nickname=" + KogPreference.getNickName(StudyRoomActivity.this);
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
        try
        {
            entity.addPart("file", new FileBody(new File(asyncFilePath)));
            Log.i("MULTIPART-ENTITY", "add addPART");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        MultipartRequest req = new MultipartRequest(Request.Method.POST, URL, entity, errListener);
        vQueue.add(req);
        Log.i("MULTIPART-ENTITY", "add queue");

        vQueue.start();
    }

    Response.ErrorListener errListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError arg0) {
// TODO Auto-generated method stub
            Log.d("errrrrrooooor", arg0.toString());
        }
    };

    private void HttpFileUpload(String urlString, String params, String fileName) {
        try {
            mFileInputStream = new FileInputStream(fileName);
            connectUrl = new URL(urlString);
            Log.d("Test", "mFileInputStream  is " + mFileInputStream);

            // open connection
            HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Test", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Test" , "File is written");
            mFileInputStream.close();
            dos.flush(); // finish upload...

            // get response
            int ch;
            InputStream is = conn.getInputStream();
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){
                b.append( (char)ch );
            }
            String s=b.toString();
            Log.e("Test", "result = " + s);
            //			mEdityEntry.setText(s);
            dos.close();

        } catch (Exception e) {
            Log.d("Test", "exception " + e.toString());
            // TODO: handle exception
        }
    }





    /* copy the file from srcFile to destFile */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /* Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed. */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(LOG_TAG, "onActivityResultX");
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                Log.d(LOG_TAG, "onActivityResult PICK_FROM_ALBUM");
                mImageCaptureUri = data.getData();
                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());
			/* copy the image for crop to SD card */
                copyFile(original_file , cpoy_file);
                break;
            }

            case PICK_FROM_CAMERA:
            {
                Log.d(LOG_TAG, "onActivityResult PICK_FROM_CAMERA");

			/* setup the image resize after taking the image */
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

			/* the path for image */
                intent.putExtra("output", mImageCaptureUri);

                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }

            case CROP_FROM_CAMERA:
            {
                Log.w(LOG_TAG, "onActivityResult CROP_FROM_CAMERA");

                Log.w(LOG_TAG, "mImageCaptureUri = " + mImageCaptureUri);
                String full_path = mImageCaptureUri.getPath();
//                String photo_path = full_path.substring(4, full_path.length());
                String photo_path = full_path;

                Log.w(LOG_TAG, "비트맵 Image path = "+photo_path);

                Bitmap photo = BitmapFactory.decodeFile(photo_path);
//                mPhotoImageView.setImageBitmap(photo);
//
//                insertImgInfoToSQLite(photo_path);
                try{
                    DoFileUpload(photo_path);
                }catch(Exception e){
                    Log.i("img", e.toString());
                }
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, "title", "descripton");
			/* for media scanning */
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
                intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
                intentFilter.addDataScheme("file");
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                        + Environment.getExternalStorageDirectory())));
                break;
            }
        }
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

    void init()
    {
        getFriendsRequest();
        soc = new SocketAsyncTask();
        soc.execute();
    }
    void close()
    {
        soc.sendMsgToSvr("exit");
        soc.cancel(true);
    }

    //////////////////////////////////////////////////
    // for exit()                                   //
    //////////////////////////////////////////////////


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
        close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        Log.i(LOG_TAG, "onResume");
//        if(savedInstanceState != null)
//        {
//
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onDestroy() {
        super.onDestroy();

        close();
        Log.d("info>> ", "unregisterReceiver()...");
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            close();


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


    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me, String _messageType) {
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        Log.i(LOG_TAG, "insert msg");
        Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String day = Integer.toString(c.get(Calendar.DATE));
        Log.i("day", "nickname : " + _senderID);
        String query = "INSERT INTO Chat " +
                //"(room_id, senderID, senderText, year, month, day, time, me) " +
                "(rid, senderID, senderText, year, month, day, me, messageType) " +
                "VALUES (" +
                "'" + rID + "','"
                + _senderID + "','"
                + _senderText + "','"
                + year + "','"
                + month + "','"
                + day + "','"
                //+ _time + "','"
                + _me + "','"
                + _messageType + " ');";
        // TODO : check _time
        Log.i(LOG_TAG, "execSQL : " + query);
        db.execSQL(query);
//        db.execSQL("INSERT INTO Chat " +
//                //"(room_id, senderID, senderText, year, month, day, time, me) " +
//                "(rid, senderID, senderText, year, month, day, me, messageType) " +
//                "VALUES (" +
//                "'" + rID + "','"
//                + _senderID + "','"
//                + _senderText + "','"
//                + year + "','"
//                + month + "','"
//                + day + "','"
//                //+ _time + "','"
//                + _me + "','"
//                + _messageType + " ');");
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
                    "senderID, senderText, time, me, messageType " +
                    "FROM Chat WHERE rid = '" + rID + "'", null);
            Log.i(LOG_TAG, "Load Text From db");
            cursor.moveToFirst();
            Log.i(LOG_TAG, "curser.getCount() : " + cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Log.i("loadText", cursor.getString(cursor.getColumnIndex("senderID")) + " :"
                            + cursor.getString(cursor.getColumnIndex("senderText")));

                    String _senderID = cursor.getString(cursor.getColumnIndex("senderID"));
                    Msg m = new Msg(StudyRoomActivity.this,
                            cursor.getString(cursor.getColumnIndex("senderID")),
                            cursor.getString(cursor.getColumnIndex("senderText")),
                            cursor.getString(cursor.getColumnIndex("time")),
                            cursor.getString(cursor.getColumnIndex("me")),
                            cursor.getString(cursor.getColumnIndex("messageType")),
                            getProfileImageName(_senderID)
                    );
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
            String f_nickname = null, rid= null, text = null;
            Bundle b = msg.getData();

            f_nickname = b.getString("nickname");
            text = b.getString("message");
            rid = b.getString("rid");


            Log.i("handleMsg", "friend_id : " + f_nickname);
            Log.i("handleMsg", "text : " + text);

            sendText(f_nickname, rid, text, "plaintext");
        }
    };
//    KogSocketConnecter kogSC;
//    public void initConnection(){
//        kogSC = KogSocketConnecter.getInstance();
////        sl = new SocketListener(getApplicationContext(), mainHandler);
////        sl.start();
////        getInitialMsg();
//    }
    public void sendMsgToSvr(String msg)
    {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("nickname", KogPreference.getNickName(StudyRoomActivity.this));
            Log.i(LOG_TAG, "sendMsgToSvr in MainThread nickName : " + KogPreference.getNickName(StudyRoomActivity.this));
            jObj.put("rid", KogPreference.getRid(StudyRoomActivity.this));
            Log.i(LOG_TAG, "sendMsgToSvr in MainThread rid : " + KogPreference.getRid(StudyRoomActivity.this));
            jObj.put("message", msg);
            Log.i(LOG_TAG, "sendMsgToSvr in MainThread msg : " + msg);

            soc.sendMsgToSvr(jObj.toString());
//            SocketManager.sendMsg(jObj.toString());
        } catch (Exception e)
        {
            Log.i(LOG_TAG,  "Json Exception!\n" + e.toString() );
            if(KogPreference.DEBUG_MODE)
            {

                Toast.makeText(getBaseContext(), "Json Exception!\n" + e.toString() , Toast.LENGTH_SHORT).show();

            }

        }
    }

    class SocketAsyncTask extends AsyncTask<Void, Void, Void> {

    private Socket client = null;
    private BufferedReader br = null;
    private BufferedWriter bw = null;

        @Override
        protected void onPreExecute() {
        }

        private void sendMsgToSvr(String msg)
        {
            try {
                bw.write(msg);

                Log.i(LOG_TAG, "client send message : " + msg);
                bw.newLine();
                bw.flush();
            }catch (Exception e) {
                if(KogPreference.DEBUG_MODE)
                {
//                    Toast.makeText(getBaseContext(), "Json Exception!\n" + e.toString() , Toast.LENGTH_SHORT).show();

                }
            }
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                client = new Socket(KogPreference.CHAT_IP, KogPreference.CHAT_PORT);
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                Log.i(LOG_TAG, "id : " + getInitialMsg());

                bw.write(getInitialMsg());
                bw.newLine();
                bw.flush();
                String read;
                JSONObject rMsg;
                while (true) {

                    if(isCancelled())
                        break;

                    read = br.readLine();
                    Log.i("R: Received:", "R: Received: " + read);



                    if (read != null) {
                        Log.i("R: Received:", "R: Received:" + read);
                    }

                    rMsg = new JSONObject(read);

                    Message ms = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("nickname", rMsg.getString("nickname"));
                    b.putString("rid", rMsg.getString("rid"));
                    b.putString("message", rMsg.getString("message"));
                    ms.setData(b);
                    handler.sendMessage(ms);
                }

                return null;
            } catch (Exception e) {

                e.printStackTrace();

                if(KogPreference.DEBUG_MODE)
                {
                    Log.i(LOG_TAG,  "소켓 에러!\n" + e.toString() );

//                    Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();
                }

            }

            return(null);
        }

        @Override
        protected void onPostExecute(Void unused) {
            try
            {
                client.close();
                client = null;

            }catch (Exception e)
            {
                Log.i(LOG_TAG,  "소켓 에러!\n" + e.toString() );
                if(KogPreference.DEBUG_MODE)
                {
//
//                    Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


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
                "Room/User" +
                "?rid=" + KogPreference.getRid(StudyRoomActivity.this) +
                "&date=" + getRealDate();

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
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
                                    if (!"null".equals(rObj.getString("nickname"))) {
                                        Log.i(LOG_TAG, "add Friends : " + rObj.getString("image") + "|" + rObj.getString("nickname") + "|" + rObj.getString("targetTime"));
                                        mFriends.add(new FriendNameAndIcon(rObj.getString("image"),
                                                rObj.getString("nickname"),
                                                rObj.getString("targetTime")));
                                    }
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
