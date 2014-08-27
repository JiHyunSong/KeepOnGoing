package com.fragmenttrasitionextendedexample;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.secsm.keepongoing.R;


public class SlidingListFragmentRight extends Fragment implements MyInterface {
    private int index =0;
    private String subject;
    TextView title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sliding_fragment_layout_right, container, false);

    }
    public void foo(){
        ((newnew)getActivity()).addTransition(getView());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    /*    title = (TextView)getView().findViewById(R.id.testtextview2);
        title.setText(""+index+" / "+ subject);*/
        Button back = (Button) getView().findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                foo();
            }
        });
       // setListAdapter(new MyListAdapter());
    }

    public void setIndex(int index){
        this.index = index;

    }

    public void setSubject(String subject){
        this.subject = subject;
    }


    public void loadDatabase(){
        // 인덱스를 가지고 로드 데이터
    }

}
