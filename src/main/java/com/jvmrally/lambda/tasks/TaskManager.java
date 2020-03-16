package com.jvmrally.lambda.tasks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final Map<Class<?>, ScheduledFuture<?>> tasks = new HashMap<>();
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private static final List<Class<?>> disabledTasks = new ArrayList<>();
    private static JDA jda; // required for task creation

    private TaskManager() {
    }

    public void setJDA(JDA jdaInstance) {
        jda = jdaInstance;
    }

    public void removeTask(String taskName) throws ClassNotFoundException {
        Class<?> taskClass =
                tasks.keySet().stream().filter(task -> task.getSimpleName().equals(taskName))
                        .findFirst().orElseThrow(ClassNotFoundException::new);
        var future = tasks.get(taskClass);
        if (future != null) {
            disabledTasks.add(taskClass);
            future.cancel(false);
            tasks.remove(taskClass);
            logger.info("Task {} removed", taskName);
        }
    }

    public void registerTaskInitial(Class<?> taskClass) {
        registerTask(taskClass, true);
    }

    public void registerTask(String taskName) throws ClassNotFoundException {
        Class<?> taskClass =
                disabledTasks.stream().filter(task -> task.getSimpleName().equals(taskName))
                        .findFirst().orElseThrow(ClassNotFoundException::new);
        disabledTasks.remove(taskClass);
        registerTask(taskClass, false);
    }

    private void registerTask(Class<?> taskClass, boolean isInitialRegistration) {
        try {
            if (!taskClass.isAnnotationPresent(Task.class)) {
                logger.error("Task {} does not have the @Task annotation", taskClass.getName());
                return;
            }
            Task taskDetails = taskClass.getAnnotation(Task.class);
            if (isInitialRegistration && taskDetails.disabled()) {
                disabledTasks.add(taskClass);
                return;
            }
            long initialDelay = 0;
            Object taskObject = taskClass.getConstructor(JDA.class).newInstance(jda);
            if (DelayedTask.class.isAssignableFrom(taskClass)) {
                Method getDelay = taskClass.getMethod("getTaskDelay");
                initialDelay = (long) getDelay.invoke(taskObject);
            }
            addTask(taskObject, initialDelay, taskDetails, taskClass);
        } catch (ReflectiveOperationException e) {
            logger.error("Error registering tasks", e);
        }
        return;
    }

    private void addTask(Object task, long initialDelay, Task taskDetails, Class<?> taskClass) {
        var future = scheduler.scheduleAtFixedRate((Runnable) task, initialDelay,
                taskDetails.frequency(), taskDetails.unit());
        tasks.put(taskClass, future);
        logger.info("Registered {} Task with delay {}", taskClass.getSimpleName(), initialDelay);
    }

}
