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
        byte[] mac = {0xb,0x4,0x3,0x0,0x5,0x2,0x0,0x2,0xb,0x5,0x2,0x1};
        String macString = macAddressByteToHexString(mac);
        Log.d(UTILS_LOG_TAG, macString);
        byte[] mac2 = macAddressHexStringToByte(macString);
        macString = macAddressByteToHexString(mac2);
        assertEquals("mac does not match string", "B4305202B521", macString);
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