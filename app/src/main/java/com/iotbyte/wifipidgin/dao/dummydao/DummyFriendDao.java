package com.iotbyte.wifipidgin.dao.dummydao;

import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;

import java.net.InetAddress;
import java.util.List;

/**
 * Dummy implementation of FriendDao interface to make code compile.
 */
public class DummyFriendDao implements FriendDao {

    @Override
    public DaoError add(Friend friend) {
        return null;
    }

    @Override
    public DaoError delete(int id) {
        return null;
    }

    @Override
    public DaoError update(Friend friend) {
        return null;
    }

    @Override
    public Friend findById(int id) {
        return null;
    }

    @Override
    public Friend findByIp(InetAddress ip) {
        return null;
    }

    @Override
    public Friend findByMacAddress(long mac) {
        return null;
    }

    @Override
    public List<Friend> findByName(String name) {
        return null;
    }

    @Override
    public List<Friend> findByIsFavourite(boolean isFavourite) {
        return null;
    }

    @Override
    public List<Friend> findAll() {
        return null;
    }
}
