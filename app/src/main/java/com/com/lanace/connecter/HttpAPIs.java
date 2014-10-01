package com.com.lanace.connecter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Lanace on 2014-09-02.
 */
public class HttpAPIs {

    private static final String LOG_TAG = "HttpAPIs";
    private static Context mContext;
    private static final String TABLE_IMAGE = "Image_table";
    private static File default_file_dir;

    public static List<String> imageList;

    public static void getImage(Context context, String fileurl, int resize_width, int resize_height, String key, Handler handler)
    {
        mContext = context;
        default_file_dir = context.getFilesDir();

        Log.w(LOG_TAG, "request getImage with key : " + key + "fileurl ; " + fileurl);

        try {
            if (key != null) {
                if (imageList.contains(key)) {

                } else {
                    imageList.add(key);
                    Thread requestDownloadThread = new Thread(new downloadImageThread(fileurl, resize_width, resize_height, key, handler));
                    requestDownloadThread.start();
                }
            } else {
                if (imageList.contains("profile_default.png")) {
                    DBHelper helper = new DBHelper(mContext);
                    Message msg = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putParcelable("image", helper.getImage("profile_default.png"));
                    b.putString("imgName", "profile_default.png");
                    msg.setData(b);
                    handler.sendMessage(msg);
                    helper.close();
                } else {
                    imageList.add("profile_default.png");
                    Thread requestDownloadThread = new Thread(new downloadImageThread(fileurl, resize_width, resize_height, "profile_default.png", handler));
                    requestDownloadThread.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void background(final HttpRequestBase request, final CallbackResponse callback) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    callback.success(HttpConnecter.getInstance().excute(request));
                } catch (Exception e) {
                    callback.error(e);
                }
            }
        }).start();
    }

    public static HttpRequestBase login(String nickname, String password,
                                        String gcmid) throws IOException {

        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "User");

        LoginDataSet dataSet = new LoginDataSet();
        dataSet.nickname = nickname;
        dataSet.password = password;
        dataSet.gcmid = gcmid;

        String json = new Gson().toJson(dataSet);

        Log.i(LOG_TAG, "LOGIN GCM : " + json.toString());

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST Time Send
     */
    public static HttpRequestBase timePost(String nickname, String target_time,
                                           String accomplished_time, String date) throws IOException {

        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Time");

        TimeDataSet dataSet = new TimeDataSet();
        dataSet.nickname = nickname;
        dataSet.targettime = target_time;
        dataSet.accomplishedtime = accomplished_time;
        dataSet.date = date;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST Time Put
     */
    public static HttpRequestBase timePut(String nickname, String target_time,
                                          String accomplished_time, String date) throws IOException {

        HttpPut httpPut = new HttpPut(HttpConnecter.getRestfullBaseURL() + "Time");

        TimeDataSet dataSet = new TimeDataSet();
        dataSet.nickname = nickname;
        dataSet.targettime = target_time;
        dataSet.accomplishedtime = accomplished_time;
        dataSet.date = date;

        String json = new Gson().toJson(dataSet);

        httpPut.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPut.setEntity(entity);

        return httpPut;
    }

    public static HttpRequestBase getFriendsRequest(String nickname) {
        HttpGet httpPut = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Friend" +
                "?nickname=" + nickname +
                "&date=" + getRealDate());
        return httpPut;
    }

    /**
     * POST Auth Send
     */
    public static HttpRequestBase authPost(String phone, int random_num) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Auth");

        AuthDataSet dataSet = new AuthDataSet();
        dataSet.phone = phone;
        dataSet.randomnum = random_num;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    public static HttpRequestBase getStudyRoomsRequest(String nickname, CallbackResponse callbackResponse) throws IOException {
        HttpGet httpPut = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Rooms" +
                "?nickname=" + nickname);

        background(httpPut, callbackResponse);

        return httpPut;
    }

    public static HttpRequestBase outRoomRequest(String room_id, String nickname) {
        HttpDelete httpPut = new HttpDelete(HttpConnecter.getRestfullBaseURL() + "Room/User" +
                "?rid=" + room_id +
                "&nickname=" + nickname);

        httpPut.setHeader("Content-Type", "application/json");
        return httpPut;
    }

    public static HttpRequestBase getFriendList(String nickname, CallbackResponse callBack) throws IOException {

        HttpGet httpPost = new HttpGet(HttpConnecter.getRestfullBaseURL()
                + "Friend"
                + "?nickname=" + nickname
                + "&date=" + getRealDate());

        httpPost.setHeader("Content-Type", "application/json");

        background(httpPost, callBack);
        return httpPost;
    }

    /**
     * POST get Friends Score in Room
     */
    public static HttpRequestBase getFriendsInRoomGet(String rid, String fromdate, String todate, CallbackResponse callbackResponse) throws IOException {
        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL()
                + "Room/User"
                + "?rid=" + rid
                + "&fromdate=" + fromdate
                + "&todate=" + todate);

        httpGet.setHeader("Content-Type", "application/json");

        background(httpGet, callbackResponse);
        return httpGet;

    }


    public static String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

    /**
     * GET Auth Send
     */
    public static HttpRequestBase authGet(String phone, int random_num) throws IOException {

        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Auth"
                + "?phone=" + phone +
                "&random_num=" + random_num);

        return httpGet;
    }

    /**
     * POST Register Send
     */
    public static HttpRequestBase registerPost(String nickname, String password, String image, String phone, String gcmid) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Register");

        RegisterDataSet dataSet = new RegisterDataSet();
        dataSet.nickname = nickname;
        dataSet.password = password;
        dataSet.image = image;
        dataSet.phone = phone;
        dataSet.gcmid = gcmid;
        String json = new Gson().toJson(dataSet);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST create LifeRoom
     */
    public static HttpRequestBase createLifeRoomPost(String nickname, String type, String rule, String roomname, String maxholidaycount) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Room");

        LifeRoomDataSet dataSet = new LifeRoomDataSet();
        dataSet.nickname = nickname;
        dataSet.type = type;
        dataSet.rule = rule;
        dataSet.roomname = roomname;
        dataSet.maxholidaycount = maxholidaycount;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST create SubjectRoom
     */
    public static HttpRequestBase createSubjectRoomPost(String nickname, String type, String rule, String roomname,
                                                        String start_time, String duration_time, String showup_time, String meet_days) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Room");

        SubjectRoomDataSet dataSet = new SubjectRoomDataSet();
        dataSet.nickname = nickname;
        dataSet.type = type;
        dataSet.rule = rule;
        dataSet.roomname = roomname;
        dataSet.starttime = start_time;
        dataSet.durationtime = duration_time;
        dataSet.showuptime = showup_time;
        dataSet.meetdays = meet_days;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST invite Friend to Room
     */
    public static HttpRequestBase inviteFriendToRoomPost(String rid, String nickname) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Room/User");

        InviteFriendDataSet dataSet = new InviteFriendDataSet();
        dataSet.rid = rid;
        dataSet.nickname = nickname;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST get Friends Score in Room
     */
    public static HttpRequestBase getFriendsScorePost(String rid, String fromdate, String todate) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Timeset");

        TimeSetDataSet dataSet = new TimeSetDataSet();
        dataSet.rid = rid;
        dataSet.fromdate = fromdate;
        dataSet.todate = todate;

        String json = new Gson().toJson(dataSet);
        Log.i(LOG_TAG, "getFriendsScore request : " + json.toString());
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * GET My Information Send
     */
    public static HttpRequestBase getMyInfoGet(String nickname, String date) throws IOException {

        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL() +
                "User" +
                "?nickname=" + nickname +
                "&date=" + date);

        return httpGet;
    }

    /**
     * PUT My Information Send
     */
    public static HttpRequestBase updateMyInfoPut(String nickname, String password, String image) throws IOException {

        HttpPut httpPut = new HttpPut(HttpConnecter.getRestfullBaseURL() + "User");

        MyInfoDataSet dataSet = new MyInfoDataSet();
        dataSet.nickname = nickname;
        dataSet.password = password;
        dataSet.image = image;

        String json = new Gson().toJson(dataSet);

        httpPut.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPut.setEntity(entity);

        return httpPut;
    }

    /**
     * POST add Friend
     */
    public static HttpRequestBase addFriendPost(String nickname, String nickname_f) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Friend");

        AddFriendDataSet dataSet = new AddFriendDataSet();
        dataSet.nickname = nickname;
        dataSet.nicknamef = nickname_f;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * GET disconnect connection
     */
    public static HttpRequestBase disconnectConnectionGet(String previousip) throws IOException {

        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Socket"
                + "?previousip=" + previousip);

        return httpGet;
    }

    /**
     * PUT Quiz Answer Register Put
     */
    public static HttpRequestBase answerRegisterPut(String srid, String num, String type,
                                                    String answer, String nickname) throws IOException {

        HttpPut httpPut = new HttpPut(HttpConnecter.getRestfullBaseURL() + "Room/Quiz");

        QuizDataSet dataSet = new QuizDataSet();

        dataSet.srid = srid;
        dataSet.num = num;
        dataSet.type = type;
        dataSet.answer = answer;
        dataSet.nickname = nickname;

        String json = new Gson().toJson(dataSet);

        httpPut.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPut.setEntity(entity);

        return httpPut;
    }

    /**
     * GET quiz question
     */
    public static HttpRequestBase questionGet(String srid, String num, String nickname) throws IOException {

        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Room/Quiz" +
                "?srid=" + srid +
                "&num="+ num +
                "&nickname=" + nickname);

        return httpGet;
    }

    /**
     * POST quiz Register
     */
    public static HttpRequestBase quizRegisterPost(String srid, String type, String question, String solution,
                                                   String nickname, String title, String date) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Room/Quiz");

        RegisterQuizDataSet dataSet = new RegisterQuizDataSet();
        dataSet.srid = srid;
        dataSet.type = type;
        dataSet.question = question;
        dataSet.solution = solution;
        dataSet.nickname = nickname;
        dataSet.title = title;
        dataSet.date = date;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * POST register question
     */
    public static HttpRequestBase questionSetPost(String nickname, String date, String types, String title) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Quizset");

        RegisterQuestionDataSet dataSet = new RegisterQuestionDataSet();
        dataSet.nickname = nickname;
        dataSet.date = date;
        dataSet.types = types;
        dataSet.title = title;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    public static HttpRequestBase logoutDelete(String nickname) throws IOException{
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "LoginSession");


        LogoutDataSet dataSet = new LogoutDataSet();
        dataSet.nickname = nickname;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, "utf-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    public static HttpRequestBase uploadImage(String URL, String rid, String nickname, String filePath) throws IOException{
        HttpPost httpPost = new HttpPost(URL + "?rid="+rid + "&nickname=" + nickname);

        Log.i(LOG_TAG, "URL : " + URL + "?rid=" + rid + "&nickname=" + nickname);

        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("file", new FileBody(new File(filePath)));

        httpPost.setEntity(multipartEntity);

        return httpPost;
    }

    public static class downloadImageThread implements Runnable{
        //        private final BlockingQueue queue;
        private String fileurl = null;
        private int resize_width;
        private int resize_height;
        private String imgName = null;
        private Handler handler = null;
        private Context context = null;

        public downloadImageThread(BlockingQueue q, String fileurl, int resize_width, int resize_height, String imgName, Handler handler) {
//            this.queue = q;
            this.fileurl = fileurl;
            this.resize_width = resize_width;
            this.resize_height = resize_height;
            this.imgName = imgName;
            this.handler = handler;
        }

        public downloadImageThread(String fileurl, int resize_width, int resize_height, String imgName, Handler handler) {
            this.fileurl = fileurl;
            this.resize_width = resize_width;
            this.resize_height = resize_height;
            this.imgName = imgName;
            this.handler = handler;
        }

        public downloadImageThread(String fileurl, String imgName, Handler handler) {
            this.fileurl = fileurl;
            this.imgName = imgName;
            this.handler = handler;
        }

        public void run()
        {
            Bitmap image = null;
            Log.w(LOG_TAG, "getImage before try, fileurl : " + fileurl);
            try {
                DBHelper helper = new DBHelper(mContext);
                String imagePath = getImage2Server(imgName, fileurl, resize_width, resize_height);
                helper.insertImage(imgName, imagePath);
                helper.close();
                Log.w(LOG_TAG, "imageMap Put : " + imgName);


                image = SafeDecodeBitmapFile(imagePath);
                Message msg = handler.obtainMessage();
                Bundle b = new Bundle();

                b.putParcelable("image", image);
                b.putString("imgName", imgName);
                msg.setData(b);
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // File 에서 이미지를 불러올 때 안전하게 불러오기 위해서 만든 함수
    // bitmap size exceeds VM budget 오류 방지용
    private static Bitmap SafeDecodeBitmapFile(String strFilePath)
    {
        File file = new File(strFilePath);
        if (file.exists() == false)
        {
            return null;
        }

        // 가로, 세로 최대 크기 (이보다 큰 이미지가 들어올 경우 크기를 줄인다.)
        final int IMAGE_MAX_SIZE    = 1500;
        BitmapFactory.Options bfo   = new BitmapFactory.Options();
        bfo.inJustDecodeBounds      = true;

        BitmapFactory.decodeFile(strFilePath, bfo);

        if(bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE)
        {
            bfo.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE
                    / (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
        }
        bfo.inJustDecodeBounds = false;
        bfo.inPurgeable = true;
        bfo.inDither = true;

        final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);

        return bitmap;
    }

    /** 서버에서 해당 URL에 있는 이미지를 가져옴 */
//    private static Bitmap getImage2Server(String URI, int resized_width, int resized_height)
//    {
//        Bitmap imgBitmap = null;
//        Bitmap imgResizedBitmap = null;
//        try
//        {
//            Log.i(LOG_TAG, "getImage2Server w/ " + URI);
//            URL url = new URL(URI);
//            URLConnection conn = url.openConnection();
//            conn.connect();
//            int nSize = conn.getContentLength();
//            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
//            imgBitmap = BitmapFactory.decodeStream(bis);
//            bis.close();
//            imgResizedBitmap = Bitmap.createScaledBitmap(imgBitmap, resized_width, resized_height, true);
//            Log.i(LOG_TAG, "getImage2Server close");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        return imgResizedBitmap;
//    }

    private static String getImage2Server(String fileName, String URI, int resized_width, int resized_height)
    {
        Bitmap imgBitmap = null;
        Bitmap imgResizedBitmap = null;
        try
        {
            Log.i(LOG_TAG, "getImage2Server w/ " + URI);
            URL url = new URL(URI);
            URLConnection conn = url.openConnection();
            conn.connect();
            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            imgResizedBitmap = Bitmap.createScaledBitmap(imgBitmap, resized_width, resized_height, true);
            Log.i(LOG_TAG, "getImage2Server close");

            FileOutputStream openFileOutput = null;
            openFileOutput = new FileOutputStream(new File( default_file_dir + fileName));


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imgResizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();


            openFileOutput.write(data);
            openFileOutput.flush();
            openFileOutput.close();

            return default_file_dir + fileName;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return default_file_dir + fileName;
    }


    public static JSONObject getHttpResponseToJSON(HttpResponse httpResponse) {
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();
        JSONObject obj = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }

            Log.e("ilju", "builder.toString() : " + builder.toString());

            obj = new JSONObject(URLDecoder.decode(builder.toString(), "utf-8"));

            System.out.println(obj.getString("status"));

            if(TextUtils.isEmpty(obj.getString("status")))
            {
                obj.put("status", 10000);
            }else if("null".equals(obj.getString("status"))){
                obj.put("status", 10000);
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(builder.toString());

        return obj;
    }

    public static class LoginDataSet {
        public String nickname;
        public String password;
        public String gcmid;
    }

    public static class TimeDataSet {
        public String nickname;
        public String targettime;
        public String accomplishedtime;
        public String date;
    }

    public static class AuthDataSet {
        public String phone;
        public int randomnum;
    }

    public static class RegisterDataSet {
        public String nickname;
        public String password;
        public String image;
        public String phone;
        public String gcmid;

    }

    public static class LifeRoomDataSet {
        public String nickname;
        public String type;
        public String rule;
        public String roomname;
        public String maxholidaycount;
    }

    public static class SubjectRoomDataSet {
        public String nickname;
        public String type;
        public String rule;
        public String roomname;
        public String starttime;
        public String durationtime;
        public String showuptime;
        public String meetdays;
    }

    public static class InviteFriendDataSet {
        public String rid;
        public String nickname;
    }

    public static class TimeSetDataSet {
        public String rid;
        public String fromdate;
        public String todate;
    }

    public static class MyInfoDataSet {
        public String nickname;
        public String password;
        public String image;
    }

    public static class AddFriendDataSet {
        public String nickname;
        public String nicknamef;
    }

    public static class QuizDataSet {
        public String srid;
        public String num;
        public String type;
        public String answer;
        public String nickname;
    }

    public static class RegisterQuizDataSet {
        public String srid;
        public String type;
        public String question;
        public String solution;
        public String nickname;
        public String title;
        public String date;
    }

    public static class RegisterQuestionDataSet{
        public String nickname;
        public String date;
        public String types;
        public String title;
    }

    public static class LogoutDataSet{
        public String nickname;
    }

}
