package com.secsm.keepongoing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KogBroadcastReceiver extends BroadcastReceiver{
    private static String LOG_TAG = "KogBroadcastReceiver";
    private static Pattern p = Pattern.compile("^([0-9]+)$");
    private Matcher m;
    private Handler handler;

    public KogBroadcastReceiver(){
    }

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "BroadcastReceiver onReceive()");

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            String sb = "";
            Bundle b = intent.getExtras();

            if(b != null){
                Object[] pdusObj = (Object[]) b.get("pdus");

                SmsMessage[] messages = new SmsMessage[pdusObj.length];
                for(int i=0; i<pdusObj.length; i++)
                {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                }


                for(SmsMessage currentMessage : messages)
                {

                    m = p.matcher(currentMessage.getMessageBody());
                    if(m.find()){
                        Log.i(LOG_TAG, "match!");
                        Log.i(LOG_TAG, "" + m.group(0));

                        if(handler != null) {
                            Message msg = handler.obtainMessage();
                            Bundle sendMsg = new Bundle();
                            sendMsg.putString("receiveNum", m.group(0));
                            msg.setData(sendMsg);
                            handler.sendMessage(msg);
                        }

                    }
                    sb = sb + "문자열 수신되었습니다.\n";
                    sb = sb + "[발신자전화번호].\n";
                    sb = sb + currentMessage.getOriginatingAddress();
                    sb = sb + "\n[수신메세지]\n";
                    sb = sb + currentMessage.getMessageBody(); //   [KeepOnGoing]인증번호는 9449입니다.
                }

                Log.i(LOG_TAG, sb);
            }
        }
    }
}
