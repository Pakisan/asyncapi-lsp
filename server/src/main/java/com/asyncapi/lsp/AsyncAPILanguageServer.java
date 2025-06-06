package com.asyncapi.lsp;

import com.asyncapi.lsp.service.AsyncAPITextDocumentService;
import com.asyncapi.lsp.service.AsyncAPIWorkspaceService;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AsyncAPILanguageServer implements LanguageServer {

    @NotNull
    private final TextDocumentService textDocumentService;

    @NotNull
    private final WorkspaceService workspaceService;

    private int shutDownStatus = 1;

    public AsyncAPILanguageServer() {
        this.textDocumentService = new AsyncAPITextDocumentService();
        this.workspaceService = new AsyncAPIWorkspaceService();
    }

    @NotNull
    @Override
    public CompletableFuture<InitializeResult> initialize(@NotNull InitializeParams initializeParams) {
        final var initializeResult = new InitializeResult(new ServerCapabilities());
        initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        initializeResult.getCapabilities().setCompletionProvider(new CompletionOptions());
        initializeResult.getCapabilities().setDiagnosticProvider(new DiagnosticRegistrationOptions());

        return CompletableFuture.supplyAsync(() -> initializeResult);
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
