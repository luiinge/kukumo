/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.util;


import iti.kukumo.core.util.TokenParser;
import java.util.*;
import org.junit.jupiter.api.*;


class TestTokenParser {

    private static final List<String> TOKENS = List.of(
        "mmmm",
        "mm",
        "dd",
        "d",
        "yyyy",
        "yy",
        "-",
        "\\",
        "/",
        " "
    );


    @Test
    void testTokenParser1() {
        TokenParser parser = new TokenParser(
            "mmmmmm/ ddd-yyyy-yy", TOKENS, List.of("'[^']*'")
        );
        assertNextToken(parser, "mmmm");
        assertNextToken(parser, "mm");
        assertNextToken(parser, "/");
        assertNextToken(parser, " ");
        assertNextToken(parser, "dd");
        assertNextToken(parser, "d");
        assertNextToken(parser, "-");
        assertNextToken(parser, "yyyy");
        assertNextToken(parser, "-");
        assertNextToken(parser, "yy");
    }


    @Test
    void testTokenParser2() {
        TokenParser parser = new TokenParser(
            "d' de 'mmmm' de 'yyyy", TOKENS, List.of("'[^']*'")
        );
        assertNextToken(parser, "d");
        assertNextToken(parser, "' de '");
        assertNextToken(parser, "mmmm");
        assertNextToken(parser, "' de '");
        assertNextToken(parser, "yyyy");
    }


    private void assertNextToken(TokenParser parser, String string) {
        Assertions.assertTrue(parser.hasMoreTokens());
        Assertions.assertEquals(parser.nextToken(),string);
    }

}
