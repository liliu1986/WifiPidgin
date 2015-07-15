package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.iotbyte.wifipidgin.utils.Utils.macAddressHexStringToByte;
import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;

/**
 * Created by yefwen@iotbyte.com on 18/06/15.
 * <p/>
 * ChannelCreationRequest is used when initiate the Channel creation
 * <p/>
 * it should include the information of all members of the channel. The friends' information should
 * include MAC, IP, PORT, NAME and DESCRIPTION
 * <p/>
 * Other fields: channelIdentifier, channel's name, channel's description.
 */
public class ChannelCreationRequest extends Message {

    private final String channelIdentifier;
    private Channel channel;
    private String channelName;
    private String channelDescription;

    final String MESSAGE_CHANNEL_IDENTIFIER = "channelidentifier";
    final String MESSAGE_CHANNEL_NAME = "channel name";
    final String MESSAGE_CHANNEL_DESCRIPTION = "channel description";

    final String FRIEND_INFO_NAME = "friend name";
    final String FRIEND_INFO_IP = "friend ip";
    final String FRIEND_INFO_MAC = "friend mac";
    final String FRIEND_INFO_PORT = "friend port";
    final String FRIEND_INFO_DESCRIPTION = "friend description";
    final String FRIEND_LIST = "friend list";


    /**
     * Constructor for ChannelCreationRequest class, it create a new message based on
     * information received from com-module. It will parse the json format
     * into a message object
     *
     * @param jsonMessageData is the received json format string
     * @param context
     */

    public ChannelCreationRequest(String jsonMessageData, Context context) throws JSONException, UnknownHostException {
        super(jsonMessageData,context);
        JSONObject json = new JSONObject(jsonMessageData);
        this.channelName = json.getString(MESSAGE_CHANNEL_NAME);
        this.channelDescription = json.getString(MESSAGE_CHANNEL_DESCRIPTION);
        this.channelIdentifier = json.getString(MESSAGE_CHANNEL_IDENTIFIER);
        JSONArray jsonArray = json.getJSONArray(FRIEND_LIST);
        this.channel = new Channel(this.channelIdentifier);
        this.channel.setName(channelName);
        this.channel.setDescription(channelDescription);
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject friendJson = (JSONObject) jsonArray.get(i);
            Friend friend = new Friend(macAddressHexStringToByte(friendJson.getString(FRIEND_INFO_MAC)),
                    InetAddress.getByName(ipFormatter(friendJson.getString(FRIEND_INFO_IP))),
                    friendJson.getInt(FRIEND_INFO_PORT));
            friend.setName(friendJson.getString(FRIEND_INFO_NAME));
            friend.setDescription(friendJson.getString(FRIEND_INFO_DESCRIPTION));
            this.channel.addFriend(friend);
        }
        this.type = MessageType.CHANNEL_CREATION_REQUEST;


    }


    /**
     * Constructor for ChannelCreationRequest class, it create a new message object based
     * on input from this client itself. This message object will be send to
     * others
     *
     * @param channel  the channel created by myself to be broadcast to all members of channel
     * @param receiver a specific member to receive the ChannelCreationRequest
     * @param context  a context
     */
    public ChannelCreationRequest(Channel channel, Friend receiver, Context context) {
        super(receiver, context);
        this.channel = channel;
        this.channelIdentifier = channel.getChannelIdentifier();
        this.channelName = channel.getName();
        this.channelDescription = channel.getDescription();
        this.type = MessageType.CHANNEL_CREATION_REQUEST;
    }


    @Override
    public String convertMessageToJson() {

        try {
            JSONObject json = new JSONObject();

            json.put(MESSAGE_TYPE, this.type.toString());

            JSONObject sender = new JSONObject();
            sender.put(MESSAGE_NAME, this.sender.getName());
            sender.put(MESSAGE_DESCRIPTION, this.sender.getDescription());
            sender.put(MESSAGE_IP, ipFormatter(this.sender.getIp().toString())); // InetAdress.toString() returns in host/ip as format
            sender.put(MESSAGE_PORT, this.sender.getPort());
            sender.put(MESSAGE_MAC, macAddressByteToHexString(this.sender.getMac()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_NAME, this.receiver.getName());
            receiver.put(MESSAGE_DESCRIPTION, this.receiver.getDescription());
            receiver.put(MESSAGE_IP, ipFormatter(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT, this.receiver.getPort());
            receiver.put(MESSAGE_MAC, macAddressByteToHexString(this.receiver.getMac()));


            JSONArray friendList = new JSONArray();

            for (Friend friend : this.channel.getFriendsList()) {
                JSONObject friendInfo = new JSONObject();
                friendInfo.put(FRIEND_INFO_NAME, friend.getName());
                friendInfo.put(FRIEND_INFO_IP, ipFormatter(friend.getIp().toString()));
                friendInfo.put(FRIEND_INFO_PORT, friend.getPort());
                friendInfo.put(FRIEND_INFO_MAC, macAddressByteToHexString(friend.getMac()));
                friendInfo.put(FRIEND_INFO_DESCRIPTION, friend.getDescription());
                friendList.put(friendInfo);
            }

            json.put(MESSAGE_SENDER, sender);
            json.put(MESSAGE_RECEIVER, receiver);
            json.put(MESSAGE_TIMESTAMP, this.timestamp.toString()); //check this out
            json.put(MESSAGE_CHANNEL_IDENTIFIER, this.channelIdentifier);
            json.put(MESSAGE_CHANNEL_NAME, this.channelName);
            json.put(MESSAGE_CHANNEL_DESCRIPTION, this.channelDescription);
            json.put(FRIEND_LIST, friendList);

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * getChannel()
     * returns the channel object to be recovered by this ChannelCreationRequest
     *
     * @return a channel which was described in this ChannelCreationRequest
     */
    public Channel getChannel() {
        return channel;
    }
}
