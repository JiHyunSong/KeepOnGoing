package com.fragmenttrasitionextendedexample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.secsm.keepongoing.Quiz.QuizSetlistData;
import com.secsm.keepongoing.Quiz.Quiz_Result_Adapter;
import com.secsm.keepongoing.R;

import java.util.ArrayList;

public class SlidingListFragmentLeft extends Fragment implements MyInterface{
    private int index = 0;
    private String subject;




    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    String[] arr = null;
    ArrayList<QuizSetlistData> list;
    //int[] imageLocations = {R.drawable.dream01, R.drawable.dream02, R.drawable.dream03};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingListView(view);
    }

    private void settingListView(View view) {

        list = new ArrayList<QuizSetlistData>();
        String output = "와우 : 문제";
        list.add(new QuizSetlistData(output));
        output = "씐나 : 문제";
        list.add(new QuizSetlistData(output));
        listView = (ListView) view.findViewById(R.id.listView_test2);
        listView.setAdapter(new Quiz_Result_Adapter(view.getContext(), list,this));

    }
    public void foo(){
        ((quiz_set_result)getActivity()).addTransition(getView());
    }

    public void animate(){
        ((quiz_set_result)getActivity()).addTransition(getView());
    }


      private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void loadDatabase() {
        // 인덱스를 가지고 로드 데이터
    }
}