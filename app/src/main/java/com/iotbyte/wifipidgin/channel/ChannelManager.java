package com.iotbyte.wifipidgin.channel;


import java.util.ArrayList;

/**
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class ChannelManager {
    private static ChannelManager instance = null;

    private ArrayList<Channel> channelList ;
    protected ChannelManager(){
        //Calling database to grep existing channelList
        // or create a new channelList if there is nothing been retrieved

        //TODO: remove the mocked channelList
        //TODO: Adding channelList retrieval functionality from database
    }

    public static ChannelManager getInstance(){
        if (instance == null){
            instance = new ChannelManager();
        }
        return instance;
    }



}
