package com.iotbyte.wifipidgin.dao;

import android.content.Context;

import com.iotbyte.wifipidgin.dao.event.DaoEventBoard;
import com.iotbyte.wifipidgin.dao.event.DaoEventBoardImpl;
import com.iotbyte.wifipidgin.dao.sqlitedao.ChannelSqliteDao;
import com.iotbyte.wifipidgin.dao.sqlitedao.FriendSqliteDao;

/**
 * Factory to create concrete Dao object
 */
public class DaoFactory {

    /**
     * Dao implementation type
     */
    public enum DaoType {
        SQLITE_DAO,
    }

    /**
     * Create a Concrete friend DAO object.
     * @param context Context
     * @param type type of dao to be created
     * @param resource resource string to be passed along to DAO object
     * @return An instance of the DAO object. null if errors.
     */
    public FriendDao getFriendDao(Context context, DaoType type, String resource) {
        switch (type) {
            case SQLITE_DAO:
                return new FriendSqliteDao(context, eventBoard);
        }
        // Should not get here.
        assert false;
        return null;
    }

    /**
     * Create a Concrete channel DAO object.
     * @param context Context
     * @param type type of dao to be created
     * @param resource resource string to be passed along to DAO object
     * @return An instance of the DAO object. null if errors.
     */
    public ChannelDao getChannelDao(Context context, DaoType type, String resource) {
        switch (type) {
            case SQLITE_DAO:
                return new ChannelSqliteDao(context, eventBoard);
        }
        // Should not get here.
        assert false;
        return null;
    }

    public static synchronized DaoFactory getInstance() {
        if (instance == null) {
            instance = new DaoFactory();
        }
        return instance;
    }

    private DaoFactory() {
        this.eventBoard = new DaoEventBoardImpl();
    }

    private static DaoFactory instance = null;

    /** Cached event board where all dao events go to. */
    private DaoEventBoard eventBoard;
}
