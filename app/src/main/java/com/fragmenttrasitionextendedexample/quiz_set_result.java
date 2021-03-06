package com.fragmenttrasitionextendedexample;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.secsm.keepongoing.Quiz.QuizSetlistData;
import com.secsm.keepongoing.R;
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


public class quiz_set_result extends Activity implements AdapterView.OnItemSelectedListener{
    private int optionSelected = 16;
    //private int optionSelected = 0;
    private SlidingListFragmentLeft mFirstFragment,newFragment;
   // private int index=0;
   ArrayList<QuizSetlistData> list;
    private String subject;
    private String datestring;
    private String title;
    public String LOG_TAG = "newnew";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newnew);
        Spinner spinner = (Spinner) findViewById(R.id.spinner22);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner, android.R.layout.simple_spinner_item);
    //    subject = new ArrayList<String>();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

//        subject=getIntent().getExtras().getStringArrayList("subject");
        subject=getIntent().getExtras().getString("subject");
        datestring=getIntent().getExtras().getString("date");
        title=getIntent().getExtras().getString("title");
//@민수 수신

    Log.e("minsu:)","this is data : "+subject+" / "+datestring+" / "+title);


        questionSetRequest();
        //Add first fragment
    /*    mFirstFragment = new SlidingListFragmentLeft();
        FragmentManager fm = getFragmentManager();

        questionSetRequest();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_place, mFirstFragment);
        fragmentTransaction.commit();*/
    }

    public void addTransition(View view,String title,String subject,String question,String solution,String date) {
        if (getFragmentManager().getBackStackEntryCount()==0) {
            Fragment secondFragment = new SlidingListFragmentRight();

            ((SlidingListFragmentRight) secondFragment).setTitle(title);
           ((SlidingListFragmentRight) secondFragment).setSubject(subject);
            ((SlidingListFragmentRight) secondFragment).setQuestion(question);
            ((SlidingListFragmentRight) secondFragment).setSolution(solution);
            ((SlidingListFragmentRight) secondFragment).setDate(date);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, mFirstFragment, secondFragment, R.id.fragment_place);
            fragmentTransactionExtended.addTransition(optionSelected);
            fragmentTransactionExtended.commit();
        }else{
            getFragmentManager().popBackStack();
     //       mFirstFragment.setIndex(++index);
    //        mFirstFragment.setSubject("1");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       // optionSelected = i;
         optionSelected = 16;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onBackPressed()
    {
      //  Button button = (Button) findViewById(R.id.button);
     //   button.setText("Push");
        super.onBackPressed();
       // finish();

    }


    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
//                TODO : implement setAllEnable();
//                setAllEnable();
            }
            else if(msg.what == -1){
//                TODO : implement setAllDisable();
//                setAllDisable();
            }
        }
    };

    /** getMyInfoRequest
     * statusCode == 200 => get My info, Update UI
     */
    Handler questionSetRequestHandler = new Handler(){

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
                    Log.i(LOG_TAG, "rMessage"+rMessageget);

                    //list에 넣기
                    list = new ArrayList<QuizSetlistData>();
                    for(int i=0;i<rMessageget.length();i++){
                        rObj=rMessageget.getJSONObject(i);
                        list.add(
                                new QuizSetlistData(
                                        URLDecoder.decode(rObj.getString("title").toString(), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("date").toString(), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("type").toString(), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("solution").toString(), "UTF-8"),
                                        URLDecoder.decode(rObj.getString("question").toString(), "UTF-8")
                                )
                        );
                    }
                    mFirstFragment = new SlidingListFragmentLeft();
                    FragmentManager fm = getFragmentManager();
                    mFirstFragment.setList(list);
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.add(R.id.fragment_place, mFirstFragment);
                    fragmentTransaction.commit();
                    //
                    // real action
                    //          GoNextPage();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "문제 가져오기가 불가능합니다.", Toast.LENGTH_SHORT).show();
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

    //@민수 통신
    private void questionSetRequest() {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase requestAuthRegister = HttpAPIs.questionSetPost(
                    KogPreference.getNickName(quiz_set_result.this),
                    datestring, subject, title);
            HttpAPIs.background(requestAuthRegister, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if (result != null) {
                        Message msg = questionSetRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        questionSetRequestHandler.sendMessage(msg);
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
