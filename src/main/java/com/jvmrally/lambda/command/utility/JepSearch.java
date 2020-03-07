package com.jvmrally.lambda.command.utility;

import static com.jvmrally.lambda.db.Tables.JEPS;
import java.util.List;
import com.jvmrally.lambda.command.PersistenceAwareCommand;
import com.jvmrally.lambda.command.entites.JepRequest;
import com.jvmrally.lambda.db.tables.pojos.Jeps;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class JepSearch extends PersistenceAwareCommand {

    private static final int FULL_EMBED_LIMIT = 3;
    private static final int SHORT_EMBED_LIMIT = 20;

    private JepRequest req;

    private JepSearch(MessageReceivedEvent e, DSLContext dsl, JepRequest req) {
        super(e, dsl);
        this.req = req;
    }

    @CommandHandler(commandName = "jep", description = "Used to search JEPs.")
    public static void execute(MessageReceivedEvent e, DSLContext dsl, JepRequest request) {
        new JepSearch(e, dsl, request).execute();
    }

    private void execute() {
        Condition queryCondition = buildQueryCondition();
        var jeps = getJeps(queryCondition);
        if (jeps.isEmpty()) {
            sendNoJepsFoundError();
        } else {
            sendJeps(jeps);
        }
    }

    private Condition buildQueryCondition() {
        Condition condition = DSL.trueCondition();
        if (req.getJepId() != -1) {
            return condition.and(JEPS.ID.equal(req.getJepId()));
        }
        if (!req.getSearchParam().isEmpty()) {
            condition = condition.and(JEPS.TITLE.like("%" + req.getSearchParam() + "%"));
        }
        if (!req.getStatusName().isEmpty()) {
            condition = condition.and(JEPS.JEP_STATUS.equal(req.getStatusName()));
        }
        if (!req.getRelease().isEmpty()) {
            condition = condition.and(JEPS.RELEASE.contains(req.getRelease()));
        }
        if (!req.getType().isEmpty()) {
            condition = condition.and(JEPS.JEP_TYPE.equal(req.getType()));
        }
        return condition;
    }

    private List<Jeps> getJeps(Condition condition) {
        return dsl.selectFrom(JEPS).where(condition).fetchInto(Jeps.class);
    }

    private void sendNoJepsFoundError() {
        Messenger.send(e.getChannel(), "No JEPs matching the criteria: " + req.toHumanReadableString();
    }

    private void sendJeps(List<Jeps> jeps) {
        if (jeps.size() > SHORT_EMBED_LIMIT) {
            sendTooManyResultsMessage();
        } else if (jeps.size() > FULL_EMBED_LIMIT) {
            sendShortJepEmbed(jeps);
        } else {
            sendFullJepDetails(jeps);
        }
    }

    private void sendTooManyResultsMessage() {
        Messenger.send(e.getChannel(),
                "More than " + SHORT_EMBED_LIMIT + " were found for " + req.toHumanReadableString() + ". Try limiting your query.");
    }

    private void sendShortJepEmbed(List<Jeps> jeps) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**" + jeps.size() + " results found for " + req.toHumanReadableString() + "**");
        for (Jeps jep : jeps) {
            eb.addField(buildFieldTitle(jep), jep.getTitle(), false);
        }
        Messenger.send(e.getChannel(), eb.build());
    }

    private String buildFieldTitle(Jeps jep) {
        return "Jep: " + jep.getId();
    }

    private void sendFullJepDetails(List<Jeps> jeps) {
        for (Jeps jep : jeps) {
            sendJepEmbed(jep);
        }
    }

    private void sendJepEmbed(Jeps jep) {
        MessageEmbed message = new EmbedBuilder()
                .setTitle("**" + jep.getJepType() + " " + jep.getId() + ":" + jep.getTitle() + "**")
                .addField("Type", jep.getJepType(), true)
                .addField("Status", jep.getJepStatus(), true).addBlankField(true)
                .addField("Java Release", jep.getRelease(), true)
                .addField("JDK Component", jep.getComponent(), true).addBlankField(true)
                .addField("URL", jep.getJepUrl(), false).build();
        Messenger.send(e.getChannel(), message);
    }
}
