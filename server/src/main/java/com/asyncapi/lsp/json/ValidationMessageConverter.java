package com.asyncapi.lsp.json;

import com.fasterxml.jackson.core.JsonLocation;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.serialization.node.JsonLocationAware;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
public class ValidationMessageConverter {

    @NotNull
    public List<Diagnostic> convert(@NotNull ValidationResult validationResult) {
        var diagnostics = new LinkedList<Diagnostic>();
        if (!validationResult.isValid()) {
            switch (validationResult.validationErrorType()) {
                case JSON_PARSING -> diagnostics.add(convert(validationResult.jsonParsingMessage()));
                case JSON_SCHEMA_VALIDATION -> validationResult.validationMessages().stream()
                        .filter(validationMessage ->
                                validationMessage.getType().equals("required") ||
                                        validationMessage.getType().equals("additionalProperties")
                        )
                        .map(this::convert)
                        .forEach(diagnostics::add);
                case null -> {
                    // do nothing
                }
            }
        }

        return diagnostics;
    }

    @NotNull
    Diagnostic convert(@NotNull ValidationMessage validationMessage) {
        final var diagnostic = new Diagnostic();

        diagnostic.setSeverity(DiagnosticSeverity.Error);
        diagnostic.setMessage(validationMessage.getMessage());

        @NotNull final JsonLocationAware instanceNode;
        if (validationMessage.getType().equals("additionalProperties")) {
            final var propertyName = validationMessage.getProperty();
            instanceNode = ((JsonLocationAware) validationMessage.getInstanceNode().findPath(propertyName));
        } else {
            instanceNode = ((JsonLocationAware) validationMessage.getInstanceNode());
        }
        diagnostic.setRange(range(instanceNode.tokenLocation()));

        return diagnostic;
    }

    @NotNull
    Diagnostic convert(@NotNull JsonParsingMessage jsonParsingMessage) {
        final var diagnostic = new Diagnostic();

        diagnostic.setSeverity(DiagnosticSeverity.Error);
        diagnostic.setMessage(jsonParsingMessage.getMessage());
        diagnostic.setRange(range(jsonParsingMessage.getLocation()));

        return diagnostic;
    }

    @NotNull
    Range range(@NotNull JsonLocation jsonLocation) {
        return new Range(
                // Visual Studio Code counts from 0
                new Position(jsonLocation.getLineNr() - 1, 0),
                new Position(jsonLocation.getLineNr() - 1, jsonLocation.getColumnNr() - 1)
        );
    }

}
