CREATE TABLE IF NOT EXISTS dm_timeouts  (
    userid bigint UNIQUE NOT NULL PRIMARY KEY,
    last_message_time bigint NOT NULL DEFAULT 0
);