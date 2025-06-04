package com.asyncapi.lsp.json;

import com.fasterxml.jackson.core.JsonLocation;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.serialization.node.JsonLocationAware;
import com.networknt.schema.serialization.node.JsonLocationAwareObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JSON validator")
public class JsonSchemaValidatorTest {

    private final JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator();

    @Test
    @DisplayName("minimal specification is valid")
    public void minimalSpecificationIsValid() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0",
                          "info": {
                            "title": "Minimalistic AsyncAPI specification",
                            "version": "1.0.0"
                          }
                        }
                        """,
                true
        );

        assertThat(ValidationResult.valid()).isEqualTo(result);
    }

    @Test
    @DisplayName("minimal specification is not valid when is malformed JSON")
    public void minimalSpecificationIsNoValidWhenIsMalformedJson() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0",
                          "info": {
                            "title": "Minimalistic AsyncAPI specification",
                            "version": "1.0.0",
                          }
                        }
                        """,
                true
        );

        assertThat(result.isValid()).isFalse();
        assertThat(ValidationErrorType.JSON_PARSING).isEqualTo(result.validationErrorType());
        assertThat(result.validationMessages()).isNull();

        checkJsonParsingMessage(
                result.jsonParsingMessage(),
                "Unexpected character ('}' (code 125)): was expecting double-quote to start field name",
                6,
                3
        );
    }

    @Test
    @DisplayName("specification without required fields is not valid")
    public void specificationWithoutRequiredFieldsIsNotValid() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0"
                        }
                        """,
                true
        );

        assertThat(result.isValid()).isFalse();
        assertThat(result.validationMessages()).isNotNull();
        assertThat(result.validationMessages()).size().isEqualTo(1);
        checkRequiredValidationMessage(
                result.validationMessages().stream().toList().getFirst(),
                "info",
                "$",
                "$: required property 'info' not found",
                1,
                1
        );
    }


    @Test
    @DisplayName("specification without required info fields is not valid")
    public void specificationWithoutRequiredInfoFieldsIsNotValid() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0",
                          "info": {
                          }
                        }
                        """,
                true
        );

        assertThat(result.isValid()).isFalse();
        assertThat(result.validationMessages()).isNotNull();
        assertThat(result.validationMessages()).size().isEqualTo(2);

        checkRequiredValidationMessage(
                result.validationMessages().stream().toList().getFirst(),
                "version",
                "$.info",
                "$.info: required property 'version' not found",
                3,
                11
        );

        checkRequiredValidationMessage(
                result.validationMessages().stream().toList().get(1),
                "title",
                "$.info",
                "$.info: required property 'title' not found",
                3,
                11
        );
    }

    @Test
    @DisplayName("specification with unknown property is not valid")
    public void specificationWithUnknownPropertyIsNotValid() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0",
                          "custom": "property",
                          "info": {
                            "title": "Minimalistic AsyncAPI specification",
                            "version": "1.0.0"
                          }
                        }
                        """,
                true
        );

        assertThat(result.isValid()).isFalse();
        assertThat(result.validationMessages()).isNotNull();

        checkAdditionalPropertiesValidationMessage(
                result.validationMessages().stream().toList().getFirst(),
                "custom",
                "$: property 'custom' is not defined in the schema and the schema does not allow additional properties",
                3,
                13
        );
    }

    @Test
    @DisplayName("specification with unknown properties is not valid")
    public void specificationWithUnknownPropertiesIsNotValid() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0",
                          "custom #1": "property",
                          "info": {
                            "title": "Minimalistic AsyncAPI specification",
                            "custom #2": "property",
                            "version": "1.0.0"
                          },
                          "custom #3": "property"
                        }
                        """,
                true
        );

        assertThat(result.isValid()).isFalse();
        assertThat(result.validationMessages()).isNotNull();
        assertThat(result.validationMessages().size()).isEqualTo(3);

        checkAdditionalPropertiesValidationMessage(
                result.validationMessages().stream().toList().getFirst(),
                "custom #2",
                "$.info: property 'custom #2' is not defined in the schema and the schema does not allow additional properties",
                6,
                18
        );

        checkAdditionalPropertiesValidationMessage(
                result.validationMessages().stream().toList().get(1),
                "custom #1",
                "$: property 'custom #1' is not defined in the schema and the schema does not allow additional properties",
                3,
                16
        );

        checkAdditionalPropertiesValidationMessage(
                result.validationMessages().stream().toList().get(2),
                "custom #3",
                "$: property 'custom #3' is not defined in the schema and the schema does not allow additional properties",
                9,
                16
        );
    }

    @Test
    @DisplayName("specification with wrong property type is not valid")
    public void specificationWithWrongPropertyTypeIsNotValid() {
        final var result = jsonSchemaValidator.validate(
                """
                        {
                          "asyncapi": "3.0.0",
                          "info": {
                            "title": "Minimalistic AsyncAPI specification",
                            "version": "1.0.0",
                            "tags": [
                                { "$ref": "https://exmaple.com/reference" },
                                1,
                                { "name": "tag #1" }
                            ]
                          },
                          "servers": []
                        }
                        """,
                true
        );

        assertThat(result.isValid()).isFalse();
        assertThat(result.validationMessages()).isNotNull();

        checkConstraintValidationMessage(
                result.validationMessages().stream().toList().getFirst(),
                "oneOf", //
                "$.info.tags[1]: must be valid to one and only one schema, but 0 are valid",
                "$.info.tags[1]",
                8,
                9
        );

        checkConstraintValidationMessage(
                result.validationMessages().stream().toList().get(1),
                "type", //
                "$.info.tags[1]: integer found, object expected",
                "$.info.tags[1]",
                8,
                9
        );

        checkConstraintValidationMessage(
                result.validationMessages().stream().toList().get(2),
                "type", //
                "$.info.tags[1]: integer found, object expected",
                "$.info.tags[1]",
                8,
                9
        );

        checkConstraintValidationMessage(
                result.validationMessages().stream().toList().get(3),
                "type", //
                "$.servers: array found, object expected",
                "$.servers",
                12,
                14
        );
    }

    public void checkLineAndColumnNumbers(
            @NotNull JsonLocation jsonLocation,
            int expectedLineNumber,
            int expectedColumnNumber
    ) throws AssertionError {
        assertThat(jsonLocation).isNotNull();
        assertThat(jsonLocation.getLineNr()).isEqualTo(expectedLineNumber);
        assertThat(jsonLocation.getColumnNr()).isEqualTo(expectedColumnNumber);
    }

    public void checkJsonParsingMessage(
            @Nullable JsonParsingMessage jsonParsingMessage,
            @NotNull String expectedMessage,
            int expectedLineNumber,
            int expectedColumnNumber
    ) throws AssertionError {
        assertThat(jsonParsingMessage).isNotNull();
        assertThat(jsonParsingMessage.getMessage()).isEqualTo(expectedMessage);

        checkLineAndColumnNumbers(jsonParsingMessage.getLocation(), expectedLineNumber, expectedColumnNumber);
    }

    public void checkRequiredValidationMessage(
            @NotNull ValidationMessage validationMessage,
            @NotNull String expectedProperty,
            @NotNull String expectedPropertyLocation,
            @NotNull String expectedMessage,
            int expectedLineNumber,
            int expectedColumnNumber
    ) throws AssertionError {
        assertThat(validationMessage).isNotNull();
        assertThat(validationMessage.getType()).isEqualTo("required");
        assertThat(validationMessage.getProperty()).isEqualTo(expectedProperty);
        assertThat(validationMessage.getInstanceLocation().toString()).isEqualTo(expectedPropertyLocation);
        assertThat(validationMessage.getMessage()).isEqualTo(expectedMessage);

        assertThat(validationMessage.getInstanceNode()).isInstanceOf(JsonLocationAware.class);
        final var location = ((JsonLocationAware) validationMessage.getInstanceNode()).tokenLocation();
        assertThat(location.getLineNr()).isEqualTo(expectedLineNumber);
        assertThat(location.getColumnNr()).isEqualTo(expectedColumnNumber);
    }

    public void checkAdditionalPropertiesValidationMessage(
            @NotNull ValidationMessage validationMessage,
            @NotNull String expectedProperty,
            @NotNull String expectedMessage,
            int expectedLineNumber,
            int expectedColumnNumber
    ) throws AssertionError {
        assertThat(validationMessage).isNotNull();
        assertThat(validationMessage.getType()).isEqualTo("additionalProperties");
        assertThat(validationMessage.getProperty()).isEqualTo(expectedProperty);
        assertThat(validationMessage.getMessage()).isEqualTo(expectedMessage);

        assertThat(validationMessage.getInstanceNode()).isInstanceOf(JsonLocationAwareObjectNode.class);
        final var instanceNode = (JsonLocationAwareObjectNode) validationMessage.getInstanceNode();
        final var forbiddenNodeLocation = ((JsonLocationAware) instanceNode.findPath(expectedProperty)).tokenLocation();
        assertThat(forbiddenNodeLocation.getLineNr()).isEqualTo(expectedLineNumber);
        assertThat(forbiddenNodeLocation.getColumnNr()).isEqualTo(expectedColumnNumber);
    }

    public void checkConstraintValidationMessage(
            @NotNull ValidationMessage validationMessage,
            @NotNull String expectedMessageKey,
            @NotNull String expectedMessage,
            @NotNull String expectedPropertyLocation,
            int expectedLineNumber,
            int expectedColumnNumber
    ) throws AssertionError {
        assertThat(validationMessage).isNotNull();
        assertThat(validationMessage.getMessageKey()).isEqualTo(expectedMessageKey);
        assertThat(validationMessage.getMessage()).isEqualTo(expectedMessage);
        assertThat(validationMessage.getInstanceLocation().toString()).isEqualTo(expectedPropertyLocation);

        assertThat(validationMessage.getInstanceNode()).isInstanceOf(JsonLocationAware.class);
        final var instanceNode = (JsonLocationAware) validationMessage.getInstanceNode();
        final var invalidNodeLocation = ((JsonLocationAware) instanceNode).tokenLocation();
        assertThat(invalidNodeLocation.getLineNr()).isEqualTo(expectedLineNumber);
        assertThat(invalidNodeLocation.getColumnNr()).isEqualTo(expectedColumnNumber);
    }

}
