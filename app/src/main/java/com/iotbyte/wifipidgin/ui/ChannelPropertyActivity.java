package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.friend.Friend;


public class ChannelPropertyActivity extends Activity {

    final String CHANNEL_PROPERTY_ACT = "Channel Property Act";
    final Context context = this;
    ListView lv;
    ChannelManager channelManager;
    ArrayAdapter<Friend> adapter;
    String ChannelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_property);

        ChannelId = (String) getIntent().getExtras().get("ChannelId");
        channelManager = ChannelManager.getInstance(context);

        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);

        lv = (ListView) findViewById(android.R.id.list);

        // populate the list view
        adapter = (new ArrayAdapter<>(lv.getContext(), android.R.layout.simple_list_item_1, channelManager.getChannelByIdentifier(ChannelId).getFriendsList()));
        lv.setAdapter(adapter);

        // listen to button click to cancel and go back to the main page
        buttonCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(CHANNEL_PROPERTY_ACT, "cancel button clicked");
                finish();
            }
        });
    }
}