package com.jvmrally.lambda.command.utility;

import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.command.entities.ToggleRequest;
import com.jvmrally.lambda.tasks.TaskManager;
import disparse.parser.dispatch.CommandRegistrar;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * DisableFeature
 */
public class ToggleFeature extends Command {

    private ToggleRequest req;

    public ToggleFeature(MessageReceivedEvent e, ToggleRequest req) {
        super(e);
        this.req = req;
    }

    @CommandHandler(commandName = "disable", description = "Disable a feature", roles = "admin",
            canBeDisabled = false)
    public static void disable(MessageReceivedEvent e, ToggleRequest req) {
        new ToggleFeature(e, req).disable();
    }

    @CommandHandler(commandName = "enable", description = "Enable a feature", roles = "admin",
            canBeDisabled = false)
    public static void enable(MessageReceivedEvent e, ToggleRequest req) {
        try {
            CommandRegistrar.REGISTRAR.enableCommand("embed");
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed");
        }
        new ToggleFeature(e, req).enable();
    }

    private void disable() {
        if (!req.getCommandName().isEmpty()) {
            CommandRegistrar.REGISTRAR.disableCommand(req.getCommandName());
        }
        if (!req.getTaskName().isEmpty()) {
            TaskManager.MANAGER.removeTask(req.getTaskName());
        }
    }

    private void enable() {
        if (!req.getCommandName().isEmpty()) {
            CommandRegistrar.REGISTRAR.enableCommand(req.getCommandName());
        }
        if (!req.getTaskName().isEmpty()) {
            TaskManager.MANAGER.registerTask(req.getTaskName());
        }
    }
}
