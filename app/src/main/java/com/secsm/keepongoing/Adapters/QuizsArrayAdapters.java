package com.secsm.keepongoing.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.secsm.keepongoing.R;

import java.util.ArrayList;

/**
 * Created by JinS on 2014. 8. 13..
 */
public class QuizsArrayAdapters extends BaseAdapter {
    Context context;
    ArrayList<String> quizArrayList;
    LayoutInflater inflater;
    int layout;

    public QuizsArrayAdapters(Context context, int layout, ArrayList<String> quizArrayList) {
        this.context = context;
        quizArrayList = quizArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    public int getCount() {
        return quizArrayList.size();

    }

    public Object getItem(int position) {
        return quizArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(layout, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.txtQuizName);
        name.setText(quizArrayList.get(position));

        return convertView;

    }
}
