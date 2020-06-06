CREATE TABLE IF NOT EXISTS disabled_commands (
    id SERIAL PRIMARY KEY,
    guild_id VARCHAR(50) NOT NULL,
    command_name VARCHAR(50) NOT NULL,
    UNIQUE (guild_id, command_name)
);
