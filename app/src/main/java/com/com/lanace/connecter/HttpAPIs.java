package com.com.lanace.connecter;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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

    /** POST Auth Send */
    public static HttpRequestBase authGet(String phone, int random_num) throws IOException {

        HttpGet httpGet = new HttpGet(HttpConnecter.getRestfullBaseURL() + "Auth"
                +"?phone=" + phone +
                "&random_num=" + random_num);

//        AuthDataSet dataSet = new AuthDataSet();
//        dataSet.phone = phone;
//        dataSet.randomnum = random_num;
//
//        String json = new Gson().toJson(dataSet);
//
//        httpPost.setHeader("Content-Type", "application/json");
//        StringEntity entity = new StringEntity(json);
//        httpPost.setEntity(entity);

        return httpGet;
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

    public static class AuthDataSet{
        public String phone;
        public int randomnum;
    }

}
