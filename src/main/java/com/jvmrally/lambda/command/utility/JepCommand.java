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
import org.postgresql.translation.messages_bg;

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

            sendJepEmbed(e.getChannel(), found);

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
            sendJepEmbed(e.getChannel(), null);
            return;
        }

        final int FULL_EMBED_LIMIT = 3; 
        final int SHORT_MESSAGE_LIMIT = 20;

        for(int i = 0; i < filtered.size() && i < FULL_EMBED_LIMIT; i++){
            sendJepEmbed(e.getChannel(), filtered.get(i));
        }

        EmbedBuilder eb = new EmbedBuilder();

        if(filtered.size() == FULL_EMBED_LIMIT + 1 ){
            eb.setTitle("**" + "Showing 1 more result." + "**");
        }else if (filtered.size() > FULL_EMBED_LIMIT + 1 ){
            eb.setTitle("**" + "There are " + (filtered.size() - 3) + " more results. You may want to narrow your search."  + " Showing up to 20." + "**");
        }
        
        for(int i = FULL_EMBED_LIMIT; i < filtered.size() && i < FULL_EMBED_LIMIT + SHORT_MESSAGE_LIMIT ; i++){
            eb.addField( filtered.get(i).getTitle() + ": ", "id:" + filtered.get(i).getId() , false);
        }

        Messenger.send(e.getChannel(), eb.build());
    }

    private static void sendJepEmbed(final MessageChannel channel, final Jep jep){

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