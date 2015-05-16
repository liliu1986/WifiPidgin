package com.iotbyte.wifipidgin.friend;

import java.net.InetAddress;

/**
 * Myself is a special Friend with ID = 0
 */
public class Myself extends Friend {

    public static final long SELF_ID = 0;

    /**
     * Constructor
     *
     * @param mac  MAC address of the Friend.
     * @param ip   IP address of the Friend.
     * @param port Port number to communicate with the Friend.
     */
    public Myself(byte[] mac, InetAddress ip, int port) {
        super(mac, ip, port);
    }

    /**
     * Get the Mac for self
     * @return mac address of current user
     */
    public byte[] getMac() {
        return mac;
    }

    /**
     * Set the mac address for the current user
     * @param inMac the mac address to be set for current user.
     */
    public void setMac(byte[] inMac) {
        mac = inMac;
    }

    private byte[] mac;


}
