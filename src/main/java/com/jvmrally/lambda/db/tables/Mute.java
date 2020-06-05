/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db.tables;


import com.jvmrally.lambda.db.DefaultSchema;
import com.jvmrally.lambda.db.Keys;
import com.jvmrally.lambda.db.tables.records.MuteRecord;

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
public class Mute extends TableImpl<MuteRecord> {

    private static final long serialVersionUID = 1194273377;

    /**
     * The reference instance of <code>mute</code>
     */
    public static final Mute MUTE = new Mute();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MuteRecord> getRecordType() {
        return MuteRecord.class;
    }

    /**
     * The column <code>mute.userid</code>.
     */
    public final TableField<MuteRecord, Long> USERID = createField(DSL.name("userid"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mute.mute_expiry</code>.
     */
    public final TableField<MuteRecord, Long> MUTE_EXPIRY = createField(DSL.name("mute_expiry"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mute.guild_id</code>.
     */
    public final TableField<MuteRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("'607965294731853855'::bigint", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>mute</code> table reference
     */
    public Mute() {
        this(DSL.name("mute"), null);
    }

    /**
     * Create an aliased <code>mute</code> table reference
     */
    public Mute(String alias) {
        this(DSL.name(alias), MUTE);
    }

    /**
     * Create an aliased <code>mute</code> table reference
     */
    public Mute(Name alias) {
        this(alias, MUTE);
    }

    private Mute(Name alias, Table<MuteRecord> aliased) {
        this(alias, aliased, null);
    }

    private Mute(Name alias, Table<MuteRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Mute(Table<O> child, ForeignKey<O, MuteRecord> key) {
        super(child, key, MUTE);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<MuteRecord> getPrimaryKey() {
        return Keys.MUTE_PKEY;
    }

    @Override
    public List<UniqueKey<MuteRecord>> getKeys() {
        return Arrays.<UniqueKey<MuteRecord>>asList(Keys.MUTE_PKEY, Keys.MUTE_GUILD_ID_KEY);
    }

    @Override
    public Mute as(String alias) {
        return new Mute(DSL.name(alias), this);
    }

    @Override
    public Mute as(Name alias) {
        return new Mute(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Mute rename(String name) {
        return new Mute(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Mute rename(Name name) {
        return new Mute(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
