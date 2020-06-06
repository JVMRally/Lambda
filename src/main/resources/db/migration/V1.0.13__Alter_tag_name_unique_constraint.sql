ALTER TABLE tags DROP CONSTRAINT tags_tagname_key;
ALTER TABLE tags ADD UNIQUE (tagname, guild_id);
