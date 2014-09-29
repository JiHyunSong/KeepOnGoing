package com.secsm.keepongoing.Quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
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
import java.util.Date;

public class Quiz_Main extends BaseActivity {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    private CustomScrollView mScrollView;
    listAdapter mAdapter = null;
    private Spinner spinner1, spinner2;
    private BootstrapButton btnSubmit;
    private static String LOG_TAG = "Quiz_submit";
    private String rMessage;
    private EditText input_problem,question_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz__main);
        list = new ArrayList<Quiz_data>();
        //settingListView();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        mScrollView=(CustomScrollView) findViewById(R.id.quizmain_scroll);
        listView = (ListView) findViewById(R.id.listView_Quiz);

    /*    listView.setOnTouvchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("minsu) ","touch zz");
                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });*/

        mAdapter = new listAdapter(this, list);
        listView.setAdapter(mAdapter);
        //listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        //listView.setAdapter(mAdapter);





       input_problem = (EditText) findViewById(R.id.txtOne);

        BootstrapButton multiplechoice = (BootstrapButton) findViewById(R.id.multiplechoice);
        multiplechoice.setOnClickListener(new BootstrapButton.OnClickListener() {

            public void onClick(View v) {
                Log.i(LOG_TAG, "list.size : " + list.size());
                list.add(new Quiz_data("multi"));
                settingListView();
            }
        });
        BootstrapButton essay = (BootstrapButton) findViewById(R.id.essaybutton);
        essay.setOnClickListener(new BootstrapButton.OnClickListener() {
            public void onClick(View v) {
                list.add(new Quiz_data("essay"));
                settingListView();
            }
        });
        BootstrapButton delete = (BootstrapButton) findViewById(R.id.delete);
        delete.setOnClickListener(new BootstrapButton.OnClickListener() {
            public void onClick(View v) {
                if(list.size()-1>=0) {
                    list.remove(list.size() - 1);
                    settingListView();
                }

            }
        });


        BootstrapButton tf = (BootstrapButton) findViewById(R.id.tf);
        tf.setOnClickListener(new BootstrapButton.OnClickListener() {
            public void onClick(View v) {
                mAdapter.addQuizData(new Quiz_data("tf"));
            }
        });


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

    String[] arr = null;
    ArrayList<Quiz_data> list;

    private void settingListView() {

        //  list  = new ArrayList<Quiz_data>();
        // list.add(new Quiz_data(""+list.size()));
//        listView = (ListView) findViewById(R.id.listView_Quiz) ;
//        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//        listView.setAdapter(new listAdapter(this, list) ) ;
        mAdapter.refresh();
    }

    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }


    // add items into spinner dynamically
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        // get the selected dropdown list value
    }

    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        btnSubmit = (BootstrapButton) findViewById(R.id.btnSubmit);
        question_title = (EditText) findViewById(R.id.question_title);


        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearFocus();
                Date today = new Date();


                if(request_check(list).equals("error"))
                    Toast.makeText(Quiz_Main.this,"모든 답안을 입력하세요",Toast.LENGTH_SHORT).show();
                else if(request_check(list).equals(""))
                    Toast.makeText(Quiz_Main.this,"답안을 추가하세요",Toast.LENGTH_SHORT).show();
                else if(input_problem.getText().toString().equals(""))
                    Toast.makeText(Quiz_Main.this,"문제를 입력하세요",Toast.LENGTH_SHORT).show();
                else {
                //    MultipartEntity(input_problem.getText().toString(), String.valueOf(spinner1.getSelectedItem()), request_check(list));
                 quizRegisterRequest(input_problem.getText().toString(), String.valueOf(spinner1.getSelectedItem()), request_check(list),question_title.getText().toString(),
                         (today.getYear() + 1900) + "/" + (today.getMonth() + 1) + "/" + today.getDate());
                }
            }

        });
    }

    private void GoNextPage() {
        // TODO Real Action
    }

    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
//                TODO : implement setAllEnable()
//                setAllEnable();
            }
            else if(msg.what == -1){
//                TODO : implement setAllDisable()
//                setAllDisable();
            }
        }
    };

    /** quizRegisterRequestHandler
     * statusCode == 200 => get My info, Update UI
     */
    Handler quizRegisterRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    // rMessage = response.getString("message");
                    JSONArray rMessageget;

                    rMessageget = result.getJSONArray("message");
                    JSONObject rObj;
                    rObj=rMessageget.getJSONObject(0);
                    //     Toast.makeText(getBaseContext(), LOG_TAG + URLDecoder.decode(rObj.getString("num").toString(), "UTF-8"), Toast.LENGTH_SHORT).show();
                    //       Log.e("minsu ):","minsu:) receive : "+ URLDecoder.decode(rObj.getString("num").toString(), "UTF-8"));
                    //   Log.e("minsu ):","minsue:) send solution : "+ temp);
                    KogPreference.setQuizNum(Quiz_Main.this,  URLDecoder.decode(rObj.getString("num").toString(), "UTF-8"));
                    Toast.makeText(getBaseContext(), "제출완료", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "퀴즈 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
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

    //@통신
    private void quizRegisterRequest(String question, String type, String solution,String title,String date) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.quizRegisterPost(
            KogPreference.getRid(Quiz_Main.this),
            type, question, solution,
            KogPreference.getNickName(Quiz_Main.this),
            title, date);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = quizRegisterRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        quizRegisterRequestHandler.sendMessage(msg);
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
                    return "error";
                }else {
                    result += position+"$"+arrays.get(position).name+"$"+mulitiplecheck(arrays, position)+"|";
                }
            }
            else if(arrays.get(position).name.equals("essay")){
                if(arrays.get(position).essay.equals("")) {
                    return "error";
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
                    return "error";
            }
        }
        Toast.makeText(Quiz_Main.this,result,2);
        return result;
    }
}
