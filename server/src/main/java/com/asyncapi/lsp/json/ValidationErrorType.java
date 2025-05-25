package com.asyncapi.lsp.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.InputFormat;

/**
 * Validation error type to recognize what exactly failed the validation process
 * 
 * @author Pavel Bodiachevskii
 * @version 1.0.0
 */
public enum ValidationErrorType {

    /**
     * Validation error related with {@link com.networknt.schema.JsonSchema#validate(JsonNode)} invocation
     */
    JSON_SCHEMA_VALIDATION,

    /**
     * Validation error related with {@link com.networknt.schema.JsonSchemaFactory#readTree(String, InputFormat)} invocation
     */
    JSON_PARSING;

}
