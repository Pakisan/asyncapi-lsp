package com.asyncapi.lsp.diagnostic;

import com.asyncapi.lsp.json.JsonSchemaValidator;
import com.asyncapi.lsp.json.ValidationMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.DocumentDiagnosticParams;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.RelatedFullDocumentDiagnosticReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * AsyncAPI diagnostic service
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
@Slf4j
public class AsyncAPIDiagnosticService {

    private final JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator();
    private final ValidationMessageConverter validationMessageConverter = new ValidationMessageConverter();

    @NotNull
    public DocumentDiagnosticReport run(@NotNull DocumentDiagnosticParams params) {
        log.debug("Analysing: {}", params.getTextDocument().getUri());
        @Nullable final var content = readFile(params.getTextDocument().getUri());

        @NotNull final var documentDiagnosticResult = new RelatedFullDocumentDiagnosticReport();
        @NotNull final var jsonSchemaValidationResult = jsonSchemaValidator.validate(content, true);

        documentDiagnosticResult.setItems(validationMessageConverter.convert(jsonSchemaValidationResult));
        return new DocumentDiagnosticReport(documentDiagnosticResult);
    }

    @Nullable
    public String readFile(@NotNull String uri) {
        log.debug("reading file - {}", uri);
        @Nullable String content = null;

        try {
            final var path = Paths.get(URI.create(uri));
            content = Files.readString(path);

        } catch (Exception e) {
            log.error("Error while reading file: {}", e.getMessage());
        }

        return content;
    }

}
