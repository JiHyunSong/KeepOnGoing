package com.secsm.keepongoing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Adapters.FriendScore;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScoreViewActivity extends Activity {
    private static String LOG_TAG = "ScoreViewActivity";
    private String[] FriendNicks;
    private Intent intent;
    private HashMap<String, ArrayList<FriendScore>> mFriendsScore;
    private FriendScore[] Scores;
    private int maxIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_view);

        intent = getIntent();
        FriendNicks = intent.getStringArrayExtra("Friends");

        if (KogPreference.DEBUG_MODE) {
            for (int i = 0; i < FriendNicks.length; i++) {
                Log.i(LOG_TAG, "get Friends nickname " + i + " : " + FriendNicks[i]);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFriendsScore = new HashMap<String, ArrayList<FriendScore>>();
        getFriendsScoreRequest();

    }


    private void setView() {

        String[] printResult = new String[(FriendNicks.length + 1) * (maxIndex + 3 + 3 + 1)];
        String[] tag = {"월", "화", "수", "목", "금", "토", "일"};//,
        String[] tag2 = {"목표합계", "달성합계", "도합점수"};

        int mI = FriendNicks.length + 1;
        int mJ = (maxIndex + 3 + 3 + 1);

        Log.i(LOG_TAG, " mI : " + mI + " mJ : " + mJ);

//        if(KogPreference.DEBUG_MODE)
//        {
//            for(int k=0; k<FriendNicks.length; k++)
//            {
//                Log.i(LOG_TAG, "get Friends nickname " + k + " : " + FriendNicks[k]);
//                Log.i(LOG_TAG, " " + mFriendsScore.get(FriendNicks[k]).get(0).getGoalTime());
//
//            }
//        }


        for (int i = 0; i < mI; i++) {
            for (int j = 0; j < 7 + 1; j++) {
                if (i == 0 && j == 0) {
//                    arrList[j][i] = "";
                    printResult[(j * mI) + i] = "일\\유저";
                } else if (j == 0) {
                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + FriendNicks[i - 1]);

//                    arrList[j][i] = FriendNicks[i-1];
                    printResult[(j * mI) + i] = FriendNicks[i - 1];
                } else if (i == 0) {
                    // 요일
                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + tag[((j - 1) % 7)]);

//                    arrList[j][i] = tag[((j-1) % 7)];
                    printResult[(j * mI) + i] = tag[((j - 1) % 7)];
                } else {
                    // 데이터 삽입
//                    arrList[j][i] = mFriendsScore.get(FriendNicks[i-1]).get(j-1).getGoalTime();
                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + mFriendsScore.get(FriendNicks[i - 1]).get(j - 1).getGoalTime());
                    printResult[(j * mI) + i] = mFriendsScore.get(FriendNicks[i - 1]).get(j - 1).getGoalTime();
                }
            }
            for (int j = 8; j < 8 + 3; j++) {
                if (i == 0) {
                    // 요일
                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + tag2[(j - 8) % 3]);

//                    arrList[j][i] = tag[((j-1) % 7)];
                    printResult[(j * mI) + i] = tag2[ (j - 8) % 3];
                } else {
                    // 데이터 삽입
//                    arrList[j][i] = mFriendsScore.get(FriendNicks[i-1]).get(j-1).getGoalTime();

                    int count = i;
                    int sum = 0;
                    int hour = 0;
                    int min = 0;
                    switch (j) {
                        case 8:
                            // 목표합계
                            for (int n = 0; n < 7; n++) {
                                count += mI;// 00:00 / 00:00
                                hour += Integer.parseInt(printResult[count].substring(8, 10));
                                min += Integer.parseInt(printResult[count].substring(11, 13));
                            }

                            hour += min/60;
                            min %= 60;

                            count += mI * 1;
                            printResult[count] = "" + (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);
                            break;
                        case 9:
                            // 달성합계
                            for (int n = 0; n < 7; n++) {
                                count += mI;
                                hour += Integer.parseInt(printResult[count].substring(0, 2));
                                min += Integer.parseInt(printResult[count].substring(3, 5));
                            }

                            hour += min/60;
                            min %= 60;

                            count += mI * 2;
                            printResult[count] = "" + (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);

                            break;
                        case 10:
                            // sum
//                            int count = i;
//                            int sum = 0;
                            for (int n = 0; n < 7; n++) {
                                count += mI;
                                sum += mFriendsScore.get(FriendNicks[i-1]).get(n).getIntScore();
                            }

                            count += mI * 3;
                            printResult[count] = "" + sum;

                            break;
                    }
                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
                    Log.i(LOG_TAG, "index : " + count + " | value : " + printResult[count]);
                }
            }
        }

        // 나머지 계산

        for (int i = 0; i < mI; i++) {
            int j = 11;
            for (j = 11; j < (maxIndex % 7) + 11 ; j++) {
                if(i == 0){
                    // 요일
//                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
//                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + tag[((j - 11) % 7)]);

//                    arrList[j][i] = tag[((j-1) % 7)];
                    printResult[(j * mI) + i] = tag[((j - 11) % 7)];
                }
                else{
                    // 데이터 삽입
//                    arrList[j][i] = mFriendsScore.get(FriendNicks[i-1]).get(j-1).getGoalTime();
//                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
//                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + mFriendsScore.get(FriendNicks[i - 1]).get(j - 1 - 3).getGoalTime());
                    printResult[(j * mI) + i] = mFriendsScore.get(FriendNicks[i - 1]).get(j - 1 - 3).getGoalTime();
                }
            }

            int temp_j = j;
            for (; j < temp_j + 3; j++) {
                if (i == 0) {
                    // 요일
//                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
//                    Log.i(LOG_TAG, "index : " + ((j * mI) + i) + " | value : " + tag2[(j-temp_j) % 3]);

//                    arrList[j][i] = tag[((j-1) % 7)];
                    printResult[(j * mI) + i] = tag2[(j-temp_j) % 3];
                } else {
                    // 데이터 삽입
//                    arrList[j][i] = mFriendsScore.get(FriendNicks[i-1]).get(j-1).getGoalTime();

                    int count = i + (mI * 10);
                    int sum = 0;
                    int hour = 0;
                    int min = 0;

                    switch (j - temp_j) {
                        case 0:
                            // 목표합계
                            for (int n = 0; n < (maxIndex % 7); n++) {
                                count += mI;// 00:00 / 00:00
                                hour += Integer.parseInt(printResult[count].substring(8, 10));
                                min += Integer.parseInt(printResult[count].substring(11, 13));
                            }

                            hour += min/60;
                            min %= 60;

                            count += mI * 1;
                            printResult[count] = "" + (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);
                            break;
                        case 1:
                            // 달성합계
                            for (int n = 0; n < (maxIndex % 7); n++) {
                                count += mI;
                                hour += Integer.parseInt(printResult[count].substring(0, 2));
                                min += Integer.parseInt(printResult[count].substring(3, 5));
                            }

                            hour += min/60;
                            min %= 60;

                            count += mI * 2;
                            printResult[count] = "" + (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);

                            break;
                        case 2:
                            // sum
//                            int count = i;
//                            int sum = 0;
                            for (int n = 7; n < maxIndex; n++) {
                                count += mI;
                                sum += mFriendsScore.get(FriendNicks[i - 1]).get(n).getIntScore();
                            }

                            count += mI * 3;
                            printResult[count] = "" + sum;

                            break;
                    }
//                    Log.i(LOG_TAG, " i : " + i + " j : " + j);
//                    Log.i(LOG_TAG, "index : " + count + " | value : " + printResult[count]);
                }
            }



        }
        // 합산2

        /*
        for(int i = 0; i < mI ; i++)
        {
            for (int j = 0 ; j < mJ; j++)
            {
                if(i == 0 && j == 0)
                {
                    continue;
                }else if(i==0 && j < mI)
                {
                    Log.i(LOG_TAG, " i : " + i + " j : " + j );
                    Log.i(LOG_TAG, "index : " + i + (j * mJ) + " | value : " + FriendNicks[j-1]);
                    printResult[i + (j * mJ)] = FriendNicks[j-1];
                }else if(j==0)
                {
                    printResult[i + (j*mJ)] = tag[((i+1) % 10)];
                }
            }
        }


         */
        Log.i(LOG_TAG, "test Message1");
        appendRow(printResult, mI);
    }

    public void appendRow(String[] strarray, int width) {

//        Log.i(LOG_TAG, "test Message2, width : " + width);
        TableLayout tb = (TableLayout) findViewById(R.id.score_view_info_table);
        tb.removeAllViewsInLayout();
        TextView[] tv = new TextView[strarray.length];
        for (int i = 1; i < strarray.length + 1; i++) {
            tv[i - 1] = new TextView(this);
            tv[i - 1].setText(strarray[i - 1].toString());
            tv[i - 1].setTextColor(Color.parseColor("#7F7F7F"));
            tv[i - 1].setBackgroundResource(R.drawable.cell_shape);
            tv[i - 1].setPadding(5, 5, 5, 5);
            tv[i - 1].setGravity(Gravity.CENTER);
            tv[i - 1].setShadowLayer(1.0f, 1, 1, Color.parseColor("#AFFFFFFF"));

            if ((i != 0 && i % width == 0)) {
                TableRow tr = new TableRow(this);

                tr.setLayoutParams(new TableRow.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                tr.setBackgroundColor(Color.parseColor("#DFDFDF"));
                Log.i("log", "i : " + i);

                for (int j = width; j > 0; j--) {
                    tr.addView(tv[i - j], new TableRow.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT, 1));
                }
//
//                tr.addView(tv[i - 3], new TableRow.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, 1));
//
//                tr.addView(tv[i - 2], new TableRow.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, 1));
//
//                tr.addView(tv[i - 1], new TableRow.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, 1));


                tb.addView(tr, new TableLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.FILL_PARENT));
            }
        }
    }


    private String getPrevMonday() {
        Calendar now = Calendar.getInstance();
        int weekday = now.get(Calendar.DAY_OF_WEEK);
//        Log.i("getMonday1", "now.toString : " + now.toString());
//        Log.i("getMonday1", "weekday : " + weekday);
        if (weekday != Calendar.MONDAY) {
            int days = (Calendar.SUNDAY - weekday + 1) % 7;
            now.add(Calendar.DAY_OF_YEAR, days);
        }
        now.add(Calendar.DAY_OF_YEAR, -7);
        Date date = now.getTime();

        String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return format;
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
                // TODO : implement setAllEnable()
//                setAllEnable();
            }
            else if(msg.what == -1){
                // TODO : implement setAllDisable()
//                setAllDisable();
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


    /** AuthNumRegister
     * statusCode == 200 => send SMS to phone num
     * statusCode == 1001 => auth duplicate! go back to the back page */
    Handler getFriendsScoreRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONArray rMessage;
                    rMessage = result.getJSONArray("message");

                    Log.i(LOG_TAG, "1 rMessage : " + rMessage.toString());
                    ArrayList<FriendScore> temp = new ArrayList<FriendScore>();

                    //////// real action ////////
                    JSONObject rObj;
                    for (int i = 0; i < rMessage.length(); i++) {
                        rObj = rMessage.getJSONObject(i);
                        Log.i(LOG_TAG, "2 rObj : " + rObj.toString());

                        if (maxIndex < Integer.parseInt(rObj.getString("index"))) {
                            maxIndex = Integer.parseInt(rObj.getString("index"));
                        }
//                                    Log.i(LOG_TAG, "3 maxIndex : " + maxIndex);

                        FriendScore fs = new FriendScore(
                                rObj.getString("score"),
                                rObj.getString("index"),
                                rObj.getString("accomplishedtime"),
                                rObj.getString("date"),
                                rObj.getString("targettime"),
                                URLDecoder.decode(rObj.getString("nickname"), "UTF-8")
                        );
                        temp.add(fs);
                    }
                    Log.i(LOG_TAG, "temp filled");
//                                Log.i(LOG_TAG, "FriendNicks.length : " + FriendNicks.length);
                    for (int i = 0; i < FriendNicks.length; i++) {
                        ArrayList<FriendScore> tempFs = new ArrayList<FriendScore>();
                        tempFs.clear();
                        for (int j = 0; j < temp.size(); j++) {
                            if (temp.get(j).getNickname().equals(FriendNicks[i])) {
                                tempFs.add(Integer.parseInt(temp.get(j).getIndex()), temp.get(j));
//                                            Log.i(LOG_TAG, "add TempFS : " + temp.get(j));
                            }
                        }
                        mFriendsScore.put(FriendNicks[i], tempFs);
//                                    Log.i(LOG_TAG, "put mFriendsScore : " + FriendNicks[i]);

                    }

                    maxIndex++;// if index 0 ~ 11, maxIndex will be 12

                    Log.i(LOG_TAG, "check log ");
                    if(KogPreference.DEBUG_MODE)
                    {
                        Iterator<String> iterator = mFriendsScore.keySet().iterator();
                        while(iterator.hasNext())
                        {
                            String Key = (String) iterator.next();
                            Log.i(LOG_TAG, "KEY : " + Key + " VALUE : " +mFriendsScore.get(Key));
                            for(int i=0; i<maxIndex; i++)
                                Log.i(LOG_TAG, "KEY : " + Key + " VALUE : " +mFriendsScore.get(Key).get(i));


                        }
                    }

                    setView();

                } else {
                    Toast.makeText(getBaseContext(), "통신 에러 : \n친구 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (Exception e)
            {
                errorHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    };

    private void getFriendsScoreRequest() {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase getFriendsScoreRequest = HttpAPIs.getFriendsScorePost(
                    KogPreference.getRid(ScoreViewActivity.this),
                    getPrevMonday(),
                    getRealDate());
            HttpAPIs.background(getFriendsScoreRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = getFriendsScoreRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        getFriendsScoreRequestHandler.sendMessage(msg);
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
            e.printStackTrace();
        }
    }


}
