package com.jvmrally.lambda.injectable;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import disparse.parser.reflection.Injectable;

/**
 * JooqConn
 */
public class JooqConn {

    private static final Logger logger = LogManager.getLogger(JooqConn.class);
    private static HikariConfig config = null;
    private static DSLContext dsl = null;

    @Injectable
    public static DSLContext getJooqContext() {
        if (dsl == null && config == null) {
            init();
        }
        return dsl;
    }

    private static void init() {
        logger.info("Initialising Jooq context");
        config = new HikariConfig();
        config.setJdbcUrl(System.getenv("LAMBDA_DB_HOST"));
        config.setUsername(System.getenv("LAMBDA_DB_USER"));
        config.setPassword(System.getenv("LAMBDA_DB_PASSWORD"));
        dsl = DSL.using(new HikariDataSource(config), SQLDialect.POSTGRES);
    }
}
