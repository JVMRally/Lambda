package com.jvmrally.lambda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JooqConn
 */
public class JooqConn {

    private static final Logger logger = LogManager.getLogger(JooqConn.class);
    private static Connection conn = null;
    private static DSLContext dsl = null;

    public static DSLContext getContext() {
        if (dsl == null && conn == null) {
            String url = System.getenv("LAMBDA_DB_HOST");
            String user = System.getenv("LAMBDA_DB_USER");
            String password = System.getenv("LAMBDA_DB_PASSWORD");
            try {
                conn = DriverManager.getConnection(url, user, password);
                dsl = DSL.using(conn, SQLDialect.POSTGRES);
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        return dsl;
    }
}
