package com.iotbyte.wifipidgin.channel;


import android.content.Context;

import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.friend.Friend;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ChannelManager is a Singleton class to manage the all existing Channels.
 * Any channel will  become available to user if it is managed under
 * ChannelManager
 * <p/>
 * <p/>
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class ChannelManager {
    private static ChannelManager instance = null;

    private Context context;

    private HashMap<String, Channel> channelMap; // channelIdentifier and Channel pair
    //FIXME:: remove UnknowHostExceptions after remove mock code
    private ChannelManager(Context context) throws UnknownHostException{
        this.context = context;

        //Calling database to grep existing channelList
        // or create a new channelList if there is nothing been retrieved

        //TODO: remove the mocked channelList
        //TODO: Adding channelList retrieval functionality from database

        InetAddress xiaoMingIP = InetAddress.getByName("192.168.1.2");
        byte[] xiaoMingMac = {0x5,0xc,0x0,0xa,0x5,0xb,0x4,0x8,0x4,0x5,0x4,0x6};
        int xiaoMingPort = 55;
        Friend xiaoMing = new Friend(xiaoMingMac,xiaoMingIP,xiaoMingPort);
        xiaoMing.setDescription("wo shi huang xiao ming");
        xiaoMing.setName("HXM");


        InetAddress xiaoPangIP = InetAddress.getByName("192.168.1.179");
        byte[] xiaoPangMac = {0x5,0xc,0x0,0xa,0x5,0xb,0xa,0xa,0xc,0xa,0xc,0x5};
        int xiaoPangPort = 55;
        Friend xiaoPang = new Friend(xiaoPangMac,xiaoPangIP,xiaoPangPort);
        xiaoPang.setDescription("wo shi xiao pang");
        xiaoPang.setName("stackHeap");

        List<Friend> mockList = new ArrayList<>();
        mockList.add(xiaoMing);
        mockList.add(xiaoPang);
        Channel mockChannel = new Channel(mockList,"xiao channel","heiheihei");
        //save mocked channels;
        ChannelDao cd = DaoFactory.getInstance().getChannelDao(context,DaoFactory.DaoType.SQLITE_DAO,null);
        cd.add(mockChannel);


        //below is not mocked!!!!
        channelMap = new HashMap<>();


        List<Channel> channelList =
            DaoFactory.getInstance().getChannelDao(context, DaoFactory.DaoType.SQLITE_DAO, null).findAll();

        for (Channel aChannel:channelList){
            channelMap.put(aChannel.getChannelIdentifier(),aChannel);
        }

    }
    //FixME: remove unknowHostException
    public static ChannelManager getInstance(Context context) throws UnknownHostException{
        if (instance == null) {
            //Thread Safe with synchronized block
            synchronized (ChannelManager.class) {
                if (instance == null) {
                    instance = new ChannelManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * saveChannelsInfoIntoDatabase()
     * <p/>
     * It will save all information back to the database, this should be called
     * properly to retain all channel information
     *
     * @return true if all channel information is saved successfully, false otherwise.
     */

    //TODO: adding implementation to add all channels back to database
    public boolean saveChannelsInfoIntoDatabase() {
        return true;
    }

    /**
     * getChannelStatus()
     * <p/>
     * it returns the Status of a channel with it's given channelIdentifier
     *
     * @param channelIdentifier the channelIdentifier to be lookup
     * @return ChannelStatus.ONLINE if the channel is active, ChannelStatus.OFFLINE if the channel
     * is not active
     */

    //TODO: implement the getChannelStatus
    public ChannelStatus getChannelStatus(String channelIdentifier) {
        return ChannelStatus.OFFLINE;
    }

    /**
     * getChannelByIdentifier()
     * <p/>
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
     * addChannel()
     * <p/>
     * Add a channel to the ChannelManager
     * Require update the UI when return true
     *
     * @param channel the channel to be added
     * @return true for add successfully or false for channel already exist
     */
    public boolean addChannel(Channel channel) {
        if (channelMap.containsKey(channel)) {
            return false;
        } else {
            channelMap.put(channel.getChannelIdentifier(), channel);
            return true;
        }
    }

    /**
     * deleteChannel()
     * <p/>
     * Remove a channel from ChannelManager
     * Require update the UI when return true
     *
     * @param channel the channel to be deleted
     * @return true for successfully delete or false for channel does not exist previously.
     */
    public boolean deleteChannel(Channel channel) {
        if (!channelMap.containsKey(channel)) {
            return false;
        } else {
            channelMap.remove(channel.getChannelIdentifier());
            return true;
        }
    }

    /**
     * getChannelList()
     * <p/>
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
