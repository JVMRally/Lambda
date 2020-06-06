package com.jvmrally.lambda.command.utility;

import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.command.entities.ToggleRequest;
import com.jvmrally.lambda.injectable.JooqConn;
import com.jvmrally.lambda.tasks.TaskManager;
import disparse.discord.jda.Dispatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import disparse.parser.dispatch.CommandRegistrar;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jooq.DSLContext;

/**
 * DisableFeature
 */
public class ToggleFeature extends Command {

    private static final Logger logger = LogManager.getLogger(ToggleFeature.class);

    private ToggleRequest req;
    private Dispatcher dispatcher;

    public ToggleFeature(Dispatcher dispatcher, MessageReceivedEvent e, ToggleRequest req) {
        super(e);
        this.dispatcher = dispatcher;
        this.req = req;
    }

    @CommandHandler(commandName = "disable", description = "Disable a feature", roles = "admin",
            canBeDisabled = false)
    public static void disable(Dispatcher dispatcher, MessageReceivedEvent e, ToggleRequest req) {
        new ToggleFeature(dispatcher, e, req).disable();
    }

    @CommandHandler(commandName = "enable", description = "Enable a feature", roles = "admin",
            canBeDisabled = false)
    public static void enable(Dispatcher dispatcher, MessageReceivedEvent e, ToggleRequest req) {
        new ToggleFeature(dispatcher, e, req).enable();
    }

    private void disable() {
        if (!req.getCommandName().isEmpty()) {
            dispatcher.disableCommand(e, req.getCommandName());
        }
        if (!req.getTaskName().isEmpty()) {
            try {
                TaskManager.MANAGER.removeTask(req.getTaskName());
            } catch (ClassNotFoundException e) {
                logger.error("Task {} not found", req.getTaskName(), e);
            }
        }
    }

    private void enable() {
        if (!req.getCommandName().isEmpty()) {
            dispatcher.enableCommand(e, req.getCommandName());
        }
        if (!req.getTaskName().isEmpty()) {
            try {
                TaskManager.MANAGER.registerTask(req.getTaskName());
            } catch (ClassNotFoundException e) {
                logger.error("Task {} not found", req.getTaskName(), e);
            }
        }
    }
}
