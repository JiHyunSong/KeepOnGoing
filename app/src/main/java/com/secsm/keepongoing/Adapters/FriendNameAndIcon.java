package com.secsm.keepongoing.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;

import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;

public class FriendNameAndIcon {
    private int icon;
    private String profile_path;
    private String name;
    private String targetTime;
    private String accomplishedTime;
    private String isMaster;
    private String score;
    private Context mContext;
    private Bitmap profile_image;
    private Handler refreshHandler;
    private DBHelper helper;
    private static String LOG_TAG = "FriendNameAndIcon class";

    public FriendNameAndIcon(Context context, Handler refreshHandler, String profile_path, String name, String _targetTime, String isMaster, String _accomplishedTime, String _score) {
        mContext = context;
        this.refreshHandler = refreshHandler;
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        if(_targetTime!=null) {
            if (!_targetTime.equals("null") && _targetTime.length()>5) {
                Log.i("adapter", "targetTime sub(0,5)" + _targetTime);
                this.targetTime = _targetTime.substring(0, 5);
            } else {
                Log.i("adapter", "targetTime " + _targetTime);
                this.targetTime = "00:00";
            }
        }else
        {
            this.targetTime = "00:00";
        }
        this.isMaster = isMaster;
        if(_accomplishedTime !=null) {
            if (!_accomplishedTime.equals("null") && _accomplishedTime.length()>5) {
                Log.i("adapter", "_accomplishedTime sub(0,5)" + _accomplishedTime);
                this.accomplishedTime = _accomplishedTime.substring(0, 5);
            } else {
                Log.i("adapter", "_accomplishedTime 00:00" + _accomplishedTime);
                this.accomplishedTime = "00:00";
            }
        }else
        {
            Log.i("adapter", "_accomplishedTime 00:00" + _accomplishedTime);
            this.accomplishedTime = "00:00";
        }
        this.score = _score;
        helper = new DBHelper(mContext);
        if(helper.isImageExist(this.profile_path))
        {
            this.profile_image = helper.getImage(this.profile_path);
        }else {
            downloadProfileImage(this.profile_path);
        }
        helper.close();
    }

    public FriendNameAndIcon(Context context, Handler refreshHandler, String profile_path, String name, String _targetTime, String isMaster, String _accomplishedTime) {
        mContext = context;
        this.refreshHandler = refreshHandler;
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        if(_targetTime!=null) {
            if (!_targetTime.equals("null") && _targetTime.length() > 5) {
                Log.i("adapter", "targetTime " + _targetTime);
                this.targetTime = _targetTime.substring(0, 5);
            } else {
                Log.i("adapter", "targetTime " + _targetTime);
                this.targetTime = "00:00";
            }
        }else
        {
            this.targetTime = "00:00";
        }
        this.isMaster = isMaster;
        if(_accomplishedTime !=null) {
            if (!_accomplishedTime.equals("null") && _accomplishedTime.length() > 5) {
                this.accomplishedTime = _accomplishedTime.substring(0, 5);
            } else {
                this.accomplishedTime = "00:00";
            }
        }else
        {
            this.accomplishedTime = "00:00";
        }
        helper = new DBHelper(mContext);
        if(helper.isImageExist(this.profile_path))
        {
            this.profile_image = helper.getImage(this.profile_path);
        }else {
            downloadProfileImage(this.profile_path);
        }
        helper.close();
    }

    public FriendNameAndIcon(Context context, Handler refreshHandler, String profile_path, String name, String targetTime, String isMaster) {
        mContext = context;
        this.refreshHandler = refreshHandler;
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        if(targetTime!=null) {
            if (!targetTime.equals("null") && targetTime.length() > 5) {
                this.targetTime = targetTime.substring(0, 5);
            } else {
                this.targetTime = "00:00";
            }
        }else
        {
            this.targetTime = "00:00";
        }
        this.isMaster = isMaster;
        this.score = null;
        helper = new DBHelper(mContext);
        if(helper.isImageExist(this.profile_path))
        {
            this.profile_image = helper.getImage(this.profile_path);
        }else {
            downloadProfileImage(this.profile_path);
        }
        helper.close();
    }

    public FriendNameAndIcon(Context context, Handler refreshHandler, String profile_path, String name, String targetTime) {
        mContext = context;
        this.refreshHandler = refreshHandler;
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        if(targetTime!=null) {
            if (!targetTime.equals("null")) {
                this.targetTime = targetTime.substring(0, targetTime.length() - 3);
            } else {
                this.targetTime = "00:00";
            }
        }
        this.isMaster = "false";
        this.score = null;

        helper = new DBHelper(mContext);
        if(helper.isImageExist(this.profile_path))
        {
            Log.i(LOG_TAG, "image exist, name : " + this.name + " path : " + profile_path);
            this.profile_image = helper.getImage(this.profile_path);
        }else {
            downloadProfileImage(this.profile_path);
        }
        helper.close();

    }


    private void downloadProfileImage(String ImgName)
    {
        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + ImgName;
//        int width = imgView.getWidth();
//        imgView.setMaxHeight(width);

        HttpAPIs.getImage(mContext, ImgURL, 150, 150, ImgName, ProfileImageSetHandler);
    }



    Handler ProfileImageSetHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i(LOG_TAG, "ProfileImageSetHandler2 : " + msg.getData().getString("imgName"));
                profile_image = msg.getData().getParcelable("image");
                refreshHandler.sendEmptyMessage(1);
            }catch (Exception e)
            {
                e.printStackTrace();

            }
        }
    };



    public String getAccomplishedTime() {
        return accomplishedTime;
    }

    public void setAccomplishedTime(String accomplishedTime) {
        this.accomplishedTime = accomplishedTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(String targetTime) {
        this.targetTime = targetTime;
    }

    public Bitmap getProfile_image() {
        return profile_image;
    }
}
