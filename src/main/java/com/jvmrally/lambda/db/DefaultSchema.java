/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db;


import com.jvmrally.lambda.db.tables.Audit;
import com.jvmrally.lambda.db.tables.Ban;
import com.jvmrally.lambda.db.tables.DmTimeouts;
import com.jvmrally.lambda.db.tables.Jeps;
import com.jvmrally.lambda.db.tables.Mute;
import com.jvmrally.lambda.db.tables.Tags;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 403401150;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>audit</code>.
     */
    public final Audit AUDIT = Audit.AUDIT;

    /**
     * The table <code>ban</code>.
     */
    public final Ban BAN = Ban.BAN;

    /**
     * The table <code>dm_timeouts</code>.
     */
    public final DmTimeouts DM_TIMEOUTS = DmTimeouts.DM_TIMEOUTS;

    /**
     * The table <code>jeps</code>.
     */
    public final Jeps JEPS = Jeps.JEPS;

    /**
     * The table <code>mute</code>.
     */
    public final Mute MUTE = Mute.MUTE;

    /**
     * The table <code>tags</code>.
     */
    public final Tags TAGS = Tags.TAGS;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.<Sequence<?>>asList(
            Sequences.AUDIT_SERIAL,
            Sequences.TAGS_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Audit.AUDIT,
            Ban.BAN,
            DmTimeouts.DM_TIMEOUTS,
            Jeps.JEPS,
            Mute.MUTE,
            Tags.TAGS);
    }
}
