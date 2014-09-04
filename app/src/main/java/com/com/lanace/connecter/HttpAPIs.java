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
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
        dataSet.target_time = target_time;
        dataSet.accomplished_time = accomplished_time;
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
        dataSet.target_time = target_time;
        dataSet.accomplished_time = accomplished_time;
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

    public static HttpRequestBase getStudyRoomsRequest(String nickname){
        HttpGet httpPut = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Rooms" +
                "?nickname=" + nickname);
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

    public static class LoginDataSet{
        public String nickname;
        public String password;
        public String gcmid;
    }

    public static class TimeDataSet{
        public String nickname;
        public String target_time;
        public String accomplished_time;
        public String date;
    }

}
