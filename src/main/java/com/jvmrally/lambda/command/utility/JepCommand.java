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

    private JepCommand() {
    }

    @CommandHandler(commandName = "jep", description = "Details information about the bot.")
    public static void execute(final MessageReceivedEvent e, final JepRequest request) {
     
        // filter by id, return straight away if id present
        if(request.getJepId() != -1){

            var jep = JooqConn.getJooqContext()
                                        .select()
                                        .from(JEPS)
                                        .where(JEPS.ID.equal(request.getJepId()))
                                        .fetchInto(Jeps.class);

            Jep found = jep.isEmpty() ? null : new Jep(jep.get(0));

            sendFiltered(e.getChannel(), found);

            return;
        }

        Condition result = DSL.trueCondition();

        // add title search filter
        if (!request.getSearchParam().isEmpty()){
            result = result.and(JEPS.TITLE.like("%" + request.getSearchParam() + "%"));
        }

        // add status filter
        if(!request.getStatusName().isEmpty()){
            result = result.and(JEPS.JEP_STATUS.equal(request.getStatusName().toUpperCase()));
        }

        // add release filter
        if(request.getTarget() != -1){
            result = result.and(JEPS.RELEASE.contains(Integer.toString(request.getTarget())));
        }

        // add type filter
        if(!request.getType().isEmpty()){
          result = result.and(JEPS.JEP_TYPE.equal(request.getType().toUpperCase()));
        }

        final List<Jep> filtered = JooqConn.getJooqContext()
                        .select()
                        .from(JEPS)
                        .where(result)
                        .fetchInto(Jeps.class)
                        .stream()
                        .map(Jep::new)
                        .collect(Collectors.toList());

        if(filtered.isEmpty()){
            sendFiltered(e.getChannel(), null);
            return;
        }

        //TODO: If filtered > 3 send only 3 and for rest use TITLE : ID pairs

        filtered.forEach( j -> sendFiltered(e.getChannel(), j));
    }

    private static void sendFiltered(final MessageChannel channel, final Jep jep){

        if(jep == null){
            Messenger.send(channel, "No JEP matching the criteria.");
            return;
        }

        MessageEmbed message = new EmbedBuilder()
                                .setTitle("**" + jep.getJepType() + " " + jep.getId() + ": " + jep.getTitle() + "**")
                                .addField("Type", jep.getJepType().name(), true)
                                .addField("Status", jep.getStatus().name(), true)
                                .addBlankField(true)
                                .addField("Java Release", jep.getRelease(), true)
                                .addField("JDK Component", jep.getComponent(), true)
                                .addBlankField(true)
                                .addField("URL", jep.getUrl(), false)
                                .build();

        Messenger.send(channel, message);
    }
}