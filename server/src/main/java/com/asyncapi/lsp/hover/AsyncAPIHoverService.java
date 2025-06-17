package com.asyncapi.lsp.hover;

import com.asyncapi.lsp.TextDocumentCompletion;
import com.asyncapi.lsp.service.DocumentStorage;
import com.asyncapi.lsp.service.Utils;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * AsyncAPI completion service
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
@Slf4j
public class AsyncAPIHoverService {

    private final DocumentStorage documentStorage = DocumentStorage.instance;

    @NotNull
    public Hover run(@NotNull HoverParams hoverParams) {
        final var uri = hoverParams.getTextDocument().getUri();
        final var cursorPosition = hoverParams.getPosition();
        final var emptyHover = new Hover();
        emptyHover.setContents(new MarkupContent(MarkupKind.PLAINTEXT, ""));

        final var content = documentStorage.read(uri);
        if (content == null) {
            return emptyHover;
        }

        log.debug("Getting information about field: {}", uri);
        final var nodePath = Utils.nodePath(content, cursorPosition);
        if (nodePath == null) {
            return emptyHover;
        }

        @NotNull final var numericPattern = Pattern.compile("\\d+"); // always positive

        @Nullable Class<?> classToAnalyze = AsyncAPI.class;
        @NotNull final var pathElements = new LinkedList<>(List.of(nodePath.split("/")));
        @NotNull final String fieldName = pathElements.removeLast();
        boolean nextPathElementIsMapKey = false;
        if (pathElements.size() > 1) {
            for (@NotNull String pathElement : pathElements) {
                try {
                    /*
                        Given next path: /info/tags/0/name
                        Ensure that: current pathElement is not numeric
                        To avoid: NoSuchFieldException and get an element inside a collection
                     */
                    if (pathElement.isEmpty()
                            || numericPattern.matcher(pathElement).matches()
                            || nextPathElementIsMapKey
                    ) {
                        nextPathElementIsMapKey = false;
                        continue;
                    }

                    @NotNull final var classField = classToAnalyze.getDeclaredField(pathElement);
                    if (List.class.equals(classField.getType()) || Object.class.equals(classField.getType())) {
                        classToAnalyze = Utils.recognizeClass(classField);

                    } else if (Map.class.equals(classField.getType())) {
                        classToAnalyze = Utils.recognizeClass(classField);
                        nextPathElementIsMapKey = true;
                    } else {
                        classToAnalyze = classToAnalyze.getDeclaredField(pathElement).getType();
                    }
                } catch (Exception e) {
                    classToAnalyze = null;
                    log.debug("Field info resolving failure: {}", e.getMessage());
                }
            }
        }
        if (classToAnalyze != null) {
            try {
                return asHover(classToAnalyze.getDeclaredField(fieldName));
            } catch (Exception e) {
                log.debug("Field info resolving failure: {}", e.getMessage());
            }
        }


        return emptyHover;
    }

    @NotNull
    public Hover asHover(@NotNull Field field) {
        final var hover = new Hover();
        final String content;

        @Nullable final TextDocumentCompletion completionHint = field.getAnnotation(TextDocumentCompletion.class);
        if (completionHint != null) {
            content = completionHint.documentation();
        } else {
            content = "";
        }

        hover.setContents(new MarkupContent(MarkupKind.PLAINTEXT, content));
        return hover;
    }

}
