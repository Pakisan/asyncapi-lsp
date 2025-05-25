package com.asyncapi.lsp.json;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Contains details about JSON parsing error
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
@Data
public class JsonParsingMessage {

    /**
     * Message from Jackson, which describes what went wrong during JSON parsing
     */
    @NotNull
    private final String message;

    /**
     * Where error occurs
     */
    @NotNull
    private final JsonLocation location;

    /**
     * Create a message with parsing error details
     *
     * @param jsonParseException JSON parsing exception
     */
    public JsonParsingMessage(@NotNull JsonParseException jsonParseException) {
        this.message = jsonParseException.getOriginalMessage();
        this.location = jsonParseException.getLocation();
    }

}