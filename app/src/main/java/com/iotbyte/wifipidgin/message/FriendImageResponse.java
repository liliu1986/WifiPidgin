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
 * Created by fire on 21/06/15.
 */
public class FriendImageResponse extends Message{

    /**
     * Constructor for FriendImageResponse Class, it create a new message object based on
     * receiver's information including the image file in Base64 format. This message
     * object will be send to others.
     *
     * @param receiver receiver of the message
     */

    public FriendImageResponse(Friend receiver,Context context, String imageBase64){
        super(receiver,context);
        this.imageBase64Encode = imageBase64;
        this.type = MessageType.FRIEND_IMAGE_RESPONSE;
    }
    /**
     * Constructor for FriendImageResponse class, it create a new message based on json received
     * from com-module. It will parse the json format into a message object.
     *
     * @param jsonMessageData is the received json format string
     * @param context
     * @throws JSONException
     * @throws UnknownHostException
     */
    public FriendImageResponse(String jsonMessageData, Context context) throws JSONException, UnknownHostException{
        super(jsonMessageData, context);
        assert this.type == MessageType.FRIEND_IMAGE_RESPONSE;
        JSONObject json = new JSONObject(jsonMessageData);
        JSONObject sender = json.getJSONObject(MESSAGE_SENDER);
        this.imageBase64Encode = sender.optString(MESSAGE_IMAGE_BASE64);
    }

    @Override
    public String convertMessageToJson() {
        try {
            JSONObject json = new JSONObject();
            Log.d(TAG, "Creating FriendImageResponse");
            json.put(MESSAGE_TYPE, this.type.toString());

            JSONObject sender = new JSONObject();
            sender.put(MESSAGE_NAME,this.sender.getName());
            sender.put(MESSAGE_DESCRIPTION,this.sender.getDescription());
            sender.put(MESSAGE_IP,ipFormatter(this.sender.getIp().toString())); // InetAdress.toString() returns in host/ip as format
            sender.put(MESSAGE_PORT,this.sender.getPort());
            sender.put(MESSAGE_MAC,macAddressByteToHexString(this.sender.getMac()));
            sender.put(MESSAGE_IMAGE_BASE64, imageBase64Encode);
            // Log.d("AAA", convertImgToBase64(this.sender.getImagePath()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_NAME,this.receiver.getName());
            receiver.put(MESSAGE_DESCRIPTION,this.receiver.getDescription());
            receiver.put(MESSAGE_IP,ipFormatter(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT,this.receiver.getPort());
            receiver.put(MESSAGE_MAC,macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER,sender);
            json.put(MESSAGE_RECEIVER,receiver);
            json.put(MESSAGE_TIMESTAMP,this.timestamp.toString()); //check this out

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();
            Log.d("CCC", json.toString(4));

            return json.toString(); //this is for easy to visualize/debugging purpose

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }


    public String getImageBase64Encode(){
        return imageBase64Encode;
    }
    final private String MESSAGE_IMAGE_BASE64 = "imageBase64";
    private String imageBase64Encode = null;
    private static final String TAG = "FriendImageResponse";

}
