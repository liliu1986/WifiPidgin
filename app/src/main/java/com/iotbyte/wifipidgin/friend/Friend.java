package com.iotbyte.wifipidgin.friend;

import java.net.InetAddress;

/**
 * Data object to store information about a friend.
 *
 * A friend is any client who has been discovered by the NSD
 */
public class Friend {

    /** No id indicates this object has not been published to storage */
    public static final long NO_ID = -1;

    /** Status a friend can be in */
    public enum FriendStatus {
        ONLINE(0),
        BUSY(1),
        INVISIBLE(2),
        OFFLINE(3);

        public int getValue() {
            return value;
        }

        private FriendStatus(int value) {
            this.value = value;
        }

        private final int value;
    }

    /**
      * Constructor
      * @param mac MAC address of the Friend.
      */
	public Friend(byte[] mac) {
        this.mac = mac;
        this.id = NO_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public byte[] getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    /** id to identify friend in storage. */
    private long id;

    /** IP address of Friend. */
    private InetAddress ip;

    /** MAC address of Friend. */
    private final byte[] mac;

    /** Name of this Friend. */
    private String name;

    /** Description of this Friend. */
    private String description;

    /** Status of this Friend. */
    private FriendStatus status;

    /** Friend image path */
    private String imagePath;

    /** Whether this Friend has is a favourite */
    private boolean isFavourite;

    @Override
    public String toString() {
        return "Friend id:" + id + ",IP:" + ip.getHostAddress() + ",MAC:" + mac;
    }
}

