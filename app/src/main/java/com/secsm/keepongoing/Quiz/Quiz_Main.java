package com.secsm.keepongoing.Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.secsm.keepongoing.R;

import java.util.ArrayList;

public class Quiz_Main extends Activity {
    private ArrayAdapter<String> _arrAdapter ;
    private ListView listView;
    public String LOG_TAG = "QUIZ MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz__main);
        list  = new ArrayList<Quiz_data>();
        settingListView();





        BootstrapButton multiplechoice = (BootstrapButton) findViewById(R.id.multiplechoice);
        multiplechoice.setOnClickListener(new BootstrapButton.OnClickListener() {

            public void onClick(View v) {
                Log.i(LOG_TAG, "list.size : " + list.size());
                list.add(new Quiz_data(""));
                settingListView();
            }
        });
        BootstrapButton essay = (BootstrapButton) findViewById(R.id.essay);
        essay.setOnClickListener(new BootstrapButton.OnClickListener() {
            public void onClick(View v) {
                list.add(new Quiz_data("essay"));
                settingListView();
            }
        });
        BootstrapButton tf = (BootstrapButton) findViewById(R.id.tf);
        tf.setOnClickListener(new BootstrapButton.OnClickListener() {
            public void onClick(View v) {
                list.add(new Quiz_data("tf"));
                settingListView();
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_main, menu);
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
    protected void onResume() {
        super.onResume();
        settingListView();
    }
    String[] arr = null;
    ArrayList<Quiz_data> list;

    private void settingListView() {

      //  list  = new ArrayList<Quiz_data>();
       // list.add(new Quiz_data(""+list.size()));
        listView = (ListView) findViewById(R.id.listView_Quiz) ;
        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setAdapter(new listAdapter(this, list) ) ;
    }

    private void refresh( String $inputValue ) {
        _arrAdapter.add( $inputValue ) ;
        _arrAdapter.notifyDataSetChanged() ;
    }
}
