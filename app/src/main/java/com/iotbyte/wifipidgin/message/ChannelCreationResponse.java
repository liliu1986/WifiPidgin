package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;

/**
 * Created by yefwen@iotbyte.com on 25/06/15.
 */
public class ChannelCreationResponse extends Message {


    private final String channelIdentifier;
    final String MESSAGE_CHANNEL_IDENTIFIER = "channelidentifier";

    /**
     * Constructor for ChannelCreationResponse class, it create a new message based on
     * information received from com-module. It will parse the json format
     * into a message object
     *
     * @param jsonMessageData is the received json format string
     * @param context
     */

    public ChannelCreationResponse(String jsonMessageData, Context context) throws JSONException, UnknownHostException {
        super(jsonMessageData, context);
        JSONObject json = new JSONObject(jsonMessageData);
        this.channelIdentifier = json.getString(MESSAGE_CHANNEL_IDENTIFIER);
        this.type = MessageType.CHANNEL_CREATION_RESPONSE;
    }


    public ChannelCreationResponse(String channelIdentifier, Friend receiver, Context context) {
        super(receiver, context);
        this.channelIdentifier = channelIdentifier;
        this.type = MessageType.CHANNEL_CREATION_RESPONSE;
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

            json.put(MESSAGE_SENDER, sender);
            json.put(MESSAGE_RECEIVER, receiver);
            json.put(MESSAGE_TIMESTAMP, this.timestamp.toString()); //check this out
            json.put(MESSAGE_CHANNEL_IDENTIFIER, this.channelIdentifier);

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getChannelIdentifier() {
        return channelIdentifier;
    }
}
