package com.asyncapi.lsp.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Dummy implementation of Documents storage
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
@Slf4j
public class DocumentStorage {

    @Getter
    public static final DocumentStorage instance = new DocumentStorage();

    private final ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();

    public void write(@NotNull String uri, @NotNull String content) {
        log.debug("Write file: {}", uri);
        documents.put(uri, content);
    }

    @Nullable
    public String read(@NotNull String uri) {
        log.debug("Read file: {}", uri);
        return documents.get(uri);
    }

    public void remove(@NotNull String uri) {
        log.debug("Delete file: {}", uri);
        documents.remove(uri);
    }

}
