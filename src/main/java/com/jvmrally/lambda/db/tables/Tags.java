/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db.tables;


import com.jvmrally.lambda.db.DefaultSchema;
import com.jvmrally.lambda.db.Keys;
import com.jvmrally.lambda.db.tables.records.TagsRecord;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tags extends TableImpl<TagsRecord> {

    private static final long serialVersionUID = -1467714301;

    /**
     * The reference instance of <code>tags</code>
     */
    public static final Tags TAGS = new Tags();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TagsRecord> getRecordType() {
        return TagsRecord.class;
    }

    /**
     * The column <code>tags.id</code>.
     */
    public final TableField<TagsRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('tags_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>tags.tagname</code>.
     */
    public final TableField<TagsRecord, String> TAGNAME = createField(DSL.name("tagname"), org.jooq.impl.SQLDataType.VARCHAR(50), this, "");

    /**
     * The column <code>tags.content</code>.
     */
    public final TableField<TagsRecord, String> CONTENT = createField(DSL.name("content"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>tags.updated_at</code>.
     */
    public final TableField<TagsRecord, OffsetDateTime> UPDATED_AT = createField(DSL.name("updated_at"), org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false), this, "");

    /**
     * The column <code>tags.guild_id</code>.
     */
    public final TableField<TagsRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("'607965294731853855'::bigint", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>tags</code> table reference
     */
    public Tags() {
        this(DSL.name("tags"), null);
    }

    /**
     * Create an aliased <code>tags</code> table reference
     */
    public Tags(String alias) {
        this(DSL.name(alias), TAGS);
    }

    /**
     * Create an aliased <code>tags</code> table reference
     */
    public Tags(Name alias) {
        this(alias, TAGS);
    }

    private Tags(Name alias, Table<TagsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Tags(Name alias, Table<TagsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Tags(Table<O> child, ForeignKey<O, TagsRecord> key) {
        super(child, key, TAGS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<TagsRecord, Integer> getIdentity() {
        return Keys.IDENTITY_TAGS;
    }

    @Override
    public UniqueKey<TagsRecord> getPrimaryKey() {
        return Keys.TAGS_PKEY;
    }

    @Override
    public List<UniqueKey<TagsRecord>> getKeys() {
        return Arrays.<UniqueKey<TagsRecord>>asList(Keys.TAGS_PKEY, Keys.TAGS_TAGNAME_GUILD_ID_KEY);
    }

    @Override
    public Tags as(String alias) {
        return new Tags(DSL.name(alias), this);
    }

    @Override
    public Tags as(Name alias) {
        return new Tags(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tags rename(String name) {
        return new Tags(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tags rename(Name name) {
        return new Tags(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, String, String, OffsetDateTime, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
