package com.iotbyte.wifipidgin.message;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

/**
 * Created by yefWen@iotbyte.com on 06/05/15.
 */
public class MessageFactory {

    static final  String MESSAGE_TYPE = "type";

    public static Message buildMessageByType(MessageType type){
          Message message = null;


          return message;
    }

    public static Message buildMessageByReceiver(Friend Receiver){
        Message message = null;

        return message;
    }

    public static Message buildMessageByJson(String jsonString) throws JSONException, UnknownHostException{
        JSONObject json = new JSONObject(jsonString);
        String typeString = json.getString(MESSAGE_TYPE);
        MessageType type = MessageType.fromString(typeString);
        switch  (type) {
            case CHAT_MESSAGE:
                return new ChatMessage(jsonString);
            case FRIEND_CREATION_REQUEST:
                return new FriendCreationRequest(jsonString);
            case FRIEND_CREATION_RESPONSE:
                return new FriendCreationResponse(jsonString);
            case FRIEND_INFO_UPDATE_REQUEST:
                return new FriendInfoUpdateRequest(jsonString);
            case FRIEND_INFO_UPDATE_RESPONSE:
                return new FriendInfoUpdateResponse(jsonString);
            case FRIEND_IMAGE_REQUEST:
                return new FriendImageRequest(jsonString);
            case FRIEND_IMAGE_RESPONSE:
                return new FriendImageResponse(jsonString);
            default:
                return null;
        }
    }
}
