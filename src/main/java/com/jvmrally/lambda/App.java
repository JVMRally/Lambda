package com.jvmrally.lambda;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.config.JooqCodeGen;
import com.jvmrally.lambda.listener.DirectMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.reflections.Reflections;
import disparse.discord.Dispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

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
        jda.getPresence().setActivity(Activity.playing("DM to contact staff"));
        registerScheduledTasks(jda);
    }

    private static void registerScheduledTasks(JDA jda) {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var reflections = new Reflections("com.jvmrally.lambda.tasks");
        Set<Class<? extends Runnable>> classes = reflections.getSubTypesOf(Runnable.class);
        for (Class<? extends Object> c : classes) {
            if (c.isAnnotationPresent(Task.class)) {
                Task task = c.getAnnotation(Task.class);
                try {
                    scheduler.schedule((Runnable) Class.forName(c.getName())
                            .getConstructor(JDA.class).newInstance(jda), task.frequency(),
                            task.unit());
                    logger.info("Registered {} Task", c.getName());
                } catch (ReflectiveOperationException e) {
                    logger.error("Error registering tasks", e);
                }
            } else {
                logger.error("Task {} does not have the @Task annotation", c.getName());
            }
        }
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
}
