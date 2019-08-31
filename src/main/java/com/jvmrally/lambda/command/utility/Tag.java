package com.jvmrally.lambda.command.utility;

import static com.jvmrally.lambda.db.tables.Tags.TAGS;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.db.tables.pojos.Tags;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.messaging.Messenger;

import org.jooq.DSLContext;

import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Tag
 */
public class Tag {

    @ParsedEntity
    static class TagRequest {
        @Flag(shortName = 'c', longName = "create", description = "Create a new tag")
        private Boolean create = Boolean.FALSE;

        @Flag(shortName = 'e', longName = "edit", description = "Alter the content of an existing tag")
        private Boolean edit = Boolean.FALSE;

        @Flag(shortName = 'n', longName = "name", description = "Name of the tag")
        private String name = "";

        @Flag(shortName = 'd', longName = "delete", description = "Delete a tag")
        private Boolean delete = Boolean.FALSE;

        @Flag(shortName = 'l', longName = "list", description = "List all tags")
        private Boolean list = Boolean.FALSE;

        @Flag(longName = "content", description = "Content to create or edit a tag")
        private String content = "";
    }

    @CommandHandler(commandName = "tag", description = "Create, edit, or display useful messages. For creating and editing, place all message content between two `\"`")
    public static void execute(Auditor auditor, DSLContext dsl, TagRequest req, MessageReceivedEvent e) {
        if (manipulateTag(auditor, dsl, req, e)) {
            return;
        }
        if (req.list) {
            listTags(dsl, e);
            return;
        }
        if (!req.name.isEmpty()) {
            dsl.selectFrom(TAGS).where(TAGS.TAGNAME.eq(req.name)).fetchOptionalInto(Tags.class).ifPresentOrElse(
                    tag -> Messenger.toChannel(m -> m.to(e.getChannel()).message(tag.getContent())),
                    () -> Messenger.toChannel(m -> m.to(e.getChannel()).message("Tag does not exist")));
        }
    }

    private static boolean manipulateTag(Auditor auditor, DSLContext dsl, TagRequest req, MessageReceivedEvent e) {
        if (req.create) {
            createTag(auditor, dsl, req, e);
            return true;
        }
        if (req.edit) {
            editTag(auditor, dsl, req, e);
            return true;
        }
        if (req.delete) {
            deleteTag(auditor, dsl, req, e);
            return true;
        }
        return false;
    }

    private static void createTag(Auditor auditor, DSLContext dsl, TagRequest req, MessageReceivedEvent e) {
        if (isContentEmpty(req, e)) {
            return;
        }
        findTag(dsl, req.name).ifPresentOrElse(
                tag -> Messenger
                        .toChannel(m -> m.to(e.getChannel()).message("Tag `" + tag.getTagname() + "` already exists.")),
                () -> {
                    dsl.insertInto(TAGS).columns(TAGS.TAGNAME, TAGS.CONTENT, TAGS.UPDATED_AT)
                            .values(req.name, req.content, OffsetDateTime.now()).execute();
                    auditor.log(AuditAction.CREATED_TAG, e.getAuthor().getIdLong());
                });
        Messenger.toChannel(m -> m.to(e.getChannel()).message("Tag created!"));
    }

    private static void editTag(Auditor auditor, DSLContext dsl, TagRequest req, MessageReceivedEvent e) {
        if (isContentEmpty(req, e)) {
            return;
        }
        findTag(dsl, req.name).ifPresentOrElse(tag -> {
            dsl.update(TAGS).set(TAGS.CONTENT, req.content).set(TAGS.UPDATED_AT, OffsetDateTime.now());
            auditor.log(AuditAction.EDITED_TAG, e.getAuthor().getIdLong());
        }, () -> Messenger.toChannel(m -> m.to(e.getChannel()).message("Tag does not exist.")));
    }

    private static void deleteTag(Auditor auditor, DSLContext dsl, TagRequest req, MessageReceivedEvent e) {
        findTag(dsl, req.name).ifPresentOrElse(tag -> {
            dsl.deleteFrom(TAGS).where(TAGS.TAGNAME.eq(tag.getTagname())).execute();
            auditor.log(AuditAction.DELETED_TAG, e.getAuthor().getIdLong());
            Messenger.toChannel(m -> m.to(e.getChannel()).message("Tag `" + tag.getTagname() + "` has been deleted."));
        }, () -> Messenger.toChannel(m -> m.to(e.getChannel()).message("Tag does not exist.")));
    }

    /**
     * Lists all tags. If no tags are found a default message is sent
     * 
     * @param dsl
     * @param e
     */
    private static void listTags(DSLContext dsl, MessageReceivedEvent e) {
        List<String> tags = dsl.select(TAGS.TAGNAME).from(TAGS).fetchInto(String.class);
        String response;
        if (tags.isEmpty()) {
            response = "There are no tags yet";
        } else {
            response = "**Tags: **" + String.join(", ", tags);
        }
        Messenger.toChannel(m -> m.to(e.getChannel()).message(response));
    }

    /**
     * Finds a tag by name
     * 
     * @param dsl  dslcontext
     * @param name the name of the tag to find
     * @return
     */
    private static Optional<Tags> findTag(DSLContext dsl, String name) {
        return dsl.selectFrom(TAGS).where(TAGS.TAGNAME.eq(name)).fetchOptionalInto(Tags.class);
    }

    /**
     * Checks whether the content supplied is empty
     * 
     * @param req the request flags
     * @param e   the message event
     * @return true if the content is empty
     */
    private static boolean isContentEmpty(TagRequest req, MessageReceivedEvent e) {
        if (req.content.isEmpty()) {
            Messenger.toChannel(m -> m.to(e.getChannel()).message("You must supply content."));
            return true;
        }
        return false;
    }

}
