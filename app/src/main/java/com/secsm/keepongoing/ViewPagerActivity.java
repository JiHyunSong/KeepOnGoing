package com.secsm.keepongoing;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.secsm.keepongoing.Alarm.Preference;
import com.secsm.keepongoing.Alarm.alram_list;


public class ViewPagerActivity extends Activity implements View.OnClickListener {

    private ViewPager mPager;
    private Button mOne;
    private Button   mTwo;
    private Button   mThree;
    private TextView tiger;
    private static String LOG_TAG = "ViewPagerActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        initLayout();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one:
                setCurrentItem(0);
                break;
            case R.id.two:
                setCurrentItem(1);
                break;
            case R.id.three:
                setCurrentItem(2);
                break;
        }
    }

    private void setCurrentItem(int index){
        if(index == 0){
            mPager.setCurrentItem(0);


        }else if(index == 1){
            mPager.setCurrentItem(1);
        }else{
            mPager.setCurrentItem(2);
        }
    }

    private void initLayout(){
        mOne = (Button) findViewById(R.id.one);
        mTwo = (Button) findViewById(R.id.two);
        mThree = (Button) findViewById(R.id.three);
        mOne.setOnClickListener(this);
        mTwo.setOnClickListener(this);
        mThree.setOnClickListener(this);
        mPager = (ViewPager)findViewById(R.id.viewpager);
        mPager.setAdapter(new ViewPagerAdapter(this));
    }
    private class ViewPagerAdapter extends PagerAdapter{

        private LayoutInflater mLayoutInflater;
        public ViewPagerAdapter(Context context){
            super();
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Object instantiateItem(View pager, int index) {
            View view = null;

            if(index == 0){
                view = mLayoutInflater.inflate(R.layout.one, null);
            }
            else if(index == 1) {
                view = mLayoutInflater.inflate(R.layout.two, null);


//                tiger=(TextView)view.findViewById(R.id.tiger);
//                view.findViewById(R.id.blue).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(ViewPagerActivity.this,
//                            "hi yoshi", 2).show();
//                        tiger.setText("hi Tiger");
//                    }
//
//                });


            }else if(index == 2){
                view = mLayoutInflater.inflate(R.layout.three, null);

            }else{
                view = mLayoutInflater.inflate(R.layout.four, null);
            }
            ((ViewPager)pager).addView(view, 0);
//            yoshi = (Button) findViewById(R.id.yoshi);
//            yoshi.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Toast.makeText(ViewPagerActivity.this,
//                            "hi yoshi", 2).show();
//                }
//            });
            return view;
        }
        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }
        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}
    }
}