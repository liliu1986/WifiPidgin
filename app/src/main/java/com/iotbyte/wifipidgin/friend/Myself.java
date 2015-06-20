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
        this.mac = mac;
    }


    /**
     * Constructor that converts a friend into Myself.
     * @param self Friend that's self.
     * @throws RuntimeException If self passed in does not have id equal to SELF_ID
     */
    public Myself(Friend self) throws RuntimeException {
        super(self);
        if (self.getId() != SELF_ID) {
            throw new RuntimeException("Try to construct Myself from a Friend with id != " + SELF_ID);
        }
        this.mac = self.getMac();
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

    /** Mac is not final in this case, because when self is populated in the db at installation,
     *  the mac is unknown yet. Once the program initialize self mac will be saved into the db.
     */
    private byte[] mac;
}
