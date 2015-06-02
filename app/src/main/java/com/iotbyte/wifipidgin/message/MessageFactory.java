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


    public static Message buildMessageByJson(String json) throws JSONException, UnknownHostException{
        MessageType type =getMessageType(json);
        String typeString = type.toString();
          switch  (typeString){
              case MessageType.MESSAGE_TYPE_CHAT_MESSAGE:
                  return new ChatMessage(json);
              case MessageType.MESSAGE_TYPE_FRIEND_CREATION_REQUEST:
                  return new FriendCreationRequest(json);
              case MessageType.MESSAGE_TYPE_FRIEND_CREATION_RESPONSE:
                  return new FriendCreationResponse(json);
              default:
                  return null;

          }

    }


    public static MessageType getMessageType(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        String typeString = json.getString(MESSAGE_TYPE);

         switch (typeString){
            case MessageType.MESSAGE_TYPE_CHAT_MESSAGE:
                return MessageType.CHAT_MESSAGE;

            case MessageType.MESSAGE_TYPE_FRIEND_CREATION_REQUEST:
                return MessageType.FRIEND_CREATION_REQUEST;

             case MessageType.MESSAGE_TYPE_FRIEND_CREATION_RESPONSE:
                 return MessageType.FRIEND_CREATION_RESPONSE;

            default:
                return MessageType.ERROR;
        }
    }


}
