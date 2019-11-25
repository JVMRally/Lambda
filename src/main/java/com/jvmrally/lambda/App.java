package com.jvmrally.lambda;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import javax.security.auth.login.LoginException;
import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.config.JooqCodeGen;
import com.jvmrally.lambda.tasks.DelayedTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.reflections.Reflections;
import disparse.discord.Dispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * App Entry
 *
 */
public class App {
    private static final String TOKEN = "LAMBDA_TOKEN";
    private static final String PREFIX = "!";
    private static final Logger logger = LogManager.getLogger(App.class);

    private String[] args;
    private JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException {
        App app = new App(args);
        app.start();
    }

    public App(String[] args) {
        this.args = args;
    }

    private void start() throws LoginException, InterruptedException {
        initDatabase();
        initJDA();
    }

    private void initJDA() throws LoginException, InterruptedException {
        jda = addListeners(Dispatcher.init(new JDABuilder(System.getenv(TOKEN)), PREFIX));
        jda.awaitReady();
        jda.getPresence().setActivity(Activity.playing("DM to contact staff"));
        registerScheduledTasks();
    }

    private JDA addListeners(JDABuilder jdaBuilder) throws LoginException {
        var reflections = new Reflections("com.jvmrally.lambda.listener");
        Set<Class<? extends ListenerAdapter>> classes =
                reflections.getSubTypesOf(ListenerAdapter.class);
        ListenerAdapter[] listeners = new ListenerAdapter[classes.size()];
        Iterator<Class<? extends ListenerAdapter>> itr = classes.iterator();
        int i = 0;
        while (itr.hasNext()) {
            var obj = itr.next();
            try {
                logger.info("Added {} Listener", obj.getName());
                listeners[i] = (ListenerAdapter) Class.forName(obj.getName())
                        .getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                logger.error("Error", e);
            }
            i++;
        }
        jdaBuilder.addEventListeners((Object[]) listeners);
        return jdaBuilder.build();
    }

    /**
     * Finds all tasks in the task package and registers them with the scheduler. Scheduling info is
     * read from the Task annotation
     * 
     * @param jda the jda instance
     */
    private void registerScheduledTasks() {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var reflections = new Reflections("com.jvmrally.lambda.tasks");
        Set<Class<? extends Runnable>> classes = reflections.getSubTypesOf(Runnable.class);
        for (var clazz : classes) {
            if (clazz.isAnnotationPresent(Task.class)) {
                Task task = clazz.getAnnotation(Task.class);
                try {
                    long initialDelay = 0;
                    Object taskObject = Class.forName(clazz.getName()).getConstructor(JDA.class)
                            .newInstance(jda);
                    if (DelayedTask.class.isAssignableFrom(clazz)) {
                        Method getDelay = clazz.getMethod("getTaskDelay");
                        initialDelay = (long) getDelay.invoke(taskObject);
                    }
                    scheduler.scheduleAtFixedRate((Runnable) taskObject, initialDelay,
                            task.frequency(), task.unit());
                    logger.info("Registered {} Task with delay {}", clazz.getName(), initialDelay);
                } catch (ReflectiveOperationException e) {
                    logger.error("Error registering tasks", e);
                }
            } else {
                logger.error("Task {} does not have the @Task annotation", clazz.getName());
            }
        }
    }

    /**
     * Run flyway migrations, and if --generate arg is passed, run Jooq Code generation
     * 
     * @param args command line args
     */
    private void initDatabase() {
        String url = System.getenv("LAMBDA_DB_HOST");
        String user = System.getenv("LAMBDA_DB_USER");
        String password = System.getenv("LAMBDA_DB_PASSWORD");
        Flyway.configure().dataSource(url, user, password).load().migrate();
        if (args.length == 1 && "--generate".equals(args[0])) {
            try {
                JooqCodeGen.runJooqCodeGen(url, user, password);
            } catch (Exception e) {
                logger.error("Problem with code gen.", e);
            }
        }
    }
}
