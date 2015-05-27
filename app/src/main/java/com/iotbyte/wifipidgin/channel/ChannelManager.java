package com.iotbyte.wifipidgin.channel;


import android.content.Context;

import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.dao.event.DaoEvent;
import com.iotbyte.wifipidgin.dao.event.DaoEventSubscriber;
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

    final String CHANNEL_MANAGER_DEBUG = "Channel Manager Debug";

    private Context context;

    private HashMap<String, Channel> channelMap; // channelIdentifier and Channel pair

    private ChannelDatabaseChangeListener channelDatabaseChangeListener;

    private ChannelManager(Context context) {
        this.context = context;

        ChannelDao channelDao  = DaoFactory.getInstance().getChannelDao(this.context,DaoFactory.DaoType.SQLITE_DAO, null);

        /* In the event that some where else update the Channel Information in database, update the Channel Information
        from database, and notify the UI to update (notify UI require UI side to registrant this listener */
        channelDao.getDaoEventBoard().registerEventSubscriber(new DaoEventSubscriber() {
            @Override
            public void onEvent(DaoEvent event) {
                updateChannelInfoFromDatabase();
                if (null != channelDatabaseChangeListener) {
                    channelDatabaseChangeListener.onChannelDatabaseChange();
                }
            }
        });


        //Calling database to grep existing channelList
        // or create a new channelList if there is nothing been retrieved
        channelMap = new HashMap<>();

        updateChannelInfoFromDatabase();
        //TODO: remove the mocked channelList
        mockChannelInfo();


    }

    public static ChannelManager getInstance(Context context) {
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

    /**
     * updateChannelInfoFromDatabase()
     * <p/>
     * Update the channel information by retrieves all Channels from database
     *
     * @return true if success, false otherwise
     */

    public boolean updateChannelInfoFromDatabase() {
        ChannelDao cd = DaoFactory.getInstance().getChannelDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
        if (null == cd) return false;
        List<Channel> channelList = cd.findAll();


        for (Channel aChannel : channelList) {
            channelMap.put(aChannel.getChannelIdentifier(), aChannel);
        }

        return true;
    }

    /**
     * saveChannelInfoToDataBase()
     * <p/>
     * save all existing channel information back to the database. Use as front-end activity life cycle
     * management API
     *
     * @return true if success, false otherwise
     */

    //TODO: where friend information should be updated? I mean the ip changes
    public boolean saveChannelInfoToDataBase() {

        ChannelDao cd = DaoFactory.getInstance().getChannelDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
        FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);

        if (null == cd || null == fd) {
            return false;
        }

        List<Channel> channels = getChannelList();

        //TODO:: require optimization, as O^2 is not acceptable!
        for (Channel channel : channels) {
            for (Friend friend : channel.getFriendsList()) {
                if (friend.NO_ID == friend.getId()) {
                    fd.add(friend);
                } else {
                    fd.update(friend); //TODO:: might need to change this depends on how to handles friend update
                }
            }
            if (channel.NO_ID == channel.getId()) {
                cd.add(channel);
            } else {
                cd.update(channel);   //TODO:: might need to change this depends on how to handles channel update
            }
        }
        return true;
    }

    /**
     * mockChannelInfo()
     * <p/>
     * some mocked channel Info
     */
    private void mockChannelInfo() {
        FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
        if (1 == fd.findAll().size()) {

            try {
                InetAddress xiaoMingIP = InetAddress.getByName("192.168.1.2");
                byte[] xiaoMingMac = {0x5, 0xc, 0x0, 0xa, 0x5, 0xb, 0x4, 0x8, 0x4, 0x5, 0x4, 0x6};
                int xiaoMingPort = 55;
                Friend xiaoMing = new Friend(xiaoMingMac, xiaoMingIP, xiaoMingPort);
                xiaoMing.setDescription("wo shi huang xiao ming");
                xiaoMing.setName("HXM");

                InetAddress xiaoPangIP = InetAddress.getByName("192.168.1.179");
                byte[] xiaoPangMac = {0x5, 0xc, 0x0, 0xa, 0x5, 0xb, 0xa, 0xa, 0xc, 0xa, 0xc, 0x5};
                int xiaoPangPort = 55;
                Friend xiaoPang = new Friend(xiaoPangMac, xiaoPangIP, xiaoPangPort);
                xiaoPang.setDescription("wo shi xiao pang");
                xiaoPang.setName("stackHeap");
                List<Friend> mockList = new ArrayList<>();
                mockList.add(xiaoMing);
                mockList.add(xiaoPang);
                String channelName = "xiao channel";
                Channel mockChannel = new Channel(mockList, channelName, "heiheihei");
                //just a work around for mock data:
           /* boolean existFlag = false;
            for(Channel channel : DaoFactory.getInstance().getChannelDao(context, DaoFactory.DaoType.SQLITE_DAO, null).findAll()){
                if (0 == channel.getName().compareTo(channelName)){
                    existFlag = true;
                    break;
                }
            }
            if (!existFlag){
                this.addChannel(mockChannel);
            }*/
                this.addChannel(mockChannel);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }

            saveChannelInfoToDataBase();
        }
    }

    public void setChannelDatabaseChangeListener(ChannelDatabaseChangeListener listener){
        this.channelDatabaseChangeListener = listener;
    }
}
