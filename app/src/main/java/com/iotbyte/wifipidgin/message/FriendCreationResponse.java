package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;


/**
 * Created by yefwen@iotbyte.com on 18/05/15.
 */
public class FriendCreationResponse extends Message {


    /**
     * Constructor for FriendCreationResponse Class, it create a new message object based on
     * receiver's information including name and description. This message object will be send
     * to others.
     *
     * @param receiver receiver of the message
     */
    public FriendCreationResponse(Friend receiver,Context context){
        super(receiver,context);
        this.type = MessageType.FRIEND_CREATION_RESPONSE;
    }

    /**
     * Constructor for FriendCreationResponse class, it create a new message based on json received
     * from com-module. It will parse the json format into a message object.
     *
     * @param jsonMessageData is the received json format string
     * @throws JSONException
     * @throws UnknownHostException
     */
    public FriendCreationResponse (String jsonMessageData) throws JSONException, UnknownHostException{
        super(jsonMessageData);
        assert this.type == MessageType.FRIEND_CREATION_RESPONSE;
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

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}
