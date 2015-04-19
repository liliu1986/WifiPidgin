package com.iotbyte.wifipidgin.channel;


import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Channel Class defines a channel, includes it's name, description, channelIdentifier
 * and a list of friend. id is what been used to track with the database.
 *
 * All channels are managed by ChannelManager. Channel class constructor should be called
 * only by the ChannelManager.addChannel method, due to the weak relationship between Channel
 * and ChannelManager
 *
 * Created by yefwen@iotbyte.com on 26/03/15.
 */

public class Channel {
    private String name;
    private String description;
    private final String channelIdentifier;
    private List<Friend> friendList;
    private int id;   // id is added as the primary key to be used in data persist
    private static final int NO_ID = -1;
    private static final String CHANNEL_TAG = "Channel Class";
    // TODO:: the following string should be move to res/string later
    private static final String DEFAULT_CHANNEL_NAME = "Un-named Channel";
    private static final String DEFAULT_CHANNEL_DESCRIPTION = "Too lazy to leave one";

    /**
     * Default constructor create the required channelIdentifier, set id to NO_ID
     * allocate friendList
     */

    public Channel(){
        this.name = DEFAULT_CHANNEL_NAME;
        this.description = DEFAULT_CHANNEL_DESCRIPTION;
        this.id = NO_ID;
        friendList = new ArrayList<>();
        // The channelIdentifier is sha1 of creating device (wifi) MAC concatenate with channel

        // Get Device MAC Address

        //The MAC address feature will work on a real device but not on emulator
        //As the current emulator does not support WiFi
        String address = com.iotbyte.wifipidgin.utils.Utils.getMACAddress("wlan0");
        Log.v(CHANNEL_TAG, "the MAC address is " + address);


        // Get Timestamp
        Date mDate = new Date();
        Timestamp mTimestamp = new Timestamp(mDate.getTime());
        String identifier = address + mTimestamp.toString();
        String identifierHolder;
        try {
            identifierHolder = com.iotbyte.wifipidgin.utils.Utils.sha1(identifier);
        } catch (NoSuchAlgorithmException e) {
            identifierHolder = identifier;
        }
        this.channelIdentifier = identifierHolder;

        Log.v(CHANNEL_TAG, "the channelIdentifier is " + this.channelIdentifier);
    }

    public Channel(List<Friend> friendList, String name, String description) {
        this();
        this.name = name;
        this.description = description;
        this.friendList = friendList;
    }

    /**
     * Getter method of name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method of name
     *
     * @param name name of the channel
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method of description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method of description
     *
     * @param description description of the channel
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter method of channelIdentifier
     *
     * @return channelIdentifier
     */
    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    /**
     * Getter method of friendsList
     *
     * @return friendList
     */
    public List<Friend> getFriendsList() {
        return friendList;
    }

    /**
     * Getter method of id
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter method of id
     *
     * @param id id of the channel
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Add friend to the list
     *
     * @param friend target friend to be added
     * @return true for successfully added,
     * false for duplicated friend found
     */
    public boolean addFriend(Friend friend) {
        if (friendList.contains(friend)) {
           return false;
        } else
        {
            friendList.add(friend);
            return true;
        }
    }
}

