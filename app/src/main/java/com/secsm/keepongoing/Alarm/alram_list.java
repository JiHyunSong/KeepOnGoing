package com.secsm.keepongoing.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.secsm.keepongoing.R;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class alram_list extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,time_picker{
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_list);
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
        findViewById(R.id.dateButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        findViewById(R.id.timeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setVibrate(isVibrate());
                timePickerDialog.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
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


        settingListView();
    }

    public void date_pick(){
        datePickerDialog.setVibrate(isVibrate());
        datePickerDialog.setYearRange(1985, 2028);
        datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
    }
    public void time_pick(){
        timePickerDialog.setVibrate(isVibrate());
        timePickerDialog.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
    }


    private boolean isVibrate() {
        return true;
    }

    private boolean isCloseOnSingleTapDay() {
        return ((CheckBox) findViewById(R.id.checkBoxCloseOnSingleTapDay)).isChecked();
    }

    private boolean isCloseOnSingleTapMinute() {
        return ((CheckBox) findViewById(R.id.checkBoxCloseOnSingleTapMinute)).isChecked();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(alram_list.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Date mTime=new Date();
        Date today=new Date();
        AlarmManager mManager;

        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mManager.setTimeZone("GMT+9:00");
   //     mCalendar.set(mTime.getYear(),mTime.getMonth(),mTime.getDay(),hourOfDay,minute);
        mTime.setHours(hourOfDay);
        mTime.setMinutes(minute);
        DBContactHelper helper = new DBContactHelper(alram_list.this);
          helper.updateContact(new Contact(1, hourOfDay,minute));
        Log.e("minsu:)", "Alram time : " + mTime.toString());
        if(today.getTime()<=mTime.getTime())
            mManager.set(AlarmManager.RTC_WAKEUP, mTime.getTime(), pendingIntent());
        else
            mManager.set(AlarmManager.RTC_WAKEUP, mTime.getTime()+24*60*60*1000, pendingIntent());

        settingListView();
    }
    private PendingIntent pendingIntent() {
        Intent i = new Intent(this, alert.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        return pi;
    }


    @Override
    protected void onResume() {

        super.onResume();
        settingListView();
    }

    String[] arr = null;
    ArrayList<AlramData> list;

    private void settingListView() {
        DBContactHelper helper = new DBContactHelper(this);
        Contact contact;
        String output = "기상시간 : ";
        contact = helper.getContact(1);
        list = new ArrayList<AlramData>();
        if (contact.gethour() < 10)
            output += "0" + contact.gethour();
        else
            output += contact.gethour();
        if (contact.getminute() < 10)
            output += ": 0" + contact.getminute();
        else
            output += ": " + contact.getminute();
        list.add(new AlramData(output));


        contact = helper.getContact(2);
        output = "목표시간 : ";
        if (contact.gethour() < 10)
            output += "0" + contact.gethour();
        else
            output += contact.gethour();
        if (contact.getminute() < 10)
            output += ": 0" + contact.getminute();
        else
            output += ": " + contact.getminute();
        list.add(new AlramData(output));

        listView = (ListView) findViewById(R.id.listView_test);
        listView.setAdapter(new AlramAdapter(this, list,this));
    }



    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }
}
