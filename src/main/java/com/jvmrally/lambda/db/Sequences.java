/*
 * This file is generated by jOOQ.
 */
package com.jvmrally.lambda.db;


import org.jooq.Sequence;
import org.jooq.impl.Internal;


/**
 * Convenience access to all sequences in 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>audit_serial</code>
     */
    public static final Sequence<Integer> AUDIT_SERIAL = Internal.createSequence("audit_serial", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTEGER.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>disabled_commands_id_seq</code>
     */
    public static final Sequence<Integer> DISABLED_COMMANDS_ID_SEQ = Internal.createSequence("disabled_commands_id_seq", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTEGER.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>tags_id_seq</code>
     */
    public static final Sequence<Integer> TAGS_ID_SEQ = Internal.createSequence("tags_id_seq", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTEGER.nullable(false), null, null, null, null, false, null);
}
