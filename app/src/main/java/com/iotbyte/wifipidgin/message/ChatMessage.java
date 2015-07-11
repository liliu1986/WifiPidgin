package com.iotbyte.wifipidgin.message;

import android.content.Context;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;

/**
 * ChatMessage is the object that will be transferred between clients
 * <p/>
 * Created by dev on 12/04/15.
 */
public class ChatMessage extends Message {


    private final String channelIdentifier;
    private String messageBody;
    private String imageHashCode;
    private String nameDescriptionHashCode;
    private boolean isMyself;



    final String MESSAGE_CHANNEL_IDENTIFIER = "channelidentifier";
    final String MESSAGE_MESSAGE_BODY = "message";
    final String MESSAGE_IMAGE_HASH = "image hash";
    final String MESSAGE_NAME_DESCRIPTION_HASH = "name & description hash";

    /**
     * Constructor for ChatMessage class, it create a new message based on
     * information received from com-module. It will parse the json format
     * into a message object
     *
     * @param jsonMessageData is the received json format string
     * @param context
     */

    public ChatMessage(String jsonMessageData, Context context) throws JSONException, UnknownHostException {
        super(jsonMessageData,context );
        assert this.type == MessageType.CHAT_MESSAGE;
        JSONObject json = new JSONObject(jsonMessageData);
        this.messageBody = json.getString(MESSAGE_MESSAGE_BODY);
        this.channelIdentifier = json.getString(MESSAGE_CHANNEL_IDENTIFIER);
        this.imageHashCode = json.getString(MESSAGE_IMAGE_HASH);
        this.nameDescriptionHashCode = json.getString(MESSAGE_NAME_DESCRIPTION_HASH);
        this.isMyself = false;
    }

    /**
     * Constructor for ChatMessage class, it create a new message object based
     * on input from this client itself. This message object will be send to
     * others
     *
     * @param receiver          receiver of the message
     * @param channelIdentifier it defines where the message belongs to
     * @param messageBody       actual message body
     */
    public ChatMessage(Friend receiver, String channelIdentifier, String messageBody,Context context) {

        super(receiver,context);
        this.type = MessageType.CHAT_MESSAGE;
        this.channelIdentifier = channelIdentifier;
        this.messageBody = messageBody;
        this.imageHashCode = sender.getImageHash();
        this.nameDescriptionHashCode = sender.getNameDescriptionHash();
        this.isMyself = true;
    }

    /**
     * convertMessageToJson()
     * <p/>
     * It will convert this ChatMessage to it's json format
     *
     * @return a json string
     */

    public String convertMessageToJson() {

        try {
            JSONObject json = new JSONObject();

            json.put(MESSAGE_TYPE,this.type.toString());

            JSONObject sender = new JSONObject();
            sender.put(MESSAGE_IP, ipFormatter(this.sender.getIp().toString())); // InetAdress.toString() returns in host/ip as format
            sender.put(MESSAGE_PORT,this.sender.getPort());
            sender.put(MESSAGE_MAC,macAddressByteToHexString(this.sender.getMac()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_IP, ipFormatter(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_PORT,this.receiver.getPort());
            receiver.put(MESSAGE_MAC,macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER,sender);
            json.put(MESSAGE_RECEIVER,receiver);
            json.put(MESSAGE_MESSAGE_BODY,this.messageBody);
            json.put(MESSAGE_TIMESTAMP,this.timestamp.toString()); //check this out
            json.put(MESSAGE_CHANNEL_IDENTIFIER,this.channelIdentifier);
            json.put(MESSAGE_IMAGE_HASH,this.imageHashCode);
            json.put(MESSAGE_NAME_DESCRIPTION_HASH,this.nameDescriptionHashCode);

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
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


    /**
     * isFromMyself()
     *
     * Return whether the message original is myself
     *
     * @return true if the message is construct through the multi value constructor
     * false if message is construct through {@link #ChatMessage(String, Context)}
     */
    public boolean isFromMyself(){
        return this.isMyself;
    }

    public String getImageHashCode() {
        return imageHashCode;
    }

    public String getNameDescriptionHashCode() {
        return nameDescriptionHashCode;
    }
}
