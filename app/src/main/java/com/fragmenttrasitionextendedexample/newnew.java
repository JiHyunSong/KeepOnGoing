package com.fragmenttrasitionextendedexample;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.secsm.keepongoing.R;



public class newnew extends Activity implements AdapterView.OnItemSelectedListener{
    private int optionSelected = 16;
    //private int optionSelected = 0;
    private SlidingListFragmentLeft mFirstFragment,newFragment;
    private int index=0;
    private String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newnew);
        Spinner spinner = (Spinner) findViewById(R.id.spinner22);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        subject=getIntent().getExtras().getString("position");


        //Add first fragment
        mFirstFragment = new SlidingListFragmentLeft();
        FragmentManager fm = getFragmentManager();
        mFirstFragment.setSubject(subject);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_place, mFirstFragment);
        fragmentTransaction.commit();
    }

    public void addTransition(View view) {
        Button button = (Button) findViewById(R.id.button);
        if (getFragmentManager().getBackStackEntryCount()==0) {
            Fragment secondFragment = new SlidingListFragmentRight();
            ((SlidingListFragmentRight) secondFragment).setIndex(++index);
            ((SlidingListFragmentRight) secondFragment).setSubject(subject);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, mFirstFragment, secondFragment, R.id.fragment_place);
            fragmentTransactionExtended.addTransition(optionSelected);
            fragmentTransactionExtended.commit();
        }else{
            getFragmentManager().popBackStack();
            mFirstFragment.setIndex(++index);
            mFirstFragment.setSubject(subject);
        }
    }



    public void backTransition(View view) {
        Button button = (Button) findViewById(R.id.button2);
        if (getFragmentManager().getBackStackEntryCount()==0) {
            Fragment secondFragment = new SlidingListFragmentRight();
            ((SlidingListFragmentRight) secondFragment).setIndex(--index);
            ((SlidingListFragmentRight) secondFragment).setSubject(subject);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, mFirstFragment, secondFragment, R.id.fragment_place);
            fragmentTransactionExtended.addTransition(optionSelected);
            fragmentTransactionExtended.commit();
        }else{
            getFragmentManager().popBackStack();
            mFirstFragment.setIndex(--index);
            mFirstFragment.setSubject(subject);
        }

    }





    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       // optionSelected = i;
         optionSelected = 16;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onBackPressed()
    {
      //  Button button = (Button) findViewById(R.id.button);
     //   button.setText("Push");
       // super.onBackPressed();
        finish();

    }

}
