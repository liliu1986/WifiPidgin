-- Creates SQLite database schema.
-- When modifying this file:
--      Need to update DB version in WifiPidginSqliteHelper
--      Need to update dropDb.sql

CREATE TABLE friend
(
    _id         INTEGER PRIMARY KEY AUTOINCREMENT,
    -- MAC stored as hex string
    mac_addr    TEXT NOT NULL UNIQUE,
    -- IP stored as string (InetAddress.getHostAddress())
    ip          TEXT NOT NULL,
    port        INTEGER NOT NULL,
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

-- Create self in database
INSERT INTO friend (_id, mac_addr, ip, port, name, description, status, image_path)
 VALUES (0, "ff:ff:ff:ff:ff:ff", "127.0.0.1", 65535, "New User", "", 1, "");