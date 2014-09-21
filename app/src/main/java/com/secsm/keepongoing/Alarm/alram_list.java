package com.secsm.keepongoing.Alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.BaseFragmentActivity;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class alram_list extends BaseFragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,time_picker{
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

    public void setAccomplishedTime(){
        mDialog = createTimePickerDialog();
        mDialog.show();
    }

// Dialog
    private AlertDialog mDialog = null;
    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;

    DBContactHelper helper = new DBContactHelper(alram_list.this);


    private AlertDialog createTimePickerDialog() {
        final View innerView = getLayoutInflater().inflate(R.layout.alram, null);
        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mManager.setTimeZone("GMT+9:00");
        mCalendar = new GregorianCalendar();
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),
                helper.getContact(2).gethour(), helper.getContact(2).getminute());

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        mDate = (DatePicker) innerView.findViewById(R.id.date_picker);
        mDate.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), onDateChanged);
        mTime = (TimePicker) innerView.findViewById(R.id.time_picker);

        mTime.setIs24HourView(true);
        mTime.setCurrentHour(helper.getContact(2).gethour());
        mTime.setCurrentMinute(helper.getContact(2).getminute());
        mTime.setOnTimeChangedListener(onTimeChanged);
//
//        info_iconFriend = (ImageView) innerView.findViewById(R.id.info_iconFriend);
//        info_txtFriendNickname = (TextView) innerView.findViewById(R.id.info_txtFriendNickname);
//        info_txtTargetTime = (TextView) innerView.findViewById(R.id.info_txtTargetTime);
//        info_iconFriend.setBackgroundResource(R.drawable.ic_action_add_group);

        ab.setTitle("목표시간 정하기");
        ab.setView(innerView);

        ab.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setTimer();
                setDismiss(mDialog);
            }
        });
        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);

            }
        });

        return ab.create();
    }

    //일자 설정 클래스의 상태변화 리스너
    private DatePicker.OnDateChangedListener onDateChanged = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
            Log.i("HelloAlarmActivity", "onDateChanged : " + mCalendar.getTime().toString());
        }
    };

    //시각 설정 클래스의 상태변화 리스너
    private TimePicker.OnTimeChangedListener onTimeChanged = new TimePicker.OnTimeChangedListener(){
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            Log.i("hh", "" + mDate.getMonth());
            mCalendar.set(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
            Log.i("HelloAlarmActivity", "onTimeChanged : " + mCalendar.getTime().toString());
        }
    };

    private void setTimer() {
        DBContactHelper helper = new DBContactHelper(alram_list.this);
        helper.updateContact(new Contact(2, mCalendar.getTime().getHours(), mCalendar.getTime().getMinutes()));
//        finish();
    }

    private void resetTimer() {
        DBContactHelper helper = new DBContactHelper(alram_list.this);
        helper.updateContact(new Contact(2, 10, 0));
//        finish();
    }

    private void refreshActivity() {
        Intent _intent = new Intent(this, alram_list.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(_intent);
    }



    /**
     * 다이얼로그 종료
     *
     * @param dialog
     */
    private void setDismiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            refreshActivity();
        }
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
