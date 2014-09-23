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

    private ViewHolder viewHolder = null;
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
        return roomArrayList.get(position).roomname;
    }

    public long getItemId(int position) {
        return position;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.type = (TextView) convertView.findViewById(R.id.txtRoomType);
            viewHolder.room_icon = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.roomname = (TextView) convertView.findViewById(R.id.txtRoomName);
            viewHolder.start_time = (TextView) convertView.findViewById(R.id.txtRoomTime);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.roomname.setText(roomArrayList.get(position).roomname);
        if("liferoom".equals(roomArrayList.get(position).type))
        {
            viewHolder.room_icon.setImageResource(R.drawable.speach_bubble_ico);
            viewHolder.type.setText("생활 스터디방");
            viewHolder.start_time.setText("");
        }else{
            viewHolder.room_icon.setImageResource(R.drawable.subject_room_ico);
            viewHolder.type.setText("과목 스터디방");
            viewHolder.start_time.setText(roomArrayList.get(position).start_time);
        }
        return convertView;

    }

    class ViewHolder {
        public TextView type = null;
        public TextView roomname = null;
        public TextView start_time = null;
        public ImageView room_icon = null;
//        public String name = null;
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private void free() {
        inflater = null;
//        infoList = null;
        viewHolder = null;
        context = null;
    }
}