package com.iotbyte.wifipidgin.friend;

import com.iotbyte.wifipidgin.utils.Utils;

import java.net.InetAddress;
import java.sql.Timestamp;

/**
 * Data object to store information about a friend.
 * <p/>
 * A friend is any client who has been discovered by the NSD
 */
public class Friend {

    /**
     * No id indicates this object has not been published to storage
     */
    public static final long NO_ID = -1;

    /**
     * ID indicating this Friend is self.
     */
    public static final long SELF_ID = 0;

    /**
     * Status a friend can be in
     */
    public enum FriendStatus {
        UNINIT(0),
        ONLINE(1),
        BUSY(2),
        INVISIBLE(3),
        OFFLINE(4);

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
     *
     * @param mac  MAC address of the Friend.
     * @param ip   IP address of the Friend.
     * @param port Port number to communicate with the Friend.
     */
    public Friend(byte[] mac, InetAddress ip, int port) {
        this.mac = mac;
        this.id = NO_ID;
        this.ip = ip;
        this.port = port;
        this.name = UNINIT_STRING;
        this.description = UNINIT_STRING;
        this.status = FriendStatus.UNINIT;
        this.imagePath = UNINIT_STRING;
        this.isFavourite = false;
        this.nameDescriptionHash = UNINIT_STRING;
        this.imageHash = UNINIT_STRING;
        this.lastOnlineTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Copy constructor. Performs a deep copy of the other friend.
     * @param other the other friend to be copy-constructed.
     */
    public Friend(Friend other) {
        this.id = other.getId();
        this.ip = other.getIp();
        this.port = other.getPort();
        this.mac = other.getMac();
        this.name = other.getName();
        this.description = other.getDescription();
        this.status = other.getStatus();
        this.imagePath = other.getImagePath();
        this.isFavourite = other.isFavourite();
        this.nameDescriptionHash = other.getNameDescriptionHash();
        this.imageHash = other.getImageHash();
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        // recalculate hash
        this.nameDescriptionHash = Utils.sha1(this.name + this.description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        // recalculate hash
        this.nameDescriptionHash = Utils.sha1(this.name + this.description);
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
        this.imageHash = Utils.getFileHash(imagePath, IMAGE_HASHABLE_BYTES);
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getNameDescriptionHash() {
        return nameDescriptionHash;
    }

    public String getImageHash() {
        return imageHash;
    }

    public Timestamp getLastOnlineTimeStamp(){
        return lastOnlineTimeStamp;
    }

    public void setLastOnlineTimeStamp(Timestamp ts){
        lastOnlineTimeStamp = ts;
    }
    static private final String UNINIT_STRING = "Un-init";

    /** Number of bytes used to calculate image hash */
    static private final int IMAGE_HASHABLE_BYTES = 100;

    /**
     * id to identify friend in storage.
     */
    private long id;

    /**
     * IP address of Friend.
     */
    private InetAddress ip;

    /**
     * port number to communicate to this friend
     */
    private int port;

    /**
     * MAC address of Friend.
     */
    final private byte[] mac;

    /**
     * Name of this Friend.
     */
    private String name;


    /**
     * Description of this Friend.
     */
    private String description;

    /**
     * Status of this Friend.
     */
    private FriendStatus status;

    /**
     * Friend image path
     */
    private String imagePath;

    /**
     * Whether this Friend has is a favourite
     */
    private boolean isFavourite;

    /**
     * Hash of name and description. The hash is sent with every chat message.
     * When remote client detects a hash change, it will request the updated name and description.
     */
    private String nameDescriptionHash;

    /**
     * Hash of friend image. The hash is sent with every chat message.
     * When remote client detects a hash change, it will request the updated image.
     */
    private String imageHash;

    private Timestamp lastOnlineTimeStamp;

    @Override
    public String toString() {
        return this.getName();
    }
}

