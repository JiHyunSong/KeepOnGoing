package com.fragmenttrasitionextendedexample;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Quiz.Quiz_data;
import com.secsm.keepongoing.Quiz.listAdapter_Solve;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class SlidingListFragmentRight extends Fragment implements MyInterface {
    private int index =0;
    listAdapter_Solve mAdapter = null;
    private String question;
    public String LOG_TAG = "SlidingListFragmentRight";
    private String titlez;
    private String subject;
    private String solution;
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    private View view;
    ArrayList<Quiz_data> list;
    private RequestQueue vQueue;
    private Button btnSubmit;
    private String answer;
    private String rMessageput;
    private String date;
    TextView title_view,question_view,subject_view,date_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // view = inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
      //  listView = (ListView) view.findViewById(R.id.listView_test2);
    //    listView.setAdapter(new Quiz_Result_Adapter(view.getContext(), list,this));
//        list = new ArrayList<Quiz_data>();
//        listView = (ListView) view.findViewById(R.id.listView_Quiz_solve);
//        mAdapter = new listAdapter_Solve(view.getContext(), list);
//        listView.setAdapter(mAdapter);
//        return view;
                return inflater.inflate(R.layout.sliding_fragment_layout_right, container, false);

    }
    public void foo(int position){
        getFragmentManager().popBackStack();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        this.view=getView();


        vQueue = MyVolley.getRequestQueue(view.getContext());
        title_view = (TextView)getView().findViewById(R.id.question_title_content);
        title_view.setText(titlez);
        subject_view = (TextView)getView().findViewById(R.id.solve_subject_solve);
        subject_view.setText(subject);
        question_view = (TextView)getView().findViewById(R.id.textview_solve);
        question_view.setText(question);
        date_view = (TextView)getView().findViewById(R.id.date_view);
        date_view.setText("출제일 : "+date);

        Button back = (Button) getView().findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                foo(0);
            }
        });
       // setListAdapter(new MyListAdapter());



        //삽입
        list = new ArrayList<Quiz_data>();
          mAdapter = new listAdapter_Solve(view.getContext(), list);
        listView = (ListView) view.findViewById(R.id.listView_Quiz_solve);
        listView.setAdapter(mAdapter);

        addListenerOnButton();
        addlist(solution,answer);

    }

    public void setTitle(String title){
        this.titlez = title;
    }
    public void setQuestion(String question){
        this.question = question;
    }
    public void setSolution(String solution){
        this.solution = solution;
    }
    public void setDate(String date){
        this.date = date;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }


    public void loadDatabase(){
        // 인덱스를 가지고 로드 데이터
    }


//@삽입




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



    private void settingListView() {
        mAdapter.refresh();
    }

    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }


    public void addListenerOnButton() {
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit_solve);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearFocus();
                //@민수 제출
                int total=checkanswer(solution,request_check(list).toString());
                TextView solve_main_tv=(TextView)view.findViewById(R.id.solve_main_tv);
                solve_main_tv.setText("점수 : +"+ total);

                answerRegisterRequest(subject,request_check(list).toString());
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
                    Toast.makeText(view.getContext(), "답안 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "통신 장애", Toast.LENGTH_SHORT).show();
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
                    KogPreference.getRid(view.getContext()),
                    KogPreference.getQuizNum(view.getContext()),
                    type, answer, KogPreference.getNickName(view.getContext()));
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
//
//    private void answerRegisterRequest(String type, String answer) {
//        //@통신
//
//        answer = answer.trim().replace(" ", "%20");
//        answer = answer.trim().replace("\n", "%0A");
//        String get_url = KogPreference.REST_URL +
//                "Room/Quiz" +
//                "?srid=" + KogPreference.getRid(view.getContext()) +
//                "&num="+ KogPreference.getQuizNum(view.getContext()) +
//                "&type="+type+//type 받아와야됨
//                "&answer=" + answer+
//                "&nickname="+ KogPreference.getNickName(view.getContext());
//
//
//        Log.i(LOG_TAG, "get_url : " + get_url);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, Encrypt.encodeIfNeed(get_url), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "get JSONObject");
//                        Log.i(LOG_TAG, response.toString());
//
//                        try {
//                           int status_code = response.getInt("status");
//                            if (status_code == 200) {
//                                rMessageput = response.getString("message");
//
//                            } else if (status_code == 9001) {
//                                //Toast.makeText(getBaseContext(), "답안 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
//                            } else {
//                               // Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
//                                if (KogPreference.DEBUG_MODE) {
//                              //      Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(LOG_TAG, "Response Error");
//                //Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
//                if (KogPreference.DEBUG_MODE) {
//                    //Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//        );
//        vQueue.add(jsObjRequest);
//    }
}
