package com.secsm.keepongoing.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
                MyVolley.init(mContext);

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

//    private void downloadProfileImage(String ImgName, ImageView imgView)
//    {
//        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + ImgName;
//        int width = imgView.getWidth();
//        imgView.setMaxHeight(width);
//
////        if (!HttpAPIs.imageMap.containsKey(ImgName))
////        {
//////            imageMap.put(ImgName, HttpAPIs.getImage(ImgURL, width, width, imgView, ImgName, ImageSetHandler));
////            HttpAPIs.getImage(ImgURL, width, width, imgView, ImgName, ImageSetHandler);
////        }
//        HttpAPIs.getImage(mContext, ImgURL, ImgName, ProfileImageSetHandler);
//    }

    public void refresh()
    {
        Log.i(LOG_TAG, "adaptor refresh in FriendsArrayAdaptor");
        this.notifyDataSetChanged();
    }

//    Handler ImageSetHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            try {
//                Bundle b = msg.getData();
//                JSONObject result = new JSONObject(b.getString("JSONData"));
//                ImageView getView = (ImageView) result.get("imageView");
//                Bitmap imgFile = (Bitmap) result.get("imageBitmap");
//                String imgName = result.getString("imgName");
//                getView.setImageBitmap(imgFile);
//                HttpAPIs.imageMap.put(imgName, imgFile);
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    };


    Handler ProfileImageSetHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
//                Bundle b = msg.getData();
//                JSONObject result = new JSONObject(b.getString("JSONData"));
//                ImageView getView = (ImageView) result.get("imageView");
//                Bitmap imgFile = (Bitmap) result.get("imageBitmap");
//                String imgName = result.getString("imgName");
//                getView.setImageBitmap(imgFile);

                DBHelper helper1 = new DBHelper(mContext);
                viewHolder.icon.setImageBitmap(helper1.getImage(msg.getData().getString("imgName")));
                helper1.close();
            }catch (Exception e)
            {
                e.printStackTrace();

            }
        }
    };

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
