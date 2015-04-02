package com.iotbyte.wifipidgin.dao;

import com.iotbyte.wifipidgin.friend.Friend;

import java.net.InetAddress;
import java.util.List;

/**
 * Provides an interface to publish and retrieve Friend object to/from storage
 */
//TODO: some methods throws exceptions.
public interface FriendDao {
    /**
     * Publish a friend to storage.
     * <p/>
     * The id field of friend is updated to the id to find it in the storage after successfully
     * publishing the friend to storage.
     *
     * @param friend Friend to be published. Must have id == NO_ID else will error.
     * @return NO_ERROR upon successfully publishing friend to storage. Otherwise respective error code.
     */
    public DaoError add(Friend friend);

    /**
     * Delete a friend from storage.
     * <p/>
     * A friend with the id must be in the storage.
     *
     * @param id ID of friend to be deleted from storage.
     * @return NO_ERROR upon successfully deleting friend from storage. Otherwise respective error code.
     */
    public DaoError delete(int id);

    /**
     * Update a friend in the storage.
     * <p/>
     * The friend object must already have been published to the storage.
     *
     * @param friend Friend object to be updated.
     * @return NO_ERROR upon successfully updating friend in storage. Otherwise respective error code.
     */
    public DaoError update(Friend friend);

    /**
     * Find a friend in storage by its ID.
     *
     * @param id Id to find friend by.
     * @return A reference to Friend object found in storage. null if no friend with such ID exists.
     */
    public Friend findById(int id);

    /**
     * Find a friend in storage by its ip address.
     *
     * @param ip IP address to find friend by.
     * @return A reference to Friend object found in storage. null if no friend with such ID exists.
     */
    public Friend findByIp(InetAddress ip);

    /**
     * Find a friend in storage by its mac address.
     *
     * @param mac MAC address to find friend by.
     * @return A reference to Friend object found in storage. null if no friend with such ID exists.
     */
    public Friend findByMacAddress(long mac);

    /**
     * Find friend in storage with a certain name.
     *
     * @param name Name of the friend to find by.
     * @return A list of Friend object found in storage.
     */
    public List<Friend> findByName(String name);

    /**
     * Find friend in storage with a certain favor state
     *
     * @param isFavourite Favor state to find by.
     * @return A list of Friend object found in storage.
     */
    public List<Friend> findByIsFavourite(boolean isFavourite);

    //TODO: define status in friend
    //public List<Friend> findByStatus(Friend.Status status);

    /**
     * Get a list of all friends.
     * @return A list of all friends in storage.
     */
    public List<Friend> findAll();
}
