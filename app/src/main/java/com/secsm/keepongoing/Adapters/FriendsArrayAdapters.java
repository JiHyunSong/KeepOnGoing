package com.secsm.keepongoing.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by JinS on 2014. 8. 13..
 */
public class FriendsArrayAdapters extends BaseAdapter {

    private ViewHolder viewHolder = null;
    Context context;
    ArrayList<FriendNameAndIcon> friendArrayList;
    private ArrayList<FriendNameAndIcon> arraylist;
    LayoutInflater inflater;
    int layout;

    public FriendsArrayAdapters(Context context, int layout, ArrayList<FriendNameAndIcon> friendList) {
        this.context = context;
        friendArrayList = friendList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        arraylist = new ArrayList<FriendNameAndIcon>();
        arraylist.addAll(friendArrayList);
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
            MyVolley.init(context);

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
//                                getProfileFromURL(m.getFileName(), viewHolder.iv);

        getProfileFromURL(friendArrayList.get(position).getProfile_path(),viewHolder.icon);

//        viewHolder.icon.setImageResource(friendArrayList.get(position).icon);

        viewHolder.name.setText(friendArrayList.get(position).name);

        return convertView;

    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        friendArrayList.clear();
        if (charText.length() == 0) {
            friendArrayList.addAll(arraylist);
        } else {
            for (FriendNameAndIcon fni : arraylist) {
                if (fni.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    friendArrayList.add(fni);
                }
            }
        }
        notifyDataSetChanged();
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

    void getProfileFromURL(String ImgURL, ImageView imgView) {
        ImgURL = KogPreference.MEDIA_URL + ImgURL;
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.profile_default,
                        R.drawable.profile_default)
        );
    }

}
