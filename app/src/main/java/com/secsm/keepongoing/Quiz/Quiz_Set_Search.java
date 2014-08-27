package com.secsm.keepongoing.Quiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.fragmenttrasitionextendedexample.newnew;
import com.secsm.keepongoing.R;

import java.util.ArrayList;


public class Quiz_Set_Search extends Activity implements View.OnClickListener {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    private String subject="";
    private String roomname="";
    String[] arr = null;
    ArrayList<QuizSetlistData> list;
    CheckBox chk1,chk2,chk3,chk4,chk5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz__set__search);
        settingListView();


        Button gotonewnew = (Button) findViewById(R.id.Search);
        gotonewnew.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                chk1 = (CheckBox)findViewById(R.id.check1);
                chk2 = (CheckBox)findViewById(R.id.check2);
                chk3 = (CheckBox)findViewById(R.id.check3);
                chk4 = (CheckBox)findViewById(R.id.check4);
                chk5 = (CheckBox)findViewById(R.id.check5);


                if(chk1.isChecked()==true)
                    subject+="1";
                if(chk2.isChecked()==true) {
                    if(!subject.equals(""))
                        subject+="|";
                    subject += "2";
                }
                if(chk3.isChecked()==true) {
                    if(!subject.equals(""))
                        subject+="|";
                    subject += "3";
                }
                if(chk4.isChecked()==true) {
                    if(!subject.equals(""))
                        subject+="|";
                    subject += "4";
                }
                if(chk5.isChecked()==true) {
                    if(!subject.equals(""))
                        subject+="|";
                    subject += "5";
                }


                for(int i=0;i<list.size();i++) {
                    if(list.get(i).chk) {
                        if(!roomname.equals(""))
                            roomname+="|";
                        roomname += list.get(i).name;
                    }
                }
                Log.e("minsu:)","this subject is "+subject);
                Log.e("minsu:)","this roomname is "+roomname);
                Intent intent = new Intent(Quiz_Set_Search.this, newnew.class);
                intent.putExtra("position", subject);
                intent.putExtra("position", roomname);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {

         chk1 = (CheckBox)findViewById(R.id.check1);
        chk1.setChecked(false);
         chk2 = (CheckBox)findViewById(R.id.check2);
        chk2.setChecked(false);
         chk3 = (CheckBox)findViewById(R.id.check3);
        chk3.setChecked(false);
         chk4 = (CheckBox)findViewById(R.id.check4);
        chk4.setChecked(false);
         chk5 = (CheckBox)findViewById(R.id.check5);
        chk5.setChecked(false);
        subject="";
        roomname="";
        super.onResume();
        settingListView();
    }


    private void settingListView() {

        list = new ArrayList<QuizSetlistData>();
       String output = "장성택";
       list.add(new QuizSetlistData(output));
        output = "괴뢰군";
        list.add(new QuizSetlistData(output));
        listView = (ListView) findViewById(R.id.listView_test);
        listView.setAdapter(new QuizSetAdapter(this, list));
    }

    @Override
    public void onClick(View v) {
    }

    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }
}
