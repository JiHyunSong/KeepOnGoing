package com.secsm.keepongoing.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.secsm.keepongoing.R;

import java.util.ArrayList;

/**
 * Created by JinS on 2014. 8. 13..
 */
public class RoomsArrayAdapters extends BaseAdapter {

    Context context;
    ArrayList<RoomNaming> roomArrayList;
    LayoutInflater inflater;
    int layout;

    public RoomsArrayAdapters(Context context, int layout, ArrayList<RoomNaming> roomList) {
        this.context = context;
        roomArrayList = roomList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    public int getCount() {
        return roomArrayList.size();

    }

    public Object getItem(int position) {
        return roomArrayList.get(position).name;
    }

    public long getItemId(int position) {
        return position;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(layout, parent, false);

        TextView type = (TextView) convertView.findViewById(R.id.txtRoomType);
        type.setText(roomArrayList.get(position).type);

        TextView name = (TextView) convertView.findViewById(R.id.txtRoomName);
        name.setText(roomArrayList.get(position).name);

        TextView time = (TextView) convertView.findViewById(R.id.txtRoomTime);
        time.setText(roomArrayList.get(position).time);
        return convertView;

    }

}