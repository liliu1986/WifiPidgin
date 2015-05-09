package com.iotbyte.wifipidgin.chat;

import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChatMessage;

import junit.framework.TestCase;

import java.net.InetAddress;

public class ChatMessageTest extends TestCase {
    public static final String MESSAGE_TEST = "ChatMessage Test";

    public void setUp() throws Exception {
        super.setUp();

        // A sample JSON is defined as the following: with help from https://www.jsoneditoronline.org/
        //http://jsonschema.net/#/
/*        {
            "type" : "chat message",
            "sender": {
            "name": "coolsender",
            "description": "too lazy",
            "ip": "192.168.1.0",
            "port": 55,
            "mac": "FC:AA:14:79:AE:BF"
        },
            "reciver": {
            "name": "receiverCool",
            "description": "I am not very lazy",
            "ip": "192.168.1.1",
            "port": 55,
           "mac": "FC:AA:14:79:AE:BD"
        },
            "message": "\"yueMa?\"",
            "timestamp": "2015-04-23 23:40:44.96"
            "channelidentifier": "abs13443"
        }*/

      //  ChatMessage multipleVariableMessage = new message ();

    }

    public void tearDown() throws Exception {

    }

    public void testConvertMessageToJson() throws Exception {
        //the following construct a chatMessage to be send out
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        int aPort = 55;
        Friend aFriend = new Friend(aMac,aIp,aPort);

        InetAddress bIp = InetAddress.getByName("localhost");
        byte[] bMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xf};//fc:aa:14:79:ae:bf
        int bPort = 57;
        Friend bFriend = new Friend(bMac,bIp,bPort);

        ChatMessage chatMessage = new ChatMessage(aFriend,"12345","hahaha");
        Log.v(MESSAGE_TEST, chatMessage.convertMessageToJson());

        /*
             {
                 "type": "chat message",
                 "sender": {
                     "name": "myself",
                     "description": "I am who I am",
                     "ip": "192.168.1.1",
                     "port": 55,
                     "mac": "FC:AA:14:79:AE:BF"
                 },
                 "receiver": {
                     "name": "Un-init",
                     "description": "Un-init",
                     "ip": "www.google.com74.125.226.113",
                     "port": 55,
                     "mac": "FC:AA:14:79:AE:BD"
                 },
                 "message": "hahaha",
                 "timestamp": "2015-05-08 00:54:42.115",
                 "channelidentifier": "12345"
             }


         */

        //TODO:: Discussion what is the format of IP and MAC address that will be used in com_module
    }

    public void testGetSender() throws Exception {

    }

    public void testSetSender() throws Exception {

    }

    public void testGetReceiver() throws Exception {

    }

    public void testSetReceiver() throws Exception {

    }

    public void testGetMessageBody() throws Exception {
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        int aPort = 55;
        Friend aFriend = new Friend(aMac,aIp,aPort);

        ChatMessage chatMessage = new ChatMessage(aFriend,"12345","hahaha");
        assertEquals("Can't get proper chatMessage body","hahaha", chatMessage.getMessageBody());

    }

    public void testSetMessageBody() throws Exception {
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        int aPort = 55;
        Friend aFriend = new Friend(aMac,aIp,aPort);

        ChatMessage chatMessage = new ChatMessage(aFriend,"12345","hahaha");
        chatMessage.setMessageBody("this is insane!");
        assertEquals("Can't set chatMessage body properly", "this is insane!", chatMessage.getMessageBody());

    }

    public void testGetChannelIdentifier() throws Exception {
        //the following construct a chatMessage to be send out
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        int aPort = 55;
        Friend aFriend = new Friend(aMac,aIp,aPort);

        ChatMessage chatMessage = new ChatMessage(aFriend,"12345","hahaha");

        assertEquals("channelIdentifier not match","12345", chatMessage.getChannelIdentifier());
    }

    public void testGetTimestamp() throws Exception {
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        int aPort = 55;
        Friend aFriend = new Friend(aMac,aIp,aPort);

        ChatMessage chatMessage = new ChatMessage(aFriend,"12345","hahaha");
        assertTrue(chatMessage.getTimestamp().toString().matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}(?: [AP]M)?(?: [+-]\\d{4})?$"));
    }

    public void testConstructor() throws Exception {
        InetAddress aIp = InetAddress.getByName("192.168.0.2");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        int aPort = 55;
        Friend aFriend = new Friend(aMac,aIp,aPort);

        ChatMessage chatMessage = new ChatMessage(aFriend,"12345","hahaha");
        String json = chatMessage.convertMessageToJson();
        Log.v(MESSAGE_TEST,json);


        ChatMessage sameChatMessage = new ChatMessage(json);

        assertEquals("timestamp does not convert properly", chatMessage.getTimestamp().toString(), sameChatMessage.getTimestamp().toString());
    }
}