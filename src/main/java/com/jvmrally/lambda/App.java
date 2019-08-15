package com.jvmrally.lambda;

import javax.security.auth.login.LoginException;
import com.jvmrally.lambda.config.JooqCodeGen;
import com.jvmrally.lambda.listener.DirectMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import disparse.discord.Dispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

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
        int migrations = Flyway.configure().dataSource(url, user, password).load().migrate();
        if (migrations > 0) {
            try {
                JooqCodeGen.runJooqCodeGen(url, user, password);
            } catch (Exception e) {
                logger.error("Problem with code gen.", e);
            }
        }
    }
}
