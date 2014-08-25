package com.secsm.keepongoing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.secsm.keepongoing.Shared.KogPreference;

import java.util.ArrayList;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String LOG_TAG = "GCM Intent Service";
    private static final int ROOM_INVITE = 0;
    private static final int FRIEND_INVITE = 1;
    private static final int CHAT_MESSAGE_CHAT = 2;
    private static final int CHAT_MESSAGE_IMAGE = 3;
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle

//          * Filter messages based on message type. Since it is likely that GCM
//          * will be extended in the future with new message types, just ignore
//          * any message types you're not interested in, or that you don't
//          * recognize.

            Log.e("GCM", "GCMIntentService");
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String msg = intent.getStringExtra("message");
                int _messageType = intent.getIntExtra("messageType", -1);
                // room invite
                // friend invite
                // chat message - message
                // chat message - image
                Log.i("GcmIntentService.java | onHandleIntent", "Received: " + extras.toString());
                switch(_messageType)
                {
                    case ROOM_INVITE:
                        pushRoomInvite(GcmIntentService.this, intent);
                        break;
                    case FRIEND_INVITE:
                        pushFriendInvite(GcmIntentService.this, intent);
                        break;
                    case CHAT_MESSAGE_CHAT:
                        pushChatMessage(GcmIntentService.this, intent);
                        break;
                    case CHAT_MESSAGE_IMAGE:
                        break;
                    default:
                        Log.e(LOG_TAG, "GCM message type error");
                        break;
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    String senderName;
    String message;
    String roomID;

    private void pushRoomInvite(Context context, Intent intent) {
        ArrayList<String> roomInvites = KogPreference.getStringArrayPref(context, "ROOM_INVITES");
        String inviteRoomID = intent.getExtras().getString("roomID");
        String inviteRoomName = intent.getExtras().getString("roomName");
        String inviteRoomMessage = intent.getExtras().getString("message");
//        roomInvites.add();

    }

    private void pushFriendInvite(Context context, Intent intent) {
    }

    private void pushChatMessage(Context context, Intent intent) {

        Log.e("C2DM", "handle");
        PushWakeLock.acquireCpuWakeLock(context);
        Vibrator mVib = (Vibrator) context
                .getSystemService(context.VIBRATOR_SERVICE);
        mVib.vibrate(500);

        senderName = intent.getExtras().getString("senderID");
        roomID = intent.getExtras().getString("roomID");

        message = intent.getExtras().getString("message");
        Intent a = new Intent(context, PushWakeKogDialog.class);
        Bundle b2 = new Bundle();
        b2.putString("senderID", senderName);
        b2.putString("roomID", roomID);
        b2.putString("message", message);
        a.putExtras(b2);

        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(a);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500});

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}