package com.secsm.keepongoing.Adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/* my customized adapter for listview */
public class MessageAdapter extends ArrayAdapter<Msg> {
    private static String serverFilePath = "http://203.252.195.122/files/";
    private static String LOG_TAG = "MESSAGE ADAPTER";
    private static final String TABLE_IMAGE = "Image_table";
    private ViewHolder viewHolder = null;
    private ArrayList<Msg> items;
    private Context mContext;
    private DBHelper helper;

    public MessageAdapter(Context context, int textViewResourceId, ArrayList<Msg> items) {
        super(context, textViewResourceId, items);
        this.mContext = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        helper = new DBHelper(mContext);
        if (v == null) {
            MyVolley.init(mContext);

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.message_row, null);

            viewHolder = new ViewHolder();


            viewHolder.tt = (TextView) v.findViewById(R.id.msg_topText);
            viewHolder.wv = (WebView) v.findViewById(R.id.msg_bottomTextWebView);
            viewHolder.time = (TextView) v.findViewById(R.id.msg_time);
            viewHolder.iv = (ImageView) v.findViewById(R.id.msg_person_photo);
            viewHolder.wv_rl = (RelativeLayout.LayoutParams) viewHolder.wv.getLayoutParams();
            viewHolder.wv_rl.addRule(RelativeLayout.RIGHT_OF, R.id.msg_person_photo);
            viewHolder.wv_rl.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            viewHolder.wv_rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

            viewHolder.p_iv = (ImageView) v.findViewById(R.id.msg_bottomTextImageView);


            // set invisible
            viewHolder.wv.setVisibility(View.GONE);
            viewHolder.p_iv.setVisibility(View.GONE);

            v.setTag(viewHolder);
        } else {
            // reuse
            viewHolder = (ViewHolder) v.getTag();

        }

        android.view.ViewGroup.LayoutParams params = viewHolder.p_iv.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;;
        int width = (viewHolder.p_iv.getWidth() > 350 ? viewHolder.p_iv.getWidth() : 350);
        Log.i(LOG_TAG, "textImage width : " + width);
        params.height = width;
        viewHolder.p_iv.setLayoutParams(params);

        Msg m = items.get(position);
        if (m != null) {
            if (m.getMessageType().equals(KogPreference.MESSAGE_TYPE_TEXT)) {
                viewHolder.wv.setVisibility(View.GONE);
                viewHolder.p_iv.setVisibility(View.GONE);
                viewHolder.wv.setVisibility(View.VISIBLE);

                if (viewHolder.tt != null && viewHolder.wv != null && viewHolder.time != null && viewHolder.iv != null) {
                    if ("나 : ".equals(m.getName())) {
                        viewHolder.tt.setText(m.getTime());
                        viewHolder.wv.setBackgroundColor(0); // 투명처리
                        String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
                                "<html>" + "<body text='#8d5e42'> ";
                        String htmlFormText = m.getText();
                        String htmlForm2 = "</body></html>";

                        viewHolder.wv.loadUrl("about:blank");
                        viewHolder.wv.invalidate();
                        // TODO : check scrolling

                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");

                        viewHolder.time.setText(m.getName());

                        String fileName = m.getFileName();

                        Log.i("filename", "id : " + m.getId() + " | me : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*") || fileName.matches(".*png.*")) {
                                viewHolder.iv.setImageBitmap(m.getProfile_image());
                            } else {
                                viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                        }

                    } else {
                        viewHolder.tt.setText(m.getName());
                        viewHolder.wv.setBackgroundColor(0); // 투명처리
                        String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
                                "<html>" + "<body> ";
                        String htmlFormText = m.getText();
                        String htmlForm2 = "</body></html>";

                        viewHolder.wv.loadUrl("about:blank");
                        viewHolder.wv.invalidate();
                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");

                        viewHolder.time.setText(m.getTime());

                        String fileName = m.getFileName();
                        Log.i("filename", "id : " + m.getId() + " | friend : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*") || fileName.matches(".*png.*")) {

                                viewHolder.iv.setImageResource(R.drawable.profile_default);

                                viewHolder.iv.setImageBitmap(m.getProfile_image());

                            } else {
                                viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                        }

                    }
                }
            } else if (m.getMessageType().equals(KogPreference.MESSAGE_TYPE_IMAGE))
            {
                viewHolder.wv.setVisibility(View.GONE);
                viewHolder.p_iv.setVisibility(View.GONE);
                viewHolder.p_iv.setVisibility(View.VISIBLE);

                if (viewHolder.tt != null && viewHolder.p_iv != null && viewHolder.time != null && viewHolder.iv != null) {
                    if ("나 : ".equals(m.getName())) {
                        viewHolder.tt.setText(m.getTime());
                        viewHolder.wv.setBackgroundColor(0); // 투명처리
                        String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
                                "<html>" + "<body bgcolor='#ffc0cb'> ";
                        String htmlFormText = "";
                        String htmlForm2 = "</body></html>";

                        viewHolder.wv.loadUrl("about:blank");
                        viewHolder.wv.invalidate();
                        // TODO : check scrolling

                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");


                        viewHolder.p_iv.setImageResource(R.drawable.no_image);

                        viewHolder.p_iv.setImageBitmap(m.getText_image());


                        viewHolder.time.setText(m.getName());

                        String fileName = m.getFileName();
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                viewHolder.iv.setImageResource(R.drawable.profile_default);

                                viewHolder.iv.setImageBitmap(m.getProfile_image());

                            } else {
                                viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                        }

                    } else {
                        viewHolder.tt.setText(m.getName());

                        viewHolder.p_iv.setImageResource(R.drawable.no_image);


                        viewHolder.p_iv.setImageBitmap(m.getText_image());

                        viewHolder.time.setText(m.getTime());

                        String fileName = m.getFileName();
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                viewHolder.iv.setImageResource(R.drawable.profile_default);
                                viewHolder.iv.setImageBitmap(m.getProfile_image());
                            } else {
                                viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                        }

                    }
                }
            }else if(m.getMessageType().equals(KogPreference.MESSAGE_TYPE_DUMMY)){


            }
            else
            {

            }
            helper.close();
            viewHolder.wv_rl.addRule(RelativeLayout.RIGHT_OF, R.id.msg_person_photo);
            viewHolder.wv_rl.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            viewHolder.wv_rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        }
        return v;
    }

    public void refresh(){
        this.notifyDataSetChanged();
    }

    class ViewHolder {
        public Bitmap friend_profile = null;
        public String friend_nickname = null;
        public TextView tt = null;
        public WebView wv = null;
        public RelativeLayout.LayoutParams wv_rl = null;
        public TextView time = null;
        public ImageView iv = null;
        public ImageView p_iv = null;
    }

//    private void downloadChatImage(String ImgName, ImageView imgView)
//    {
//        String ImgURL = KogPreference.DOWNLOAD_CHAT_IMAGE_URL + KogPreference.getRid(mContext) + "/" + ImgName;
//
//        android.view.ViewGroup.LayoutParams params = imgView.getLayoutParams();
//        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        int width = imgView.getWidth();
//        Log.i(LOG_TAG, "ChatImage width (imgView.getWidth()): " + width);
//        Log.i(LOG_TAG, "ChatImage width (params.width): " + params.width);
//        params.height = width;
//
//        HttpAPIs.getImage(mContext, ImgURL, ImgName, ChatImageSetHandler);
//    }
//
//    private void downloadProfileImage(String ImgName, ImageView imgView)
//    {
//        String ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + ImgName;
//        int width = imgView.getWidth();
//        imgView.setMaxHeight(width);
//
//        HttpAPIs.getImage(mContext, ImgURL, ImgName, ProfileImageSetHandler);
//    }
//
//
//
//    Handler ProfileImageSetHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            try {
////                Bundle b = msg.getData();
////                JSONObject result = new JSONObject(b.getString("JSONData"));
////                ImageView getView = (ImageView) result.get("imageView");
////                Bitmap imgFile = (Bitmap) result.get("imageBitmap");
////                String imgName = result.getString("imgName");
////                getView.setImageBitmap(imgFile);
//                Log.i(LOG_TAG, "ProfileImageSetHandler2 : " + msg.getData().getString("imgName"));
//                DBHelper helper1 = new DBHelper(mContext);
//                viewHolder.iv.setImageBitmap(helper1.getImage(msg.getData().getString("imgName")));
//                helper1.close();
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//
//            }
//        }
//    };
//
//    Handler ChatImageSetHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            try {
//                Log.i(LOG_TAG, "ChatImageSetHandler");
//                DBHelper helper1 = new DBHelper(mContext);
//                viewHolder.p_iv.setImageBitmap(helper1.getImage(msg.getData().getString("imgName")));
//                helper1.close();
//
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    };

    void getChatImageFromURL(String ImgURL, ImageView imgView) {
        ImgURL = KogPreference.DOWNLOAD_CHAT_IMAGE_URL + KogPreference.getRid(mContext) + "/" + ImgURL;
        android.view.ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;;
        int width = imgView.getWidth();
        params.height = width;
        imgView.setLayoutParams(params);
        // S3 358
//        Log.i(LOG_TAG, "img view width : " + width);
         ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.no_image,
                        R.drawable.no_image)
        );
    }

    void getProfileFromURL(String ImgURL, ImageView imgView) {
        ImgURL = KogPreference.DOWNLOAD_PROFILE_URL + ImgURL;
//        Log.i(LOG_TAG, "getProfileFromURL img URL : " + ImgURL);

        int width = imgView.getWidth();
        imgView.setMaxHeight(width);


        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.profile_default,
                        R.drawable.profile_default)
        );
    }


}


