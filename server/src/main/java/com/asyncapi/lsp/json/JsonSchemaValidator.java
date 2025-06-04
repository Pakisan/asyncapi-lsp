package com.asyncapi.lsp.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.serialization.DefaultJsonNodeReader;
import org.jetbrains.annotations.NotNull;

/**
 * Validates JSON AsyncAPI specification against AsyncAPI JSON Schema
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
public class JsonSchemaValidator {

    @NotNull
    protected final JsonSchemaFactory jsonSchemaFactory;

    @NotNull
    protected final JsonSchema asyncAPIJsonSchemaV3;

    public JsonSchemaValidator() {
        jsonSchemaFactory = JsonSchemaFactory.getInstance(
                SpecVersion.VersionFlag.V7,
                builder -> builder.jsonNodeReader(DefaultJsonNodeReader.builder().locationAware().build())
        );
        asyncAPIJsonSchemaV3 = jsonSchemaFactory.getSchema(
                JsonSchemaValidator.class.getClassLoader().getResourceAsStream("./schema/asyncapi.json")
        );
    }

    @NotNull
    public ValidationResult validate(@NotNull String document, boolean isJson) {
        final var inputFormat = isJson ? InputFormat.JSON : InputFormat.YAML;

        try {
            @NotNull final var validationMessages = asyncAPIJsonSchemaV3.validate(document, inputFormat);
            @NotNull final ValidationResult validationResult = validationMessages.isEmpty()
                    ? ValidationResult.valid()
                    : ValidationResult.validationError(validationMessages);

            return validationResult;
        } catch (IllegalArgumentException illegalArgumentException) {
            if (illegalArgumentException.getCause() instanceof JsonParseException jsonParseException) {
                return ValidationResult.parsingError(jsonParseException);
            }

            throw illegalArgumentException;
        }
    }

}
