package com.secsm.keepongoing.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.R;


import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by JinS on 2014. 8. 13..
 */
public class FriendsArrayAdapters extends BaseAdapter {

    private static String LOG_TAG = "FriendsArrayAdapter";
    private ViewHolder viewHolder = null;
    Context mContext;
    ArrayList<FriendNameAndIcon> friendArrayList;
    private ArrayList<FriendNameAndIcon> arraylist;
    LayoutInflater inflater;
    int layout;
    DBHelper helper;

    public FriendsArrayAdapters(Context context, int layout, ArrayList<FriendNameAndIcon> friendList) {
        this.mContext = context;
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
        helper = new DBHelper(mContext);
        if (friendArrayList != null) {
            if (v == null) {
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
                viewHolder.icon.setImageBitmap(friendArrayList.get(position).getProfile_image());

            }

//        viewHolder.icon.setImageBitmap();
//                                getProfileFromURL(m.getFileName(), viewHolder.iv);
//            viewHolder.icon.setImageResource(R.drawable.profile_default);

            viewHolder.icon.setImageResource(R.drawable.profile_default);
            viewHolder.icon.setImageBitmap(friendArrayList.get(position).getProfile_image());

//            String fileName = friendArrayList.get(position).getProfile_path();
//            if(helper.isImageExist(fileName))
//            {
//                viewHolder.icon.setImageBitmap(helper.getImage(fileName));
//            }else {
//                downloadProfileImage(fileName, viewHolder.icon);
//            }

//            getProfileFromURL(friendArrayList.get(position).getProfile_path(), viewHolder.icon);

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
        helper.close();
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

    public void refresh()
    {
        Log.i(LOG_TAG, "adaptor refresh in FriendsArrayAdaptor");
        this.notifyDataSetChanged();
    }
}
