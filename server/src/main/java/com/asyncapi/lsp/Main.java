package com.asyncapi.lsp;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jetbrains.annotations.NotNull;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@Slf4j
public class Main {

    public static void main(String[] args) {
        final var serverPort = 5005;
        @NotNull final var languageServer = new AsyncAPILanguageServer();

        try (@NotNull final var threadPool = Executors.newCachedThreadPool()) {
            Callable<Void> callableTask =
                    () -> {
                        for (;;) {
                            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                                Socket socket = serverSocket.accept();

                                Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(languageServer, socket.getInputStream(), socket.getOutputStream());
                                launcher.startListening();
                            }
                        }
                    };

            threadPool.submit(callableTask);
        } catch (Exception e) {
            log.error("error - {}", e.getMessage());
            System.exit(1);
        } finally {
            log.info("Exiting");
        }
    }

}