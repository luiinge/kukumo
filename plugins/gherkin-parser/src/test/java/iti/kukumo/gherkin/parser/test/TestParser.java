package iti.kukumo.gherkin.parser.test;

import java.io.StringReader;
import java.util.*;
import iti.kukumo.gherkin.parser.*;
import iti.kukumo.gherkin.parser.elements.*;
import iti.kukumo.gherkin.parser.elements.Tag;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestParser {


    private static final GherkinParser parser = new GherkinParser(KeywordMap.DEFAULT);

    @Test
    @DisplayName("A Gherkin document can be parsed")
    void gherkinDocumentCanBeParsed() {

        var document = """
            # this is a comment
            @tagA @tagB
            Feature: Account Holder withdraws cash
    
            @tagC
            # this is a comment for the scenario
            Scenario: Account has sufficient funds
                Given the account balance is $100
                Then the ATM should dispense:
                ```json
                { "amount" : 30.5 }
                ```
                And the card should return:
                | amount | taxes |
                | 45     | 0.48  |
                | 85     | 0.745 |
            """;

        var parsed = parser.parse(new StringReader(document));

        var expectedScenario = new Scenario(
            new Location(7,1),
            List.of( new Comment( new Location(6,1), "# this is a comment for the scenario")),
            List.of ( new Tag( new Location(5,1), "@tagC") ),
            "Scenario",
            "Account has sufficient funds",
            "",
            List.of(
                new Step(
                    new Location(8,5),
                    List.of(),
                    "Given",
                    "the account balance is $100",
                    null
                ),
                // --step
                new Step(
                    new Location(9,5),
                    List.of(),
                    "Then",
                    "the ATM should dispense:",
                    new DocString(
                        new Location(10,5),
                        "json",
                        """
                        { "amount" : 30.5 }"""
                    )
                ),
                // --step
                new Step(
                    new Location(13,5),
                    List.of(),
                    "And",
                    "the card should return:",
                    new DataTable(
                        new Location(14,5),
                        List.of(
                            new TableRow( new Location(14,5), List.of (
                                new TableCell( new Location(14,7), "amount"),
                                new TableCell( new Location(14,16), "taxes")
                            )),
                            new TableRow( new Location(15,5), List.of (
                                new TableCell( new Location(15,7), "45"),
                                new TableCell( new Location(15,16),"0.48")
                            )),
                            new TableRow( new Location(16,5), List.of (
                                new TableCell( new Location(16,7), "85"),
                                new TableCell( new Location(16,16), "0.745")
                            ))
                        )
                    )
                )
                // --step
            )
        );


        var expectedFeature = new Feature(
            new Location(3,1),
            List.of( new Comment( new Location(1,1), "# this is a comment")),
            List.of (
                new Tag( new Location(2,1), "@tagA"),
                new Tag( new Location(2,7), "@tagB")
            ),
            "Feature",
            "Account Holder withdraws cash",
            "",
            List.of(expectedScenario),
            "en"
        );


        var expected = new GherkinDocument(expectedFeature);

        assertEquals(expected, parsed);
    }

}
