package com.fragmenttrasitionextendedexample;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.secsm.keepongoing.Quiz.Quiz_data;
import com.secsm.keepongoing.R;

import java.util.ArrayList;


public class SlidingListFragmentRight extends Fragment implements MyInterface {
    private int index =0;
    private String question;
    private String titlez;
    private String subject;
    private String solution;
    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    private View view;
    ArrayList<Quiz_data> list;
    private String date;
    TextView title_view,question_view,subject_view,date_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sliding_fragment_layout_right, container, false);
    /*    listView = (ListView) view.findViewById(R.id.listView_test2);
        listView.setAdapter(new Quiz_Result_Adapter(view.getContext(), list,this));
        return view;*/

    }
    public void foo(int position){
        getFragmentManager().popBackStack();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title_view = (TextView)getView().findViewById(R.id.question_title_content);
        title_view.setText(titlez);
        subject_view = (TextView)getView().findViewById(R.id.solve_subject_solve);
        subject_view.setText(subject);
        question_view = (TextView)getView().findViewById(R.id.textview_solve);
        question_view.setText(question);
        date_view = (TextView)getView().findViewById(R.id.date_view);
        date_view.setText("출제일 : "+date);
        Button back = (Button) getView().findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                foo(0);
            }
        });
       // setListAdapter(new MyListAdapter());
    }

    public void setTitle(String title){
        this.titlez = title;
    }
    public void setQuestion(String question){
        this.question = question;
    }
    public void setSolution(String solution){
        this.solution = solution;
    }
    public void setDate(String date){
        this.date = date;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }


    public void loadDatabase(){
        // 인덱스를 가지고 로드 데이터
    }



}
