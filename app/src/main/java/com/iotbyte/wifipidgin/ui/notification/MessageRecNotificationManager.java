package com.iotbyte.wifipidgin.ui.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChatMessage;
import com.iotbyte.wifipidgin.ui.ChatActivity;

/**
 * Created by fire on 05/05/15.
 */
public class MessageRecNotificationManager {

    private static MessageRecNotificationManager instance = null;
    private MessageRecNotificationManager(){
    }
    public static MessageRecNotificationManager getInstance() {
        if (instance == null){
            synchronized (MessageRecNotificationManager.class) {
                if (instance == null) {
                    instance = new MessageRecNotificationManager();
                }
            }
        }
        return instance;
    }



    public void sendChatMessageNorification(ChatMessage chatMessage, Context mContext){

        ChannelDao cd = DaoFactory.getInstance().getChannelDao(mContext, DaoFactory.DaoType.SQLITE_DAO, null);
        FriendDao fd = DaoFactory.getInstance().getFriendDao(mContext, DaoFactory.DaoType.SQLITE_DAO, null);

        Friend sender = fd.findByMacAddress(chatMessage.getSender().getMac());
        Channel channel = cd.findByChannelIdentifier(chatMessage.getChannelIdentifier());

        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */

        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("ChannelId", channel.getChannelIdentifier());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        /**
         * Use NotificationCompat.Builder to set up our notification.
         */

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.ic_launcher);
        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));

        builder.setContentTitle(sender.getName() + " says: ");
        builder.setContentText(chatMessage.getMessageBody());
        builder.setSubText(channel.getName());

        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(
                mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
    public void sendChannelCreationNorification(ChatMessage chatMessage, Context mContext){

    }
    public static final int NOTIFICATION_ID = 1;
}
