package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormater;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;

/**
 * Created by dev on 08/05/15.
 */
public class FriendCreationRequest extends Message {


    /**
     * Constructor for FriendCreationRequest Class, it create a new message object based on known
     * receiver information.  This message object will be send to others.
     *
     * @param receiver     receiver of the message
     */
    public FriendCreationRequest(Friend receiver,Context context){
        super(receiver,context);
        this.type = MessageType.FRIEND_CREATION_REQUEST;
    }

    /**
     * Constructor for FriendCreationRequest Class, it create a new message based on json received
     * from com-module. It will parse the json format into a message object.
     *
     * @param jsonMessageData is the received json format string
     * @throws JSONException
     * @throws UnknownHostException
     */
    public FriendCreationRequest(String jsonMessageData) throws JSONException, UnknownHostException {
        super(jsonMessageData);

    }

    @Override
    public String convertMessageToJson() {
        try {
            JSONObject json = new JSONObject();

            json.put(MESSAGE_TYPE,this.type.toString());

            JSONObject sender = new JSONObject();

            sender.put(MESSAGE_IP,ipFormater(this.sender.getIp().toString())); // InetAdress.toString() returns in host/ip as format
            sender.put(MESSAGE_PORT,this.sender.getPort());
            sender.put(MESSAGE_MAC, macAddressByteToHexString(this.sender.getMac()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_IP, ipFormater(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT,this.receiver.getPort());
            receiver.put(MESSAGE_MAC,macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER,sender);
            json.put(MESSAGE_RECEIVER,receiver);
            json.put(MESSAGE_TIMESTAMP,this.timestamp.toString()); //check this out


            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}
