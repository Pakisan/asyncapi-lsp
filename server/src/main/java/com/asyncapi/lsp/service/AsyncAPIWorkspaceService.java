package com.asyncapi.lsp.service;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncAPIWorkspaceService implements WorkspaceService {

    @Nullable
    @Override
    public CompletableFuture<Object> executeCommand(@NotNull ExecuteCommandParams params) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>>> symbol(@NotNull WorkspaceSymbolParams workspaceSymbolParams) {
        return null;
    }

    @Override
    public void didChangeConfiguration(@NotNull DidChangeConfigurationParams didChangeConfigurationParams) {
        // do nothing
    }

    @Override
    public void didChangeWatchedFiles(@NotNull DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
        // do nothing
    }

    @Override
    public void didChangeWorkspaceFolders(@NotNull DidChangeWorkspaceFoldersParams params) {
        // do nothing
    }

}