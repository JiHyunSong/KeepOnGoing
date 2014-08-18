package com.secsm.keepongoing.Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONObject;

import java.util.ArrayList;

public class Solve_Main extends Activity {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    public String LOG_TAG = "Solve MAIN";
    listAdapter_Solve mAdapter = null;
    private Button btnSubmit;
    private int status_code;
    private JSONObject rMessageget;
    private String rMessageput;
    private RequestQueue vQueue;
    private String question;
    private String questiontype;
    private String num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_solve__main);
        list = new ArrayList<Quiz_data>();

         questionRequest(num);

        settingTextView();



        list.add(new Quiz_data("multi"));
        list.add(new Quiz_data("essay"));
        list.add(new Quiz_data("tf"));

        mAdapter = new listAdapter_Solve(this, list);
        listView = (ListView) findViewById(R.id.listView_Quiz_solve);
        //listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setAdapter(mAdapter);
        addListenerOnButton();

    }

    public void settingTextView() {

        TextView subject =  (TextView) findViewById(R.id. solve_subject_solve);
        TextView textview = (TextView) findViewById(R.id.textview_solve);
        subject.setText(questiontype);
        subject.setText(question);


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

    String[] arr = null;
    ArrayList<Quiz_data> list;

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


                Toast.makeText(Solve_Main.this,"@SERVER : \n"
                                +
                                "Answer : "+ request_check(list)
                        , Toast.LENGTH_SHORT
                ).show();
                answerRegisterRequest(request_check(list).toString());
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
        return temp;
    }

    public String request_check(ArrayList<Quiz_data> arrays){
        int position;
        String result="";
        for(position=0;position<arrays.size();position++) {
            Log.e("minsu) : ", "list content name: " + arrays.get(position).name);
            if(arrays.get(position).name.equals("multi")){
                if(mulitiplecheck(arrays,position).equals("error"))
                    result+=position+ arrays.get(position).name+"|";
                else {
                    result += position+arrays.get(position).name+mulitiplecheck(arrays, position)+"|";
                }
            }
            else if(arrays.get(position).name.equals("essay")){
                if(arrays.get(position).essay.equals("")) {
                    result+=position+ arrays.get(position).name+"|";
                }
                else {
                    result+=position+ arrays.get(position).name+arrays.get(position).essay+"|";
                    Log.e("minsu) : ", "list content string: " + result);
                }}
            else if(arrays.get(position).name.equals("tf")){
                if(arrays.get(position).truebtn==true) {
                    result+=position+ arrays.get(position).name+true+"|";
                }
                else if(arrays.get(position).falsebtn==true) {
                    result+=position+arrays.get(position).name+false+"|";
                }
                else
                    result+=position+ arrays.get(position).name+"|";
            }
        }
        return result;
    }


    //@통신
    private void answerRegisterRequest(String answer) {
        String get_url = KogPreference.REST_URL +
                "Room/Quiz" +
                "?srid=" + KogPreference.getRid(Solve_Main.this) +
                "&num="+"7"+
                "&type="+"type"+//type 받아와야됨
                 "&answer=" + answer+
                "&nickname="+"jenny";// + KogPreference.getNickName(Solve_Main.this);


        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try {
                            status_code = response.getInt("status");
                            if (status_code == 200) {
                                rMessageput = response.getString("message");
                                // real action
                      //          GoNextPage();

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


    private void questionRequest(String num) {
        String get_url = KogPreference.REST_URL +
                "Room/Quiz" +
                "?srid=" + "35"+ //KogPreference.getRid(Solve_Main.this) +
                "&num="+"2"+//num 받아와야됨
                "&nickname=" + "qwer";//KogPreference.getNickName(Solve_Main.this);

        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, "hi : "+response.toString());

                        try {
                            status_code = response.getInt("status");
                            Log.e(LOG_TAG, "minsu) : status code :" + Integer.toString(status_code));
                            if (status_code == 200) {
                               rMessageget = response.getJSONObject("message");
                                Toast.makeText(Solve_Main.this, "rMessageget :. "+rMessageget, Toast.LENGTH_SHORT).show();
                                Log.i("minsu) : ","anything2");
                                Log.e(LOG_TAG, "minsu) : message :" + rMessageget);

                                Log.e("minsu) : ","minsu) get: "+
                                        rMessageget.getString("type")+"|"+
                                        rMessageget.getString("question")+"|"+
                                        rMessageget.getString("solution")+"|"+
                                        rMessageget.getString("answer"));
                                questiontype=rMessageget.getString("type");
                                question=rMessageget.getString("solution");

                                // real action
                                //          GoNextPage();
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


}

