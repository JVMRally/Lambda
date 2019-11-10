package com.jvmrally.lambda.command.utility;

import java.util.List;
import java.util.stream.Collectors;
import com.jvmrally.lambda.command.entites.JepRequest;
import com.jvmrally.lambda.db.tables.pojos.Jeps;
import com.jvmrally.lambda.injectable.JooqConn;
import com.jvmrally.lambda.jdk.Jep;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.jvmrally.lambda.db.Tables.JEPS;

public class JepCommand {

    static final int FULL_EMBED_LIMIT = 3;
    static final int SHORT_EMBED_LIMIT = 20;

    private JepCommand() {
    }

    @CommandHandler(commandName = "jep", description = "Used to search JEPs.")
    public static void execute(final MessageReceivedEvent e, final JepRequest request) {

        final Condition result = buildFilteringCondition(request);

        final List<Jep> filtered = JooqConn.getJooqContext().select().from(JEPS).where(result)
                .fetchInto(Jeps.class).stream().map(Jep::new).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Messenger.send(e.getChannel(), "No JEP matching the criteria.");
            return;
        }

        sendJeps(e.getChannel(), filtered);
    }

    /**
     * Builds JOOQ condition to filter out JEPs by parameters provided with flags. If id flag is set
     * it will return condition straight away.
     * 
     * @param request
     * @return
     */
    private static Condition buildFilteringCondition(final JepRequest request) {
        Condition result = DSL.trueCondition();

        // id search filter
        if (request.getJepId() != -1) {
            return result.and(JEPS.ID.equal(request.getJepId()));
        }

        // add title search filter
        if (!request.getSearchParam().isEmpty()) {
            result = result.and(JEPS.TITLE.like("%" + request.getSearchParam() + "%"));
        }

        // add status filter
        if (!request.getStatusName().isEmpty()) {
            result = result.and(JEPS.JEP_STATUS.equal(request.getStatusName().toUpperCase()));
        }

        // add release filter
        if (request.getTarget() != -1) {
            result = result.and(JEPS.RELEASE.contains(Integer.toString(request.getTarget())));
        }

        // add type filter
        if (!request.getType().isEmpty()) {
            result = result.and(JEPS.JEP_TYPE.equal(request.getType().toUpperCase()));
        }

        return result;
    }

    /**
     * Function which sends list of JEPs via Messenger. Outputs "full" embed for first N jeps up to
     * FULL_EMBED_LIMIT. Outputs JEP title and id for rest jeps up to SHORT_MESSAGE_LIMIT.
     * 
     * @param channel
     * @param jepList
     */
    private static void sendJeps(final MessageChannel channel, final List<Jep> jepList) {

        for (int i = 0; i < jepList.size() && i < FULL_EMBED_LIMIT; i++) {
            sendJepEmbed(channel, jepList.get(i));
        }

        if (jepList.size() <= FULL_EMBED_LIMIT) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        if (jepList.size() == FULL_EMBED_LIMIT + 1) {
            eb.setTitle("**" + "Showing 1 more result." + "**");
        } else if (jepList.size() > FULL_EMBED_LIMIT + 1) {
            eb.setTitle("** There are " + (jepList.size() - FULL_EMBED_LIMIT) + " more results."
                    + "You may want to narrow your search. Showing up to 20.**");
        }

        for (int i = FULL_EMBED_LIMIT; i < jepList.size()
                && i < FULL_EMBED_LIMIT + SHORT_EMBED_LIMIT; i++) {

            eb.addField(jepList.get(i).getTitle() + ": ", "id:" + jepList.get(i).getId(), false);
        }
        Messenger.send(channel, eb.build());
    }

    /**
     * Uses Messenger to send full description of JEP passed as argument.
     * 
     * @param channel
     * @param jep
     */
    private static void sendJepEmbed(final MessageChannel channel, final Jep jep) {

        MessageEmbed message = new EmbedBuilder()
                .setTitle("**" + jep.getJepType() + " " + jep.getId() + ":" + jep.getTitle() + "**")
                .addField("Type", jep.getJepType().name(), true)
                .addField("Status", jep.getStatus().name(), true).addBlankField(true)
                .addField("Java Release", jep.getRelease(), true)
                .addField("JDK Component", jep.getComponent(), true).addBlankField(true)
                .addField("URL", jep.getUrl(), false).build();

        Messenger.send(channel, message);
    }
}
