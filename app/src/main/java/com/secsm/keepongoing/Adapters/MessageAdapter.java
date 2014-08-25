package com.secsm.keepongoing.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;
import com.secsm.keepongoing.Shared.MyVolley;

import java.util.ArrayList;

/* my customized adapter for listview */
public class MessageAdapter extends ArrayAdapter<Msg> {
    private static String serverFilePath = "http://203.252.195.122/files/";
    private static String LOG_TAG = "MESSAGE ADAPTER";
    private ViewHolder viewHolder = null;
    private ArrayList<Msg> items;
    Context mContext;

    public MessageAdapter(Context context, int textViewResourceId, ArrayList<Msg> items) {
        super(context, textViewResourceId, items);
        this.mContext = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            MyVolley.init(mContext);

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.message_row, null);

            viewHolder = new ViewHolder();


            viewHolder.tt = (TextView) v.findViewById(R.id.msg_topText);
            viewHolder.wv = (WebView) v.findViewById(R.id.msg_bottomTextWebView);
//            android:layout_width="match_parent"
//            android:layout_height="wrap_content"
//            viewHolder.wv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
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
                                "<html>" + "<body bgcolor='#ffc0cb'> ";
                        String htmlFormText = m.getText();
                        String htmlForm2 = "</body></html>";

                        //                    for(int i = 0 ; i < emoArr.length; i++){
                        //                        htmlFormText = htmlFormText.replaceAll(repEmoArr[i], htmlEmoArr[i]);
                        //                    }
                        viewHolder.wv.loadUrl("about:blank");
                        viewHolder.wv.invalidate();
                        // TODO : check scrolling

                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");

                        viewHolder.time.setText(m.getName());

                        // TODO : profile image path download from server
                        //                    String fileName = imgFromServ(myID);
                        String fileName = null;

                        Log.i("filename", "id : " + m.getId() + " | me : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                getProfileFromURL(m.getFileName(), viewHolder.iv);

//                                String mecodedURL = serverFilePath + fileName;
//                                Log.d("imageView", "file Name : " + fileName);
//                                try {
//                                    URL mURL = new URL(mecodedURL);
//                                    // TODO : profile image path download from server
//                                    //                                Bitmap bm = getRemoteImage(mURL);
//                                    //                                iv.setImageBitmap(bm);
//                                } catch (Exception e) {
//                                    Log.e("photo", e.toString());
//                                }
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

                        //                    for(int i = 0 ; i < emoArr.length; i++){
                        //                        htmlFormText = htmlFormText.replaceAll(repEmoArr[i], htmlEmoArr[i]);
                        //                    }
                        //                    wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2 , "text/html", "utf-8", "file:///android_assest/");
                        viewHolder.wv.loadUrl("about:blank");
                        viewHolder.wv.invalidate();
                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");

                        viewHolder.time.setText(m.getTime());

                        //        				String fileName = m.getFileName();
                        // TODO : profile image path download from server
                        //                    String fileName = imgFromServ(myID);
                        String fileName = null;
                        Log.i("filename", "id : " + m.getId() + " | friend : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                getProfileFromURL(m.getFileName(), viewHolder.iv);

//                                String mecodedURL = serverFilePath + fileName;
//                                Log.d("imageView", "file Name : " + fileName);
//                                try {
//                                    URL mURL = new URL(mecodedURL);
//                                    // TODO : profile image path download from server
//                                    //                                Bitmap bm = getRemoteImage(mURL);
//                                    //                                iv.setImageBitmap(bm);
//                                } catch (Exception e) {
//                                    Log.e("photo", e.toString());
//                                }
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

                        // set p_iv
                        getChatImageFromURL(m.getText(), viewHolder.p_iv);


                        viewHolder.time.setText(m.getName());

                        // TODO : profile image path download from server
                        //                    String fileName = imgFromServ(myID);
                        String fileName = null;

                        Log.i("filename", "id : " + m.getId() + " | me : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                getProfileFromURL(m.getFileName(), viewHolder.iv);

//                                String mecodedURL = serverFilePath + fileName;
//                                Log.d("imageView", "file Name : " + fileName);
//                                try {
//                                    URL mURL = new URL(mecodedURL);
//
//                                    // TODO : profile image path download from server
//                                    //                                Bitmap bm = getRemoteImage(mURL);
//                                    //                                iv.setImageBitmap(bm);
//                                } catch (Exception e) {
//                                    Log.e("photo", e.toString());
//                                }
                            } else {
                                viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                        }

                    } else {
                        viewHolder.tt.setText(m.getName());

                        // set p_iv
                        getChatImageFromURL(m.getText(), viewHolder.p_iv);


                        viewHolder.time.setText(m.getTime());

                        //        				String fileName = m.getFileName();
                        // TODO : profile image path download from server
                        //                    String fileName = imgFromServ(myID);
                        String fileName = null;
                        Log.i("filename", "id : " + m.getId() + " | friend : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                getProfileFromURL(m.getFileName(), viewHolder.iv);
//                                String mecodedURL = serverFilePath + fileName;
//                                Log.d("imageView", "file Name : " + fileName);
//                                try {
//                                    URL mURL = new URL(mecodedURL);
//                                    // TODO : profile image path download from server
//                                    //                                Bitmap bm = getRemoteImage(mURL);
//                                    //                                iv.setImageBitmap(bm);
//                                } catch (Exception e) {
//                                    Log.e("photo", e.toString());
//                                }
                            } else {
                                viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.iv.setBackgroundResource(R.drawable.profile_default);
                        }

                    }
                }
            }else
            {

            }
            viewHolder.wv_rl.addRule(RelativeLayout.RIGHT_OF, R.id.msg_person_photo);
            viewHolder.wv_rl.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            viewHolder.wv_rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;


//            viewHolder.wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

//            ViewGroup.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            viewHolder.wv.setLayoutParams(lp);
//            viewHolder.wv.postInvalidate();
//            v.postInvalidate();
        }
        return v;
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


    void getChatImageFromURL(String ImgURL, ImageView imgView) {
        Log.i(LOG_TAG, "getChatImageFromURL img URL : " + ImgURL);
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.no_image,
                        R.drawable.no_image)
        );
    }

    void getProfileFromURL(String ImgURL, ImageView imgView) {
        Log.i(LOG_TAG, "getProfileFromURL img URL : " + ImgURL);
        ImageLoader imageLoader = MyVolley.getImageLoader();
        imageLoader.get(ImgURL,
                ImageLoader.getImageListener(imgView,
                        R.drawable.profile_default,
                        R.drawable.profile_default)
        );
    }

}


