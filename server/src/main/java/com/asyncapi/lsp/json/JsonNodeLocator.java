package com.asyncapi.lsp.json;

import com.fasterxml.jackson.core.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Utility class to locate JSON nodes by their position in the source file.
 *
 * @author AI Assistant
 * @since 1.0.0
 */
@Slf4j
public class JsonNodeLocator {

    /**
     * Find a JSON node at the specified line and column in the JSON document.
     *
     * @param jsonContent The JSON document content
     * @param line The line number (1-based)
     * @param column The column number (1-based)
     * @return The JSON node at the specified location, or null if not found
     * @throws IOException If there's an error parsing the JSON
     */
    @Nullable
    public static String findNodeAtLocation(@NotNull String jsonContent, int line, int column) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(jsonContent)) {
            jsonParser.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            jsonParser.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
            jsonParser.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

            return findNodeAtLocation(jsonParser, line, column);
        } catch (IOException e) {
            log.error("Error while parsing JSON: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Find a JSON node at the specified line and column using a JsonParser to track locations.
     *
     * @param parser The JsonParser used to track token locations
     * @param targetLine The line number to find (1-based)
     * @param targetColumn The column number to find (1-based)
     * @return The JSON node at the specified location, or null if not found
     */
    @Nullable
    private static String findNodeAtLocation(
            @NotNull JsonParser parser, 
            int targetLine,
            int targetColumn
    ) throws IOException {
        String result = null;

        try {
            while (parser.nextToken() != null) {
                JsonToken token = parser.currentToken();
                JsonLocation location = parser.currentLocation();
                int line = location.getLineNr();
                int column = location.getColumnNr();

                if (token == JsonToken.FIELD_NAME) {
                    if (line == targetLine) {
                        result = parser.getParsingContext().pathAsPointer().toString();
                        break;
                    }
                }
            }
        } catch (JsonParseException jsonParseException) {
            result = recognizeTokenToComplete(jsonParseException);
        }

        return result;
    }

    public static String recognizeTokenToComplete(@NotNull JsonParseException exception) throws IOException {
        String tokenToComplete;

        if (exception.getProcessor().getCurrentToken() == null) {
            tokenToComplete = exception.getProcessor().getParsingContext().pathAsPointer().toString();
        } else {
            final var parent = exception.getProcessor().getParsingContext().getParent().pathAsPointer();
            tokenToComplete = parent + "/" + exception.getProcessor().getText();
        }

        return tokenToComplete;
    }

}
