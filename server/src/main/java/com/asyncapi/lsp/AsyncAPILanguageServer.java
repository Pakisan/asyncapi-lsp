package com.asyncapi.lsp;

import com.asyncapi.lsp.service.AsyncAPITextDocumentService;
import com.asyncapi.lsp.service.AsyncAPIWorkspaceService;
import lombok.Getter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AsyncAPILanguageServer implements LanguageServer, LanguageClientAware {

    @NotNull
    private final TextDocumentService textDocumentService;

    @NotNull
    private final WorkspaceService workspaceService;

    @Getter
    @NotNull
    private LanguageClient client;

    private int shutDownStatus = 1;

    public AsyncAPILanguageServer() {
        this.textDocumentService = new AsyncAPITextDocumentService(this);
        this.workspaceService = new AsyncAPIWorkspaceService();
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    @NotNull
    @Override
    public CompletableFuture<InitializeResult> initialize(@NotNull InitializeParams initializeParams) {
        // textDocument/completion
        final var completionOptions = new CompletionOptions();
        completionOptions.setResolveProvider(false);

        // textDocument/hover
        final var hoverOptions = new HoverOptions();
        hoverOptions.setWorkDoneProgress(false);

        // textDocument/publishDiagnostics
        final var diagnosticOptions = new DiagnosticRegistrationOptions();

        final var capabilities = new ServerCapabilities();
        capabilities.setCompletionProvider(completionOptions);
        capabilities.setDiagnosticProvider(diagnosticOptions);
        capabilities.setHoverProvider(hoverOptions);
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);

        final var initializeResult = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(initializeResult);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        this.shutDownStatus = 1;
        return null;
    }

    @Override
    public void exit() {
        System.exit(shutDownStatus);
    }

    @NotNull
    @Override
    public TextDocumentService getTextDocumentService() {
        return this.textDocumentService;
    }

    @NotNull
    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

}
