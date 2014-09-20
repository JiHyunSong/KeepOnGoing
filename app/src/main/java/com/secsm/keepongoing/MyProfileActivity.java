package com.secsm.keepongoing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.secsm.keepongoing.Alarm.Contact;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MultipartRequest;
import com.secsm.keepongoing.Shared.MyVolley;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Map;

public class MyProfileActivity extends BaseActivity {
    private AlertDialog mDialog;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;

    private static String LOG_TAG = "MY PROFILE";

    private Button my_profile_image_crop_btn;
    private ImageView my_profile_image_iv;
    private TextView my_profile_my_nickname;
    private TextView my_profile_my_target_time;
    private TextView my_profile_phone_num;
    private RelativeLayout my_profile_ll1;

    String my_nickname;
    String my_target_time;
    String my_profile;

    DBHelper helper;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mContext = getBaseContext();
        my_profile_ll1 = (RelativeLayout) findViewById(R.id.my_profile_ll1);
        my_profile_image_crop_btn = (Button) findViewById(R.id.my_profile_image_crop_btn);
        my_profile_image_iv = (ImageView)findViewById(R.id.my_profile_image_iv);
        my_profile_my_nickname = (TextView)findViewById(R.id.my_profile_my_nickname);
        my_profile_my_target_time = (TextView)findViewById(R.id.my_profile_my_target_time);
        my_profile_phone_num = (TextView)findViewById(R.id.my_profile_phone_num);

        Log.i(LOG_TAG, " my pre " +  KogPreference.getNickName(MyProfileActivity.this));
        my_profile_image_crop_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getImage();
            }
        });
    }

    private void setAllEnable(){
        my_profile_image_crop_btn.setEnabled(true);
        my_profile_image_iv.setEnabled(true);
        my_profile_my_nickname.setEnabled(true);
        my_profile_my_target_time.setEnabled(true);
        my_profile_phone_num.setEnabled(true);
    }

    private void setAllDisable(){
        my_profile_image_crop_btn.setEnabled(false);
        my_profile_image_iv.setEnabled(false);
        my_profile_my_nickname.setEnabled(false);
        my_profile_my_target_time.setEnabled(false);
        my_profile_phone_num.setEnabled(false);
    }



    @Override
    protected void onResume() {
        super.onResume();
        setAllDisable();
        getMyInfoRequest();
    }

    private void setInit()
    {
        my_profile_my_nickname.setText(KogPreference.getNickName(MyProfileActivity.this));
        if(!my_target_time.equals("null")) {
            my_target_time = my_target_time.substring(0, my_target_time.length()-3);
        }else{
            my_target_time = "00:00";
        }

        my_profile_my_target_time.setText(my_target_time);
//        getImageFromURL(my_profile, my_profile_image_iv);

        helper = new DBHelper(mContext);
        if(helper.isImageExist(this.my_profile))
        {
            Log.i(LOG_TAG, "image exist, path : " + this.my_profile);
            this.my_profile_image_iv.setImageBitmap(helper.getImage(this.my_profile));
        }else {
            downloadProfileImage(this.my_profile);
        }
        helper.close();

    }

    private void downloadProfileImage(String ImgName)
    {
        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + ImgName;
//        int width = imgView.getWidth();
//        imgView.setMaxHeight(width);

//        my_profile_image_iv.setImageResource(R.drawable.profile_default);
//        my_profile_image_iv.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.profile_default));
        HttpAPIs.getImage(mContext, ImgURL, 150, 150, ImgName, ProfileImageSetHandler);
    }



    Handler ProfileImageSetHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i(LOG_TAG, "ProfileImageSetHandler2 : " + msg.getData().getString("imgName"));
//                Bitmap image = msg.getData().getParcelable("image");
//                Log.i(LOG_TAG, "ProfileImageSetHandler2 : " + image.toString());
                helper = new DBHelper(getBaseContext());

                my_profile_image_iv.setImageBitmap(helper.getImage(msg.getData().getString("imgName")));
                my_profile_image_iv.postInvalidate();
                helper.close();

                refreshActivity();
                Log.i(LOG_TAG, "setImage Bitmap done");
//                my_profile_image_iv.;
            }catch (Exception e)
            {
                e.printStackTrace();

            }
        }
    };
    ///////////////////
    // upload image  //
    ///////////////////
//    private FileInputStream mFileInputStream = null;
//    private URL connectUrl = null;
//    String lineEnd = "\r\n";
//    String twoHyphens = "--";
//    String boundary = "*****";

    void getImageFromURL(String img_name, ImageView imgView) {

        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL+img_name;


        // TODO R.drawable.error_image
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.no_image,
                        R.drawable.no_image)
        );
    }

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

//        VolleyUploadImage();
        ImageUpload(filePath);

    }
    String asyncFilePath;
    boolean imageUploadFlag = false;

    Handler ImageUploadHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    /////////////////////////////
                    String uploadedProfileName = result.getString("message");
                    Log.i(LOG_TAG, "profile rMessage : " + uploadedProfileName);
                    Log.i(LOG_TAG, "내 사진을 업로드 할거야!");

                    updateMyInfoRequest(uploadedProfileName);

                    setAllDisable();
                    getMyInfoRequest();

                    /////////////////////////////
                }
            }catch (JSONException e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };


    void ImageUpload(String filePath){
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.uploadImage(
                    KogPreference.UPLOAD_PROFILE_URL,
                    KogPreference.getRid(MyProfileActivity.this),
                    KogPreference.getNickName(MyProfileActivity.this),
                    filePath);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = ImageUploadHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        ImageUploadHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
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
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
//                        + Environment.getExternalStorageDirectory())));
                refreshActivity();
                break;
            }
        }
    }

    private void refreshActivity() {
        Intent _intent = new Intent(this, MyProfileActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(_intent);
    }

    public String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
                setAllEnable();
            }
            else if(msg.what == -1){
                setAllDisable();
            }
        }
    };

    Handler errorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Toast.makeText(getBaseContext(), "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /** getMyInfoRequest
     * statusCode == 200 => get My info, Update UI
     */
    Handler getMyInfoRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONArray rMessage = result.getJSONArray("message");
                    // URLDecoder.decode(rObj.getString("nickname"), "UTF-8")
                    if(URLDecoder.decode(rMessage.getJSONObject(0).getString("nickname"), "UTF-8") != null) {
                        my_nickname = URLDecoder.decode(rMessage.getJSONObject(0).getString("nickname"), "UTF-8");
                        my_target_time = rMessage.getJSONObject(0).getString("targetTime");
                        my_profile = URLDecoder.decode(rMessage.getJSONObject(0).getString("image"), "UTF-8");
                        Log.i(LOG_TAG, "getMyInfoRequest, nick " + my_nickname);
                        Log.i(LOG_TAG, "getMyInfoRequest, my_profile " + my_profile);
                        setInit();
                    }

                    /////////////////////////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };


    private void getMyInfoRequest() {

        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase getMyInfoRequest = HttpAPIs.getMyInfoGet(
                    KogPreference.getNickName(MyProfileActivity.this), getRealDate());
            HttpAPIs.background(getMyInfoRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = getMyInfoRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        getMyInfoRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " + e.toString());
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            errorHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }

//        //TODO : check POST/GET METHOD and get_URL
//        String get_url = KogPreference.REST_URL +
//                "User" +
//                "?nickname=" + KogPreference.getNickName(MyProfileActivity.this) +
//                "&date=" + getRealDate();
//
//        Log.i(LOG_TAG, "URL : " + get_url);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Encrypt.encodeIfNeed(get_url), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject getMyInfoRequest");
//                        Log.i(LOG_TAG, response.toString());
//
//                        try {
//
//                            int status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                /////////////////////////////
//                                JSONArray rMessage = response.getJSONArray("message");
//                                // URLDecoder.decode(rObj.getString("nickname"), "UTF-8")
//                                if(URLDecoder.decode(rMessage.getJSONObject(0).getString("nickname"), "UTF-8") != null) {
//                                    my_nickname = URLDecoder.decode(rMessage.getJSONObject(0).getString("nickname"), "UTF-8");
//                                    my_target_time = rMessage.getJSONObject(0).getString("targetTime");
//                                    my_profile = URLDecoder.decode(rMessage.getJSONObject(0).getString("image"), "UTF-8");
//                                    Log.i(LOG_TAG, "getMyInfoRequest, nick " + my_nickname);
//                                    Log.i(LOG_TAG, "getMyInfoRequest, my_profile " + my_profile);
//                                    setAllEnable();
//                                    setInit();
//                                }
//
//                                /////////////////////////////
//                            } else {
//                                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                                if (KogPreference.DEBUG_MODE) {
////                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e(LOG_TAG, "get User error : " + e.toString());
//                            Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                            MyProfileActivity.this.finish();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
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

    /** getMyInfoRequest
     * statusCode == 200 => Upload Image done -> request get my info for refresh
     */
    Handler updateMyInfoRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    /////////////////////////////
                    Toast.makeText(getBaseContext(), "사진 업로드 완료!", Toast.LENGTH_SHORT).show();

                    setAllDisable();
                    getMyInfoRequest();
                    /////////////////////////////
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (JSONException e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };

    private void updateMyInfoRequest(String _image) {

        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase updateMyInfoRequest = HttpAPIs.updateMyInfoPut(
                    KogPreference.getNickName(MyProfileActivity.this),
                    Encrypt.encodingMsg(KogPreference.getPassword(MyProfileActivity.this)),
                    _image);
            HttpAPIs.background(updateMyInfoRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = updateMyInfoRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        updateMyInfoRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
        });
        } catch (IOException e) {
            errorHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }

//        //TODO : check POST/GET METHOD and get_URL
//        String get_url = KogPreference.REST_URL +
//                "User"; // +
////                "?nickname=" + KogPreference.getNickName(MyProfileActivity.this) +
////                "&password=" + Encrypt.encodingMsg(KogPreference.getPassword(MyProfileActivity.this))+
////                "&image=" + _image;
//        JSONObject sendBody = new JSONObject();
//
//        try {
//            sendBody.put("nickname", KogPreference.getNickName(MyProfileActivity.this));
//            sendBody.put("password", Encrypt.encodingMsg(KogPreference.getPassword(MyProfileActivity.this)));
//            sendBody.put("image", _image);
//            Log.i(LOG_TAG, "sendbody : " + sendBody.toString());
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, "UserLogin error : " + e.toString());
//
//        }
//
//        Log.i(LOG_TAG, "URL : " + get_url);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, Encrypt.encodeIfNeed(get_url), sendBody,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject updateMyInfoReqeust");
//                        Log.i(LOG_TAG, response.toString());
//
//                        try {
//
//                            int status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                /////////////////////////////
//
//
//                                Toast.makeText(getBaseContext(), "사진 업로드 완료!", Toast.LENGTH_SHORT).show();
//
//                                setAllDisable();
//                                getMyInfoRequest();
//
//                                /////////////////////////////
//                            } else {
//                                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                                if (KogPreference.DEBUG_MODE) {
////                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e(LOG_TAG, "get User error : " + e.toString());
//                            Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
//                            MyProfileActivity.this.finish();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
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


    private class ProfileMultipartRequest extends MultipartRequest
    {
        private final Gson g_gson = new Gson();
        public ProfileMultipartRequest(int method, String url, MultipartEntity params, Response.ErrorListener errorListener) {
            super(method, url, params, errorListener);
        }
        @SuppressWarnings("rawtypes")
        @Override
        protected Response<Map> parseNetworkResponse(NetworkResponse response)
        {
            try
            {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                Log.i("ProfileMultipartRequest", "response.data : " + new String(response.data, "UTF-8"));
                Log.i("ProfileMultipartRequest", "response.headers : " + response.headers);
                try {
                    JSONObject _response = new JSONObject(new String(response.data, "UTF-8"));
                    int status_code = _response.getInt("status");
                    Log.i(LOG_TAG, "profile status code : " + status_code);
                    if (status_code == 200) {
                        /////////////////////////////
                        String uploadedProfileName = URLDecoder.decode(_response.getString("message"), "UTF-8");
                        Log.i(LOG_TAG, "profile rMessage : " + uploadedProfileName);
                        Log.i(LOG_TAG, "내 사진을 업로드 할거야!");

                        updateMyInfoRequest(uploadedProfileName);

                        setAllDisable();
                        getMyInfoRequest();

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
}
