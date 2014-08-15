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
import android.widget.TextView;

import com.secsm.keepongoing.R;

import java.net.URL;
import java.util.ArrayList;

/* my customized adapter for listview */
public class MessageAdapter extends ArrayAdapter<Msg> {
    private static String serverFilePath = "http://203.252.195.122/files/";
    private ArrayList<Msg> items;
    Context mContext;
    public MessageAdapter(Context context, int textViewResourceId, ArrayList<Msg> items){
        super(context, textViewResourceId, items);
        this.mContext = context;
        this.items = items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if( v == null )
        {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.message_row, null);
        }
        Msg m = items.get(position);
        if( m != null ){
            TextView tt = (TextView) v.findViewById(R.id.msg_topText);
            WebView wv = (WebView) v.findViewById(R.id.msg_bottomTextWebView);
            TextView time  = (TextView) v.findViewById(R.id.msg_time);
            ImageView iv = (ImageView) v.findViewById(R.id.msg_person_photo);

            if(tt != null && wv != null && time != null && iv != null){
                if("나 : ".equals(m.getName())){
                    tt.setText(m.getTime());
                    wv.setBackgroundColor(0); // 투명처리
                    String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
                            "<html>" +	"<body bgcolor='#ffc0cb'> ";
                    String htmlFormText = m.getText();
                    String htmlForm2 = "</body></html>";

//                    for(int i = 0 ; i < emoArr.length; i++){
//                        htmlFormText = htmlFormText.replaceAll(repEmoArr[i], htmlEmoArr[i]);
//                    }
                    wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2 , "text/html", "utf-8", "file:///android_assest/");

                    time.setText(m.getName());

                    // TODO : profile image path download from server
//                    String fileName = imgFromServ(myID);
                    String fileName = null;

                    Log.i("filename", "id : " + m.getId() + " | me : " + fileName);
                    if(fileName != null){
                        if(fileName.matches(".*jpg.*")){
                            String mecodedURL = serverFilePath + fileName;
                            Log.d("imageView", "file Name : " + fileName);
                            try{
                                URL mURL = new URL(mecodedURL);
                                // TODO : profile image path download from server
//                                Bitmap bm = getRemoteImage(mURL);
//                                iv.setImageBitmap(bm);
                            }catch(Exception e){
                                Log.e("photo", e.toString());
                            }
                        }else
                        {
                            iv.setBackgroundResource(R.drawable.profile_default);
                        }
                    }else
                    {
                        iv.setBackgroundResource(R.drawable.profile_default);
                    }

                }else{
                    tt.setText(m.getName());
                    wv.setBackgroundColor(0); // 투명처리
                    String htmlForm1 = "<metahttp-equiv='Content-Type' content='text'/html; charset = utf-8 /> " +
                            "<html>" +	"<body> ";
                    String htmlFormText = m.getText();
                    String htmlForm2 = "</body></html>";

//                    for(int i = 0 ; i < emoArr.length; i++){
//                        htmlFormText = htmlFormText.replaceAll(repEmoArr[i], htmlEmoArr[i]);
//                    }
//                    wv.loadDataWithBaseURL("file:///android_asset/", htmlForm1 + htmlFormText + htmlForm2 , "text/html", "utf-8", "file:///android_assest/");

                    time.setText(m.getTime());

                    //        				String fileName = m.getFileName();
                    // TODO : profile image path download from server
//                    String fileName = imgFromServ(myID);
                    String fileName = null;
                    Log.i("filename", "id : " + m.getId() + " | friend : " +fileName );
                    if(fileName != null){
                        if(fileName.matches(".*jpg.*")){
                            String mecodedURL = serverFilePath + fileName;
                            Log.d("imageView", "file Name : " + fileName);
                            try{
                                URL mURL = new URL(mecodedURL);
                                // TODO : profile image path download from server
//                                Bitmap bm = getRemoteImage(mURL);
//                                iv.setImageBitmap(bm);
                            }catch(Exception e){
                                Log.e("photo", e.toString());
                            }
                        }else
                        {
                            iv.setBackgroundResource(R.drawable.profile_default);
                        }
                    }else
                    {
                        iv.setBackgroundResource(R.drawable.profile_default);
                    }

                }
            }

        }
        return v;
    }

}