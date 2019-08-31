CREATE TABLE IF NOT EXISTS tags (
    id SERIAL PRIMARY KEY,
    tagname varchar(50) UNIQUE,
    content text NOT NULL,
    updated_at timestamptz NOT NULL
);