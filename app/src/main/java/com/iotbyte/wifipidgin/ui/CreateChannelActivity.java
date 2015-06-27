package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.friend.Friend;

import java.util.ArrayList;
import java.util.List;


public class CreateChannelActivity extends Activity {

    final String CREATE_CHANNEL_ACT = "Create Channel Activity";
    final Context context = this;
    ListView lv;
    TextView tv;
    ChannelManager channelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        channelManager = ChannelManager.getInstance(context);

        Button buttonCreateChannel = (Button) findViewById(R.id.buttonCreateChannel);
        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);

        tv = (TextView) findViewById(R.id.enterChannelName);
        // populate list view
        ArrayAdapter<Friend> aa = (new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null).findAll()));
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(aa);

        // listen to button click to create channel
        buttonCreateChannel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                List<Friend> channelFriendList = new ArrayList<>();
                String channelName;
                Friend currentFriend;

                Log.d(CREATE_CHANNEL_ACT, "create channel confirm button clicked");

                SparseBooleanArray selectedFriends = lv.getCheckedItemPositions();

                if (selectedFriends.size() != 0) {
                    for (int i = 0; i < selectedFriends.size(); i++) {
                        if (selectedFriends.valueAt(i)) {
                            currentFriend = (Friend) lv.getAdapter().getItem(selectedFriends.keyAt(i));
                            channelFriendList.add(currentFriend);
                            Log.d(CREATE_CHANNEL_ACT, currentFriend.toString() + " was selected");
                        }
                    }
                }

                channelName = tv.getText().toString();
                Log.d(CREATE_CHANNEL_ACT, "Channel Name: " + channelName);

                Channel newChannel = new Channel(context,channelFriendList, channelName, channelName);
                //save new channel;
                if (!channelManager.addChannel(newChannel)) {
                    Log.e(CREATE_CHANNEL_ACT, "Error occurred during creating Channel" + newChannel.toString());
                }

                finish();
            }
        });

        // listen to button click to cancel and go back to the main page
        buttonCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(CREATE_CHANNEL_ACT, "cancel button clicked");
                finish();
            }
        });
    }
}