package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * ToggleRequest
 */
@ParsedEntity
public class ToggleRequest {

    @Flag(shortName = 'c', longName = "command", description = "Toggle a command.")
    private String commandName = "";

    @Flag(shortName = 't', longName = "task", description = "Toggle a task.")
    private String taskName = "";

    /**
     * @return the commandName
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

}
