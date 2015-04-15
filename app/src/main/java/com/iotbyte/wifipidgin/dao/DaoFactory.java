package com.iotbyte.wifipidgin.dao;

import com.iotbyte.wifipidgin.dao.dummydao.DummyChannelDao;
import com.iotbyte.wifipidgin.dao.dummydao.DummyFriendDao;

/**
 * Factory to create concrete Dao object
 */
public class DaoFactory {

    /**
     * Dao implementation type
     */
    public enum DaoType {
        SQLITE_DAO,
        DUMMY_DAO,
    }

    /**
     * Create a Concrete friend DAO object.
     * @param type type of dao to be created
     * @param resource resource string to be passed along to DAO object
     * @return An instance of the DAO object. null if errors.
     */
    public FriendDao getFriendDao(DaoType type, String resource) {
        switch (type) {
            case SQLITE_DAO:
                return null;
            case DUMMY_DAO:
                return new DummyFriendDao();
        }
        // Should not get here.
        assert false;
        return null;
    }

    /**
     * Create a Concrete channel DAO object.
     * @param type type of dao to be created
     * @param resource resource string to be passed along to DAO object
     * @return An instance of the DAO object. null if errors.
     */
    public ChannelDao getChannelDao(DaoType type, String resource) {
        switch (type) {
            case SQLITE_DAO:
                return null;
            case DUMMY_DAO:
                return new DummyChannelDao();
        }
        // Should not get here.
        assert false;
        return null;
    }

}
