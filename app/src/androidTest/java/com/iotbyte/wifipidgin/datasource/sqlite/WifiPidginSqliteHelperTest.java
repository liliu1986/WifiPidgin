package com.iotbyte.wifipidgin.datasource.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test for WifiPidginSqliteHelper
 */
public class WifiPidginSqliteHelperTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception{
        super.tearDown();
    }

    /** Test a writable database can be created and all expected tables are in the database */
    public void testOnCreate() {
        Context context = getContext();
        context.deleteDatabase(WifiPidginSqliteHelper.DB_NAME);

        WifiPidginSqliteHelper dut = new WifiPidginSqliteHelper(context);

        // implicitly calls dut.onCreate() if the database is not yet created.
        SQLiteDatabase db = dut.getWritableDatabase();
        assertTrue(db.isOpen());

        // Test all expected tables are in the database
        ArrayList<String> expectedTables =
            new ArrayList<>(Arrays.asList("friend", "channel", "channel_friend_list"));

        // SELECT name FROM sqlite_master WHERE type = "table" AND NOT name = "sqlite_sequence";
        final String table = "sqlite_master";
        final String[] columns = {"name"};
        final String where = "type = ? AND NOT name IN (?, ?)";
        final String[] whereArgs = {"table", "sqlite_sequence", "android_metadata"};
        Cursor c = db.query(table, columns, where, whereArgs, null, null, null);
        assertEquals("Database contains unexpected number of tables",
                     expectedTables.size(), c.getCount());

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String tableName = c.getString(0);
            assertTrue("Database contains unexpected table:" + tableName,
                    expectedTables.contains(tableName));
            c.moveToNext();
        }

        c.close();
        db.close();
        context.deleteDatabase(WifiPidginSqliteHelper.DB_NAME);
    }
}
