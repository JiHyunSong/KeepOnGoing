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

    private static String LOG_TAG = "FriendsArrayAdapter";
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
        return friendArrayList.get(position).getName();
    }

    public long getItemId(int position) {
        return position;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (friendArrayList != null) {
            if (v == null) {
                MyVolley.init(context);

                v = inflater.inflate(layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) v.findViewById(R.id.iconFriend);
                viewHolder.name = (TextView) v.findViewById(R.id.txtFriendNickname);
                viewHolder.isMaster = (TextView) v.findViewById(R.id.isMasterFriend);
                viewHolder.isMaster.setVisibility(View.GONE);
                viewHolder.score = (TextView) v.findViewById(R.id.txtFriendScore);
                v.setTag(viewHolder);

            } else {
                // reuse
                viewHolder = (ViewHolder) v.getTag();

            }


//        viewHolder.icon.setImageBitmap();
//                                getProfileFromURL(m.getFileName(), viewHolder.iv);

            getProfileFromURL(friendArrayList.get(position).getProfile_path(), viewHolder.icon);

//        viewHolder.icon.setImageResource(friendArrayList.get(position).icon);

            viewHolder.name.setText(friendArrayList.get(position).getName());

            viewHolder.score.setVisibility(View.GONE);
            if (friendArrayList.get(position).getScore() != null) {
                viewHolder.score.setVisibility(View.VISIBLE);
                viewHolder.score.setText(friendArrayList.get(position).getScore());
            }else
            {
                viewHolder.score.setVisibility(View.GONE);
            }

            viewHolder.score.setText("");
            if (friendArrayList.get(position).getScore() != null) {
                viewHolder.score.setText(friendArrayList.get(position).getScore());
            }

            viewHolder.isMaster.setVisibility(View.GONE);
            if(friendArrayList.get(position).getIsMaster() != null) {
                if (friendArrayList.get(position).getIsMaster().equals("true")) {
                    viewHolder.isMaster.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.isMaster.setVisibility(View.GONE);
                }
            }
        }
        return v;
    }

    public void filter(String charText) {
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
        public TextView isMaster = null;
        public TextView score = null;
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
        ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + ImgURL;
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.profile_default,
                        R.drawable.profile_default)
        );
    }

}
