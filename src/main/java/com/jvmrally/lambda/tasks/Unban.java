package com.jvmrally.lambda.tasks;

import static com.jvmrally.lambda.db.Tables.BAN;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.db.tables.pojos.Ban;
import com.jvmrally.lambda.injectable.JooqConn;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import net.dv8tion.jda.api.JDA;

/**
 * Unban
 */
@Task(unit = TimeUnit.HOURS, frequency = 1)
public class Unban implements Runnable {

    private static final Logger logger = LogManager.getLogger(Unban.class);
    private JDA jda;

    public Unban(JDA jda) {
        this.jda = jda;
    }

    /**
     * Periodically unban users where their ban time has expired
     */
    @Override
    public void run() {
        DSLContext dsl = JooqConn.getJooqContext();
        Map<Long, Ban> bans = dsl.selectFrom(BAN).where(BAN.BAN_EXPIRY.le(System.currentTimeMillis()))
                .fetchInto(Ban.class)
                .stream()
                .collect(Collectors.toMap(Ban::getGuildId, b -> b));

        bans.forEach((guildId, ban) -> {
            Guild guild = jda.getGuildById(guildId);
            guild.unban(ban.getUserid().toString()).queue();
            logger.info("Unbanning userid: {}", ban.getUserid());
            dsl.deleteFrom(BAN)
                    .where(BAN.USERID.eq(ban.getUserid()))
                    .and(BAN.GUILD_ID.eq(ban.getGuildId())).execute();
        });
    }
}
