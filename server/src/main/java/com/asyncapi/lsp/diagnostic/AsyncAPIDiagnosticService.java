package com.asyncapi.lsp.diagnostic;

import com.asyncapi.lsp.json.JsonSchemaValidator;
import com.asyncapi.lsp.json.ValidationMessageConverter;
import com.asyncapi.lsp.service.DocumentStorage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.DocumentDiagnosticParams;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.RelatedFullDocumentDiagnosticReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * AsyncAPI diagnostic service
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
@Slf4j
public class AsyncAPIDiagnosticService {

    private final DocumentStorage documentStorage = DocumentStorage.instance;
    private final JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator();
    private final ValidationMessageConverter validationMessageConverter = new ValidationMessageConverter();

    @NotNull
    public DocumentDiagnosticReport run(@NotNull DocumentDiagnosticParams params) {
        final var uri = params.getTextDocument().getUri();

        log.debug("Analysing: {}", uri);
        @Nullable final var content = documentStorage.read(uri);

        @NotNull final var documentDiagnosticResult = new RelatedFullDocumentDiagnosticReport();
        @NotNull final var jsonSchemaValidationResult = jsonSchemaValidator.validate(content, true);

        documentDiagnosticResult.setItems(validationMessageConverter.convert(jsonSchemaValidationResult));
        return new DocumentDiagnosticReport(documentDiagnosticResult);
    }

}
