package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

/**
 * Message to request remote peer to update friend description
 */
class FriendInfoUpdateRequest extends Message {

    /**
     * Constructor
     * @param receiver receiver of the message
     * @param context context
     */
    public FriendInfoUpdateRequest(Friend receiver, Context context) {
        super(receiver, context);
        this.type = MessageType.FRIEND_INFO_UPDATE_REQUEST;
    }

    public FriendInfoUpdateRequest(String jsonMessageData, Context context) throws JSONException, UnknownHostException {
        super(jsonMessageData,context );
        assert this.type == MessageType.FRIEND_INFO_UPDATE_REQUEST;
    }

    @Override
    public String convertMessageToJson() {
        try {
            JSONObject json = new JSONObject();

            json.put(MESSAGE_TYPE,this.type.toString());

            JSONObject sender = new JSONObject();

            sender.put(MESSAGE_IP, Utils.ipFormatter(this.sender.getIp().toString()));
            sender.put(MESSAGE_PORT, this.sender.getPort());
            sender.put(MESSAGE_MAC, Utils.macAddressByteToHexString(this.sender.getMac()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_IP, Utils.ipFormatter(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT, this.receiver.getPort());
            receiver.put(MESSAGE_MAC, Utils.macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER, sender);
            json.put(MESSAGE_RECEIVER, receiver);
            json.put(MESSAGE_TIMESTAMP, this.timestamp.toString());

            //this is for easy to visualize/debugging purpose
            return json.toString(4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
