package com.secsm.keepongoing.Life_Study;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.secsm.keepongoing.R;


    public class achieve_time_show extends ActionBarActivity {


        boolean isPageOpen = false;



        Animation translateLeftAnim;
        Animation translateRightAnim;


        LinearLayout slidingPage01;


        Button button1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_achieve_time_show);






            button1 = (Button) findViewById(R.id.button1);

            // 슬라이딩으로 보여질 레이아웃 객체 참조
            slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);

            // 애니메이션 객체 로딩
            translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
            translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

            // 애니메이션 객체에 리스너 설정
            SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
            translateLeftAnim.setAnimationListener(animListener);
            translateRightAnim.setAnimationListener(animListener);

        }

        public void onButton1Clicked(View v) {
            // 애니메이션 적용
            if (isPageOpen) {

                slidingPage01.startAnimation(translateLeftAnim);
                Log.e("minsu)","minsu): left");
                slidingPage01.setVisibility(View.VISIBLE);

            } else {
                slidingPage01.startAnimation(translateRightAnim);
                Log.e("minsu)","minsu):right");
            }
        }

        /**
         * 애니메이션 리스너 정의
         */
        private class SlidingPageAnimationListener implements AnimationListener {
            /**
             * 애니메이션이 끝날 때 호출되는 메소드
             */
            public void onAnimationEnd(Animation animation) {
                if (isPageOpen) {

                    slidingPage01.setVisibility(View.INVISIBLE);
                    button1.setText("Open");
                    isPageOpen = false;
                } else {
                    slidingPage01.setVisibility(View.VISIBLE);
                    button1.setText("Close");
                    isPageOpen = true;
                }
            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationStart(Animation animation) {

            }

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
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