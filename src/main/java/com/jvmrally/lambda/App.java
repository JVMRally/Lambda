package com.jvmrally.lambda;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import com.jvmrally.lambda.config.JooqCodeGen;
import com.jvmrally.lambda.listener.DirectMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.reflections.Reflections;
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
    private static final int HOUR = 60 * 60 * 1000;

    public static void main(String[] args) throws LoginException, InterruptedException {
        initFlyway(args);
        JDA jda = Dispatcher.init(new JDABuilder(System.getenv(TOKEN)), PREFIX)
                .addEventListeners(new DirectMessageListener()).build();
        jda.awaitReady();
        registerScheduledTasks(jda);
    }

    private static void registerScheduledTasks(JDA jda) {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var reflections = new Reflections("com.jvmrally.lambda.tasks");
        Set<Class<? extends Runnable>> classes = reflections.getSubTypesOf(Runnable.class);
        int registeredTasks = 0;
        for (Class<? extends Object> c : classes) {
            try {
                scheduler
                        .scheduleWithFixedDelay(
                                (Runnable) Class.forName(c.getName()).getConstructor(JDA.class)
                                        .newInstance(jda),
                                millisToNextHour(), HOUR, TimeUnit.MILLISECONDS);
                registeredTasks++;
            } catch (ReflectiveOperationException e) {
                logger.error("Error registering tasks", e);
            }
        }
        logger.info("Registered {} tasks", registeredTasks);
    }

    private static void initFlyway(String[] args) {
        String url = System.getenv("LAMBDA_DB_HOST");
        String user = System.getenv("LAMBDA_DB_USER");
        String password = System.getenv("LAMBDA_DB_PASSWORD");
        Flyway.configure().dataSource(url, user, password).load().migrate();
        if (args.length == 1 && args[0].equals("--generate")) {
            try {
                JooqCodeGen.runJooqCodeGen(url, user, password);
            } catch (Exception e) {
                logger.error("Problem with code gen.", e);
            }
        }
    }

    private static long millisToNextHour() {
        Calendar calendar = Calendar.getInstance();
        long minutesToNextHour = TimeUnit.MINUTES.toMillis(60 - calendar.get(Calendar.MINUTE));
        long secondsToNextHour = TimeUnit.SECONDS.toMillis(60 - calendar.get(Calendar.SECOND));
        int millisToNextHour = 1000 - calendar.get(Calendar.MILLISECOND);
        return minutesToNextHour + secondsToNextHour + millisToNextHour;
    }
}
