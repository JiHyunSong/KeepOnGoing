package com.fragmenttrasitionextendedexample;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.secsm.keepongoing.Quiz.QuizSetlistData;
import com.secsm.keepongoing.Quiz.Quiz_Result_Adapter;
import com.secsm.keepongoing.R;

import java.util.ArrayList;

public class SlidingListFragmentLeft extends Fragment implements MyInterface{
    private int position=0;

    private ArrayAdapter<String> _arrAdapter;
    private ListView listView;
    private TextView error_msg;
    private View view;
    ArrayList<QuizSetlistData> list;
    //int[] imageLocations = {R.drawable.dream01, R.drawable.dream02, R.drawable.dream03};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sliding_fragment_layout_left, container, false);
        error_msg = (TextView)view.findViewById(R.id.error_msg);
        listView = (ListView) view.findViewById(R.id.listView_test2);
        error_msg.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(new Quiz_Result_Adapter(view.getContext(), list,this));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
        settingListView(view);
    }

    private void settingListView(View view) {


        //list = new ArrayList<QuizSetlistData>();


        //String output = "와우 : 문제";
        //list.add(new QuizSetlistData(output));
        //output = "씐나 : 문제";
        //list.add(new QuizSetlistData(output));
//        listView = (ListView) view.findViewById(R.id.listView_test2);
//        listView.setAdapter(new Quiz_Result_Adapter(view.getContext(), list,this));
    }


    public void foo(int position){
        this.position=position;
        ((quiz_set_result)getActivity()).addTransition(getView(),list.get(position).name,list.get(position).subject,
                list.get(position).question,list.get(position).solution,list.get(position).date);
    }

    public void animate(){
        ((quiz_set_result)getActivity()).addTransition(getView(),list.get(position).name,list.get(position).subject,
                list.get(position).question,list.get(position).solution,list.get(position).date);
    }




      private void refresh(String $inputValue) {
        _arrAdapter.add($inputValue);
        _arrAdapter.notifyDataSetChanged();
    }

    public void setList(ArrayList<QuizSetlistData> list) {
        this.list = new ArrayList<QuizSetlistData>();
        this.list.addAll(list);
    settingListView(this.view);
        if(list.size()==1&&list.get(0).date=="null") {
            listView.setVisibility(View.GONE);
            error_msg.setVisibility(View.VISIBLE);
            Log.e("minsu:)", "listview list size : " + list.size() + "list contents : " + list.get(0).date);

        }
    }

    public void loadDatabase() {
        // 인덱스를 가지고 로드 데이터
    }
}