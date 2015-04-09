package com.iotbyte.wifipidgin.channel;

import android.test.AndroidTestCase;


public class ChannelTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();


    }

    public void tearDown() throws Exception {

    }

    public void testGetName() throws Exception {
        final String name= "test string name";
        Channel mChannel = new Channel(null,name, null);
        assertEquals("name not match",name,mChannel.getName());
    }

    public void testSetName() throws Exception {

    }

    public void testGetDescription() throws Exception {

    }

    public void testSetDescription() throws Exception {

    }

    public void testGetChannelIdentifier() throws Exception {

    }

    public void testGetId() throws Exception {

    }

    public void testSetId() throws Exception {

    }

}