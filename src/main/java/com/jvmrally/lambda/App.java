package com.jvmrally.lambda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import com.jvmrally.lambda.listener.DirectMessageListener;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import disparse.discord.Dispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * App Entry
 *
 */
public class App {
    private static final String TOKEN = "LAMBDA_TOKEN";
    private static final String PREFIX = "!";
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws LoginException, InterruptedException {
        initFlyway();
        JDA jda = Dispatcher.init(new JDABuilder(System.getenv(TOKEN)), PREFIX)
                .addEventListeners(new DirectMessageListener()).build();
        jda.awaitReady();
    }

    private static void initFlyway() {
        String url = System.getenv("LAMBDA_DB_HOST");
        String user = System.getenv("LAMBDA_DB_USER");
        String password = System.getenv("LAMBDA_DB_PASSWORD");
        Flyway.configure().dataSource(url, user, password).load().migrate();
    }
}
