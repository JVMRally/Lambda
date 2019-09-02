package com.jvmrally.lambda.utility.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EmbedField
 */
public class EmbedField {

    private final String title;
    private final String content;
    private final boolean inline;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EmbedField(@JsonProperty("title") String title, @JsonProperty("content") String content,
            @JsonProperty("inline") boolean inline) {
        this.title = title;
        this.content = content;
        this.inline = inline;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the inline
     */
    public boolean isInline() {
        return inline;
    }
}
