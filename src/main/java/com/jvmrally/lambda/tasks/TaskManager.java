package com.jvmrally.lambda.tasks;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import com.jvmrally.lambda.annotation.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.JDA;

/**
 * TaskManager
 */
public class TaskManager {
    private static final Logger logger = LogManager.getLogger(TaskManager.class);
    public static final TaskManager MANAGER = new TaskManager();
    private static final Map<String, ScheduledFuture<?>> tasks = new HashMap<>();
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private static JDA jda;

    private TaskManager() {
    }

    public void setJDA(JDA jdaInstance) {
        jda = jdaInstance;
    }

    public void addTask(Object task, long initialDelay, Task taskDetails, String taskName) {
        var future = scheduler.scheduleAtFixedRate((Runnable) task, initialDelay,
                taskDetails.frequency(), taskDetails.unit());
        tasks.put(taskName, future);
        logger.info("Registered {} Task with delay {}", taskName, initialDelay);
    }

    public void removeTask(String clazz) {
        var future = tasks.get(clazz);
        if (future != null) {
            future.cancel(false);
        }
    }

    public void registerTask(String taskName) {
        taskName = "com.jvmrally.lambda.tasks." + taskName;
        try {
            long initialDelay = 0;
            Class<?> clazz = Class.forName(taskName);
            Task taskDetails = clazz.getAnnotation(Task.class);
            if (taskDetails == null) {
                logger.error("Error");
                return;
            }
            Object taskObject = clazz.getConstructor(JDA.class).newInstance(jda);
            if (DelayedTask.class.isAssignableFrom(clazz)) {
                Method getDelay = clazz.getMethod("getTaskDelay");
                initialDelay = (long) getDelay.invoke(taskObject);
            }
            addTask(taskObject, initialDelay, taskDetails, taskName);
        } catch (ReflectiveOperationException e) {
            logger.error("Error", e);
        }
    }

}
