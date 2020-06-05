/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db.tables.records;


import com.jvmrally.lambda.db.tables.DmTimeouts;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DmTimeoutsRecord extends UpdatableRecordImpl<DmTimeoutsRecord> implements Record2<Long, Long> {

    private static final long serialVersionUID = -1530295268;

    /**
     * Setter for <code>dm_timeouts.userid</code>.
     */
    public void setUserid(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>dm_timeouts.userid</code>.
     */
    public Long getUserid() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>dm_timeouts.last_message_time</code>.
     */
    public void setLastMessageTime(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>dm_timeouts.last_message_time</code>.
     */
    public Long getLastMessageTime() {
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
        return DmTimeouts.DM_TIMEOUTS.USERID;
    }

    @Override
    public Field<Long> field2() {
        return DmTimeouts.DM_TIMEOUTS.LAST_MESSAGE_TIME;
    }

    @Override
    public Long component1() {
        return getUserid();
    }

    @Override
    public Long component2() {
        return getLastMessageTime();
    }

    @Override
    public Long value1() {
        return getUserid();
    }

    @Override
    public Long value2() {
        return getLastMessageTime();
    }

    @Override
    public DmTimeoutsRecord value1(Long value) {
        setUserid(value);
        return this;
    }

    @Override
    public DmTimeoutsRecord value2(Long value) {
        setLastMessageTime(value);
        return this;
    }

    @Override
    public DmTimeoutsRecord values(Long value1, Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DmTimeoutsRecord
     */
    public DmTimeoutsRecord() {
        super(DmTimeouts.DM_TIMEOUTS);
    }

    /**
     * Create a detached, initialised DmTimeoutsRecord
     */
    public DmTimeoutsRecord(Long userid, Long lastMessageTime) {
        super(DmTimeouts.DM_TIMEOUTS);

        set(0, userid);
        set(1, lastMessageTime);
    }
}
