package com.iotbyte.wifipidgin.dao.sqlitedao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.datasource.sqlite.WifiPidginSqliteHelper;

import java.util.List;

/**
 * SQLite implementation of ChannelDao
 */
public class ChannelSqliteDao implements ChannelDao {
    /**
     * Constructor
     * @param context Context
     */
    public ChannelSqliteDao(Context context) {
        this.sqliteHelper = new WifiPidginSqliteHelper(context);
    }

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

    /** Database helper for db operation */
    private WifiPidginSqliteHelper sqliteHelper;
}
