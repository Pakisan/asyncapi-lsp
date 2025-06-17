package com.asyncapi.lsp.service;

import com.asyncapi.lsp.json.JsonNodeLocator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Utility class
 *
 * @author Pavel Bodiachevskii
 */
public class Utils {

    @Nullable
    public static String nodePath(@NotNull String content, @NotNull Position cursorPosition) {
        return JsonNodeLocator.findNodeAtLocation(
                content,
                cursorPosition.getLine() + 1,
                cursorPosition.getCharacter() + 1
        );
    }

    @Nullable
    public static Class<?> recognizeClass(@NotNull Field classField) throws NoSuchMethodException {
        @Nullable Class<?> classToComplete = null;
        @Nullable final JsonDeserialize deserializeStrategy = classField.getAnnotation(JsonDeserialize.class);
        if (deserializeStrategy != null) {
            @Nullable final Class<?> deserializeStrategyImplementation = deserializeStrategy.using();

            if (deserializeStrategyImplementation != null) {
                @NotNull final var method = deserializeStrategyImplementation.getMethod("objectTypeClass");
                @NotNull final var returnType = (ParameterizedType) method.getGenericReturnType();

                classToComplete = (Class<?>) returnType.getActualTypeArguments()[0];
            }
        }

        return classToComplete;
    }

}
