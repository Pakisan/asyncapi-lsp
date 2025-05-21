package com.asyncapi.lsp.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class AsyncAPITextDocumentService implements TextDocumentService {

    @NotNull
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(@NotNull CompletionParams completionParams) {
        return CompletableFuture.supplyAsync(() -> {
            var completionItems = Collections.<CompletionItem>emptyList();
            try {
                log.debug("Completing: {}", completionParams.getTextDocument().getUri());
//                completionItems = ContentParserUtil.getCompletions(completionParams);
            } catch (Exception e) {
                log.error("Error while completion: {}", e.getMessage());
            }

            return Either.forLeft(completionItems);
        });
    }

    @Nullable
    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(@NotNull CompletionItem completionItem) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<Hover> hover(@NotNull HoverParams params) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(@NotNull SignatureHelpParams params) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(@NotNull DefinitionParams textDocumentPositionParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<? extends Location>> references(@NotNull ReferenceParams referenceParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(@NotNull DocumentHighlightParams textDocumentPositionParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(@NotNull DocumentSymbolParams documentSymbolParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(@NotNull CodeActionParams codeActionParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(@NotNull CodeLensParams codeLensParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(@NotNull CodeLens codeLens) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(@NotNull DocumentFormattingParams documentFormattingParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(@NotNull DocumentRangeFormattingParams documentRangeFormattingParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(@NotNull DocumentOnTypeFormattingParams documentOnTypeFormattingParams) {
        return null;
    }

    @Nullable
    @Override
    public CompletableFuture<WorkspaceEdit> rename(@NotNull RenameParams renameParams) {
        return null;
    }

    @Override
    public void didOpen(@NotNull DidOpenTextDocumentParams didOpenTextDocumentParams) {
        @NotNull final var uri = didOpenTextDocumentParams.getTextDocument().getUri();
        @NotNull final var content = didOpenTextDocumentParams.getTextDocument().getText();

        // do nothing
    }

    @Override
    public void didChange(@NotNull DidChangeTextDocumentParams didChangeTextDocumentParams) {
        @NotNull final var uri = didChangeTextDocumentParams.getTextDocument().getUri();
        @NotNull final var content = didChangeTextDocumentParams.getContentChanges().getFirst().getText();

        // do nothing
    }

    @Override
    public void didClose(@NotNull DidCloseTextDocumentParams didCloseTextDocumentParams) {
        @NotNull final var uri = didCloseTextDocumentParams.getTextDocument().getUri();

        // do nothing
    }

    @Override
    public void didSave(@NotNull DidSaveTextDocumentParams didSaveTextDocumentParams) {
        // do nothing
    }

}