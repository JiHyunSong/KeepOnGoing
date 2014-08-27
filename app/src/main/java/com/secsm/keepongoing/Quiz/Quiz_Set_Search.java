package com.secsm.keepongoing.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.sample.date_pick;
import com.fragmenttrasitionextendedexample.newnew;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.BaseFragmentActivity;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;


public class Quiz_Set_Search extends BaseFragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    private String subject="";
    private String roomname="";
    private String date="";
    String[] arr = null;
    ArrayList<QuizSetlistData> list;
    CheckBox chk1,chk2,chk3,chk4,chk5;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz__set__search);
        settingListView();
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        EditText date_pick=(EditText)findViewById(R.id.date_pick);
        date_pick.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });
        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

        Button date = (Button) findViewById(R.id.date);
        date.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Quiz_Set_Search.this, date_pick.class);
                startActivity(intent);
            }
        });


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



    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }
    private boolean isVibrate() {
        return ((CheckBox) findViewById(R.id.checkBoxVibrate)).isChecked();
    }

    private boolean isCloseOnSingleTapDay() {
        return ((CheckBox) findViewById(R.id.checkBoxCloseOnSingleTapDay)).isChecked();
    }

    private boolean isCloseOnSingleTapMinute() {
        return ((CheckBox) findViewById(R.id.checkBoxCloseOnSingleTapMinute)).isChecked();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(Quiz_Set_Search.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Toast.makeText(Quiz_Set_Search.this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
    }
}
