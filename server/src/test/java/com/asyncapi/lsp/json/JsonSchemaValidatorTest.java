package com.asyncapi.lsp.json;

import com.networknt.schema.serialization.node.JsonLocationAwareObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("JSON")
public class JsonSchemaValidatorTest {

    private final JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator();

    @Test
    @DisplayName("validate: info without title and version")
    public void validateInfoWithoutTitleVersion() throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("schema/info without title version.json")) {
            final var specification = new String(inputStream.readAllBytes());
            final var result = jsonSchemaValidator.validate(specification, true).stream().toList();

            var expectedProperty = result.getFirst();
            Assertions.assertEquals("required", expectedProperty.getType());
            Assertions.assertEquals("version", expectedProperty.getProperty());
            Assertions.assertEquals("$.info", expectedProperty.getInstanceLocation().toString());
            var location = (JsonLocationAwareObjectNode) expectedProperty.getInstanceNode();
            Assertions.assertEquals(3, location.tokenLocation().getLineNr());
            Assertions.assertEquals(11, location.tokenLocation().getColumnNr());

            expectedProperty = result.get(1);
            Assertions.assertEquals("required", expectedProperty.getType());
            Assertions.assertEquals("title", expectedProperty.getProperty());
            Assertions.assertEquals("$.info", expectedProperty.getInstanceLocation().toString());
            location = (JsonLocationAwareObjectNode) expectedProperty.getInstanceNode();
            Assertions.assertEquals(3, location.tokenLocation().getLineNr());
            Assertions.assertEquals(11, location.tokenLocation().getColumnNr());
        }
    }

}
