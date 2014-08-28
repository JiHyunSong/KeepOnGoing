package com.fragmenttrasitionextendedexample;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.secsm.keepongoing.Quiz.QuizSetlistData;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private RequestQueue vQueue;
    public String LOG_TAG = "newnew";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newnew);
        Spinner spinner = (Spinner) findViewById(R.id.spinner22);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner, android.R.layout.simple_spinner_item);
    //    subject = new ArrayList<String>();
        vQueue = Volley.newRequestQueue(this);

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


/*

    public void backTransition(View view) {
        Button button = (Button) findViewById(R.id.button2);
        if (getFragmentManager().getBackStackEntryCount()==0) {
            Fragment secondFragment = new SlidingListFragmentRight();
            ((SlidingListFragmentRight) secondFragment).setIndex(--index);
            ((SlidingListFragmentRight) secondFragment).setSubject("1");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, mFirstFragment, secondFragment, R.id.fragment_place);
            fragmentTransactionExtended.addTransition(optionSelected);
            fragmentTransactionExtended.commit();
        }else{
            getFragmentManager().popBackStack();
            mFirstFragment.setIndex(--index);
            mFirstFragment.setSubject("1");
        }

    }
*/





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
       // super.onBackPressed();
        finish();

    }




    //@민수 통신
    private void questionSetRequest() {
        String get_url = KogPreference.REST_URL +
                "Quizset";
//                "?srid=" + KogPreference.getRid(newnew.this) +
//                "&datestring=" + datestring +
//                "&title=" + title +
//                "&subject=" ;
        JSONObject sendBody = new JSONObject();
        try{
     //       sendBody.put("srid", KogPreference.getRid(newnew.this));
            sendBody.put("nickname", KogPreference.getNickName(quiz_set_result.this));
            sendBody.put("date", datestring);
            sendBody.put("types", subject);
            sendBody.put("title", title);

            Log.i(LOG_TAG, "sendbody in questionSetRequest : " + sendBody.toString());
        }catch (Exception e)

        {
            Log.e(LOG_TAG, "qeustion SetRequest Error : " + e.toString());
        }

        Log.i(LOG_TAG, "get_url : " + get_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, get_url, sendBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "get JSONObject");

                        try {
                            int status_code = response.getInt("status");
                           // Log.e(LOG_TAG, "minsu) : status code :" + Integer.toString(status_code));
                            if (status_code == 200) {
                                JSONArray rMessageget;
                                rMessageget = response.getJSONArray("message");
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
                Log.i("minsu:)", "Response Error");
                Toast.makeText(getBaseContext(), "통신 장애", Toast.LENGTH_SHORT).show();
                if (KogPreference.DEBUG_MODE) {
                    Toast.makeText(getBaseContext(), LOG_TAG + " - Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        vQueue.add(jsObjRequest);
        vQueue.start();

    }

}
