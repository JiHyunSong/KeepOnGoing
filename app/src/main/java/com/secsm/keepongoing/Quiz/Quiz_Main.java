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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.Encrypt;
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONObject;

import java.util.ArrayList;

public class Quiz_Main extends Activity {
    private ArrayAdapter<String> _arrAdapter ;
    private ListView listView;
    listAdapter mAdapter = null;
    private Spinner spinner1, spinner2;
    private Button btnSubmit;
    private static String LOG_TAG = "Profile";
    private String rMessage;
    private RequestQueue vQueue;
    private int status_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz__main);
        list  = new ArrayList<Quiz_data>();
        //settingListView();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        vQueue = Volley.newRequestQueue(this);

        mAdapter = new listAdapter(this, list);
        listView = (ListView) findViewById(R.id.listView_Quiz) ;
        //listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setAdapter(mAdapter ) ;


        BootstrapButton multiplechoice = (BootstrapButton) findViewById(R.id.multiplechoice);
        multiplechoice.setOnClickListener(new BootstrapButton.OnClickListener() {

            public void onClick(View v) {
                Log.i(LOG_TAG, "list.size : " + list.size());
                list.add(new Quiz_data(""));
                settingListView();
            }
        });
        BootstrapButton essay = (BootstrapButton) findViewById(R.id.essay);
        essay.setOnClickListener(new BootstrapButton.OnClickListener() {
            public void onClick(View v) {
                list.add(new Quiz_data("essay"));
                settingListView();
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

    private void refresh( String $inputValue ) {
        _arrAdapter.add( $inputValue ) ;
        _arrAdapter.notifyDataSetChanged() ;
    }




    // add items into spinner dynamically
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        // get the selected dropdown list value
    }
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Quiz_Main.this,
                        "OnClickListener : " +"\nSpinner 1 : " + String.valueOf(spinner1.getSelectedItem()),Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void GoNextPage()
    {
        // TODO Real Action
    }
    //@통신
    private void quizRegisterRequest(String nickName, String password, String image, String phone) {
        String get_url = KogPreference.REST_URL +
                "Register" +
                "?nickname=" + nickName +
                "&password=" + Encrypt.encodingMsg(password) +
                "&image=" + image +
                "&phone=" + phone +
                "&gcmid=" + KogPreference.getRegId(Quiz_Main.this);

        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");
                        Log.i(LOG_TAG, response.toString());

                        try{
                            status_code = response.getInt("status");
                            if(status_code == 200){
                                rMessage = response.getString("message");
                                // real action
                                GoNextPage();
                            } else if(status_code == 9001){
                                Toast.makeText(getBaseContext(), "회원가입이 불가능합니다.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                                if(KogPreference.DEBUG_MODE) {
                                    Toast.makeText(getBaseContext(), LOG_TAG + response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i(LOG_TAG, "Response Error");
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if(KogPreference.DEBUG_MODE)
                {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
        vQueue.add(jsObjRequest);
    }


}
