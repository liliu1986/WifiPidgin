package com.iotbyte.wifipidgin.chat;

import com.iotbyte.wifipidgin.friend.Friend;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Message is the object that will be transferred between clients
 * <p/>
 * Created by dev on 12/04/15.
 */
public class Message {

    private Friend sender;
    private Friend receiver;
    private final String channelIdentifier;
    private String messageBody;
    private final Timestamp timestamp;

    /**
     * Constructor for Message class, it create a new message based on
     * information received from com-module. It will parse the json format
     * into a message object
     *
     * @param jsonMessageData is the received json format string
     */

    public Message(String jsonMessageData) {

        //TODO:: after design the JSON format, update this information
        //TODO:: currently a mock timestamp and channelIdentifier only
        //TODO:: NOTE, the receiver here should be myself
        Date mDate = new Date();
        this.timestamp = new Timestamp(mDate.getTime());
        this.channelIdentifier = null;
    }

    /**
     * Constructor for Message class, it create a new message object based
     * on input from this client itself. This message object will be send to
     * others
     *
     * @param receiver          receiver of the message
     * @param channelIdentifier it defines where the message belongs to
     * @param messageBody       actual message body
     */
    public Message(Friend receiver, String channelIdentifier, String messageBody) {
        this.receiver = receiver;
        this.channelIdentifier = channelIdentifier;
        this.messageBody = messageBody;

        // Get Timestamp
        Date mDate = new Date();
        this.timestamp = new Timestamp(mDate.getTime());

        //TODO:: myself is defined as the id = 0 from database, implement this when Di complete so
        this.sender = null;
    }

    /**
     * convertMessageToJson()
     * <p/>
     * It will convert this Message to it's json format
     *
     * @return a json string
     */

    public String convertMessageToJson() {
        //TODO:: after design the JSON format, update this information
        return null;
    }

    public Friend getSender() {
        return sender;
    }

    public void setSender(Friend sender) {
        this.sender = sender;
    }

    public Friend getReceiver() {
        return receiver;
    }

    public void setReceiver(Friend receiver) {
        this.receiver = receiver;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
