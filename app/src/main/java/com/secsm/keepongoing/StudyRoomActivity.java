package com.secsm.keepongoing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.secsm.keepongoing.Adapters.FriendNameAndIcon;
import com.secsm.keepongoing.Adapters.FriendsArrayAdapters;
import com.secsm.keepongoing.Adapters.MessageAdapter;
import com.secsm.keepongoing.Adapters.Msg;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Quiz.Quiz_Main;
import com.secsm.keepongoing.Quiz.Solve_Main;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MultipartRequest;
import com.secsm.keepongoing.Shared.MyVolley;
import com.secsm.keepongoing.Shared.SocketListener;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

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
    private ArrayList<FriendNameAndIcon> mFriends;

    private RequestQueue vQueue;

//    private Socket client = null;
//    private BufferedReader br = null;
//    private BufferedWriter bw = null;

    private Animation translateLeftAnim;
    private Animation translateRightAnim;
    private LinearLayout slidingPage01;
    private boolean isPageOpen = false;
    private ListView friendList;
    private TextView room_rule_tv;
    private Handler mainHandler;
    private SocketListener sl;
    SocketAsyncTask_Reader soc_reader=null;
    SocketAsyncTask_Writer soc_writer=null;
    String type;
    String rule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.NAVIGATION_MODE_STANDARD);


//        MyVolley.init(StudyRoomActivity.this);
//        vQueue = Volley.newRequestQueue(this);
        vQueue = MyVolley.getRequestQueue();

        mDBHelper = new DBHelper(this);
        intent = getIntent();
        rID = (int) intent.getIntExtra("roomID", -1);
        type = (String) intent.getStringExtra("type");
        rule = (String) intent.getStringExtra("rule");


        rID = Integer.parseInt(KogPreference.getRid(StudyRoomActivity.this));
        if (rID == -1) {
            // TODO : 잘못된 접근, 되돌아가기
        }

        Log.i(LOG_TAG, "type" + type);

//        myID = KogPreference.getInt(StudyRoomActivity.this, "uid");

        /* initial UI */
        sendMsgBtn = (Button) findViewById(R.id.study_room_sendMsgBtn);
        messageTxt = (EditText) findViewById(R.id.study_room_messageTxtView);
        additionalBtn = (Button) findViewById(R.id.study_room_additional);

        messageHistoryMAdaptor = new MessageAdapter(StudyRoomActivity.this, R.layout.message_row, mTexts);
        messageList = (ListView) findViewById(R.id.study_room_message_list);
        messageList.setAdapter(messageHistoryMAdaptor);

        room_rule_tv = (TextView) findViewById(R.id.room_role_tv);
        room_rule_tv.setMovementMethod(new ScrollingMovementMethod());

        friendList = (ListView) findViewById(R.id.roomFriendList);
        friendList.setOnItemClickListener(itemClickListener);


        room_rule_tv.setText(rule);
		/* IF there is and exists room, load the stored message */

        /* Init connection w/ server
        *
        * send my nickname! as a type json
        * */
        // 슬라이딩으로 보여질 레이아웃 객체 참조
        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);

        // 애니메이션 객체 로딩
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        // 애니메이션 객체에 리스너 설정
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);


//        init();
        /* at First, holding the focus */
//        messageTxt.requestFocus();

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
            Log.i(LOG_TAG,  "jObj.toString()");
            jObj.put("nickname", KogPreference.getNickName(StudyRoomActivity.this));
            Log.i(LOG_TAG,  "jObj.toString() " + jObj.toString() + "\n");

            Log.i(LOG_TAG,  "Log.i(LOG_TAG,  \"jObj.toString() \" + jObj.toString() + \"\\n\");");

            return jObj.toString();
        }
        catch (JSONException e) {
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
                sendMsgToSvr(msg, KogPreference.MESSAGE_TYPE_TEXT);
//                sendText(KogPreference.getNickName(StudyRoomActivity.this), KogPreference.getRid(StudyRoomActivity.this), msg, "plaintext");

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
        Log.i(LOG_TAG, " f_nick : " + f_nick);
        if(f_nick.equals("나"))
            f_nick = KogPreference.getNickName(StudyRoomActivity.this);

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
        String Name;
        time = getRealTime();
        if(_senderNickname.equals(KogPreference.getNickName(StudyRoomActivity.this))){
            m = new Msg(StudyRoomActivity.this, "나", _text, time, "true", _messageType, _profileImageName);
            insertIntoMsgInSQLite("나", _text, time, "true", _messageType);
            messageHistoryMAdaptor.add(m);
        }
        else if("".equals(_text)){

        }
        else{
            m = new Msg(StudyRoomActivity.this, _senderNickname, _text, time, "false", _messageType, _profileImageName);
            Log.i("MSG", "Name : " + _senderNickname + "Text : " + _text + "Time : " + time);
            insertIntoMsgInSQLite(_senderNickname, _text, time, "false", _messageType);
            messageHistoryMAdaptor.add(m);
        }

    }

    /* getting the profile image from the server  */
	/* aURL is perfect URL like : http://203.252.195.122/files/tmp_1348736125550.jpg */
    void getProfileImageFromURL(String img_name, ImageView imgView) {

        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + img_name;
        // TODO R.drawable.error_image
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.no_image,
                        R.drawable.no_image)
        );
    }

    void getChatImageFromURL(String img_name, ImageView imgView) {

        String ImgURL = KogPreference.DOWNLOAD_CHAT_IMAGE_URL + KogPreference.getRid(StudyRoomActivity.this) + "/" + img_name;
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
        Button gellary = (Button)innerView.findViewById(R.id.btn_gellary_crop);
        Button cancel = (Button)innerView.findViewById(R.id.btn_cancel_crop);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doTakePhotoAction();
                setDismiss(mDialog);
            }
        });

        gellary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doTakeAlbumAction();
                setDismiss(mDialog);
            }
        });

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

        asyncFilePath = filePath;
        init();
        VolleyUploadImage();

    }
    String asyncFilePath;
    boolean imageUploadFlag = false;

    void VolleyUploadImage()
    {
        Charset c = Charset.forName("utf-8");
        String URL = KogPreference.UPLOAD_CHAT_IMAGE_URL + "?rid=" + KogPreference.getRid(StudyRoomActivity.this) + "&nickname=" + KogPreference.getNickName(StudyRoomActivity.this);
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
        try
        {
            entity.addPart("file", new FileBody(new File(asyncFilePath)));
            // add addPART, asyncFilePath : /storage/sdcard0/Pictures/tmp_1408977926598.jpg
            Log.i("MULTIPART-ENTITY", "add addPART, asyncFilePath : " + asyncFilePath);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        ChatImageMultipartRequest req = new ChatImageMultipartRequest(Request.Method.POST, URL, entity, errListener);
        vQueue.add(req);
        Log.i("MULTIPART-ENTITY", "add queue");

        vQueue.start();
    }


    private class ChatImageMultipartRequest extends MultipartRequest
    {
        private final Gson g_gson = new Gson();
        public ChatImageMultipartRequest(int method, String url, MultipartEntity params, Response.ErrorListener errorListener) {
            super(method, url, params, errorListener);
        }
        @SuppressWarnings("rawtypes")
        @Override
        protected Response<Map> parseNetworkResponse(NetworkResponse response)
        {
            try
            {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                Log.i("ChatImageMultipartRequest", "response.data : " + new String(response.data, "UTF-8"));
                Log.i("ChatImageMultipartRequest", "response.headers : " + response.headers);
                try {
                    JSONObject _response = new JSONObject(new String(response.data, "UTF-8"));
                    int status_code = _response.getInt("status");
                    Log.i(LOG_TAG, "profile status code : " + status_code);
                    if (status_code == 200) {
                        /////////////////////////////
                        String uploadedChatImgName = _response.getString("message");
                        Log.i(LOG_TAG, "chat image rMessage : " + uploadedChatImgName);
                        sendMsgToSvr(uploadedChatImgName, KogPreference.MESSAGE_TYPE_IMAGE);
                        /////////////////////////////
                    }
                } catch (JSONException e) {

                }


                return Response.success(g_gson.fromJson(json, Map.class), HttpHeaderParser.parseCacheHeaders(response));
            }
            catch (UnsupportedEncodingException e)
            {
                return Response.error(new ParseError(e));
            }
            catch (JsonSyntaxException e)
            {
                return Response.error(new ParseError(e));
            }
        }

    }



    Response.ErrorListener errListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError arg0) {
// TODO Auto-generated method stub
            Log.d("errrrrrooooor", arg0.toString());
        }
    };

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
                File copy_file = new File(mImageCaptureUri.getPath());
			/* copy the image for crop to SD card */
                copyFile(original_file , copy_file);
//                break;
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
                //mImageCaptureUri = file:///storage/sdcard0/Pictures/tmp_1408977926598.jpg
                Log.w(LOG_TAG, "mImageCaptureUri = " + mImageCaptureUri);
                String full_path = mImageCaptureUri.getPath();
//                String photo_path = full_path.substring(4, full_path.length());
                String photo_path = full_path;
                //비트맵 Image path = /storage/sdcard0/Pictures/tmp_1408977926598.jpg
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
    /**
     * 애니메이션 리스너 정의
     */
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        /**
         * 애니메이션이 끝날 때 호출되는 메소드
         */
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                slidingPage01.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else {
                slidingPage01.setVisibility(View.VISIBLE);
                isPageOpen = true;
            }
        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }

    }

    ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (adapterView.getId() == R.id.friend_list) {
                Log.i(LOG_TAG, "friends Clicked");

            }
        }
    };
    protected void showSoftKeyboard() {

        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.showSoftInput(StudyRoomActivity.this.getCurrentFocus(), InputMethodManager.SHOW_FORCED);

    }
    protected void hideSoftKeyboard(View view) {

        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    MenuItem.OnMenuItemClickListener ab_friend_list_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_friend_list_listener");

            // 애니메이션 적용
            if (isPageOpen) {
                showSoftKeyboard();
                slidingPage01.startAnimation(translateLeftAnim);
                Log.e(LOG_TAG,"left");
                slidingPage01.setVisibility(View.VISIBLE);
            } else {
//                hideSoftKeyboard(slidingPage01);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                slidingPage01.startAnimation(translateRightAnim);
                Log.e(LOG_TAG,"right");
            }




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
            Log.i(LOG_TAG, "onMenuItemClicked ab_add_quiz_listener");

            Intent intent = new Intent(StudyRoomActivity.this, Quiz_Main.class);
            startActivity(intent);
//            StudyRoomActivity.this.finish();

            return true;
        }
    };

    MenuItem.OnMenuItemClickListener ab_invite_friend_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_invite_friend_listener");
            return true;
        }
    };
    MenuItem.OnMenuItemClickListener ab_kick_off_friend_listener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem mi) {
            Log.i(LOG_TAG, "onMenuItemClicked ab_kick_off_friend_listener");
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

        if("liferoom".equals(type)) {
            actionBarThirdBtn.setVisible(false);
            actionBarSecondBtn.setVisible(false);
        }

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
        if(soc_writer==null)
        {
            Log.i(LOG_TAG,"soc=null");
            getFriendsRequest();
            soc_writer = new SocketAsyncTask_Writer();
            soc_writer.execute();
        }

        if(!KogPreference.NO_AUTH){
        }else
        {
            mFriends = new ArrayList<FriendNameAndIcon>();
            for(int i=0; i < 3; i++)
            {
                mFriends.add(new FriendNameAndIcon( "default.png", "nickname" + i , null));
            }
            FriendsArrayAdapters mockFriendArrayAdapter;
            mockFriendArrayAdapter = new FriendsArrayAdapters(StudyRoomActivity.this, R.layout.friend_list_item, mFriends);
            friendList.setAdapter(mockFriendArrayAdapter);
        }
    }
    void close()
    {
        if(soc_reader != null){
            soc_reader.cancel(true);
            soc_reader = null;
        }
        if(soc_writer != null ){
            soc_writer.sendMsgToSvr("exit");
            soc_writer.cancel(true);
            soc_writer = null;
        }
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
//            insertIntoMsgInSQLite("나", _text, time, "true", _messageType);
//    insertIntoMsgInSQLite(_senderNickname, _text, time, "false", _messageType);

    private void insertIntoMsgInSQLite(String _senderID, String _senderText, String _time, String _me, String _messageType) {
        SQLiteDatabase db;
        db = mDBHelper.getWritableDatabase();
        Log.i(LOG_TAG, "insert msg");
//        Calendar c = Calendar.getInstance();
//        String year = Integer.toString(c.get(Calendar.YEAR));
//        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
//        String day = Integer.toString(c.get(Calendar.DATE));
        Log.i("day", "nickname : " + _senderID);
        String query = "INSERT INTO Chat " +
                //"(room_id, senderID, senderText, year, month, day, time, me) " +
                "(rid, senderID, senderText, time, me, messageType) " +
                "VALUES (" +
                "'" + rID + "','"
                + _senderID + "','"
                + _senderText + "','"
                + _time + "','"
                + _me + "','"
                + _messageType + "');";
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

        Log.i(LOG_TAG, "loadText");

        try {
            SQLiteDatabase db;
            Cursor cursor = null;
            db = mDBHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT " +
                    "senderID, senderText, time, me, messageType " +
                    "FROM Chat WHERE rid = '" + rID + "'", null);
//            Log.i(LOG_TAG, "Load Text From db");
//            Log.i(LOG_TAG, "curser.getCount() : " + cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String _senderID = cursor.getString(0);
//                    Log.i("loadText", "sender ID : " + _senderID);
                    Msg m = new Msg(StudyRoomActivity.this,
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
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
            String f_nickname = null, rid= null, text = null, messageType;
            Bundle b = msg.getData();

            f_nickname = b.getString("nickname");
            text = b.getString("message");
            rid = b.getString("rid");
            messageType = b.getString("messageType");


            Log.i("handleMsg", "friend_id : " + f_nickname);
            Log.i("handleMsg", "text : " + text);

            sendText(f_nickname, rid, text, messageType);
        }
    };

    public void sendMsgToSvr(String msg, String messageType)
    {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("nickname", KogPreference.getNickName(StudyRoomActivity.this));
            jObj.put("rid", KogPreference.getRid(StudyRoomActivity.this));
            jObj.put("message", msg);
            jObj.put("messageType", messageType);
            Log.i(LOG_TAG, "send msg : " + jObj.toString());
            soc_writer.sendMsgToSvr(jObj.toString());
        } catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "메시지 전송 실패!", Toast.LENGTH_SHORT).show();

            Log.i(LOG_TAG, "Json Exception!\n" + e.toString());
            if(KogPreference.DEBUG_MODE)
            {
                Toast.makeText(getBaseContext(), "Json Exception!\n" + e.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    }


    Socket client = null;
    class SocketAsyncTask_Reader extends AsyncTask<Void, Void, Void> {
        private BufferedReader br = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Log.i(LOG_TAG, "-----------------------------reader");
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String read;
                JSONObject rMsg;
                while (true) {
                    if(isCancelled())
                        break;

                    read = br.readLine();

                    if (read != null) {
                        Log.i("R: Received:", "R: Received:" + read);
                        rMsg = new JSONObject(read);

                        Message ms = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("nickname", rMsg.getString("nickname"));
                        b.putString("rid", rMsg.getString("rid"));
                        b.putString("message", rMsg.getString("message"));
                        b.putString("messageType", rMsg.getString("messageType"));
                        ms.setData(b);
                        handler.sendMessage(ms);
                        //Log.i(LOG_TAG, "this is it~! : " + messageList.getItemAtPosition(0).toString());
                    }
                }
            }
            catch (Exception e) {
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
                br.close();
                br = null;
                soc_writer.executeClose();
            }
            catch (Exception e)
            {
                Log.i(LOG_TAG,  "소켓 에러!\n" + e.toString() );
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class SocketAsyncTask_Writer extends AsyncTask<Void, Void, Void> {

        private BufferedWriter bw = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Log.i(LOG_TAG, "-----------------------------writer");
                client = new Socket(KogPreference.CHAT_IP, KogPreference.CHAT_PORT);
                bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                bw.write(getInitialMsg());
                bw.newLine();
                bw.flush();
            }
            catch (Exception e) {
                e.printStackTrace();

                if(KogPreference.DEBUG_MODE)
                {
                    Log.i(LOG_TAG,  "소켓 에러!\n" + e.toString() );
                    Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();
                }
            }

            if(soc_reader==null)
            {
                soc_reader = new SocketAsyncTask_Reader();
                Log.i(LOG_TAG, "Socket Reader execute");
                soc_reader.execute();
            }

            return(null);
        }

        private void sendMsgToSvr(String msg)
        {
            try {
                bw.write(msg);
                bw.newLine();
                bw.flush();
                Log.i(LOG_TAG, "client sent msg. now flushed");
            }
            catch (Exception e) {
                Log.i(LOG_TAG, "client send message??????? " + e.toString());
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), "Json Exception!\n" + e.toString() , Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
        }

        public void executeClose() {
            try
            {
                bw.close();
                bw = null;
                client.close();
                client = null;
            }
            catch (Exception e)
            {
                Log.i(LOG_TAG,  "소켓 에러!\n" + e.toString() );
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), "소켓에러!\n" + e.toString() , Toast.LENGTH_SHORT).show();
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
                                        mFriends.add(new FriendNameAndIcon(
                                                URLDecoder.decode(rObj.getString("image"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("nickname"), "UTF-8"),
                                                URLDecoder.decode(rObj.getString("targetTime"), "UTF-8")));
                                    }
                                }
                                /////////////////////////////
                                FriendsArrayAdapters mockFriendArrayAdapter;
                                mockFriendArrayAdapter = new FriendsArrayAdapters(StudyRoomActivity.this, R.layout.friend_list_item, mFriends);
                                friendList.setAdapter(mockFriendArrayAdapter);

                                loadText();
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
        if(mFriends==null)
            vQueue.add(jsObjRequest);
    }
}
