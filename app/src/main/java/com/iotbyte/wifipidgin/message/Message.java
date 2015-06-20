package com.iotbyte.wifipidgin.message;

import android.content.Context;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormatter;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressHexStringToByte;

/**
 * Created by yefwen@iotbyte.com on 06/05/15.
 */
public abstract class Message {

    protected Friend sender;
    protected Friend receiver;
    protected MessageType type;
    protected Timestamp timestamp;

    protected long myselfId = 0;
    private final String MESSAGE_DEBUG = "MESSAGE CLASS";

    final protected String MESSAGE_TYPE = "type";
    final protected String MESSAGE_RECEIVER = "receiver";
    final protected String MESSAGE_SENDER = "sender";
    final protected String MESSAGE_IP = "ip";
    final protected String MESSAGE_MAC = "mac";
    final protected String MESSAGE_PORT = "port";
    final protected String MESSAGE_DESCRIPTION = "description";
    final protected String MESSAGE_NAME = "name";
    final protected String MESSAGE_TIMESTAMP = "timestamp";





    public Message ()
    {

    }

    public Message (MessageType type){
        this.type = type;
    }

    public Message (Friend receiver,Context context){
        this.receiver = receiver;

        // Get Timestamp
        Date mDate = new Date();
        this.timestamp = new Timestamp(mDate.getTime());

        FriendDao fd = DaoFactory.getInstance().getFriendDao(context,DaoFactory.DaoType.SQLITE_DAO, null);
        Friend myself = fd.findById(myselfId);
/*
        InetAddress myIp = null;
        try {
            myIp = InetAddress.getByName("192.168.1.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] myMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xf};//fc:aa:14:79:ae:bf
        int port = 55;
        Friend myself = new Friend(myMac,myIp,port);
        myself.setName("myself");
        myself.setDescription("I am who I am");
*/
        Log.d(MESSAGE_DEBUG,"myself IP from DB:"+myself.getIp());
        this.sender = myself;

    }

    public Message (String jsonMessageData) throws JSONException, UnknownHostException
    {
        //TODO:: NOTE, the receiver here should be myself
        //TODO:: discussion, what happens if the json is corrupted?, message will not be completely
        //TODO:: constructed!


        JSONObject json = new JSONObject(jsonMessageData);
        String type = json.getString(MESSAGE_TYPE);
        JSONObject sender = json.getJSONObject(MESSAGE_SENDER);
        JSONObject receiver = json.getJSONObject(MESSAGE_RECEIVER);
        InetAddress senderIp = InetAddress.getByName(ipFormatter(sender.getString(MESSAGE_IP)));
        byte[] senderMac = macAddressHexStringToByte(sender.getString(MESSAGE_MAC));
        int senderPort = sender.getInt(MESSAGE_PORT);

        Friend friend = new Friend(senderMac,senderIp,senderPort);
        friend.setDescription(sender.optString(MESSAGE_DESCRIPTION));
        friend.setName(sender.optString(MESSAGE_NAME));
        this.sender = friend;


        //TODO:: receiver should be myself, this is mock code,or require to verify if the receiver is correctly myself

        InetAddress receiverIp = InetAddress.getByName(ipFormatter(receiver.getString(MESSAGE_IP)));
        byte[] receiverMac = macAddressHexStringToByte(receiver.getString(MESSAGE_MAC));
        int receiverPort = receiver.getInt(MESSAGE_PORT);

        Friend myself = new Friend(receiverMac,receiverIp,receiverPort);
        myself.setDescription(receiver.optString(MESSAGE_DESCRIPTION));
        myself.setName(receiver.optString(MESSAGE_NAME));
        this.receiver = myself;

        this.timestamp = Timestamp.valueOf(json.optString(MESSAGE_TIMESTAMP));

        setMessageType(type);



    }

    public void setMessageType(String type) {
            switch (type){
                case MessageType.MESSAGE_TYPE_CHAT_MESSAGE:
                    this.type = MessageType.CHAT_MESSAGE;
                    break;

                case MessageType.MESSAGE_TYPE_FRIEND_CREATION_REQUEST:
                    this.type = MessageType.FRIEND_CREATION_REQUEST;
                    break;

                case MessageType.MESSAGE_TYPE_FRIEND_CREATION_RESPONSE:
                    this.type = MessageType.FRIEND_CREATION_RESPONSE;
                    break;
                default:
                    this.type = MessageType.ERROR;
            }
    }

    public abstract String convertMessageToJson();

    public Friend getReceiver() {
        return receiver;
    }

    public void setReceiver(Friend receiver) {
        this.receiver = receiver;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Friend getSender() {
        return sender;
    }

    public void setSender(Friend sender) {
        this.sender = sender;
    }


}
