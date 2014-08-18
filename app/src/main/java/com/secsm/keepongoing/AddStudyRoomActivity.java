package com.secsm.keepongoing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.secsm.keepongoing.R;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.secsm.keepongoing.Shared.KogPreference;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddStudyRoomActivity extends Activity {

    private static final String LOG_TAG = "MainmenuActivity";
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker add_study_room_subject_dp;

    private RadioGroup add_study_room_rg;
    private RadioButton add_study_room_life_rb;
    private RadioButton add_study_room_subject_rb;
    private BootstrapEditText add_study_room_name_et;
    private BootstrapEditText add_study_room_rules_et;
    private RelativeLayout add_study_room_life_rl;
    private BootstrapEditText add_study_room_life_holiday_et;
    private RelativeLayout add_study_room_subject_rl;
    private ToggleButton add_study_room_subject_mon_tg;
    private ToggleButton add_study_room_subject_tue_tg;
    private ToggleButton add_study_room_subject_wed_tg;
    private ToggleButton add_study_room_subject_thu_tg;
    private ToggleButton add_study_room_subject_fri_tg;
    private ToggleButton add_study_room_subject_sat_tg;
    private ToggleButton add_study_room_subject_sun_tg;
    private TimePicker add_study_room_subject_tp;
    private BootstrapEditText add_study_room_subject_duration_time_et;
    private BootstrapEditText add_study_room_subject_show_up_time_et;

    private Button add_study_room_invite_friend_btn_with_life_info;
    private Button add_study_room_invite_friend_btn_with_study_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study_room);

        /* initial layout */
        add_study_room_rg = (RadioGroup) findViewById(R.id.add_study_room_rg);
        add_study_room_life_rb = (RadioButton) findViewById(R.id.add_study_room_life_rb);
        add_study_room_subject_rb = (RadioButton) findViewById(R.id.add_study_room_subject_rb);

        add_study_room_name_et = (BootstrapEditText) findViewById(R.id.add_study_room_name_et);
        add_study_room_rules_et = (BootstrapEditText) findViewById(R.id.add_study_room_rules_et);

        add_study_room_life_rl = (RelativeLayout) findViewById(R.id.add_study_room_life_rl);
        add_study_room_life_holiday_et = (BootstrapEditText) findViewById(R.id.add_study_room_life_holiday_et);

        add_study_room_subject_rl = (RelativeLayout) findViewById(R.id.add_study_room_subject_rl);
        add_study_room_subject_mon_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_mon_tg);
        add_study_room_subject_tue_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_tue_tg);
        add_study_room_subject_wed_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_wed_tg);
        add_study_room_subject_thu_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_thu_tg);
        add_study_room_subject_fri_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_fri_tg);
        add_study_room_subject_sat_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_sat_tg);
        add_study_room_subject_sun_tg = (ToggleButton) findViewById(R.id.add_study_room_subject_sun_tg);
        add_study_room_subject_tp = (TimePicker) findViewById(R.id.add_study_room_subject_tp);
        add_study_room_subject_duration_time_et = (BootstrapEditText) findViewById(R.id.add_study_room_subject_duration_time_et);
        add_study_room_subject_show_up_time_et = (BootstrapEditText) findViewById(R.id.add_study_room_subject_show_up_time_et);

        add_study_room_invite_friend_btn_with_life_info = (Button) findViewById(R.id.add_study_room_invite_friend_btn_with_life_info);
        add_study_room_invite_friend_btn_with_study_info = (Button) findViewById(R.id.add_study_room_invite_friend_btn_with_study_info);
        /* Radio button handler */
        add_study_room_rg.setOnCheckedChangeListener(rb_OnCheckedChangeListener);

        /* Time Picker Listener */
        mCalendar = new GregorianCalendar();
        add_study_room_subject_dp = (DatePicker) findViewById(R.id.add_study_room_subject_dp);
        add_study_room_subject_dp.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), dp_onDateChangedListener);
        add_study_room_subject_tp.setOnTimeChangedListener(tp_onTimeChangedListener);

        /* add button handler for inviting friends */
        add_study_room_invite_friend_btn_with_life_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goNextFriendPage();
            }
        });
        add_study_room_invite_friend_btn_with_study_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goNextFriendPage();
            }
        });
    }


    private void goNextFriendPage() {

        if (add_study_room_life_rb.isChecked() && isLifeRoomValid()) {
            if (KogPreference.DEBUG_MODE) {
                Log.i(LOG_TAG, "Go Next Page with liferoom info");
                Log.i(LOG_TAG, "add_study_room_life_holiday_et.getText().toString() : " + add_study_room_life_holiday_et.getText().toString());
            }
            // life room checked
            Intent intent = new Intent(this, InviteFriendsActivity.class);
            intent.putExtra("type", "liferoom");
            intent.putExtra("rule", add_study_room_rules_et.getText().toString());
            intent.putExtra("roomname", add_study_room_name_et.getText().toString());
            intent.putExtra("max_holiday_count", add_study_room_life_holiday_et.getText().toString());
            startActivity(intent);
            this.finish();

        } else if (add_study_room_subject_rb.isChecked() && isSubjectRoomValid()) {
            if (KogPreference.DEBUG_MODE) {
                //+ add_study_room_subject_tp.
                Date dateTime = new Date();
                dateTime.setTime(mCalendar.getTimeInMillis());
                Log.i(LOG_TAG, "Go Next Page with subjectroom info");
                // I/MainmenuActivity﹕ add_study_room_subject_tp.toString() : 1408199360352
                Log.i(LOG_TAG, "add_study_room_subject_tp.toString() : " + mCalendar.getTimeInMillis());
                // I/MainmenuActivity﹕ add_study_room_subject_tp.toString() : Sat Aug 16 23:29:20 GMT+09:00 2014
                Log.i(LOG_TAG, "add_study_room_subject_tp.toString() : " + dateTime.toString());
                Log.i(LOG_TAG, "getMeetDays : " + getMeetDays());
            }
            Date dateTime = new Date();
            dateTime.setTime(mCalendar.getTimeInMillis());
            // subject room checked
            Intent intent = new Intent(this, InviteFriendsActivity.class);
            intent.putExtra("type", "subjectroom");
            intent.putExtra("rule", add_study_room_rules_et.getText().toString());
            intent.putExtra("roomname", add_study_room_name_et.getText().toString());
            intent.putExtra("start_time", dateTime.toString().substring(11,19));
            intent.putExtra("duration_time", add_study_room_subject_duration_time_et.getText().toString());
            intent.putExtra("showup_time", add_study_room_subject_show_up_time_et.getText().toString());
            intent.putExtra("meet_days", getMeetDays());
            startActivity(intent);
            this.finish();
        } else {
            Toast.makeText(getBaseContext(), "입력란을 알맞게 채워주세요!", Toast.LENGTH_SHORT).show();
            // error
        }
//        Intent intent = new Intent(this, InviteFriendsActivity.class);
//        startActivity(intent);
//        this.finish();
    }

    private String getMeetDays() {
        String meetDays = "";
        if (add_study_room_subject_mon_tg.isChecked()) {
            meetDays += "mon|";
        }
        if (add_study_room_subject_tue_tg.isChecked()) {
            meetDays += "tue|";
        }
        if (add_study_room_subject_wed_tg.isChecked()) {
            meetDays += "wed|";
        }
        if (add_study_room_subject_thu_tg.isChecked()) {
            meetDays += "thu|";
        }
        if (add_study_room_subject_fri_tg.isChecked()) {
            meetDays += "fri|";
        }
        if (add_study_room_subject_sat_tg.isChecked()) {
            meetDays += "sat|";
        }
        if (add_study_room_subject_sun_tg.isChecked()) {
            meetDays += "sun|";
        }

        if (meetDays.length() > 0) {
            meetDays = meetDays.substring(0, meetDays.length() - 1);
        }

        return meetDays;
    }

    private boolean isLifeRoomValid() {
        String roomNames = add_study_room_name_et.getText().toString();
        String roomRules = add_study_room_rules_et.getText().toString();
        String holidays = add_study_room_life_holiday_et.getText().toString();

        if (!TextUtils.isEmpty(roomNames) && !TextUtils.isEmpty(roomRules) && !TextUtils.isEmpty(holidays)) {
            return isStringInNumRange(holidays, 1, 9);
        }
        return false;
    }

    private boolean isSubjectRoomValid() {
        String roomNames = add_study_room_name_et.getText().toString();
        String roomRules = add_study_room_rules_et.getText().toString();
        String durationTimes = add_study_room_subject_duration_time_et.getText().toString();
        String showUpTimes = add_study_room_subject_show_up_time_et.getText().toString();
        if (!TextUtils.isEmpty(roomNames) && !TextUtils.isEmpty(roomRules) && !TextUtils.isEmpty(durationTimes) && !TextUtils.isEmpty(showUpTimes)) {
            return isStringInNumRange(durationTimes, 0, 60) && isStringInNumRange(showUpTimes, 0, 60);
        }
//            intent.putExtra("duration_time", add_study_room_subject_duration_time_et.getText().toString());
//            intent.putExtra("showup_time", add_study_room_subject_show_up_time_et.getText().toString());
//            intent.putExtra("meet_days", getMeetDays());

        return false;
    }

    private boolean isStringInNumRange(String inputText, int min, int max) {
        try {
            int inputInt = Integer.parseInt(inputText);
            if (inputInt >= min && inputInt <= max) {
                Log.i(LOG_TAG, "isStringInNumRange True");
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormat Exception!! from " + inputText + " to Int");
            return false;
        }
    }

    private void setInvisibleLayout() {
        add_study_room_life_rl.setVisibility(View.INVISIBLE);
        add_study_room_subject_rl.setVisibility(View.GONE);
    }

    private RadioGroup.OnCheckedChangeListener rb_OnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (add_study_room_rg.getCheckedRadioButtonId() == R.id.add_study_room_life_rb) {
                setInvisibleLayout();
                add_study_room_life_rl.setVisibility(View.VISIBLE);
            } else if (add_study_room_rg.getCheckedRadioButtonId() == R.id.add_study_room_subject_rb) {
                setInvisibleLayout();
                add_study_room_subject_rl.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_study_room, menu);
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


    TimePicker.OnTimeChangedListener tp_onTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(add_study_room_subject_dp.getYear(), add_study_room_subject_dp.getMonth(),
                    add_study_room_subject_dp.getDayOfMonth(), hourOfDay, minute);
        }
    };

    DatePicker.OnDateChangedListener dp_onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth, add_study_room_subject_tp.getCurrentHour(), add_study_room_subject_tp.getCurrentMinute());
            Log.i("HelloAlarmActivity", "onDateChanged : " + mCalendar.getTime().toString());
        }
    };


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();

//            Intent intent = new Intent(AddStudyRoomActivity.this, TabActivity.class);
////            intent.putExtra("phoneNo", phoneNo);
//            //intent.putExtra("roomName", roomNameArray.get(position));
//            //intent.putExtra("roomID", roomIDArrayFromSQLite.get(position));
//            //startActivityForResult(intent, CHATROOM_REQUEST_CODE);
//            startActivity(intent);
        }
        return false;
    }

}
