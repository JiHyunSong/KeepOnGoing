package com.secsm.keepongoing.Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

public class Quiz_set extends BaseActivity {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    public String LOG_TAG = "Solve MAIN";
    listAdapter_Solve mAdapter = null;
    private Button btnSubmit;
    private int status_code;
    private String rMessageput;
    private RequestQueue vQueue;
    private String question;
    private String questiontype;
    private String solution;
    private String answer;
    private String num;
    TextView subject,textview;
    String[] arr = null;
    ArrayList<Quiz_data> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        vQueue = Volley.newRequestQueue(this);
        vQueue = MyVolley.getRequestQueue(Quiz_set.this);
        setContentView(R.layout.activity_solve__main);
        list = new ArrayList<Quiz_data>();

        subject =  (TextView) findViewById(R.id. solve_subject_solve);
        textview = (TextView) findViewById(R.id.textview_solve);



        mAdapter = new listAdapter_Solve(this, list);
        listView = (ListView) findViewById(R.id.listView_Quiz_solve);
        //listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setAdapter(mAdapter);
        questionRequest();
        settingTextView();
        addListenerOnButton();

    }

    public void addlist(String solution,String answer){
        String[] listnum = solution.split("\\|");
        String[] listtype;


        for(int i=0;i<listnum.length;i++) {

            listtype=listnum[i].split("\\$");
            if(listtype[1].toString().equals("multi"))
                list.add(new Quiz_data("multi"));
            if(listtype[1].toString().equals("essay"))
                list.add(new Quiz_data("essay"));
            if(listtype[1].toString().equals("tf"))
                list.add(new Quiz_data("tf"));

            Log.e("minsu) : ","minsu): solution : "+i+" type : "+listtype[1]);

        }
        settingListView();


    }
    public int checkanswer(String solution,String answer){
        int total=0;
        String[] listnum = solution.split("\\|");
        String[] listtype;
        String[] ansnum = answer.split("\\|");
        String[] anstype;

        for(int i=0;i<listnum.length;i++) {

            if(ansnum[i].split("\\$")!=null&&listnum[i].split("\\$")!=null) {
                listtype = listnum[i].split("\\$");
                anstype = ansnum[i].split("\\$");
                if (listtype[1].toString().equals("multi")) {

                    if (listtype[2].toString().equals(anstype[2].toString())) {
                        list.get(i).correct=1;
                        total++;
                    }
                    else if(anstype[2].toString().equals("error"))
                        list.get(i).correct=0;
                    else
                        list.get(i).correct=-1;

                }
                if (listtype[1].toString().equals("essay")) {
                    if (listtype[2].toString().equals(anstype[2].toString())) {
                        list.get(i).correct=1;
                        total++;
                    }
                    else if(anstype[2].toString().equals("error"))
                        list.get(i).correct=0;
                    else
                        list.get(i).correct=-1;

                }
                if (listtype[1].toString().equals("tf")) {
                    if (listtype[2].toString().equals(anstype[2].toString())) {
                        list.get(i).correct=1;
                        total++;
                    }
                    else if(anstype[2].toString().equals("error"))
                        list.get(i).correct=0;
                    else
                        list.get(i).correct=-1;
                }
            }
        }
        settingListView();

        return total;
    }




    public void settingTextView() {

        subject.setText(questiontype);
        textview.setText(question);

        /*subject.setText("수학");
        textview.setText("다음중 가장 안정적인 숫자는?\n1.1024\n2. 3\n" +"3. 5\n" + "4. 6"
        +"\n"+"다음중 가장 맘에 드는 숫자는?\n1.1024\n2. 3\n" +"3. 5\n" + "4. 6");*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_main, menu);
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

    protected void onResume() {
        super.onResume();
        settingListView();
    }



    private void settingListView() {

        mAdapter.refresh();
    }

    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }


    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.btnSubmit_solve);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearFocus();
                //@민수 제출



                int total=checkanswer(solution,request_check(list).toString());
                TextView solve_main_tv=(TextView)findViewById(R.id.solve_main_tv);
                solve_main_tv.setText("total score without essay : +"+ total);

                answerRegisterRequest(subject.getText().toString(), request_check(list).toString());
            }

        });
    }

    public String mulitiplecheck(ArrayList<Quiz_data> arrays,int position){
        String temp="";
        if(arrays.get(position).chk1)
            temp+= "1";
        if(arrays.get(position).chk2)
            temp+= "2";
        if(arrays.get(position).chk3)
            temp+= "3";
        if(arrays.get(position).chk4)
            temp+= "4";
        if(arrays.get(position).chk5)
            temp+= "5";
        if(temp=="")
            return "error";
        return temp;
    }

    public String request_check(ArrayList<Quiz_data> arrays){
        int position;
        String result="";
        for(position=0;position<arrays.size();position++) {
            Log.e("minsu) : ", "list content name: " + arrays.get(position).name);
            if(arrays.get(position).name.equals("multi")){
                if(mulitiplecheck(arrays,position).equals("error")) {
                    result += position+"$"+arrays.get(position).name+"$"+"error"+"|";
                }else {
                    result += position+"$"+arrays.get(position).name+"$"+mulitiplecheck(arrays, position)+"|";
                }
            }
            else if(arrays.get(position).name.equals("essay")){
                if(arrays.get(position).essay.equals("")) {
                    result+=position+"$"+arrays.get(position).name+"$"+"error"+"|";
                }
                else {
                    result+=position+"$"+arrays.get(position).name+"$"+arrays.get(position).essay+"|";
                }}
            else if(arrays.get(position).name.equals("tf")){
                if(arrays.get(position).truebtn==true) {
                    result+=position+"$"+arrays.get(position).name+"$"+ true+"|";
                }
                else if(arrays.get(position).falsebtn==true) {
                    result+=position+"$"+arrays.get(position).name+"$"+false+"|";
                }
                else
                    result+=position+"$"+arrays.get(position).name+"$"+"error"+"|";
            }
        }
        return result;
    }





    private void answerRegisterRequest(String type, String answer) {
        //@통신

        answer = answer.trim().replace(" ", "%20");
        answer = answer.trim().replace("\n", "%0A");
        String get_url = KogPreference.REST_URL +
                "Room/Quiz";// +
//                "?srid=" + KogPreference.getRid(Quiz_set.this) +
//                "&num="+ KogPreference.getQuizNum(Quiz_set.this) +
//                "&type="+type+//type 받아와야됨
//                "&answer=" + answer+
//                "&nickname="+ KogPreference.getNickName(Quiz_set.this);

        JSONObject sendBody = new JSONObject();
        try{
            sendBody.put("srid", KogPreference.getRid(Quiz_set.this));
            sendBody.put("num", KogPreference.getQuizNum(Quiz_set.this));
            sendBody.put("type", type);
            sendBody.put("answer", answer);
            sendBody.put("nickname", KogPreference.getNickName(Quiz_set.this));
            Log.i(LOG_TAG, "sendBody : " + sendBody.toString() );
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, " sendBody e : " + e.toString());
        }

        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, Encrypt.encodeIfNeed(get_url), sendBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                rMessageput = response.getString("message");

                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "답안 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
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
                Log.i(LOG_TAG, "Response Error");
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        vQueue.add(jsObjRequest);
    }
    //@통신



    private void questionRequest() {
        String get_url = KogPreference.REST_URL +
                "Room/Quiz" +
                "?srid=" + KogPreference.getRid(Quiz_set.this) +
                "&num="+KogPreference.getQuizNum(Quiz_set.this) +//num 받아와야됨
                "&nickname=" + KogPreference.getNickName(Quiz_set.this);

        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");

                        try {
                            status_code = response.getInt("status");
                            Log.e(LOG_TAG, "minsu) : status code :" + Integer.toString(status_code));
                            if (status_code == 200) {
                                JSONArray rMessageget;

                                rMessageget = response.getJSONArray("message");
                                JSONObject rObj;

                                rObj=rMessageget.getJSONObject(0);
                                questiontype= URLDecoder.decode(rObj.getString("type").toString(), "UTF-8");
                                question=URLDecoder.decode(rObj.getString("question").toString(), "UTF-8");
                                solution=URLDecoder.decode(rObj.getString("solution").toString(), "UTF-8");
                                Log.e("minsu) :","contents2 : "+question+" | "+questiontype+" | "+solution);
                                settingTextView();
                                //question = question.replace("\\\n", System.getProperty("line.separator"));
                                addlist(solution,answer);


                                // real action
                                //          GoNextPage();
                            } else if (status_code == 9001) {
                                Toast.makeText(getBaseContext(), "문제 가져오기가 불가능합니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
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
                Log.i(LOG_TAG, "Response Error");
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        vQueue.add(jsObjRequest);
    }


}
