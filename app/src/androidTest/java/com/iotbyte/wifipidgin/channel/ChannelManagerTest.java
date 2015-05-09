package com.iotbyte.wifipidgin.channel;

import android.test.AndroidTestCase;
import android.util.Log;


public class ChannelManagerTest extends AndroidTestCase {

    final String CHANNEL_MANAGER_TEST = "channel manager test";
    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testGetInstance() throws Exception {

    }

    public void testSaveChannelsInfoIntoDatabase() throws Exception {

    }

    public void testGetChannelStatus() throws Exception {

    }

    public void testGetChannelByIdentifier() throws Exception {

    }

    public void testAddChannel() throws Exception {

    }

    public void testDeleteChannel() throws Exception {

    }

    public void testGetChannelList() throws Exception {

        Log.d(CHANNEL_MANAGER_TEST,"the size is "+ChannelManager.getInstance(getContext()).getChannelList().size());

    }
}