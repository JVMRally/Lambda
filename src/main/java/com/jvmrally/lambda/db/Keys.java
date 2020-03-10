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
import com.jvmrally.lambda.db.tables.records.AuditRecord;
import com.jvmrally.lambda.db.tables.records.BanRecord;
import com.jvmrally.lambda.db.tables.records.DmTimeoutsRecord;
import com.jvmrally.lambda.db.tables.records.JepsRecord;
import com.jvmrally.lambda.db.tables.records.MuteRecord;
import com.jvmrally.lambda.db.tables.records.TagsRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AuditRecord, Integer> IDENTITY_AUDIT = Identities0.IDENTITY_AUDIT;
    public static final Identity<TagsRecord, Integer> IDENTITY_TAGS = Identities0.IDENTITY_TAGS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AuditRecord> AUDIT_PKEY = UniqueKeys0.AUDIT_PKEY;
    public static final UniqueKey<BanRecord> BAN_PKEY = UniqueKeys0.BAN_PKEY;
    public static final UniqueKey<DmTimeoutsRecord> DM_TIMEOUTS_PKEY = UniqueKeys0.DM_TIMEOUTS_PKEY;
    public static final UniqueKey<JepsRecord> JEPS_PKEY = UniqueKeys0.JEPS_PKEY;
    public static final UniqueKey<MuteRecord> MUTE_PKEY = UniqueKeys0.MUTE_PKEY;
    public static final UniqueKey<TagsRecord> TAGS_PKEY = UniqueKeys0.TAGS_PKEY;
    public static final UniqueKey<TagsRecord> TAGS_TAGNAME_KEY = UniqueKeys0.TAGS_TAGNAME_KEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<AuditRecord, Integer> IDENTITY_AUDIT = Internal.createIdentity(Audit.AUDIT, Audit.AUDIT.ID);
        public static Identity<TagsRecord, Integer> IDENTITY_TAGS = Internal.createIdentity(Tags.TAGS, Tags.TAGS.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AuditRecord> AUDIT_PKEY = Internal.createUniqueKey(Audit.AUDIT, "audit_pkey", Audit.AUDIT.ID);
        public static final UniqueKey<BanRecord> BAN_PKEY = Internal.createUniqueKey(Ban.BAN, "ban_pkey", Ban.BAN.USERID);
        public static final UniqueKey<DmTimeoutsRecord> DM_TIMEOUTS_PKEY = Internal.createUniqueKey(DmTimeouts.DM_TIMEOUTS, "dm_timeouts_pkey", DmTimeouts.DM_TIMEOUTS.USERID);
        public static final UniqueKey<JepsRecord> JEPS_PKEY = Internal.createUniqueKey(Jeps.JEPS, "jeps_pkey", Jeps.JEPS.ID);
        public static final UniqueKey<MuteRecord> MUTE_PKEY = Internal.createUniqueKey(Mute.MUTE, "mute_pkey", Mute.MUTE.USERID);
        public static final UniqueKey<TagsRecord> TAGS_PKEY = Internal.createUniqueKey(Tags.TAGS, "tags_pkey", Tags.TAGS.ID);
        public static final UniqueKey<TagsRecord> TAGS_TAGNAME_KEY = Internal.createUniqueKey(Tags.TAGS, "tags_tagname_key", Tags.TAGS.TAGNAME);
    }
}
