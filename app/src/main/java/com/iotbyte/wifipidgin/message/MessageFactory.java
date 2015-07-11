package com.iotbyte.wifipidgin.message;

import android.content.Context;

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

    public static Message buildMessageByJson(String jsonString, Context context) throws JSONException, UnknownHostException{
        JSONObject json = new JSONObject(jsonString);
        String typeString = json.getString(MESSAGE_TYPE);
        MessageType type = MessageType.fromString(typeString);
        switch  (type) {
            case CHAT_MESSAGE:
                return new ChatMessage(jsonString,context );
            case FRIEND_CREATION_REQUEST:
                return new FriendCreationRequest(jsonString,context );
            case FRIEND_CREATION_RESPONSE:
                return new FriendCreationResponse(jsonString,context );
            case FRIEND_INFO_UPDATE_REQUEST:
                return new FriendInfoUpdateRequest(jsonString,context );
            case FRIEND_INFO_UPDATE_RESPONSE:
                return new FriendInfoUpdateResponse(jsonString,context );
            case FRIEND_IMAGE_REQUEST:
                return new FriendImageRequest(jsonString,context );
            case FRIEND_IMAGE_RESPONSE:
                return new FriendImageResponse(jsonString,context );
            case CHANNEL_CREATION_REQUEST:
                return new ChannelCreationRequest(jsonString,context );
            case CHANNEL_CREATION_RESPONSE:
                return new ChannelCreationResponse(jsonString,context );

            default:
                return null;
        }
    }
}
