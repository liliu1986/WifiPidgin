package com.iotbyte.wifipidgin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.channel.ChannelDatabaseChangeListener;
import com.iotbyte.wifipidgin.channel.ChannelManager;

public class ChannelTabFragment extends Fragment {

    final String CHANNEL_TAB_FRAG = "Channel Tab Fragment";

    Intent i;
    ChannelManager channelManager;
    ArrayAdapter<Channel> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        channelManager = ChannelManager.getInstance(getActivity());

        channelManager.setChannelDatabaseChangeListener(new ChannelDatabaseChangeListener() {
            @Override
            public void onChannelDatabaseChange() {
                Log.d(CHANNEL_TAB_FRAG, "Channel Updated");
                refreshChannelList();
            }
        });

        View v = inflater.inflate(R.layout.list_channels, container, false);

        ListView lv = (ListView) v.findViewById(android.R.id.list);
        Button button = (Button) v.findViewById(R.id.buttonCreateChannel);

        // populate the list view
        adapter = (new ArrayAdapter<>(lv.getContext(), android.R.layout.simple_list_item_1, channelManager.getChannelList()));
        lv.setAdapter(adapter);

        // listen to item click to enter a channel
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(CHANNEL_TAB_FRAG, "channel clicked");
            }
        });

        // listen to long click to enter delete channel page
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(CHANNEL_TAB_FRAG, "channel long clicked");
                i = new Intent(getActivity(), DeleteChannelActivity.class);
                startActivity(i);
                return false;
            }
        });

        // listen to button click to enter create channel page
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(CHANNEL_TAB_FRAG, "create channel button clicked");
                i = new Intent(getActivity(), CreateChannelActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    public static ChannelTabFragment newInstance() {

        ChannelTabFragment f = new ChannelTabFragment();
        Bundle b = new Bundle();

        f.setArguments(b);

        return f;
    }

    public void refreshChannelList() {
        adapter.clear();
        adapter.addAll(channelManager.getChannelList());
        adapter.notifyDataSetChanged();
    }
}