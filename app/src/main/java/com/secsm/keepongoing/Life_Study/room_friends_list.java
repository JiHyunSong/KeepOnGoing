package com.secsm.keepongoing.Life_Study;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.secsm.keepongoing.Alarm.Contact;
import com.secsm.keepongoing.Alarm.DBContactHelper;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.BaseActivity;

import java.util.ArrayList;


public class room_friends_list extends BaseActivity implements View.OnClickListener {
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_list);
        settingListView();
    }

    @Override
    protected void onResume() {

        super.onResume();
        settingListView();
    }

    String[] arr = null;
    ArrayList<room_friends_Data> list;

    private void settingListView() {
        DBContactHelper helper = new DBContactHelper(this);
        Contact contact;
        String output = "기상시간 : ";
        contact = helper.getContact(1);
        list = new ArrayList<room_friends_Data>();
        if (contact.gethour() < 10)
            output += "0" + contact.gethour();
        else
            output += contact.gethour();
        if (contact.getminute() < 10)
            output += ": 0" + contact.getminute();
        else
            output += ": " + contact.getminute();
        list.add(new room_friends_Data(output));


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
        list.add(new room_friends_Data(output));

        listView = (ListView) findViewById(R.id.listView_test);
        listView.setAdapter(new room_friends_Adapter(this, list));
    }

    @Override
    public void onClick(View v) {
    }

    private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }
}
