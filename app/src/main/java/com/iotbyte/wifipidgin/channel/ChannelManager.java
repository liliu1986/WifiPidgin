package com.iotbyte.wifipidgin.channel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class ChannelManager {
    private static ChannelManager instance = null;

    private HashMap<String, Channel> channelMap; // ChannelIdentifier and Channel pair

    protected ChannelManager() {
        //Calling database to grep existing channelList
        // or create a new channelList if there is nothing been retrieved

        //TODO: remove the mocked channelList
        //TODO: Adding channelList retrieval functionality from database
        channelMap = new HashMap<String, Channel>();
    }

    public static ChannelManager getInstance() {
        if (instance == null) {
            instance = new ChannelManager();
        }
        return instance;
    }

    //TODO: implement the getChannelStatus
    public boolean getChannelStatus(String channelIdentifier) {
        return false;
    }

    /**
     * Return the channel object with given channelIdentifier
     * @param channelIdentifier the channelIdentifier of the channel under request
     * @return Return the Channel with the specific channelIdentifier
     */
    public Channel getChannelByIdentifier(String channelIdentifier) {
        if (channelMap.containsKey(channelIdentifier)) {
            return channelMap.get(channelIdentifier);
        } else
            return null;
    }

    /**
     * Add a channel to the ChannelManager
     * @param channel the channel to be added
     */
    public void addChannel(Channel channel) {
        channelMap.put(channel.getChannelIdentifier(), channel);
        // NOTE: update the UI when this method is called
    }

    /**
     * Remove a channel from ChannelManager
     * @param channel the channel to be deleted
     */
    public void deleteChannel(Channel channel) {
        channelMap.remove(channel.getChannelIdentifier());
        // NOTE: update the UI when this method is called
    }

    /**
     * Return a List representation of the channels managed under ChannelManager
     * @return a List representation of the channels, which is backed by ArrayList
     */
    public List<Channel> getChannelList(){
        //TODO: possible adding ordering due to latest change property of channel
        List<Channel> result = new ArrayList<Channel>();
        result.addAll(channelMap.values());
        return result;
    }


}
