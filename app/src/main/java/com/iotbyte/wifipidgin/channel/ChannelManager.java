package com.iotbyte.wifipidgin.channel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**ChannelManager is a Singleton class to manage the all existing Channels.
 * Any channel will  become available to user if it is managed under
 * ChannelManager
 *
 *
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class ChannelManager {
    private static ChannelManager instance = null;

    private HashMap<String, Channel> channelMap; // ChannelIdentifier and Channel pair

    private ChannelManager() {

        //Calling database to grep existing channelList
        // or create a new channelList if there is nothing been retrieved

        //TODO: remove the mocked channelList
        //TODO: Adding channelList retrieval functionality from database
        channelMap = new HashMap<String, Channel>();
    }

    public static ChannelManager getInstance() {
        if (instance == null) {
            //Thread Safe with synchronized block
            synchronized (ChannelManager.class) {
                if (instance == null) {
                    instance = new ChannelManager();
                }
            }
        }
        return instance;
    }

    //TODO: implement the getChannelStatus
    public ChannelStatus getChannelStatus(String channelIdentifier) {
        return ChannelStatus.OFFLINE;
    }

    /**
     * Return the channel object with given channelIdentifier
     *
     * @param channelIdentifier the channelIdentifier of the channel under request
     * @return the Channel with the specific channelIdentifier or null if the
     * channelIdentifier is not found.
     */
    public Channel getChannelByIdentifier(String channelIdentifier) {
        if (channelMap.containsKey(channelIdentifier)) {
            return channelMap.get(channelIdentifier);
        } else
            return null;
    }

    /**
     * Add a channel to the ChannelManager
     * Require update the UI when return true
     *
     * @param channel the channel to be added
     * @return true for add successfully or false for channel already exist
     */
    public boolean addChannel(Channel channel) {
        if (channelMap.containsKey(channel)){
            return false;
        } else{
            channelMap.put(channel.getChannelIdentifier(), channel);
            return true;
        }
    }

    /**
     * Remove a channel from ChannelManager
     * Require update the UI when return true
     *
     * @param channel the channel to be deleted
     * @return true for successfully delete or false for channel does not exist previously.
     *
     */
    public boolean deleteChannel(Channel channel) {
        if (!channelMap.containsKey(channel)){
            return false;
        }else {
            channelMap.remove(channel.getChannelIdentifier());
            return true;
        }
    }

    /**
     * Return a List representation of the channels managed under ChannelManager
     *
     * @return a List representation of the channels, which is backed by ArrayList
     */
    public List<Channel> getChannelList() {
        //TODO: possible adding ordering due to latest change property of channel
        List<Channel> result = new ArrayList<Channel>();
        result.addAll(channelMap.values());
        return result;
    }


}
