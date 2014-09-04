package com.com.lanace.connecter;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

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
import java.net.URLDecoder;
import java.sql.Timestamp;

/**
 * Created by Lanace on 2014-09-02.
 */
public class HttpAPIs {

    private static final String LOG_TAG = "HttpAPIs";

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
//                "?rid=" + KogPreference.getRid(StudyRoomActivity.this) +
//                "&fromdate=" + getThisMonday() +
//                "&todate=" + getRealDate();
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


    public static JSONObject getHttpResponseToJSON(HttpResponse httpResponse) {
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();
        JSONObject obj = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
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
}
