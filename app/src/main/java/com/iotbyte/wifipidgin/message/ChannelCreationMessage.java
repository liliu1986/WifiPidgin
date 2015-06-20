package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;

/**
 * Created by yefwen@iotbyte.com on 18/06/15.
 *
 * ChannelCreationMessage is used when initiate the Channel creation
 *
 * it should include the information of all members of the channel. The friends' information should
 * include MAC, IP, PORT, NAME and DESCRIPTION
 *
 * Other fields: channelIdentifier, channel's name, channel's description.
 */
public class ChannelCreationMessage extends Message {

    private final String channelIdentifier;
    private String channelName;
    private String channelDescription;

    final String MESSAGE_CHANNEL_IDENTIFIER = "channelidentifier";
    final String MESSAGE_CHANNEL_NAME = "channel name";
    final String MESSAGE_CHANNEL_DESCRIPTION = "channel description";


    public ChannelCreationMessage (String jsonMessageData)throws JSONException, UnknownHostException {
        super(jsonMessageData);
        

    }


    /**
     * Constructor for ChannelCreationMessage class, it create a new message object based
     * on input from this client itself. This message object will be send to
     * others
     *
     * @param channel the channel created by myself to be broadcast to all members of channel
     * @param receiver a specific member to receive the ChannelCreationMessage
     * @param context a context
     */
    public ChannelCreationMessage(Channel channel,Friend receiver,Context context){
        super(receiver,context);
        this.channelIdentifier = channel.getChannelIdentifier();
        this.channelName = channel.getName();
        this.channelDescription = channel.getDescription();
       // this.type = MessageType.CHANNEL_CREATION_MESSAGE;
    }





    @Override
    public String convertMessageToJson() {

        try {
            JSONObject json = new JSONObject();

            json.put(MESSAGE_TYPE,this.type.toString());

            JSONObject sender = new JSONObject();
            sender.put(MESSAGE_NAME,this.sender.getName());
            sender.put(MESSAGE_DESCRIPTION,this.sender.getDescription());
            sender.put(MESSAGE_IP, ipFormatter(this.sender.getIp().toString())); // InetAdress.toString() returns in host/ip as format
            sender.put(MESSAGE_PORT,this.sender.getPort());
            sender.put(MESSAGE_MAC,macAddressByteToHexString(this.sender.getMac()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_NAME,this.receiver.getName());
            receiver.put(MESSAGE_DESCRIPTION,this.receiver.getDescription());
            receiver.put(MESSAGE_IP, ipFormatter(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT,this.receiver.getPort());
            receiver.put(MESSAGE_MAC,macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER,sender);
            json.put(MESSAGE_RECEIVER,receiver);
            json.put(MESSAGE_TIMESTAMP,this.timestamp.toString()); //check this out
            json.put(MESSAGE_CHANNEL_IDENTIFIER,this.channelIdentifier);

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}
