package com.iotbyte.wifipidgin.ui.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.chat.ChatManager;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChannelCreationRequest;
import com.iotbyte.wifipidgin.message.ChannelCreationResponse;

import org.json.JSONException;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to let user accept a channel invite.
 */
public class AcceptChannelInviteActivity extends Activity {
    private final String ACCEPT_CHANNEL_ACTIVITY = "Create Channel Activity";
    private final Context context = this;
    private ChannelCreationRequest request = null;
    private Channel channel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_channel);

        final String json = getIntent().getStringExtra("channelCreationRequestJson");

        try {
            request = new ChannelCreationRequest(json, context);
        } catch (UnknownHostException | JSONException e) {
            e.printStackTrace();
        }
        assert request != null;

        channel = request.getChannel();
        TextView channelInfo = (TextView) findViewById(R.id.channelRequestInfo);
        String channelInfoText = "Channel Invitation: " + channel.getName() + "\n" +
                                 "Following friends are in the channel:\n";
        for (Friend f : channel.getFriendsList()) {
            channelInfoText += (f.getName() + "\n");
        }
        channelInfo.setText(channelInfoText);

        Button acceptButton = (Button) findViewById(R.id.buttonAcceptChannelRequest);
        Button refuseButton = (Button) findViewById(R.id.buttonRefuseChannelRequest);

        // listen to button click to create channel
        acceptButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(ACCEPT_CHANNEL_ACTIVITY, "Accepted channel invite from channel: "
                                                + channel.getName());

                // if choose to join the channel, user will add himself into the friend list of the
                // channel then this channel into his channelManager.
                // and send out a channelCreationResponse back to the creator.
                FriendDao fd = DaoFactory.getInstance()
                        .getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
                channel.addFriend(fd.findById(Friend.SELF_ID));

                // send a ChannelCreationResponse back
                ChannelCreationResponse channelCreationResponse =
                        new ChannelCreationResponse(channel.getChannelIdentifier(),
                                                    request.getSender(),
                                                    context);

                // if the channel is created successfully, send a response
                if (ChannelManager.getInstance(context).addChannel(channel)) {
                    ChatManager mgr = ChatManager.getInstance();
                    mgr.enqueueOutGoingMessageQueue(channelCreationResponse.convertMessageToJson());
                }
                finish();
            }
        });

        // listen to button click to cancel and go back to the main page
        refuseButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            Log.d(ACCEPT_CHANNEL_ACTIVITY, "Refused channel invite from channel: "
                                         + channel.getName());
            finish();
            }
        });
    }
}
