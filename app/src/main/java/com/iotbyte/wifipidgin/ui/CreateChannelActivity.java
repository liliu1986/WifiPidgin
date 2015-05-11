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
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.friend.Friend;

import java.util.ArrayList;


public class CreateChannelActivity extends Activity {

    final String CREATE_CHANNEL_ACT = "Create Channel Activity";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);

        // populate list view
        ArrayAdapter<Friend> aa = (new ArrayAdapter<Friend>(this, android.R.layout.simple_list_item_multiple_choice, DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null).findAll()));
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(aa);

        // listen to button click to create channel
        Button buttonCreateChannel = (Button) findViewById(R.id.buttonCreateChannel);
        buttonCreateChannel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(CREATE_CHANNEL_ACT, "create channel confirm button clicked");

                // To Do: Add code to create channel

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