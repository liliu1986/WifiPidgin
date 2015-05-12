package com.iotbyte.wifipidgin.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.chat.ChatManager;

/**
 * Created by fire on 05/05/15.
 */
public class MessageRecNotification {

    Context mContext;
    public MessageRecNotification(Context context){
        mContext = context;
    }

    public void sendNotification(ChatManager inMessage) {

        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://iotbyte.com/wiki"));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.ic_launcher);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));


        builder.setContentTitle("A message has been received in WiFiPidgin");
        //builder.setContentText(inMessagegetSender().getName()+ " says: ");
        //builder.setSubText(inMessage.getMessageBody());

        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(
                mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    public static final int NOTIFICATION_ID = 1;
}
