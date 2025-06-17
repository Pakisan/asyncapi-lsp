package com.asyncapi.lsp.completion;

import com.asyncapi.lsp.TextDocumentCompletion;
import com.asyncapi.lsp.service.DocumentStorage;
import com.asyncapi.lsp.service.Utils;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AsyncAPI completion service
 *
 * @author Pavel Bodiachevskii
 * @since 1.0.0
 */
@Slf4j
public class AsyncAPICompletionService {

    private final DocumentStorage documentStorage = DocumentStorage.instance;

    @NotNull
    public CompletionList run(@NotNull CompletionParams completionParams) {
        final var uri = completionParams.getTextDocument().getUri();
        final var cursorPosition = completionParams.getPosition();
        final var emptyCompletion = new CompletionList();

        final var content = documentStorage.read(uri);
        if (content == null) {
            return emptyCompletion;
        }

        log.debug("Completing: {}", uri);
        final var nodePath = Utils.nodePath(content, cursorPosition);
        if (nodePath == null) {
            return emptyCompletion;
        }

        @NotNull final var numericPattern = Pattern.compile("\\d+"); // always positive

        @Nullable Class<?> classToComplete = AsyncAPI.class;
        @NotNull Field[] classFields = {};
        @NotNull final var pathElements = new LinkedList<>(List.of(nodePath.split("/")));
        @NotNull final String fieldPartialName = pathElements.removeLast();
        boolean nextPathElementIsMapKey = false;
        if (pathElements.size() > 1) {
            for (@NotNull String pathElement : pathElements) {
                try {
                    /*
                        Given next path: /info/tags/0/name
                        Ensure that: current pathElement is not numeric
                        To avoid: NoSuchFieldException

                        Given next path: /info/0/tags/{ - when a user is typing property outside a Tag object
                        Ensure that: user is typing inside a Tag object
                        To avoid: incorrect completion
                        Scenario:
                            {
                              "info": {
                                "tags": [
                                  nam<caret>
                     */
                    if (pathElement.isEmpty()
                            || numericPattern.matcher(pathElement).matches()
                            || "}".equals(fieldPartialName)
                            || nextPathElementIsMapKey
                    ) {
                        nextPathElementIsMapKey = false;
                        continue;
                    }

                    @NotNull final var classField = classToComplete.getDeclaredField(pathElement);
                    if (List.class.equals(classField.getType()) || Object.class.equals(classField.getType())) {
                        classToComplete = Utils.recognizeClass(classField);

                    } else if (Map.class.equals(classField.getType())) {
                        classToComplete = Utils.recognizeClass(classField);
                        nextPathElementIsMapKey = true;
                    } else {
                        classToComplete = classToComplete.getDeclaredField(pathElement).getType();
                    }
                } catch (Exception e) {
                    classToComplete = null;
                    log.debug("Completion failure: {}", e.getMessage());
                }
            }
        }
        classFields = classToComplete == null ? classFields : classToComplete.getDeclaredFields();

        final var variants = Stream.of(classFields).collect(Collectors.toSet());
        final var completion = new CompletionList();
        completion.setItems(variants.stream().map(variant -> asCompletionItem(variant, fieldPartialName)).collect(Collectors.toList()));

        return completion;
    }

    @NotNull
    public CompletionItem asCompletionItem(@NotNull Field field, @NotNull String fieldPartialName) {
        final var completionItem = new CompletionItem();

        @Nullable final TextDocumentCompletion completionHint = field.getAnnotation(TextDocumentCompletion.class);
        if (completionHint != null) {
            completionItem.setDetail(completionHint.detail());
            completionItem.setDocumentation(completionHint.documentation());
        }

        completionItem.setLabel(recognizeLabel(field));
        completionItem.setPreselect(completionItem.getLabel().startsWith(fieldPartialName));
        if ("String".equals(completionItem.getDetail())) {
            completionItem.setKind(CompletionItemKind.Field);
        } else {
            completionItem.setKind(CompletionItemKind.Struct);
        }

        return completionItem;
    }

    /**
     * Check does given field have <code>@JsonProperty</code> annotation with value or not
     * <p>
     * If value exists, then it's value will be returned otherwise original field name
     *
     * @param field field
     * @return label to use
     */
    @NotNull
    public String recognizeLabel(@NotNull Field field) {
        @NotNull String fieldName = field.getName();
        @Nullable final JsonProperty alternativeName = field.getAnnotation(JsonProperty.class);
        if (alternativeName != null) {
            if (alternativeName.value() != null) {
                fieldName = alternativeName.value();
            }
        }

        return fieldName;
    }

}
