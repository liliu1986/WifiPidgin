package com.iotbyte.wifipidgin.dao;

import com.iotbyte.wifipidgin.channel.Channel;

import java.util.List;

/**
 * Provides an interface to publish and retrieve Channel object to/from storage
 */
public interface ChannelDao {
    /**
     * Publish a channel to storage.
     * <p/>
     * The id field of channel is updated to the id to find it in the storage after successfully
     * publishing the channel to storage.
     * <p/>
     * If the channel has any friends associated with it, these friends MUST have already been
     * published to the storage.
     *
     * @param channel Channel to be published. Must have id == NO_ID else will error.
     * @return NO_ERROR upon successfully publishing channel to storage. Otherwise respective error
     * code.
     */
    public DaoError add(Channel channel);

    /**
     * Delete a channel from storage.
     * <p/>
     * A channel with the id must be in the storage.
     *
     * @param id ID of channel to be deleted from storage.
     * @return NO_ERROR upon successfully deleting channel from storage. Otherwise respective error
     * code.
     */
    public DaoError delete(long id);

    /**
     * Update a channel in the storage.
     * <p/>
     * The channel object must already have been published to the storage.
     *
     * If the channel has any friends associated with it, these friends MUST have already been
     * published to the storage.
     *
     * @param channel Channel object to be updated.
     * @return NO_ERROR upon successfully updating channel in storage. Otherwise respective error
     * code.
     */
    public DaoError update(Channel channel);

    /**
     * Find a channel in storage by its ID.
     *
     * @param id Id to find channel by.
     * @return A reference to Channel object found in storage. null if no channel with such ID
     * exists.
     */
    public Channel findById(long id);

    /**
     * Find a channel in storage by its channelIdentifier address.
     *
     * @param channelIdentifier MAC address to find channel by.
     * @return A reference to Channel object found in storage. null if no channel with such ID exists.
     */
    public Channel findByChannelIdentifier(String channelIdentifier);

    /**
     * Find channel in storage with a certain name.
     *
     * @param name Name of the channel to find by.
     * @return A list of Channel object found in storage.
     */
    public List<Channel> findByName(String name);

    /**
     * Get a list of all channels.
     * @return A list of all channels in storage.
     */
    public List<Channel> findAll();

    /**
     * set the channelChangeListener
     */
    public void setChannelListChangedListener(ChannelListChangedListener channelChangeListener);

}
