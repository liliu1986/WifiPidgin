package com.iotbyte.wifipidgin.dao.dummydao;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoError;

import java.util.List;

/**
 * Dummy implementation of ChannelDao interface to make code compile.
 */
public class DummyChannelDao implements ChannelDao {

    @Override
    public DaoError add(Channel channel) {
        return null;
    }

    @Override
    public DaoError delete(int id) {
        return null;
    }

    @Override
    public DaoError update(Channel channel) {
        return null;
    }

    @Override
    public Channel findById(int id) {
        return null;
    }

    @Override
    public Channel findByChannelIdentifier(String channelIdentifier) {
        return null;
    }

    @Override
    public List<Channel> findByName(String name) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return null;
    }
}
