package com.iotbyte.wifipidgin.dao.sqlitedao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.dao.ChannelDao;
import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.datasource.sqlite.WifiPidginSqliteHelper;
import com.iotbyte.wifipidgin.friend.Friend;

import java.util.ArrayList;
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
        this.context = context;
        this.sqliteHelper = new WifiPidginSqliteHelper(context);
    }

    @Override
    public DaoError add(Channel channel) {
        ContentValues channelValues = channelContentValues(channel);

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        // wrap everything into a transaction. Everything will be rolled back if
        // something failed in between.
        db.beginTransaction();
        try {
            long rowId = db.insert(CHANNEL_TABLE, null, channelValues);
            if (rowId == -1) {
                return DaoError.ERROR_SAVE;
            }

            // now deal with friends that belongs to a channel
            List<Friend> friendList = channel.getFriendsList();
            for (Friend f : friendList) {
                // It's safe to assume friend that belongs to a channel has
                // already been published to db.
                assert f.getId() != Friend.NO_ID;

                ContentValues values = channelFriendListContentValues(channel.getId(), f.getId());
                if (db.insert(CHANNEL_FRIEND_LIST_TABLE, null, values) == -1) {
                    return DaoError.ERROR_SAVE;
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, "Constrain violation when insert into db:" + e);
            return DaoError.ERROR_SAVE;
        } finally {
            db.endTransaction();
            db.close();
        }
        return DaoError.NO_ERROR;
    }

    @Override
    public DaoError delete(long id) {
        String[] whereArgs = {Long.toString(id)};
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        // foreign key on channel_friend_list table has a on delete cascade,
        // so it should also get taken out.
        try {
            int rows = db.delete(CHANNEL_TABLE, ID_FIELD + " = ?", whereArgs);
            if (rows == 0) {
                return DaoError.ERROR_NO_RECORD;
            }
            assert rows == 1;
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, "Constrain violation when deleting from db:" + e);
            return DaoError.ERROR_SAVE;
        } finally {
            db.close();
        }
        return DaoError.NO_ERROR;
    }

    @Override
    public DaoError update(Channel channel) {
        // sanity check
        long channelId = channel.getId();
        if (channelId == Channel.NO_ID) {
            return DaoError.ERROR_RECORD_NEVER_SAVED;
        }


        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        // wrap everything into a transaction. Everything will be rolled back if
        // something failed in between.
        db.beginTransaction();
        try {
            // update channel in database
            {
                ContentValues channelValues = channelContentValues(channel);
                String[] whereArgs = {Long.toString(channelId)};
                int rows =
                    db.update(CHANNEL_TABLE, channelValues, ID_FIELD + " = ?", whereArgs);
                if (rows == 0) {
                    return DaoError.ERROR_NO_RECORD;
                }
                assert rows == 1;
            }

            // next deal with friends in the channel:
            // get list A - friends belong to this channel from the object.
            List<ChannelFriendRelation> fromDb = new ArrayList<>();
            {
                String[] whereArgs = {Long.toString(channelId)};
                Cursor c = db.query(CHANNEL_FRIEND_LIST_TABLE,
                        CHANNEL_FRIEND_LIST_TABLE_ALL_COLUMNS,
                        CHANNEL_ID_FIELD + " = ?", whereArgs, null, null, null);
                if (c == null || !c.moveToFirst()) {
                    return DaoError.ERROR_SAVE;
                }
                do {
                    long rowId = c.getLong(c.getColumnIndex(ID_FIELD));
                    long friendId = c.getLong(c.getColumnIndex(FRIEND_ID_FIELD));
                    fromDb.add(new ChannelFriendRelation(rowId, channelId, friendId));
                } while (c.moveToNext());
                c.close();
            }

            // get list B - friends belong to this channel from db,
            List<ChannelFriendRelation> fromObj = new ArrayList<>();
            for (Friend f : channel.getFriendsList()) {
                assert f.getId() != Friend.NO_ID;
                fromObj.add(new ChannelFriendRelation(/* no row id */-1, channelId, f.getId()));
            }

            // friends only in listB needs to be deleted from db.
            for (ChannelFriendRelation o : fromDb) {
                if (!fromObj.contains(o)) {
                    String[] whereArgs = {Long.toString(o.rowId)};
                    int rows = db.delete(CHANNEL_FRIEND_LIST_TABLE, ID_FIELD + " = ?", whereArgs);
                    if (rows != 1) {
                        return DaoError.ERROR_SAVE;
                    }
                }
            }
            // friends only in listA needs to be added to db.
            for (ChannelFriendRelation o : fromObj) {
                if (!fromDb.contains(o)) {
                    ContentValues values = channelFriendListContentValues(o.channelId, o.friendId);
                    long rowId = db.insert(CHANNEL_FRIEND_LIST_TABLE, null, values);
                    if (rowId == -1) {
                        return DaoError.ERROR_SAVE;
                    }
                }
            }

        } catch (SQLiteConstraintException e) {
            Log.d(TAG, "Constrain violation when updating db:" + e);
        } finally {
            db.endTransaction();
            db.close();
        }
        return DaoError.NO_ERROR;
    }

    @Override
    public Channel findById(long id) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {Long.toString(id)};

            c = db.query(CHANNEL_TABLE,
                CHANNEL_TABLE_ALL_COLUMNS,
                ID_FIELD + "= ?", whereArgs,
                null, null, null);
            List<Channel> cl = getChannelsFromCursor(db, c);
            assert cl.size() <= 1;
            return cl.size() == 0 ? null : cl.get(0);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public Channel findByChannelIdentifier(String channelIdentifier) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {channelIdentifier};
            c = db.query(CHANNEL_TABLE,
                    CHANNEL_TABLE_ALL_COLUMNS,
                    IDENTIFIER_FIELD + "= ?", whereArgs,
                    null, null, null);
            List<Channel> cl = getChannelsFromCursor(db, c);
            assert cl.size() <= 1;
            return cl.size() == 0 ? null : cl.get(0);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public List<Channel> findByName(String name) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {name};
            c = db.query(CHANNEL_TABLE,
                    CHANNEL_TABLE_ALL_COLUMNS,
                    NAME_FIELD + "= ?", whereArgs,
                    null, null, null);
            return getChannelsFromCursor(db, c);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public List<Channel> findAll() {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(CHANNEL_TABLE,
                    CHANNEL_TABLE_ALL_COLUMNS,
                    "", null, null, null, null);
            return getChannelsFromCursor(db, c);
        } finally {
            cleanup(db, c);
        }
    }

    static final String CHANNEL_TABLE = "channel";
    static final String ID_FIELD = "_id";
    static final String IDENTIFIER_FIELD = "identifier";
    static final String NAME_FIELD = "name";
    static final String DESCRIPTIO_FIELD = "description";
    static final String[] CHANNEL_TABLE_ALL_COLUMNS = {ID_FIELD,
                                                       IDENTIFIER_FIELD,
                                                       NAME_FIELD,
                                                       DESCRIPTIO_FIELD};
    static final String CHANNEL_FRIEND_LIST_TABLE = "channel_friend_list";
    static final String FRIEND_ID_FIELD = "friend_id";
    static final String CHANNEL_ID_FIELD = "channel_id";
    static final String[] CHANNEL_FRIEND_LIST_TABLE_ALL_COLUMNS = {ID_FIELD,
                                                                   FRIEND_ID_FIELD,
                                                                   CHANNEL_ID_FIELD};
    static private final String TAG = "ChannelSqliteDao";

    private Context context;
    /** Database helper for db operation */
    private WifiPidginSqliteHelper sqliteHelper;

    /**
     * Helper method to turn Channel object into ContentValues for writing to database.
     * @param c Channel object to be turned into ContentValues
     */
    private ContentValues channelContentValues(Channel c) {
        ContentValues values = new ContentValues();
        values.put(IDENTIFIER_FIELD, c.getChannelIdentifier());
        values.put(NAME_FIELD, c.getName());
        values.put(DESCRIPTIO_FIELD, c.getDescription());
        return values;
    }

    /**
     * Helper method to create a channel_friend_list table content values
     */
    private ContentValues channelFriendListContentValues(long channelId, long friendId) {
        ContentValues values = new ContentValues();
        values.put(FRIEND_ID_FIELD, friendId);
        values.put(CHANNEL_ID_FIELD, channelId);
        return values;
    }

    /** Helper method to cleanup open database and cursor */
    private void cleanup(SQLiteDatabase db, Cursor c) {
        if (c != null) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
    }

    /**
     * Helper method to get a list of friends objects that are associated with a channel id
     */
    private List<Friend> getChannelFriends(SQLiteDatabase db, long channelId) {
        List<Friend> fl = new ArrayList<>();

        String[] whereArgs = {Long.toString(channelId)};
        Cursor c = db.query(CHANNEL_FRIEND_LIST_TABLE,
                CHANNEL_FRIEND_LIST_TABLE_ALL_COLUMNS,
                CHANNEL_ID_FIELD + "= ?", whereArgs,
                null, null, null);
        if (c == null || !c.moveToFirst()) {
            Log.e(TAG, "Failed to get friends associated with channel id:" + channelId);
            return fl;
        }

        do {
            long friendId = c.getLong(c.getColumnIndex(FRIEND_ID_FIELD));
            Friend f = DaoFactory.getInstance()
                    .getFriendDao(this.context, DaoFactory.DaoType.SQLITE_DAO, null)
                    .findById(friendId);
            if (f == null) {
                assert false;
                continue;
            }
            fl.add(f);
        } while (c.moveToNext());
        c.close();
        return fl;
    }

    /**
     * Helper method to construct Channel objects from data stored in cursor.
     * @param c Cursor containing rows of channel data returned by database.
     *          The query producing the cursor must select all columns.
     * @return Channel objects constructed from the current row that cursor is pointing to.
     *         The returned channel object has no friends associated with it.
     *         Null if there an error accessing the cursor.
     */
    private Channel getChannelFromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndex(ChannelSqliteDao.ID_FIELD));
        String channelIdentifier = c.getString(c.getColumnIndex(ChannelSqliteDao.IDENTIFIER_FIELD));
        String name = c.getString(c.getColumnIndex(ChannelSqliteDao.NAME_FIELD));
        String description = c.getString(c.getColumnIndex(ChannelSqliteDao.DESCRIPTIO_FIELD));
        Channel channel = new Channel(channelIdentifier);
        channel.setId(id);
        channel.setName(name);
        channel.setDescription(description);

        Log.d(TAG, "Creating channel from cursor data."
                + " id:" + id
                + " name:" + name
                + " channel identifier:" + channelIdentifier
                + " description:" + description);
        return channel;
    }

    /**
     * Helper method to construct a list of Channel objects from data stored in cursor
     * @param c Cursor containing the data returned by database.
     * The query producing the cursor must select all columns.
     * @return List of channel objects constructed from the cursor.
     *         Empty list if there's an error accessing the cursor.
     */
    private List<Channel> getChannelsFromCursor(SQLiteDatabase db, Cursor c) {
        ArrayList<Channel> channelList = new ArrayList<>();

        if (c == null || !c.moveToFirst() || c.getCount() == 0) {
            return channelList;
        }

        channelList.ensureCapacity(c.getCount());
        do {
            Channel channel = getChannelFromCursor(c);
            if (c != null) {
                List<Friend> fl = getChannelFriends(db, channel.getId());
                for (Friend f : fl) {
                    channel.addFriend(f);
                }
                channelList.add(channel);
            }
        } while (c.moveToNext());
        return channelList;
    }
}
