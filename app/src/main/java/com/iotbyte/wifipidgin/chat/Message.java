package com.iotbyte.wifipidgin.chat;

import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormater;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressHexStringToByte;

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


    final String MESSAGE_RECEIVER = "receiver";
    final String MESSAGE_SENDER = "sender";
    final String MESSAGE_IP = "ip";
    final String MESSAGE_MAC = "mac";
    final String MESSAGE_DESCRIPTION = "description";
    final String MESSAGE_NAME = "name";
    final String MESSAGE_CHANNEL_IDENTIFIER = "channelidentifier";
    final String MESSAGE_TIMESTAMP = "timestamp";
    final String MESSAGE_MESSAGE_BODY = "message";
    final String MESSAGE_PORT = "port";
    /**
     * Constructor for Message class, it create a new message based on
     * information received from com-module. It will parse the json format
     * into a message object
     *
     * @param jsonMessageData is the received json format string
     */

    public Message(String jsonMessageData) throws JSONException, UnknownHostException {

        //TODO:: NOTE, the receiver here should be myself
        //TODO:: discussion, what happens if the json is corrupted?, message will not be completely
        //TODO:: constructed!


            JSONObject json = new JSONObject(jsonMessageData);
            JSONObject sender = json.getJSONObject(MESSAGE_SENDER);
            JSONObject receiver = json.getJSONObject(MESSAGE_RECEIVER);
            InetAddress senderIp = InetAddress.getByName(ipFormater(sender.getString(MESSAGE_IP)));
            byte[] senderMac = macAddressHexStringToByte(sender.getString(MESSAGE_MAC));

            Friend friend = new Friend(senderMac,senderIp);
            friend.setDescription(sender.optString(MESSAGE_DESCRIPTION));
            friend.setName(sender.optString(MESSAGE_NAME));
            this.sender = friend;


            //TODO:: receiver should be myself, this is mock code,or require to verify if the receiver is correctly myself

            InetAddress receiverIp = InetAddress.getByName(ipFormater(receiver.getString(MESSAGE_IP)));
            byte[] receiverMac = macAddressHexStringToByte(receiver.getString(MESSAGE_MAC));

            Friend myself = new Friend(receiverMac,receiverIp);
            myself.setDescription(receiver.optString(MESSAGE_DESCRIPTION));
            myself.setName(receiver.optString(MESSAGE_NAME));
            this.receiver = myself;

            this.messageBody = json.getString(MESSAGE_MESSAGE_BODY);
            this.channelIdentifier = json.getString(MESSAGE_CHANNEL_IDENTIFIER);
            this.timestamp = Timestamp.valueOf(json.optString(MESSAGE_TIMESTAMP));
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
        //TODO:: remove mocked myself
        InetAddress myIp = null;
        try {
            myIp = InetAddress.getByName("192.168.1.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] myMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xf};//fc:aa:14:79:ae:bf
        Friend myself = new Friend(myMac,myIp);
        myself.setName("myself");
        myself.setDescription("I am who I am");
        this.sender = myself;

    }

    /**
     * convertMessageToJson()
     * <p/>
     * It will convert this Message to it's json format
     *
     * @return a json string
     */

    public String convertMessageToJson() {

        try {
            JSONObject json = new JSONObject();

            JSONObject sender = new JSONObject();
            sender.put(MESSAGE_NAME,this.sender.getName());
            sender.put(MESSAGE_DESCRIPTION,this.sender.getDescription());
            sender.put(MESSAGE_IP,ipFormater(this.sender.getIp().toString())); // InetAdress.toString() returns in host/ip as format
            sender.put(MESSAGE_MAC,macAddressByteToHexString(this.sender.getMac()));

            JSONObject receiver = new JSONObject();
            receiver.put(MESSAGE_NAME,this.receiver.getName());
            receiver.put(MESSAGE_DESCRIPTION,this.receiver.getDescription());
            receiver.put(MESSAGE_IP,ipFormater(this.receiver.getIp().toString()));
            receiver.put(MESSAGE_MAC,macAddressByteToHexString(this.receiver.getMac()));

            json.put(MESSAGE_SENDER,sender);
            json.put(MESSAGE_RECEIVER,receiver);
            json.put(MESSAGE_MESSAGE_BODY,this.messageBody);
            json.put(MESSAGE_TIMESTAMP,this.timestamp.toString()); //check this out
            json.put(MESSAGE_CHANNEL_IDENTIFIER,this.channelIdentifier);

            //TODO:: change to json.toString() to save on transmission space,
            // return json.toString();

            return json.toString(4); //this is for easy to visualize/debugging purpose

        }catch (JSONException e){
            e.printStackTrace();
        }

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
