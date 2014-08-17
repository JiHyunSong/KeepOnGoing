package com.secsm.keepongoing.Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.secsm.keepongoing.R;

import java.util.ArrayList;

public class Solve_Main extends Activity {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    public String LOG_TAG = "Solve MAIN";
    listAdapter mAdapter = null;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve__main);
        list = new ArrayList<Quiz_data>();
        settingTextView();
        list.add(new Quiz_data("a"));
        list.add(new Quiz_data("b"));
        list.add(new Quiz_data("c"));


        addListenerOnButton();
        mAdapter = new listAdapter(this, list);
        listView = (ListView) findViewById(R.id.listView_Quiz_solve);
        //listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setAdapter(mAdapter);

/*

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
                mAdapter.addQuizData(new Quiz_data("tf"));
            }
        });
*/


    }

public void settingTextView(){
    TextView textview=(TextView) findViewById(R.id.textview_solve);
    textview.setText("aaa\naaa\naaaa\naaaaaaaaaaa\naaaaaaaaaaaaaa\naaaaaaaaaaa\naaaaaaa\naaaa\naaaaaaa\naaaaa\naaaaaa\naa\naaaaaaaaaaaa\naaaaaaa\\nnaaaaaa\naa\naaa");


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

        mAdapter.refresh();
    }

    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }



    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.btnSubmit_solve);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }

        });
    }
}

