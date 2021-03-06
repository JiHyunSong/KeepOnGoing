package com.secsm.keepongoing.Quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.KogPreference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class Solve_Main extends BaseActivity {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    public String LOG_TAG = "Solve MAIN";
    listAdapter_Solve mAdapter = null;
    private ToggleButton btnSubmit;
    private String rMessageput;
    private String question;
    private String questiontype;
    private String solution;
    private String title;
    private String date;
    private String answer;
    private String num;
    TextView subject,textview,question_title_content,date_view;
    String[] arr = null;
    ArrayList<Quiz_data> list;
    private ScrollView sv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve__main);
        list = new ArrayList<Quiz_data>();

        sv1 = (ScrollView)findViewById(R.id.scroll_problemandsubject_solve);
        subject =  (TextView) findViewById(R.id. solve_subject_solve);
        textview = (TextView) findViewById(R.id.textview_solve);
        textview.setMovementMethod(new ScrollingMovementMethod());
        question_title_content = (TextView) findViewById(R.id.question_title_content);
        date_view=(TextView) findViewById(R.id.date_view);


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


    private void answerclear(){
        for(int i=0;i<list.size();i++)
        {
            list.get(i).correct=-3;
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
                list.get(i).answer=listtype[2].toString();
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
        textview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                sv1.requestDisallowInterceptTouchEvent(true);
                //스크롤뷰가 텍스트뷰의 터치이벤트를 가져가지 못하게 함
                return false;
            }
        });
        question_title_content.setText(title);
        date_view.setText(date);

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
        DBHelper mDBhelper = new DBHelper(getBaseContext());
        mDBhelper.UpdateQuizNew(KogPreference.getRid(getBaseContext()), KogPreference.getQuizNum(getBaseContext()), false);
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

        btnSubmit = (ToggleButton) findViewById(R.id.btnSubmit_solve);
        btnSubmit.setText("제출");
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSubmit.isChecked()) {
                    btnSubmit.setText("다시 풀기");
                    listView.clearFocus();
                    //@민수 제출
                    int total = checkanswer(solution, request_check(list).toString());
                    TextView solve_main_tv = (TextView) findViewById(R.id.solve_main_tv);
                    solve_main_tv.setText("점수 : +" + total);

                    answerRegisterRequest(subject.getText().toString(), request_check(list).toString());
                }
                else {
                    btnSubmit.setText("제출");
                    listView.clearFocus();
                    answerclear();
                    TextView solve_main_tv = (TextView) findViewById(R.id.solve_main_tv);
                    solve_main_tv.setText("점수 : ");
                  //  answerRegisterRequest(subject.getText().toString(), request_check(list).toString());

                }
            }

        });
    }

    public String mulitiplecheck(ArrayList<Quiz_data> arrays,int position){
        String temp="";
        if(arrays.get(position).chk1)
            temp+= "1";
        if(arrays.get(position).chk2) {
            if(!temp.equals(""))
                temp+=",";
            temp += "2";
        }
        if(arrays.get(position).chk3) {
            if(!temp.equals(""))
                temp+=",";
            temp += "3";
        }
        if(arrays.get(position).chk4) {
            if(!temp.equals(""))
                temp+=",";
            temp += "4";
        }
        if(arrays.get(position).chk5) {
            if(!temp.equals(""))
                temp+=",";
            temp += "5";
        }
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


    /** answerRegisterRequestHandler
     * statusCode == 200
     */
    Handler answerRegisterRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    rMessageput = result.getString("message");

                } else if (statusCode == 9001) {
                    Toast.makeText(Solve_Main.this, "답안 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Solve_Main.this, "통신 장애", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    private void answerRegisterRequest(String type, String answer) {
        answer = answer.trim().replace(" ", "%20");
        answer = answer.trim().replace("\n", "%0A");

        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase answerRegisterRequest = HttpAPIs.answerRegisterPut(
                    KogPreference.getRid(Solve_Main.this),
                    KogPreference.getQuizNum(Solve_Main.this),
                    type, answer, KogPreference.getNickName(Solve_Main.this));
            HttpAPIs.background(answerRegisterRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = answerRegisterRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        answerRegisterRequestHandler.sendMessage(msg);
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

    //@통신
    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
//                TODO : implement setAllEnable()
//                setAllEnable();
            }
            else if(msg.what == -1){
//                TODO : implemnet setAllDisable()
//                setAllDisable();
            }
        }
    };

    /** questionRequestHandler
     * statusCode == 200 => get question
     * statusCode == 9001 => server error
     */
    Handler questionRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    JSONArray rMessageget;

                    rMessageget = result.getJSONArray("message");
                    JSONObject rObj;

                    rObj=rMessageget.getJSONObject(0);
                    questiontype= URLDecoder.decode(rObj.getString("type").toString(), "UTF-8");
                    question=URLDecoder.decode(rObj.getString("question").toString(), "UTF-8");
                    solution=URLDecoder.decode(rObj.getString("solution").toString(), "UTF-8");
                    title=URLDecoder.decode(rObj.getString("title").toString(), "UTF-8");
                    date="출제일 : "+URLDecoder.decode(rObj.getString("date").toString(), "UTF-8");
                    Log.e("minsu) :","contents2 : "+question+" | "+questiontype+" | "+solution+" | "+title+"|"+date);
                    settingTextView();
                    //question = question.replace("\\\n", System.getProperty("line.separator"));
                    addlist(solution,answer);


                    // real action
                    //          GoNextPage();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "문제 가져오기가 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    if (KogPreference.DEBUG_MODE) {
                        Toast.makeText(getBaseContext(), LOG_TAG + result.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
    };

    private void questionRequest() {

        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase questionRequest = HttpAPIs.questionGet(
                    KogPreference.getRid(Solve_Main.this), KogPreference.getQuizNum(Solve_Main.this),
                    KogPreference.getNickName(Solve_Main.this));
            HttpAPIs.background(questionRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = questionRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        questionRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " + e.toString());
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

