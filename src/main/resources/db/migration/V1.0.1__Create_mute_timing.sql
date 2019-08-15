CREATE TABLE IF NOT EXISTS mute  (
    userid bigint UNIQUE NOT NULL PRIMARY KEY,
    mute_expiry bigint NOT NULL DEFAULT 0
);