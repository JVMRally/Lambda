package com.jvmrally.lambda.managers;

import com.jvmrally.lambda.db.tables.pojos.DisabledCommands;
import com.jvmrally.lambda.injectable.JooqConn;
import disparse.discord.manager.DisabledCommandManager;
import disparse.parser.Command;
import org.jooq.DSLContext;

import java.util.Optional;

import static com.jvmrally.lambda.db.tables.DisabledCommands.DISABLED_COMMANDS;

public class PersistedDisabledCommandManager implements DisabledCommandManager {

  DSLContext ctx = JooqConn.getJooqContext();

  @Override
  public boolean commandAllowedInGuild(String guildId, Command command) {
    Optional<DisabledCommands> disabledCommand = ctx.selectFrom(DISABLED_COMMANDS)
            .where(DISABLED_COMMANDS.GUILD_ID.eq(guildId))
            .and(DISABLED_COMMANDS.COMMAND_NAME.eq(command.getCommandName()))
            .fetchOptionalInto(DisabledCommands.class);

    return disabledCommand.isEmpty();
  }

  @Override
  public void disableCommandForGuild(String guildId, Command command) {
    ctx.insertInto(DISABLED_COMMANDS)
            .columns(DISABLED_COMMANDS.GUILD_ID, DISABLED_COMMANDS.COMMAND_NAME)
            .values(guildId, command.getCommandName())
            .execute();
  }

  @Override
  public void enableCommandForGuild(String guildId, Command command) {
    ctx.deleteFrom(DISABLED_COMMANDS)
            .where(DISABLED_COMMANDS.GUILD_ID.eq(guildId))
            .and(DISABLED_COMMANDS.COMMAND_NAME.eq(command.getCommandName())).execute();
  }
}
