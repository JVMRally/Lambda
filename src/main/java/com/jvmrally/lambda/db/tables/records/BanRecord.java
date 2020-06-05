/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db.tables.records;


import com.jvmrally.lambda.db.tables.Ban;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BanRecord extends UpdatableRecordImpl<BanRecord> implements Record2<Long, Long> {

    private static final long serialVersionUID = 964262143;

    /**
     * Setter for <code>ban.userid</code>.
     */
    public void setUserid(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>ban.userid</code>.
     */
    public Long getUserid() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>ban.ban_expiry</code>.
     */
    public void setBanExpiry(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>ban.ban_expiry</code>.
     */
    public Long getBanExpiry() {
        return (Long) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Long, Long> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Ban.BAN.USERID;
    }

    @Override
    public Field<Long> field2() {
        return Ban.BAN.BAN_EXPIRY;
    }

    @Override
    public Long component1() {
        return getUserid();
    }

    @Override
    public Long component2() {
        return getBanExpiry();
    }

    @Override
    public Long value1() {
        return getUserid();
    }

    @Override
    public Long value2() {
        return getBanExpiry();
    }

    @Override
    public BanRecord value1(Long value) {
        setUserid(value);
        return this;
    }

    @Override
    public BanRecord value2(Long value) {
        setBanExpiry(value);
        return this;
    }

    @Override
    public BanRecord values(Long value1, Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BanRecord
     */
    public BanRecord() {
        super(Ban.BAN);
    }

    /**
     * Create a detached, initialised BanRecord
     */
    public BanRecord(Long userid, Long banExpiry) {
        super(Ban.BAN);

        set(0, userid);
        set(1, banExpiry);
    }
}
