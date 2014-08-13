package com.secsm.keepongoing.Adapters;


import com.secsm.keepongoing.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JinS on 2014. 8. 13..
 */
public class FriendsArrayAdapters extends BaseAdapter {

    Context context;
    ArrayList<FriendNameAndIcon> friendArrayList;
    LayoutInflater inflater;
    int layout;

    public FriendsArrayAdapters(Context context, int layout, ArrayList<FriendNameAndIcon> friendList) {
        this.context = context;
        friendArrayList = friendList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    public int getCount() {
        return friendArrayList.size();

    }

    public Object getItem(int position) {
        return friendArrayList.get(position).name;
    }

    public long getItemId(int position) {
        return position;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(layout, parent, false);

        ImageView icon = (ImageView)convertView.findViewById(R.id.iconFriend);
        icon.setImageResource(friendArrayList.get(position).icon);

        TextView name = (TextView)convertView.findViewById(R.id.txtFriendNickname);
        name.setText(friendArrayList.get(position).name);

        return convertView;

    }
}
