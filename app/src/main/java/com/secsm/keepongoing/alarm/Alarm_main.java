package com.secsm.keepongoing.Alarm;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.secsm.keepongoing.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Alarm_main extends Activity implements DatePicker.OnDateChangedListener, OnTimeChangedListener {

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;
    DBContactHelper helper = new DBContactHelper(Alarm_main.this);

    /*
     * 통지 관련 맴버 변수
     */
    private NotificationManager mNotification;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //통지 매니저를 취득
        mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //알람 매니저를 취득
        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        if(getIntent().getExtras().getInt("position")==1) {
            mCalendar = new GregorianCalendar();
            mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),
               helper.getContact(2).gethour(),helper.getContact(2).getminute());
        }
        else mCalendar = new GregorianCalendar();
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.alram);
        if(getIntent().getExtras().getInt("position")==0) {
            Button b = (Button) findViewById(R.id.set);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setAlarm();
                }
            });

            b = (Button) findViewById(R.id.reset);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    resetAlarm();
                }
            });
        }
        else {
            Button b = (Button) findViewById(R.id.set);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setTimer();
                }
            });
            b = (Button) findViewById(R.id.reset);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    resetTimer();
                }
            });
        }




        //일시 설정 클래스로 현재 시각을 설정
        mDate = (DatePicker) findViewById(R.id.date_picker);
        mDate.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker) findViewById(R.id.time_picker);
        if(getIntent().getExtras().getInt("position")==1) {
            mTime.setIs24HourView(true);
            //Toast.makeText(this, "기상시간이다", Toast.LENGTH_SHORT).show();
            mTime.setCurrentHour(helper.getContact(2).gethour());
            mTime.setCurrentMinute(helper.getContact(2).getminute());
            mTime.setOnTimeChangedListener(this);
        }
        else {
            mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
            mTime.setOnTimeChangedListener(this);
        }
    }


    private void setTimer() {
        DBContactHelper helper = new DBContactHelper(Alarm_main.this);
        helper.updateContact(new Contact(2, mCalendar.getTime().getHours(), mCalendar.getTime().getMinutes()));
        finish();
    }
    private void resetTimer() {
        DBContactHelper helper = new DBContactHelper(Alarm_main.this);
        helper.updateContact(new Contact(2, 10, 0));
        finish();
    }

    //알람의 설정
    private void setAlarm() {
        DBContactHelper helper = new DBContactHelper(Alarm_main.this);
        helper.updateContact(new Contact(1, mCalendar.getTime().getHours(), mCalendar.getTime().getMinutes()));
 /*       Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
 }*/


     //     mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());



     //   Intent intent = new Intent(getApplicationContext(), alert.class);
    //   PendingIntent sender = PendingIntent.getBroadcast(Alarm_main.this, 0, intent, 0);
//@ 이 아래 부터는 반복을 위한 코드
//알람을 시작시킬 시간, 밀리세켠드 단위로 5000 이면 5초입니다.
//즉 5초후에 알람이 최초 발생이 시작됩니다.
        long startTime =  mCalendar.getTimeInMillis();

//알람이 주기적으로 발생할 때, 주기시간입니다. 여기서는 5초단위로 계속해서
//알람이 발생됩니다.
        //long cycleTime = 1000;
        long cycleTime = 24*60*60*1000;


//알람객체의 setRepeating 메소드가 알람을 주기적으로 반복해서 발생시키게 됩니다.
//여기서는 5초후에 알람이 최초 발생을 하고, 5초단위로 계속해서 알람이 발생하게 됩니다.
        mManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, cycleTime, pendingIntent());
//@시간 구해서 set하기
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
        Toast.makeText(this, "set" , 2).show();
    }

    //알람의 해제
    private void resetAlarm() {

        mManager.cancel(pendingIntent());
        Toast.makeText(this, "reset", 2).show();

    }
    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent i = new Intent(getApplicationContext(), alert.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        return pi;
    }
    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        Log.i("HelloAlarmActivity", "onDateChanged : " + mCalendar.getTime().toString());
    }
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        Log.i("hh", ""+ mDate.getMonth());
        mCalendar.set(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
        Log.i("HelloAlarmActivity" , "onTimeChanged : " + mCalendar.getTime().toString());
    }
}