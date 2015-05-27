package com.iotbyte.wifipidgin.datasource.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

/**
 * SQLite database helper for WifiPidgin
 */
public class WifiPidginSqliteHelper extends SQLiteOpenHelper {
    /** SQLite database name */
    public static final String DB_NAME = "WifiPidgin";

    public WifiPidginSqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            DbUtils.executeSqlScript(context, db, CREATE_DB_SCRIPT);
        } catch (IOException e) {
            Log.e(TAG, "Failed to open " + CREATE_DB_SCRIPT + " to create database.");
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
        db.enableWriteAheadLogging();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: onUpgrade - Not tested.
        try {
            DbUtils.executeSqlScript(context, db, DROP_DB_SCRIPT);
        } catch (IOException e) {
            Log.e(TAG, "Failed to open " + DROP_DB_SCRIPT + " to delete database.");
            e.printStackTrace();
        }
        onCreate(db);
    }

    /** Tag for logging */
    private static final String TAG = "WifiPidginSqliteHelper";
    /** SQLite database version */
    private static final int DB_VERSION = 1;
    /** SQLite create database script */
    private static final String CREATE_DB_SCRIPT = "createDb.sql";
    /** SQLite drop database script */
    private static final String DROP_DB_SCRIPT = "dropDb.sql";

    /** Saved context */
    private Context context;
}
