package com.iotbyte.wifipidgin.dao.sqlitedao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.datasource.sqlite.WifiPidginSqliteHelper;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.utils.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
        Log.d(TAG, "About to add friend. mac:" + friend.getMac().toString() +
                   " ip:" + friend.getIp().getHostAddress());
        ContentValues values = friendToContentValues(friend);
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        try {
            long rowId = db.insert(FRIEND_TABLE, null, values);
            friend.setId(rowId);
            if (rowId == -1) {
                return DaoError.ERROR_SAVE;
            }
        } finally {
            db.close();
        }
        return DaoError.NO_ERROR;
    }

    @Override
    public DaoError delete(long id) {
        String[] whereArgs = {Long.toString(id)};
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        try {
            int rows = db.delete(FRIEND_TABLE, ID_FIELD + " = ?", whereArgs);
            if (rows == 0) {
                return DaoError.ERROR_NO_RECORD;
            }
            assert rows == 1;
        } finally {
            db.close();
        }
        return DaoError.NO_ERROR;
    }

    @Override
    public DaoError update(Friend friend) {
        long id = friend.getId();
        if (id == Friend.NO_ID) {
            return DaoError.ERROR_RECORD_NEVER_SAVED;
        }

        ContentValues values = friendToContentValues(friend);
        String[] whereArgs = {Long.toString(id)};
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        try {
            int rows = db.update(FRIEND_TABLE, values, ID_FIELD + " = ?", whereArgs);
            if (rows == 0) {
                return DaoError.ERROR_NO_RECORD;
            }
            assert rows == 1;
        } finally {
            db.close();
        }
        return DaoError.NO_ERROR;
    }

    @Override
    public Friend findById(long id) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {Long.toString(id)};
            c = db.query(FRIEND_TABLE, ALL_COLUMNS, ID_FIELD + " = ?", whereArgs, null, null, null);
            List<Friend> fl = getFriendsFromCursor(c);
            assert fl.size() <= 1;
            return fl.size() == 0 ? null : fl.get(0);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public Friend findByIp(InetAddress ip) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {ip.getHostAddress()};
            c = db.query(FRIEND_TABLE, ALL_COLUMNS, IP_FIELD + " = ?", whereArgs, null, null, null);
            List<Friend> fl = getFriendsFromCursor(c);
            assert fl.size() <= 1;
            return fl.size() == 0 ? null : fl.get(0);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public Friend findByMacAddress(byte[] mac) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {Utils.bytesToHex(mac)};
            c = db.query(FRIEND_TABLE, ALL_COLUMNS, MAC_ADDR_FIELD + " = ?", whereArgs, null, null, null);
            List<Friend> fl = getFriendsFromCursor(c);
            assert fl.size() <= 1;
            return fl.size() == 0 ? null : fl.get(0);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public List<Friend> findByName(String name) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {name};
            c = db.query(FRIEND_TABLE, ALL_COLUMNS, NAME_FIELD + " = ?", whereArgs, null, null, null);
            return getFriendsFromCursor(c);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public List<Friend> findByIsFavourite(boolean isFavourite) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] whereArgs = {isFavourite ? "1" : "0"};
            c = db.query(FRIEND_TABLE, ALL_COLUMNS, IS_FAVOURITE_FIELD + " = ?", whereArgs, null, null, null);
            return getFriendsFromCursor(c);
        } finally {
            cleanup(db, c);
        }
    }

    @Override
    public List<Friend> findAll() {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(FRIEND_TABLE, ALL_COLUMNS, "", null, null, null, null);
            return getFriendsFromCursor(c);
        } finally {
            cleanup(db, c);
        }
    }

    static final String FRIEND_TABLE = "friend";

    static final String ID_FIELD = "_id";
    static final String MAC_ADDR_FIELD = "mac_addr";
    static final String IP_FIELD = "ip";
    static final String PORT_FIELD = "port";
    static final String NAME_FIELD = "name";
    static final String DESCRIPTION_FIELD = "description";
    static final String STATUS_FIELD = "status";
    static final String IMAGE_PATH_FIELD = "image_path";
    static final String IS_FAVOURITE_FIELD = "is_favourite";

    static final String[] ALL_COLUMNS = {ID_FIELD,
                                         MAC_ADDR_FIELD,
                                         IP_FIELD,
                                         PORT_FIELD,
                                         NAME_FIELD,
                                         DESCRIPTION_FIELD,
                                         STATUS_FIELD,
                                         IMAGE_PATH_FIELD,
                                         IS_FAVOURITE_FIELD};
    static private final String TAG = "FriendSqliteDao";

    /** Database helper for db operation */
    private WifiPidginSqliteHelper sqliteHelper;

    /**
     * Helper method to turn Friend object into ContentValues for writing to database.
     * @param f Friend object to be turned into ContentValues
     */
    private ContentValues friendToContentValues(Friend f) {
        ContentValues values = new ContentValues();
        values.put(MAC_ADDR_FIELD, Utils.bytesToHex(f.getMac()));
        values.put(IP_FIELD, f.getIp().getHostAddress());
        values.put(NAME_FIELD, f.getName());
        values.put(PORT_FIELD, f.getPort());
        values.put(DESCRIPTION_FIELD, f.getDescription());
        values.put(STATUS_FIELD, f.getStatus().getValue());
        values.put(IMAGE_PATH_FIELD, f.getImagePath());
        values.put(IS_FAVOURITE_FIELD, f.isFavourite());
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
     * Helper method to construct a Friend objects from data stored in cursor
     * @param c Cursor containing rows of friend data returned by database.
     *          The query producing the cursor must select all columns.
     * @return Friend object constructed from the current row that the cursor is pointing to.
     *         Null if there an error accessing the cursor.
     */
    private Friend getFriendFromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndex(FriendSqliteDao.ID_FIELD));
        String macAddrStr = c.getString(c.getColumnIndex(FriendSqliteDao.MAC_ADDR_FIELD));
        byte[] macAddr = Utils.hexStringToByteArray(macAddrStr);
        InetAddress ip = null;
        String ipHost = c.getString(c.getColumnIndex(FriendSqliteDao.IP_FIELD));
        try {
            ip = InetAddress.getByName(ipHost);
        } catch (UnknownHostException e) {
            // should not hit this, only if database is corrupted
            assert false;
            Log.e(TAG, "Unknown ip:" + ipHost
                    + ", rowId:" + id + " read from database.");
            return null;
        }
        int port = c.getInt(c.getColumnIndex(FriendSqliteDao.PORT_FIELD));
        String name = c.getString(c.getColumnIndex(FriendSqliteDao.NAME_FIELD));
        String description = c.getString(c.getColumnIndex(FriendSqliteDao.DESCRIPTION_FIELD));
        Friend.FriendStatus status = null;
        int statusInt = c.getInt(c.getColumnIndex(FriendSqliteDao.STATUS_FIELD));
        try {
            status = Friend.FriendStatus.values()[statusInt];

        } catch (ArrayIndexOutOfBoundsException e) {
            assert false;
            Log.e(TAG, "Unknown status:" + statusInt
                    + ", rowId:" + id + " read from database.");
            return null;
        }
        String imagePath = c.getString(c.getColumnIndex(FriendSqliteDao.IMAGE_PATH_FIELD));
        boolean isFavourite = c.getInt(c.getColumnIndex(FriendSqliteDao.IS_FAVOURITE_FIELD)) == 0;

        Friend f = new Friend(macAddr, ip, port);
        f.setId(id);
        f.setName(name);
        f.setDescription(description);
        f.setStatus(status);
        f.setImagePath(imagePath);
        f.setFavourite(isFavourite);

        Log.d(TAG, "Creating friend from cursor data."
                +  " id:" + id
                +  " macAddr:" + macAddr
                +  " ip:" + ip.getAddress()
                +  " port:" + port
                +  " name:" + name
                +  " description:" + description
                +  " status:" + statusInt
                +  " imagePath:" + imagePath
                +  " isFavourite:" + isFavourite);
        return f;
    }

    /**
     * Helper method to construct a list of Friend objects from data stored in cursor
     * @param c Cursor containing the data returned by database.
     * The query producing the cursor must select all columns.
     * @return List of friend objects constructed from the cursor.
     *         Empty list if there's an error accessing the cursor.
     */
    private List<Friend> getFriendsFromCursor(Cursor c) {
        ArrayList<Friend> friendList = new ArrayList<>();

        if (c == null || !c.moveToFirst() || c.getCount() == 0) {
            // empty list
            return friendList;
        }

        friendList.ensureCapacity(c.getCount());
        do {
            Friend f = getFriendFromCursor(c);
            if (f != null) {
                friendList.add(f);
            }
        } while (c.moveToNext());
        return friendList;
    }
}
