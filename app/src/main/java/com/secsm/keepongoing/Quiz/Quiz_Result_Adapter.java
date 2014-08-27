package com.secsm.keepongoing.Quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fragmenttrasitionextendedexample.MyInterface;
import com.secsm.keepongoing.R;

import java.util.ArrayList;
public class Quiz_Result_Adapter extends BaseAdapter {
        private LayoutInflater inflater = null;
        private ArrayList<QuizSetlistData> infoList = null;
        private ViewHolder viewHolder = null;
        public Context mContext = null;
        private MyInterface listener;

        public Quiz_Result_Adapter(Context c, ArrayList<QuizSetlistData> arrays,MyInterface listener) {
            this.mContext = c;
            this.inflater = LayoutInflater.from(c);
            this.infoList = arrays;


            this.listener=listener;
        }

        // Adapter가 관리할 Data의 개수를 설정 합니다.
        @Override
        public int getCount() {
            return infoList.size();
        }

        // Adapter가 관리하는 Data의 Item 의 Position을 <객체> 형태로 얻어 옵니다.
        @Override
        public QuizSetlistData getItem(int position) {
            return infoList.get(position);
        }

        // Adapter가 관리하는 Data의 Item 의 position 값의 ID 를 얻어 옵니다.
        @Override
        public long getItemId(int position) {
            return position;
        }

        // ListView의 뿌려질 한줄의 Row를 설정 합니다.
        @Override
        public View getView(final int position, final View convertview, ViewGroup parent) {
            View v = convertview;
            final View temp=v;

            if (v == null) {
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.quiz_set_row, null);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.foo();
                        if (position == 0) {
                            Toast.makeText(v.getContext(), "선택된자의 이름은 기상시간", 2).show();
                        }
                        if (position == 1) {
                             Toast.makeText(v.getContext(), "선택된자의 이름은 목표시간", 2).show();
                        }
                    }
                });

                viewHolder.tv_title = (TextView) v.findViewById(R.id.child_textview);
                viewHolder.tv_title.setText(infoList.get(position).name);
                v.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) v.getTag();
            }
            return v;

        }


        public void setArrayList(ArrayList<QuizSetlistData> arrays) {
            this.infoList = arrays;
        }

        public ArrayList<QuizSetlistData> getArrayList() {
            return infoList;
        }

        private View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        /*
         * ViewHolder
         * getView의 속도 향상을 위해 쓴다.
         * 한번의 findViewByID 로 재사용 하기 위해 viewHolder를 사용 한다.
         */
        class ViewHolder {
            public TextView tv_title = null;
        }

        @Override
        protected void finalize() throws Throwable {
            free();
            super.finalize();
        }

        private void free() {
            inflater = null;
            infoList = null;
            viewHolder = null;
            mContext = null;
        }
    }