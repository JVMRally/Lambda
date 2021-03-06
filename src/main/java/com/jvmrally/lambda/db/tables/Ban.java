/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db.tables;


import com.jvmrally.lambda.db.DefaultSchema;
import com.jvmrally.lambda.db.Keys;
import com.jvmrally.lambda.db.tables.records.BanRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class Ban extends TableImpl<BanRecord> {

    private static final long serialVersionUID = 2125711973;

    /**
     * The reference instance of <code>ban</code>
     */
    public static final Ban BAN = new Ban();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BanRecord> getRecordType() {
        return BanRecord.class;
    }

    /**
     * The column <code>ban.userid</code>.
     */
    public final TableField<BanRecord, Long> USERID = createField(DSL.name("userid"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>ban.ban_expiry</code>.
     */
    public final TableField<BanRecord, Long> BAN_EXPIRY = createField(DSL.name("ban_expiry"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("'1976256242000'::bigint", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>ban.guild_id</code>.
     */
    public final TableField<BanRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("'607965294731853855'::bigint", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>ban</code> table reference
     */
    public Ban() {
        this(DSL.name("ban"), null);
    }

    /**
     * Create an aliased <code>ban</code> table reference
     */
    public Ban(String alias) {
        this(DSL.name(alias), BAN);
    }

    /**
     * Create an aliased <code>ban</code> table reference
     */
    public Ban(Name alias) {
        this(alias, BAN);
    }

    private Ban(Name alias, Table<BanRecord> aliased) {
        this(alias, aliased, null);
    }

    private Ban(Name alias, Table<BanRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Ban(Table<O> child, ForeignKey<O, BanRecord> key) {
        super(child, key, BAN);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<BanRecord> getPrimaryKey() {
        return Keys.BAN_PKEY;
    }

    @Override
    public List<UniqueKey<BanRecord>> getKeys() {
        return Arrays.<UniqueKey<BanRecord>>asList(Keys.BAN_PKEY);
    }

    @Override
    public Ban as(String alias) {
        return new Ban(DSL.name(alias), this);
    }

    @Override
    public Ban as(Name alias) {
        return new Ban(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Ban rename(String name) {
        return new Ban(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Ban rename(Name name) {
        return new Ban(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
