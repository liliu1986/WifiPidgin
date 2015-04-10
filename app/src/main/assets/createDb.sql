-- Creates SQLite database schema.
-- When modifying this file:
--      Need to update DB version in WifiPidginSqliteHelper
--      Need to update dropDb.sql

CREATE TABLE friend
(
    _id         INT PRIMARY KEY AUTOINCREMENT,
    mac_addr    TEXT NOT NULL UNIQUE,
    ip          INT NOT NULL UNIQUE,
    name        TEXT,
    description TEXT,
    status      INT,
    is_favourite INT NOT NULL DEFAULT 0
);

CREATE TABLE channel
(
    _id         INT PRIMARY KEY AUTOINCREMENT,
    identifier  TEXT NOT NULL UNIQUE,
    name        TEXT,
    description TEXT
);

CREATE TABLE channel_friend_list
(
    _id         INT PRIMARY KEY AUTOINCREMENT,
    friend_id   INT NOT NULL,
    channel_id  INT NOT NULL,
    -- restrict on deletion of friend if it still belongs to a channel
    FOREIGN KEY(friend_id) REFERENCES friend(_id) ON DELETE RESTRICT,
    -- cascade delete all friends-channel record when channel is deleted
    FOREIGN KEY(channel_id) REFERENCES channel(_id) ON DELETE CASCADE
);
