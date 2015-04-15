package com.iotbyte.wifipidgin.dao.sqlitedao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.datasource.sqlite.WifiPidginSqliteHelper;
import com.iotbyte.wifipidgin.friend.Friend;

import java.net.InetAddress;
import java.util.List;

/**
 * SQLite implementation of FriendDao
 */
public class FriendSqliteDao implements FriendDao {
    /**
     * Constructor
     * @param context Context
     */
    public FriendSqliteDao(Context context) {
        this.sqliteHelper = new WifiPidginSqliteHelper(context);
    }

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

    /** Database helper for db operation */
    private WifiPidginSqliteHelper sqliteHelper;
}
