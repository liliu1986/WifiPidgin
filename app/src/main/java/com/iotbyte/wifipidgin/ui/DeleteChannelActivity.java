package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoFactory;


public class DeleteChannelActivity extends Activity {

    final String DELETE_CHANNEL_ACT = "Delete Channel Activity";
    final Context context = this;
    ListView lv;
    ChannelManager channelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_channel);

        channelManager = ChannelManager.getInstance(context);

        Button buttonDeleteChannel = (Button) findViewById(R.id.buttonDeleteChannel);
        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);

        // populate list view

        ArrayAdapter<Channel> aa = (new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, channelManager.getChannelList()));
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(aa);


        // listen to button click to delete channel
        buttonDeleteChannel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(DELETE_CHANNEL_ACT, "delete channel confirm button clicked");

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are You Sure?");
                // Add the buttons
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Channel currentChannel;
                        SparseBooleanArray selectedChannels = lv.getCheckedItemPositions();
                        ChannelDao cd = DaoFactory.getInstance().getChannelDao(context, DaoFactory.DaoType.SQLITE_DAO, null);

                        if (selectedChannels.size() != 0) {
                            for (int i = 0; i < selectedChannels.size(); i++) {
                                if (selectedChannels.valueAt(i)) {
                                    currentChannel = (Channel) lv.getAdapter().getItem(selectedChannels.keyAt(i));
                                    Log.d(DELETE_CHANNEL_ACT, currentChannel.toString() + " was selected");
                                    channelManager.deleteChannel(currentChannel);
                                }
                            }
                        }

                        finish();
                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                // show it
                dialog.show();
            }
        });

        // listen to button click to cancel and go back to main page
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(DELETE_CHANNEL_ACT, "cancel button clicked");
                finish();
            }
        });
    }
}
