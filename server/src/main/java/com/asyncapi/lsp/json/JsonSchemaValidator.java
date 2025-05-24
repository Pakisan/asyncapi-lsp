package com.asyncapi.lsp.json;

import com.networknt.schema.*;
import com.networknt.schema.serialization.DefaultJsonNodeReader;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
    public Set<ValidationMessage> validate(@NotNull String document, boolean isJson) {
        final var inputFormat = isJson ? InputFormat.JSON : InputFormat.YAML;

        return asyncAPIJsonSchemaV3.validate(document, inputFormat);
    }

}
