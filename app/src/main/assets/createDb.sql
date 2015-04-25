-- Creates SQLite database schema.
-- When modifying this file:
--      Need to update DB version in WifiPidginSqliteHelper
--      Need to update dropDb.sql

CREATE TABLE friend
(
    _id         INTEGER PRIMARY KEY AUTOINCREMENT,
    -- MAC stored as a byte field
    mac_addr    TEXT NOT NULL UNIQUE,
    -- IP stored as a byte field
    ip          TEXT NOT NULL UNIQUE,
    name        TEXT,
    description TEXT,
    status      INTEGER,
    image_path  TEXT,
    is_favourite INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE channel
(
    _id         INTEGER PRIMARY KEY AUTOINCREMENT,
    identifier  TEXT NOT NULL UNIQUE,
    name        TEXT,
    description TEXT
);

CREATE TABLE channel_friend_list
(
    _id         INTEGER PRIMARY KEY AUTOINCREMENT,
    friend_id   INTEGER NOT NULL,
    channel_id  INTEGER NOT NULL,
    -- restrict on deletion of friend if it still belongs to a channel
    FOREIGN KEY(friend_id) REFERENCES friend(_id) ON DELETE RESTRICT,
    -- cascade delete all friends-channel record when channel is deleted
    FOREIGN KEY(channel_id) REFERENCES channel(_id) ON DELETE CASCADE
);
