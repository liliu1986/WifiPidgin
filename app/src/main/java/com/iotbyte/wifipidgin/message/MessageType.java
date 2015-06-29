package com.iotbyte.wifipidgin.message;

/**
 * Created by dev on 06/05/15.
 */
public enum MessageType {
    CHAT_MESSAGE,FRIEND_CREATION_REQUEST,FRIEND_CREATION_RESPONSE, CHANNEL_CREATION_REQUEST, CHANNEL_CREATION_RESPONSE, FRIEND_IMAGE_REQUEST, FRIEND_IMAGE_RESPONSE,
    ERROR;

    public static final String MESSAGE_TYPE_CHAT_MESSAGE = "chat message";
    public static final String MESSAGE_TYPE_FRIEND_CREATION_REQUEST = "friend creation request";
    public static final String MESSAGE_TYPE_FRIEND_CREATION_RESPONSE = "friend creation response";
    public static final String MESSAGE_TYPE_CHANNEL_CREATION_REQUEST = "channel creation request";
    public static final String MESSAGE_TYPE_CHANNEL_CREATION_RESPONSE = "channel creation response";
    public static final String MESSAGE_TYPE_FRIEND_IMAGE_REQUEST = "friend image file request";
    public static final String MESSAGE_TYPE_FRIEND_IMAGE_RESPONSE = "friend image file response";
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
            case CHANNEL_CREATION_REQUEST:
                return MESSAGE_TYPE_CHANNEL_CREATION_REQUEST;
            case CHANNEL_CREATION_RESPONSE:
                return MESSAGE_TYPE_CHANNEL_CREATION_RESPONSE;
            case FRIEND_IMAGE_REQUEST:
                return MESSAGE_TYPE_FRIEND_IMAGE_REQUEST;
            case FRIEND_IMAGE_RESPONSE:
                return MESSAGE_TYPE_FRIEND_IMAGE_RESPONSE;
            case ERROR:
                return MESSAGE_TYPE_ERROR;
            default:throw new IllegalArgumentException();
        }
    }
}
