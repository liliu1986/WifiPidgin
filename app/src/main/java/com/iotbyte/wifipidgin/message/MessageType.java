package com.iotbyte.wifipidgin.message;

/**
 * Message type to be passed between clients.
 */
public enum MessageType {
    CHAT_MESSAGE,
    FRIEND_CREATION_REQUEST,
    FRIEND_CREATION_RESPONSE,
    FRIEND_INFO_UPDATE_REQUEST,
    FRIEND_INFO_UPDATE_RESPONSE,
    FRIEND_IMAGE_REQUEST,
    FRIEND_IMAGE_RESPONSE,
    CHANNEL_CREATION_REQUEST,
    CHANNEL_CREATION_RESPONSE,
    ERROR;

    public static final String MESSAGE_TYPE_CHAT_MESSAGE = "chat message";
    public static final String MESSAGE_TYPE_FRIEND_CREATION_REQUEST = "friend creation request";
    public static final String MESSAGE_TYPE_FRIEND_CREATION_RESPONSE = "friend creation response";
    public static final String MESSAGE_TYPE_CHANNEL_CREATION_REQUEST = "channel creation request";
    public static final String MESSAGE_TYPE_CHANNEL_CREATION_RESPONSE = "channel creation response";
    public static final String MESSAGE_TYPE_FRIEND_INFO_UPDATE_REQUEST = "friend info update request";
    public static final String MESSAGE_TYPE_FRIEND_INFO_UPDATE_RESPONSE = "friend info update response";
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
            case FRIEND_INFO_UPDATE_REQUEST:
                return MESSAGE_TYPE_FRIEND_INFO_UPDATE_REQUEST;
            case FRIEND_INFO_UPDATE_RESPONSE:
                return MESSAGE_TYPE_FRIEND_INFO_UPDATE_RESPONSE;
            case FRIEND_IMAGE_REQUEST:
                return MESSAGE_TYPE_FRIEND_IMAGE_REQUEST;
            case FRIEND_IMAGE_RESPONSE:
                return MESSAGE_TYPE_FRIEND_IMAGE_RESPONSE;
            case ERROR:
                return MESSAGE_TYPE_ERROR;
            default:throw new IllegalArgumentException();
        }
    }

    static public MessageType fromString(String messageTypeString) {
        switch (messageTypeString) {
            case MESSAGE_TYPE_CHAT_MESSAGE:
                return CHAT_MESSAGE;
            case MESSAGE_TYPE_FRIEND_CREATION_REQUEST:
                return FRIEND_CREATION_REQUEST;
            case MESSAGE_TYPE_FRIEND_CREATION_RESPONSE:
                return FRIEND_CREATION_RESPONSE;
            case MESSAGE_TYPE_FRIEND_INFO_UPDATE_REQUEST:
                return FRIEND_INFO_UPDATE_REQUEST;
            case MESSAGE_TYPE_FRIEND_INFO_UPDATE_RESPONSE:
                return FRIEND_INFO_UPDATE_RESPONSE;
            case MESSAGE_TYPE_FRIEND_IMAGE_REQUEST:
                return FRIEND_IMAGE_REQUEST;
            case MESSAGE_TYPE_FRIEND_IMAGE_RESPONSE:
                return FRIEND_IMAGE_RESPONSE;
            case MESSAGE_TYPE_CHANNEL_CREATION_REQUEST:
                return CHANNEL_CREATION_REQUEST;
            case MESSAGE_TYPE_CHANNEL_CREATION_RESPONSE:
                return CHANNEL_CREATION_RESPONSE;
            default:
                return ERROR;
        }
    }
}
