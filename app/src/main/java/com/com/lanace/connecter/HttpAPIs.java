package com.com.lanace.connecter;

import android.util.Log;

import com.google.gson.Gson;
import com.secsm.keepongoing.Shared.KogPreference;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

/**
 * Created by Lanace on 2014-09-02.
 */
public class HttpAPIs {

    private static final String LOG_TAG = "HttpAPIs";

    public static JSONObject getJSONData(HttpResponse response){
        JSONObject result = new JSONObject();
        try {
            Log.e(LOG_TAG, "zzz) Set-Cookie 길이: " + response.getHeaders("Set-Cookie").length);
            if(response.getHeaders("Set-Cookie").length > 0)
                result.put("Set-Cookie", response.getHeaders("Set-Cookie")[0].getValue());

            result.put("httpStatusCode", response.getStatusLine().getStatusCode());
            StringBuffer sb = new StringBuffer();
            byte[] b = new byte[1024];

            long langht = response.getEntity().getContentLength();
            int index;
            InputStream is = response.getEntity().getContent();
            for(int  n=0;  n < langht; n+=index){

                if((index = is.read(b)) > 0)
                    sb.append(new String(b, 0, index));
                else
                    Log.e(LOG_TAG, "read value is " + index);
            }

            Log.i(LOG_TAG, "res: " + sb.toString());
            result.put("res", sb.toString() );

            return result;

        } catch (Exception e) {
            Log.e(LOG_TAG, "err: " + e.getMessage());
        } finally{
        }
        return null;
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

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /** POST Time Send */
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
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /** POST Time Put */
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
        StringEntity entity = new StringEntity(json);
        httpPut.setEntity(entity);

        return httpPut;
    }

    public static HttpRequestBase getFriendsRequest(String nickname){
        HttpGet httpPut = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Friend" +
                "?nickname=" + nickname +
                "&date=" + getRealDate());
        return httpPut;
    }
    /** POST Auth Send */
    public static HttpRequestBase authPost(String phone, int random_num) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Auth");

        AuthDataSet dataSet = new AuthDataSet();
        dataSet.phone = phone;
        dataSet.randomnum = random_num;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    public static HttpRequestBase getStudyRoomsRequest(String nickname, CallbackResponse callbackResponse) throws IOException {
        HttpGet httpPut = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Rooms" +
                "?nickname=" + nickname);

        background(httpPut, callbackResponse);

        return httpPut;
    }

    public static HttpRequestBase outRoomRequest(String room_id, String nickname){
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

    public static String getRealDate() {
        long time = System.currentTimeMillis();
        Timestamp currentTimestamp = new Timestamp(time);
        return currentTimestamp.toString().substring(0, 10);
    }

    /** GET Auth Send */
    public static HttpRequestBase authGet(String phone, int random_num) throws IOException {

        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Auth"
                +"?phone=" + phone +
                "&random_num=" + random_num);

        return httpGet;
    }

    /** POST Register Send */
    public static HttpRequestBase registerPost(String nickname, String password, String image, String phone, String gcmid) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Auth");

        RegisterDataSet dataSet = new RegisterDataSet();
        dataSet.nickname = nickname;
        dataSet.password = password;
        dataSet.image = image;
        dataSet.phone = phone;
        dataSet.gcmid = gcmid;
        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /** POST create LifeRoom */
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
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /** POST create SubjectRoom */
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
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /** POST invite Friend to Room */
    public static HttpRequestBase inviteFriendToRoomPost(String rid, String nickname) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Room/User");

        InviteFriendDataSet dataSet = new InviteFriendDataSet();
        dataSet.rid = rid;
        dataSet.nickname = nickname;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /** POST get Friends Score in Room*/
    public static HttpRequestBase getFriendsScorePost(String rid, String fromdate, String todate) throws IOException {
        HttpPost httpPost = new HttpPost(HttpConnecter.getRestfullBaseURL() + "Timeset");

        TimeSetDataSet dataSet = new TimeSetDataSet();
        dataSet.rid = rid;
        dataSet.fromdate = fromdate;
        dataSet.todate = todate;

        String json = new Gson().toJson(dataSet);

        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);

        return httpPost;
    }

    public static JSONObject getHttpResponseToJSON(HttpResponse httpResponse) {
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();
        JSONObject obj = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            obj = new JSONObject(builder.toString());
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

    public static class LoginDataSet{
        public String nickname;
        public String password;
        public String gcmid;
    }

    public static class TimeDataSet{
        public String nickname;
        public String targettime;
        public String accomplishedtime;
        public String date;
    }

    public static class AuthDataSet{
        public String phone;
        public int randomnum;
    }

    public static class RegisterDataSet{
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

    public static class SubjectRoomDataSet{
        public String nickname;
        public String type;
        public String rule;
        public String roomname;
        public String starttime;
        public String durationtime;
        public String showuptime;
        public String meetdays;
    }

    public static class InviteFriendDataSet{
        public String rid;
        public String nickname;
    }

    public static class TimeSetDataSet{
        public String rid;
        public String fromdate;
        public String todate;
    }

}
