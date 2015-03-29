package com.iotbyte.wifipidgin.channel;


import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yefwen@iotbyte.com on 26/03/15.
 */

public class Channel {
    private String channelName;
    private String channelDescription;
    private String channelIdentifier;
    private ArrayList<Friends> channelFriendsList;
    public static final String CHANNEL_TAG = "Channel Class";

    public Channel(ArrayList<Friends> friendsList, String channelName, String description) {
        this.channelName = channelName;
        this.channelDescription = description;
        this.channelFriendsList = friendsList;

        // The channelIdentifier is sha1 of creating device (wifi) MAC concatenate with channel
        // creation timestamp


        // Get Device MAC Address

        //The MAC address feature will work on a real device but not on emulator
        //As the current emulator does not support WiFi
        String address = com.iotbyte.wifipidgin.utils.Utils.getMACAddress("wlan0");
        Log.v(CHANNEL_TAG,"the MAC address is "+ address);


        // Get Timestamp
        Date mDate = new Date();
        Timestamp mTimestamp = new Timestamp(mDate.getTime());
        String identifier = address + mTimestamp.toString();
        try {
            this.channelIdentifier = com.iotbyte.wifipidgin.utils.Utils.sha1(identifier);
        } catch (NoSuchAlgorithmException e) {
            this.channelIdentifier = identifier;
        }
        Log.v(CHANNEL_TAG,"the channelIdentifier is "+ this.channelIdentifier);
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    public ArrayList<Friends> getChannelFriendsList() {
        return channelFriendsList;
    }

    //TODO: implement the getChannelStatus
    public boolean getChannelStatus() {
        return false;
    }

}


//TODO: Remove mock Friends Class

class Friends {
    Friends() {

    }
}