package com.asyncapi.lsp.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.networknt.schema.ValidationMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * JSON Schema validation result
 *
 * @param isValid is valid JSON Schema?
 *
 * @author Pavel Bodiachevskii
 * @version 1.0.0
 */
public record ValidationResult(
        boolean isValid,
        @Nullable ValidationErrorType validationErrorType,
        @Nullable Set<ValidationMessage> validationMessages,
        @Nullable JsonParsingMessage jsonParsingMessage
) {

    public static ValidationResult valid() {
        return new ValidationResult(true, null, null, null);
    }

    public static ValidationResult validationError(@NotNull Set<ValidationMessage> validationMessages) {
        return new ValidationResult(
                false,
                ValidationErrorType.JSON_SCHEMA_VALIDATION,
                validationMessages,
                null
        );
    }

    public static ValidationResult parsingError(@NotNull JsonParseException jsonParseException) {
        return new ValidationResult(
                false,
                ValidationErrorType.JSON_PARSING,
                null,
                new JsonParsingMessage(jsonParseException)
        );
    }

}
