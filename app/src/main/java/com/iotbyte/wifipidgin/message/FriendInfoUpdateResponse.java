package com.iotbyte.wifipidgin.message;

import android.content.Context;
import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;

/**
 * Message to respond to remote peer to provide updated friend description
 */
public class FriendInfoUpdateResponse extends Message {

    /**
     * Construct from a description string.
     * @param receiver receiver of this message
     * @param context context
     * @param description friend description as a response.
     */
    public FriendInfoUpdateResponse(Friend receiver, Context context, String description) {
        super(receiver, context);
        this.type = MessageType.FRIEND_INFO_UPDATE_RESPONSE;
        this.description = description;
    }

    public FriendInfoUpdateResponse(String jsonMessageData) throws JSONException, UnknownHostException {
        super(jsonMessageData);
        assert this.type == MessageType.FRIEND_INFO_UPDATE_RESPONSE;
        JSONObject json = new JSONObject(jsonMessageData);
        this.description = json.getString(MESSAGE_DESCRIPTION);
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String convertMessageToJson() {
        try {
            JSONObject json = new JSONObject();
            json.put(MESSAGE_TYPE, this.type.toString());

            JSONObject sender = new JSONObject();
            sender.put(MESSAGE_NAME, this.sender.getName());
            sender.put(MESSAGE_DESCRIPTION, this.sender.getDescription());
            sender.put(MESSAGE_IP, ipFormatter(this.sender.getIp().toString()));
            sender.put(MESSAGE_PORT, this.sender.getPort());
            sender.put(MESSAGE_MAC, macAddressByteToHexString(this.sender.getMac()));
            sender.put(MESSAGE_DESCRIPTION, description);

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_NAME, this.receiver.getName());
            receiver.put(MESSAGE_DESCRIPTION, this.receiver.getDescription());
            receiver.put(MESSAGE_IP, ipFormatter(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT, this.receiver.getPort());
            receiver.put(MESSAGE_MAC, macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER, sender);
            json.put(MESSAGE_RECEIVER, receiver);
            json.put(MESSAGE_TIMESTAMP, this.timestamp.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Description to be updated */
    private String description;
}
