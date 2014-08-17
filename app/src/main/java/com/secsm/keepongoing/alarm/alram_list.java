package com.secsm.keepongoing.Alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.secsm.keepongoing.R;

import java.util.ArrayList;


public class alram_list extends Activity implements View.OnClickListener {
    private ArrayAdapter<String> _arrAdapter ;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_list);

        settingListView();
        Button go_to_alram = (Button)findViewById(R.id.addAlram);
        go_to_alram.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(alram_list.this, Alarm_main.class);
                //Intent intent = new Intent(MainActivity.this, Alarm_main.class);
                startActivity(intent);
            }
        });

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
        String output="기상시간 : ";
        contact=helper.getContact(1);
        list  = new ArrayList<AlramData>();
        if(contact.gethour()<10)
            output+="0"+contact.gethour();
        else
            output+=contact.gethour();
        if(contact.getminute()<10)
            output+=": 0"+contact.getminute();
        else
            output+=": "+contact.getminute();
        list.add(new AlramData(output));


        contact=helper.getContact(2);
        output="목표시간 : ";
        if(contact.gethour()<10)
            output+="0"+contact.gethour();
        else
            output+=contact.gethour();
        if(contact.getminute()<10)
            output+=": 0"+contact.getminute();
        else
            output+=": "+contact.getminute();
        list.add(new AlramData(output));

/*
        list  = new ArrayList<AlramData>();
        list.add(new AlramData("a :"));
        list.add(new AlramData("b :"));
*/

        //_arrAdapter = new ArrayAdapter<String>( alram_list.this, android.R.layout.simple_list_item_1, list) ;
        listView = (ListView) findViewById(R.id.listView_test) ;
        listView.setAdapter(new AlramAdapter(this, list) ) ;
//        listView.setClickable(true);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(alram_list.this, "선택된자의 이름은 " + list.get(position).name, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onClick(View v) {
   //     EditText editText = (EditText) findViewById( R.id.editField ) ;
    //    String inputValue = editText.getText().toString() ;
     //   refresh( inputValue ) ;
    }

    private void refresh( String $inputValue ) {
        _arrAdapter.add( $inputValue ) ;
        _arrAdapter.notifyDataSetChanged() ;
    }
}



//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alram_list);
//        DBContactHelper db = new DBContactHelper(this);
//
//        /**
//         * CRUD Operations
//         * */
//        // 샘플데이타 넣기
//        Log.d("Insert: ", "Inserting ..");
//        db.addContact(new Contact("Ravi", "9100000000"));
//        db.addContact(new Contact("Srinivas", "9199999999"));
//        db.addContact(new Contact("Tommy", "9522222222"));
//        db.addContact(new Contact("Karthik", "9533333333"));
//
//        // 집어넣은 데이타 다시 읽어들이기
//        Log.d("Reading: ", "Reading all contacts..");
//        List<Contact> contacts = db.getAllContacts();
//
//        for (Contact cn : contacts) {
//            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
//            Log.d("Name: ", log);
//        }
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.alram_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
