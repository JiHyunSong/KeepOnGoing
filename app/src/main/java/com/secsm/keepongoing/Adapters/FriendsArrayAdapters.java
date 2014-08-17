package com.secsm.keepongoing.Adapters;


import com.secsm.keepongoing.R;

import android.content.Context;
import android.graphics.Bitmap;
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

    private ViewHolder viewHolder = null;
    Context context;
    ArrayList<FriendNameAndIcon> friendArrayList;
    LayoutInflater inflater;
    int layout;

    public FriendsArrayAdapters(Context context, int layout, ArrayList<FriendNameAndIcon> friendList) {
        this.context = context;
        friendArrayList = friendList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconFriend);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txtFriendNickname);
            convertView.setTag(viewHolder);

        } else {
            // reuse
            viewHolder = (ViewHolder) convertView.getTag();

        }


//        viewHolder.icon.setImageBitmap();


        viewHolder.icon.setImageResource(friendArrayList.get(position).icon);

        viewHolder.name.setText(friendArrayList.get(position).name);

        return convertView;

    }

    class ViewHolder {
        public ImageView icon = null;
        public TextView name = null;
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
//        viewHolder = null;
//        mContext = null;
    }
}
