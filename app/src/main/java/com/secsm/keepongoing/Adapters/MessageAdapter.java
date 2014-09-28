package com.secsm.keepongoing.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.R;
import com.secsm.keepongoing.Shared.KogPreference;
import java.util.ArrayList;

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
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.message_row, null);

            viewHolder = new ViewHolder();


            viewHolder.topLeftText = (TextView) v.findViewById(R.id.msg_topText);
//            viewHolder.wv = (WebView) v.findViewById(R.id.msg_bottomTextWebView);
            viewHolder.messageText = (TextView) v.findViewById(R.id.msg_bottomTextTextView);
            viewHolder.topRightText = (TextView) v.findViewById(R.id.msg_time);
            viewHolder.profileImage = (ImageView) v.findViewById(R.id.msg_person_photo);
            viewHolder.wv_rl = (RelativeLayout.LayoutParams) viewHolder.messageText.getLayoutParams();
            viewHolder.wv_rl.addRule(RelativeLayout.RIGHT_OF, R.id.msg_person_photo);
            viewHolder.wv_rl.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            viewHolder.wv_rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

            viewHolder.messageImage = (ImageView) v.findViewById(R.id.msg_bottomTextImageView);


            // set invisible
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.GONE);

            v.setTag(viewHolder);
        } else {
            // reuse
            viewHolder = (ViewHolder) v.getTag();

        }

        android.view.ViewGroup.LayoutParams params = viewHolder.messageImage.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;;
        int width = (viewHolder.messageImage.getWidth() > 350 ? viewHolder.messageImage.getWidth() : 350);
        Log.i(LOG_TAG, "textImage width : " + width);
        params.height = width;
        viewHolder.messageImage.setLayoutParams(params);

        Msg m = items.get(position);
        if (m != null) {
            if (m.getMessageType().equals(KogPreference.MESSAGE_TYPE_TEXT)) {
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.VISIBLE);

                if (viewHolder.topLeftText != null && viewHolder.messageText != null && viewHolder.topRightText != null && viewHolder.profileImage != null) {
                    if ("나 : ".equals(m.getName())) {
                        viewHolder.topLeftText.setText(m.getTime());
//                        viewHolder.wv.setBackgroundColor(0); // 투명처리
//                        String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
//                                "<html>" + "<body text='#8d5e42'> ";
//                        String htmlFormText = m.getText();
//                        String htmlForm2 = "</body></html>";

//                        viewHolder.wv.loadUrl("about:blank");
//                        viewHolder.wv.invalidate();
//                        // TODO : check scrolling
//
//                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");

                        viewHolder.messageText.setText(m.getText());
                        viewHolder.topRightText.setText(m.getName());

                        String fileName = m.getFileName();

                        Log.i("filename", "id : " + m.getId() + " | me : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*") || fileName.matches(".*png.*")) {
                                viewHolder.profileImage.setImageBitmap(m.getProfile_image());
                            } else {
                                viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                        }

                    } else {
                        viewHolder.topLeftText.setText(m.getName());
//                        viewHolder.wv.setBackgroundColor(0); // 투명처리
//                        String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
//                                "<html>" + "<body> ";
//                        String htmlFormText = m.getText();
//                        String htmlForm2 = "</body></html>";
//
//                        viewHolder.wv.loadUrl("about:blank");
//                        viewHolder.wv.invalidate();
//                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");

                        viewHolder.messageText.setText(m.getText());

                        viewHolder.topRightText.setText(m.getTime());

                        String fileName = m.getFileName();
                        Log.i("filename", "id : " + m.getId() + " | friend : " + fileName);
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*") || fileName.matches(".*png.*")) {

                                viewHolder.profileImage.setImageResource(R.drawable.profile_default);

                                viewHolder.profileImage.setImageBitmap(m.getProfile_image());

                            } else {
                                viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                        }

                    }
                }
            } else if (m.getMessageType().equals(KogPreference.MESSAGE_TYPE_IMAGE))
            {
//                viewHolder.wv.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.VISIBLE);

                if (viewHolder.topLeftText != null && viewHolder.messageImage != null && viewHolder.topRightText != null && viewHolder.profileImage != null) {
                    if ("나 : ".equals(m.getName())) {
                        viewHolder.topLeftText.setText(m.getTime());
//                        viewHolder.wv.setBackgroundColor(0); // 투명처리
//                        String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
//                                "<html>" + "<body>"; //+ "<body bgcolor='#ffc0cb'> ";
//                        String htmlFormText = "";
//                        String htmlForm2 = "</body></html>";
//
//                        viewHolder.wv.loadUrl("about:blank");
//                        viewHolder.wv.invalidate();
//                        // TODO : check scrolling
//
//                        viewHolder.wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2, "text/html", "utf-8", "file:///android_assest/");


                        viewHolder.messageText.setText("");
                        viewHolder.messageImage.setImageResource(R.drawable.no_image);

                        viewHolder.messageImage.setImageBitmap(m.getText_image());


                        viewHolder.topRightText.setText(m.getName());

                        String fileName = m.getFileName();
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                viewHolder.profileImage.setImageResource(R.drawable.profile_default);

                                viewHolder.profileImage.setImageBitmap(m.getProfile_image());

                            } else {
                                viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                        }

                    } else {
                        viewHolder.topLeftText.setText(m.getName());

                        viewHolder.messageImage.setImageResource(R.drawable.no_image);


                        viewHolder.messageImage.setImageBitmap(m.getText_image());

                        viewHolder.topRightText.setText(m.getTime());

                        String fileName = m.getFileName();
                        if (fileName != null) {
                            if (fileName.matches(".*jpg.*")) {
                                viewHolder.profileImage.setImageResource(R.drawable.profile_default);
                                viewHolder.profileImage.setImageBitmap(m.getProfile_image());
                            } else {
                                viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
                            }
                        } else {
                            viewHolder.profileImage.setBackgroundResource(R.drawable.profile_default);
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
        public TextView topLeftText = null;
//        public WebView wv = null;
        public TextView messageText = null;
        public RelativeLayout.LayoutParams wv_rl = null;
        public TextView topRightText = null;
        public ImageView profileImage = null;
        public ImageView messageImage = null;
    }
}


