CREATE TYPE audit_action AS ENUM ('BANNED', 'UNBANNED', 'WARNED', 'MUTED', 'UNMUTED', 'GIVE_ROLE', 'REMOVE_ROLE', 'CREATED_TAG', 'EDITED_TAG', 'DELETED_TAG');

CREATE TABLE IF NOT EXISTS audit  (
    id int UNIQUE NOT NULL PRIMARY KEY,
    user_id bigint NOT NULL DEFAULT 0,
    mod_action audit_action NOT NULL,
    target_user bigint NULL,
    reason varchar NULL,
    created timestamptz NOT NULL
);