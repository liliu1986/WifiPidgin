package com.iotbyte.wifipidgin.channel;


import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

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
    private ArrayList<Friend> channelFriendList;
    private long id;   // id is added as the primary key to be used in data persist
    public static final String CHANNEL_TAG = "Channel Class";

    public Channel(ArrayList<Friend> friendList, String channelName, String description) {
        this.channelName = channelName;
        this.channelDescription = description;
        this.channelFriendList = friendList;
        this.id = -1;

        // The channelIdentifier is sha1 of creating device (wifi) MAC concatenate with channel
        // creation timestamp


        // Get Device MAC Address

        //The MAC address feature will work on a real device but not on emulator
        //As the current emulator does not support WiFi
        String address = com.iotbyte.wifipidgin.utils.Utils.getMACAddress("wlan0");
        Log.v(CHANNEL_TAG, "the MAC address is " + address);


        // Get Timestamp
        Date mDate = new Date();
        Timestamp mTimestamp = new Timestamp(mDate.getTime());
        String identifier = address + mTimestamp.toString();
        try {
            this.channelIdentifier = com.iotbyte.wifipidgin.utils.Utils.sha1(identifier);
        } catch (NoSuchAlgorithmException e) {
            this.channelIdentifier = identifier;
        }
        Log.v(CHANNEL_TAG, "the channelIdentifier is " + this.channelIdentifier);
    }

    /**
     * Getter method of channelName
     * @return channelName
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Setter method of channelName
     * @param channelName
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * Getter method of channelDescription
     * @return channelDescription
     */
    public String getChannelDescription() {
        return channelDescription;
    }

    /**
     * Setter method of channelDescription
     * @param channelDescription
     */
    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    /**
     * Getter method of channelIdentifier
     * @return channelIdentifier
     */
    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    /**
     * Getter method of channelFriendsList
     * @return channelFriendList
     */
    public ArrayList<Friend> getChannelFriendsList() {
        return channelFriendList;
    }

    /**
     * Getter method of id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter method of id
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }
}

