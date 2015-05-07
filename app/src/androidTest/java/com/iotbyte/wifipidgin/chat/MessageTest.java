package com.iotbyte.wifipidgin.chat;

import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

import junit.framework.TestCase;

import java.net.InetAddress;

public class MessageTest extends TestCase {
    public static final String MESSAGE_TEST = "Message Test";

    public void setUp() throws Exception {
        super.setUp();

        // A sample JSON is defined as the following: with help from https://www.jsoneditoronline.org/
        //http://jsonschema.net/#/
/*        {
            "sender": {
            "name": "coolsender",
            "description": "too lazy",
            "ip": "192.168.1.0",
            "mac": "FC:AA:14:79:AE:BF"
        },
            "reciver": {
            "name": "receiverCool",
            "description": "I am not very lazy",
            "ip": "192.168.1.1",
           "mac": "FC:AA:14:79:AE:BD"
        },
            "message": "\"yueMa?\"",
            "timestamp": "2015-04-23 23:40:44.96"
            "channelidentifier": "abs13443"
        }*/

      //  Message multipleVariableMessage = new message ();

    }

    public void tearDown() throws Exception {

    }

    public void testConvertMessageToJson() throws Exception {
        //the following construct a message to be send out
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        Friend aFriend = new Friend(aMac,aIp);

        InetAddress bIp = InetAddress.getByName("localhost");
        byte[] bMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xf};//fc:aa:14:79:ae:bf
        Friend bFriend = new Friend(bMac,bIp);

        Message message = new Message(aFriend,"12345","hahaha");
        Log.v(MESSAGE_TEST,message.convertMessageToJson());

        /*
            {
                "sender": {
                    "name": "myself",
                    "description": "I am who I am",
                    "ip": "192.168.1.1",
                    "mac": "FC:AA:14:79:AE:BF"
                },
                "receiver": {
                    "ip": "192.168.0.2",
                    "mac": "FC:AA:14:79:AE:BD"
                },
                "message": "hahaha",
                "timestamp": "2015-04-25 01:20:16.339",
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
        Friend aFriend = new Friend(aMac,aIp);

        Message message = new Message(aFriend,"12345","hahaha");
        assertEquals("Can't get proper message body","hahaha",message.getMessageBody());

    }

    public void testSetMessageBody() throws Exception {
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        Friend aFriend = new Friend(aMac,aIp);

        Message message = new Message(aFriend,"12345","hahaha");
        message.setMessageBody("this is insane!");
        assertEquals("Can't set message body properly", "this is insane!", message.getMessageBody());

    }

    public void testGetChannelIdentifier() throws Exception {
        //the following construct a message to be send out
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        Friend aFriend = new Friend(aMac,aIp);

        Message message = new Message(aFriend,"12345","hahaha");

        assertEquals("channelIdentifier not match","12345",message.getChannelIdentifier());
    }

    public void testGetTimestamp() throws Exception {
        InetAddress aIp = InetAddress.getByName("www.google.com");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        Friend aFriend = new Friend(aMac,aIp);

        Message message = new Message(aFriend,"12345","hahaha");
        assertTrue(message.getTimestamp().toString().matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}(?: [AP]M)?(?: [+-]\\d{4})?$"));
    }

    public void testConstructor() throws Exception {
        InetAddress aIp = InetAddress.getByName("192.168.0.2");
        byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
        Friend aFriend = new Friend(aMac,aIp);

        Message message = new Message(aFriend,"12345","hahaha");
        String json = message.convertMessageToJson();
        Log.v(MESSAGE_TEST,json);


        Message sameMessage = new Message(json);

        assertEquals("timestamp does not convert properly",message.getTimestamp().toString(),sameMessage.getTimestamp().toString());
    }
}