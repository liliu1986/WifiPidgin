package com.iotbyte.wifipidgin.utils;

import android.util.Log;

import junit.framework.TestCase;

import static com.iotbyte.wifipidgin.utils.Utils.ipFormater;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressByteToHexString;
import static com.iotbyte.wifipidgin.utils.Utils.macAddressHexStringToByte;

//import junit.framework.TestCase;

//import junit.framework.TestCase;

public class UtilsTest extends TestCase {
    static final String UTILS_LOG_TAG = "Utils log";

    public void testMacAddressByteToHexString() throws Exception {
        byte[] mac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xf};
        String macString = macAddressByteToHexString(mac);
        Log.v(UTILS_LOG_TAG,macString);
        assertEquals("mac does not match string", "FC:AA:14:79:AE:BF", macString);
     }

    public void testMacAddressHexStringToByte() throws Exception {
        String macString = "FC:AA:14:79:AE:BF";
        byte[] mac = macAddressHexStringToByte(macString);
        byte[] expectMac =  {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xf};
        android.test.MoreAsserts.assertEquals("mac does not match byte arrays",expectMac,mac);
    }

    public void testIpFormater() throws Exception{
        String expectedIpAddress = "192.168.0.1";
        String ipAddressToBeProcessed = "/192.168.0.1";
        assertEquals("ip formater not working",expectedIpAddress,ipFormater(ipAddressToBeProcessed));
    }
}