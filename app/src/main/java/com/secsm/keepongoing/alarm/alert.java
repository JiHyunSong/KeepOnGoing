package com.secsm.keepongoing.Alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.secsm.keepongoing.R;

import java.util.Date;

public class alert extends Activity {
    private MediaPlayer mMediaPlayer;   // MediaPlayer 변수 선언
    private Vibrator mVibrator;
    private static final long[] sVibratePattern = new long[]{500, 500};   // 진동 패턴 정의(0.5초 진동, 0.5초 쉼)

    private Ringtone r;
    private AlarmManager mManager;

    @Override

    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        vibrate_function();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        //ringtone();
        Button btnsnooze = (Button) findViewById(R.id.snooze);
        btnsnooze.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Date mCalendar = new Date();
                mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTime() + 10 * 60 * 1000, pendingIntent());
                Log.e("minsu) : AlertActivity : ", "" + mCalendar.toString());
                Toast.makeText(alert.this, "10분뒤에 울립니다.", 2).show();
                r.stop();
                mVibrator.cancel();   // 진동 중지
                finish();
            }
        });

        Button btndismiss = (Button) findViewById(R.id.dismiss);
        btndismiss.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Date mCalendar = new Date();
                mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTime() + 24 * 60 * 60 * 1000, pendingIntent());
                Log.e("minsu) : AlertActivity : ", "" + mCalendar.toString());
                Toast.makeText(alert.this, "기상 완료", 2).show();
                r.stop();
                mVibrator.cancel();   // 진동 중지
                finish();
            }
        });
    }

    private PendingIntent pendingIntent() {
        Intent i = new Intent(alert.this, alert.class);
        PendingIntent pi = PendingIntent.getActivity(alert.this, 0, i, 0);
        return pi;
    }

    @Override
    protected void onPause() {
        super.onPause();
        r.stop();
        mVibrator.cancel();   // 진동 중지
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }//다른 입력 무시


    private void vibrate_function() {
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(sVibratePattern, 0);   // 진동 시작 (패턴으로 진동, '0':무한 반복, -1:반복 없음)

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alert, menu);
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

}
