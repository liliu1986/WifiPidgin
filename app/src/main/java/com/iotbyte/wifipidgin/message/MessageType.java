package com.iotbyte.wifipidgin.message;

/**
 * Created by dev on 06/05/15.
 */
public enum MessageType {
    CHAT_MESSAGE,FRIEND_CREATION_REQUEST,FRIEND_CREATION_RESPONSE,CHANNEL_CREATION_MESSAGE,ERROR;

    public static final String MESSAGE_TYPE_CHAT_MESSAGE = "chat message";
    public static final String MESSAGE_TYPE_FRIEND_CREATION_REQUEST = "friend creation request";
    public static final String MESSAGE_TYPE_FRIEND_CREATION_RESPONSE = "friend creation response";
    public static final String MESSAGE_TYPE_CHANNEL_CREATION_MESSAGE = "channel creation message";
    public static final String MESSAGE_TYPE_ERROR = "error";

    @Override
    public String toString(){
        switch (this){
            case CHAT_MESSAGE:
                return MESSAGE_TYPE_CHAT_MESSAGE;
            case FRIEND_CREATION_REQUEST:
                return MESSAGE_TYPE_FRIEND_CREATION_REQUEST;
            case FRIEND_CREATION_RESPONSE:
                return MESSAGE_TYPE_FRIEND_CREATION_RESPONSE;
            case CHANNEL_CREATION_MESSAGE:
                return MESSAGE_TYPE_CHANNEL_CREATION_MESSAGE;
            case ERROR:
                return MESSAGE_TYPE_ERROR;
            default:throw new IllegalArgumentException();
        }
    }
}
