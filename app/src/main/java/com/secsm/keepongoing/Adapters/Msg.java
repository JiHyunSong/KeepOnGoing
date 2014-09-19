package com.secsm.keepongoing.Adapters;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;


/* for customized listview, we need class for message information */
public class Msg {
    private String Name;
    private String Text;
    private String Time;
    private String FileName;
    /* flag for the message from me or the other */
    private String Flag;
    private String MessageType;
    private String Id = null;
    private Bitmap profile_image;
    private Bitmap text_image;
    private DBHelper helper;
    private Context mContext;
    private Handler refreshHandler;
    private static String LOG_TAG = "Msg Class";

    public String getMessageType() {
        return MessageType;
    }

    /* if flag is true, me! exchange format */
    public Msg(Context context, String _Name, String _Text, String _Time, String _Flag, String _MessageType, String _FileName, Handler refreshHandler) {
        this.Name = _Name + " : ";
        this.Text = _Text;
        this.Time = _Time;
        this.FileName = _FileName;
        this.Flag = _Flag;
        this.MessageType = _MessageType;
        this.mContext = context;
        this.refreshHandler = refreshHandler;

        helper = new DBHelper(mContext);
        if(helper.isImageExist(this.FileName))
        {
            this.profile_image = helper.getImage(this.FileName);
        }else {
            downloadProfileImage(this.FileName);
        }
        helper.close();

        if(_MessageType.equals(KogPreference.MESSAGE_TYPE_IMAGE))
        {
            if(helper.isImageExist(this.Text))
            {
                this.profile_image = helper.getImage(this.Text);
            }else {
                downloadChatImage(this.Text);
            }

        }
    }


    private void downloadChatImage(String ImgName)
    {
        String ImgURL = KogPreference.DOWNLOAD_CHAT_IMAGE_URL + KogPreference.getRid(mContext) + "/" + ImgName;

//        android.view.ViewGroup.LayoutParams params = imgView.getLayoutParams();
//        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        int width = imgView.getWidth();
//        Log.i(LOG_TAG, "ChatImage width (imgView.getWidth()): " + width);
//        Log.i(LOG_TAG, "ChatImage width (params.width): " + params.width);
//        params.height = width;

        HttpAPIs.getImage(mContext, ImgURL, 1000, 1000, ImgName, ChatImageSetHandler);
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

    Handler ChatImageSetHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i(LOG_TAG, "ChatImageSetHandler");
                profile_image = msg.getData().getParcelable("image");
                refreshHandler.sendEmptyMessage(1);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public String getName() {
        return Name;
    }

    public String getText() {
        return Text;
    }

    public String getTime() {
        return Time;
    }

    public String getFlag() {
        return Flag;
    }

    public String getFileName() {
        return FileName;
    }

    public String getId() {
        return Id;
    }

    public Bitmap getProfile_image() {
        return profile_image;
    }

    public Bitmap getText_image() {
        return text_image;
    }

}
