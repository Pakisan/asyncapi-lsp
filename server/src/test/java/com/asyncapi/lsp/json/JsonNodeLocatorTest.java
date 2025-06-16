package com.asyncapi.lsp.json;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JSON Node Locator")
public class JsonNodeLocatorTest {

    public static Stream<Arguments> infoCompletionWithoutQuotes() {
        return Stream.of(
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  i
                """, "/i"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  in
                """, "/in"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  inf
                """, "/inf"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  info
                """, "/info"),

                //
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  i
                  "servers": {}
                """, "/i"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  in
                  "servers": {}
                """, "/in"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  inf
                  "servers": {}
                """, "/inf"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  info
                  "servers": {}
                """, "/info")
        );
    }

    public static Stream<Arguments> infoCompletionWithSingleQuote() {
        return Stream.of(
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "i
                """.trim(), "/i"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "in
                """.trim(), "/in"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "inf
                """.trim(), "/inf"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info
                """.trim(), "/info"),

                //
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "i
                  "servers": {}
                """.trim(), "/i"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "in
                  "servers": {}
                """.trim(), "/in"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "inf
                  "servers": {}
                """.trim(), "/inf"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info
                  "servers": {}
                """.trim(), "/info")
        );
    }

    public static Stream<Arguments> infoCompletionQuoted() {
        return Stream.of(
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "i"
                """, "/i"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "in"
                """, "/in"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "inf"
                """, "/inf"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info"
                """, "/info"),

                //
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "i"
                  "servers": {}
                """, "/i"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "in"
                  "servers": {}
                """, "/in"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "inf"
                  "servers": {}
                """, "/inf"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info"
                  "servers": {}
                """, "/info")
        );
    }

    @ParameterizedTest
    @DisplayName("complete info")
    @MethodSource({
            "infoCompletionWithoutQuotes",
            "infoCompletionWithSingleQuote",
            "infoCompletionQuoted"
    })
    public void completeInfo(@NotNull String specification, @NotNull String expectedPointer) throws IOException {
        String pointer = JsonNodeLocator.findNodeAtLocation(specification, 3, 5);
        assertThat(pointer).isEqualTo(expectedPointer);
    }

    public static Stream<Arguments> infoTitleCompletionWithoutQuotes() {
        return Stream.of(
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    t
                """, "/info/t"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    ti
                """, "/info/ti"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    tit
                """, "/info/tit"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    titl
                """, "/info/titl"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    title
                """, "/info/title"),

                //
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    t
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/t"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    ti
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/ti"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    tit
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/tit"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    titl
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/titl"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    title
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/title")
        );
    }

    public static Stream<Arguments> infoTitleCompletionWithSingleQuote() {
        return Stream.of(
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "t
                """, "/info/t"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "ti
                """, "/info/ti"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "tit
                """, "/info/tit"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "titl
                """, "/info/titl"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "title
                """, "/info/title"),

                //
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "t
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/t"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "ti
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/ti"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "tit
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/tit"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "titl
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/titl"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "title
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/title")
        );
    }

    public static Stream<Arguments> infoTitleCompletionQuoted() {
        return Stream.of(
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "t"
                """, "/info/t"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "ti"
                """, "/info/ti"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "tit"
                """, "/info/tit"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "titl"
                """, "/info/titl"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "title"
                """, "/info/title"),

                //
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "t"
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/t"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "ti"
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/ti"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "tit"
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/tit"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "titl"
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/titl"),
                Arguments.of("""
                {
                  "asyncapi": "3.0.0",
                  "info": {
                    "title"
                    "version": "1.0.0"
                  },
                  "servers": {}
                }
                """, "/info/title")
        );
    }

    @ParameterizedTest
    @DisplayName("complete info title")
    @MethodSource({
            "infoTitleCompletionWithoutQuotes",
//            "infoTitleCompletionWithSingleQuote", // TODO: fix this scenario
            "infoTitleCompletionQuoted"
    })
    public void completeInfoTitle(@NotNull String specification, @NotNull String expectedPointer) throws IOException {
        String pointer = JsonNodeLocator.findNodeAtLocation(specification, 4, 5);
        assertThat(pointer).isEqualTo(expectedPointer);
    }

    @Test
    public void completeChannel() throws IOException {
        final var pointer = JsonNodeLocator.findNodeAtLocation(
                """
                        {
                          "asyncapi": "3.0.0",
                          "info": {
                            "title": "Minimalistic AsyncAPI specification",
                            "version": "1.0.0"
                          },
                          "channels": {
                            "channel-a": {
                              d
                            }
                          }
                        }
                        """, 9, 8
        );
        assertThat(pointer).isEqualTo("/channels/channel-a/d");
    }

}