package com.iotbyte.wifipidgin.dao.sqlitedao;

import com.iotbyte.wifipidgin.channel.Channel;

/**
 * Data structure to hold channel friend association info
 * Note the class is only visible in the package.
 */
final class ChannelFriendRelation {
    /** database row id */
    public final long rowId;
    /** channel id */
    public final long channelId;
    /** friend id */
    public final long friendId;

    public ChannelFriendRelation(long rowId, long channelId, long friendId) {
        this.rowId = rowId;
        this.channelId = channelId;
        this.friendId = friendId;
    }

    @Override
    /** rowId is not taken into account for equality */
    public boolean equals(Object o) {
        if (!(o instanceof ChannelFriendRelation)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        ChannelFriendRelation rhs = (ChannelFriendRelation)o;
        return this.channelId == rhs.channelId && this.friendId == rhs.friendId;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Long.valueOf(channelId).hashCode();
        hash = hash * 31 + Long.valueOf(friendId).hashCode();
        return hash;
    }
}