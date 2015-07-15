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
import com.iotbyte.wifipidgin.message.ChannelCreationRequest;
import com.iotbyte.wifipidgin.message.ChatMessage;
import com.iotbyte.wifipidgin.ui.ChatActivity;

/**
 * Helper methods to send appropriate notification to UI
 */
public class UiNotificationHelper {

    /**
     * Send UI notification for a received chat message.
     *
     * Expect sender and channel to be already known.
     *
     * @param message chat message received.
     * @param context application context
     */
    static public void notifyChatMessage(ChatMessage message, Context context) {
        ChannelDao cd = DaoFactory.getInstance()
                                  .getChannelDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
        Channel channel = cd.findByChannelIdentifier(message.getChannelIdentifier());
        assert channel != null;

        FriendDao fd = DaoFactory.getInstance()
                                 .getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
        Friend sender = fd.findByMacAddress(message.getSender().getMac());
        assert sender != null;

        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("ChannelId", channel.getChannelIdentifier());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent)
               .setAutoCancel(true)
               .setSmallIcon(R.drawable.ic_launcher)
               .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                       R.drawable.ic_launcher))
               .setContentTitle(sender.getName() + " says: ")
               .setContentText(message.getMessageBody())
               .setSubText(channel.getName());

        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_RECEIVED_CHAT_MESSAGE, builder.build());
    }

    static public void notifyChannelCreationRequest(ChannelCreationRequest message,
                                                    Context context) {
        Channel channel = message.getChannel();
        String friendNames = "";
        for (Friend f : channel.getFriendsList()) {
            friendNames += f.getName() + ", ";
        }

        Intent intent = new Intent(context, AcceptChannelInviteActivity.class);
        intent.putExtra("channelCreationRequestJson", message.convertMessageToJson());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent)
               .setAutoCancel(true)
               .setSmallIcon(R.drawable.ic_launcher)
               .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                       R.drawable.ic_launcher))
               .setContentTitle("Channel '" + channel.getName() + "' invited you to join.")
               .setContentText("Channel has: " + friendNames);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_RECEIVED_CHANNEL_CREATION_REQUEST, builder.build());
    }

    private static final int NOTIFY_RECEIVED_CHAT_MESSAGE = 1;
    private static final int NOTIFY_RECEIVED_CHANNEL_CREATION_REQUEST = 2;
}
