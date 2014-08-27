package com.secsm.keepongoing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MultipartRequest;
import com.secsm.keepongoing.Shared.MyVolley;

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
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

public class MyProfileActivity extends BaseActivity {
    private AlertDialog mDialog;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    private RequestQueue vQueue;

    private static String LOG_TAG = "MY PROFILE";

    private Button my_profile_image_crop_btn;
    private ImageView my_profile_image_iv;
    private TextView my_profile_my_nickname;
    private TextView my_profile_my_target_time;
    private TextView my_profile_phone_num;

    String my_nickname;
    String my_target_time;
    String my_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        my_profile_image_crop_btn = (Button) findViewById(R.id.my_profile_image_crop_btn);
        my_profile_image_iv = (ImageView)findViewById(R.id.my_profile_image_iv);
        my_profile_my_nickname = (TextView)findViewById(R.id.my_profile_my_nickname);
        my_profile_my_target_time = (TextView)findViewById(R.id.my_profile_my_target_time);
        my_profile_phone_num = (TextView)findViewById(R.id.my_profile_phone_num);


        my_profile_image_crop_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getImage();
            }
        });
        vQueue = MyVolley.getRequestQueue(MyProfileActivity.this);

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
        my_profile_my_target_time.setText(my_target_time);
        getImageFromURL(my_profile, my_profile_image_iv);
    }

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

        VolleyUploadImage();

    }
    String asyncFilePath;
    boolean imageUploadFlag = false;

    void VolleyUploadImage()
    {
        Charset c = Charset.forName("utf-8");
        String URL = KogPreference.UPLOAD_PROFILE_URL+ "?rid=" + KogPreference.getRid(MyProfileActivity.this) + "&nickname=" + KogPreference.getNickName(MyProfileActivity.this);
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

        ProfileMultipartRequest req = new ProfileMultipartRequest(Request.Method.POST, URL, entity, errListener);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_profile, menu);
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

    private void getMyInfoRequest() {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "User" +
                "?nickname=" + KogPreference.getNickName(MyProfileActivity.this);

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
                                /////////////////////////////
                                JSONArray rMessage = response.getJSONArray("message");
                                my_nickname = rMessage.getJSONObject(0).getString("nickname");
                                my_target_time = rMessage.getJSONObject(0).getString("targetTime");
                                my_profile = rMessage.getJSONObject(0).getString("image");

                                setAllEnable();
                                setInit();

                                /////////////////////////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
//                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "get User error : " + e.toString());
                            Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                            MyProfileActivity.this.finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
        vQueue.start();
    }


    private void updateMyInfoRequest(String _image) {

        //TODO : check POST/GET METHOD and get_URL
        String get_url = KogPreference.REST_URL +
                "User" +
                "?nickname=" + KogPreference.getNickName(MyProfileActivity.this) +
                "&password=" + Encrypt.encodingMsg(KogPreference.getPassword(MyProfileActivity.this))+
                "&image=" + _image;

        Log.i(LOG_TAG, "URL : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, Encrypt.encodeIfNeed(get_url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {

                            int status_code = response.getInt("status");
                            if (status_code == 200) {
                                /////////////////////////////
                                Toast.makeText(getBaseContext(), "사진 업로드 완료!", Toast.LENGTH_SHORT).show();
                                /////////////////////////////
                            } else {
                                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                                if (KogPreference.DEBUG_MODE) {
//                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "get User error : " + e.toString());
                            Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                            MyProfileActivity.this.finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "통신 에러 : \n상태를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Response Error");
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );
        vQueue.add(jsObjRequest);
        vQueue.start();
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
                        String uploadedProfileName = _response.getString("message");
                        Log.i(LOG_TAG, "profile rMessage : " + uploadedProfileName);

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
