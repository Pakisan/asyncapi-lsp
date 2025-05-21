package com.asyncapi.lsp;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("AsyncAPI Language Server started");
        @NotNull final var languageServer = new AsyncAPILanguageServer();

        try (@NotNull final var serverSocket = new ServerSocket(5005)) {
            final var socket = serverSocket.accept();
            @NotNull final var languageServerLauncher = serverLauncher(languageServer, socket)
                    .startListening()
                    .get();
        } catch (IOException e) {
            log.error("socket creation error - {}", e.getMessage());
            System.exit(1);
        } finally {
            log.info("AsyncAPI Language Server stopped");
        }
    }

    @NotNull
    public static Launcher<LanguageClient> serverLauncher(
            @NotNull final AsyncAPILanguageServer languageServer,
            @NotNull final Socket socket
    ) throws IOException {
        return LSPLauncher.createServerLauncher(
                languageServer,
                socket.getInputStream(),
                socket.getOutputStream()
        );
    }

}