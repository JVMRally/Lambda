package com.jvmrally.lambda.utility.messaging;

import java.awt.Color;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Embed
 */
public class EmbedMessage {

    private final String title;
    private final String description;
    private final Color color;
    private final String author;
    private final EmbedField[] fields;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EmbedMessage(@JsonProperty("title") String title,
            @JsonProperty("description") String description, @JsonProperty("color") String color,
            @JsonProperty("author") String author, @JsonProperty("fields") EmbedField[] fields) {
        this.title = title;
        this.description = description;
        this.color = Color.decode(color);
        this.author = author;
        this.fields = fields;
    }

    public EmbedBuilder build() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(description);
        eb.setColor(color);
        eb.setAuthor(author);
        for (EmbedField field : fields) {
            eb.addField(field.getTitle(), field.getContent(), field.isInline());
        }
        return eb;
    }
}
