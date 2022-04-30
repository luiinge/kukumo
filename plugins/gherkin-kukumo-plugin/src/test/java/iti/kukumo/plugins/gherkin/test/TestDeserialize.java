package iti.kukumo.plugins.gherkin.test;

import iti.kukumo.plugin.api.plan.*;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestDeserialize {
    
    @Test
    @DisplayName("serialized JSON can be deserialez")
    void deserialize() throws IOException {

        var actual = new PlanSerializer().deserialize("""
                {
                  "nodeType" : "AGGREGATOR",
                  "name" : "Test - Scenario with arguments",
                  "language" : "en",
                  "keyword" : "Feature",
                  "properties" : {
                    "gherkinType" : "feature"
                  },
                  "children" : [ {
                    "nodeType" : "TEST_CASE",
                    "name" : "Test Scenario with document",
                    "language" : "en",
                    "keyword" : "Scenario",
                    "properties" : {
                      "gherkinType" : "scenario"
                    },
                    "children" : [ {
                      "nodeType" : "STEP",
                      "name" : "a number with value 8.02 and another number with value 9",
                      "displayName" : "Given a number with value 8.02 and another number with value 9",
                      "language" : "en",
                      "keyword" : "Given",
                      "properties" : {
                        "gherkinType" : "step"
                      }
                    }, {
                      "nodeType" : "STEP",
                      "name" : "both numbers are multiplied",
                      "displayName" : "When both numbers are multiplied",
                      "language" : "en",
                      "keyword" : "When",
                      "properties" : {
                        "gherkinType" : "step"
                      }
                    }, {
                      "nodeType" : "STEP",
                      "name" : "the result is equals to:",
                      "displayName" : "Then the result is equals to:",
                      "language" : "en",
                      "keyword" : "Then",
                      "argument" : {
                        "document" : {
                          "contentType" : "json",
                          "content" : "{\\n  \\"result\\": 72.18\\n}"
                        }
                      },
                      "properties" : {
                        "gherkinType" : "step"
                      }
                    } ]
                  }, {
                    "nodeType" : "TEST_CASE",
                    "name" : "Test Scenario with data table",
                    "language" : "en",
                    "keyword" : "Scenario",
                    "properties" : {
                      "gherkinType" : "scenario"
                    },
                    "children" : [ {
                      "nodeType" : "STEP",
                      "name" : "a number with value 8.02 and another number with value 9",
                      "displayName" : "Given a number with value 8.02 and another number with value 9",
                      "language" : "en",
                      "keyword" : "Given",
                      "properties" : {
                        "gherkinType" : "step"
                      }
                    }, {
                      "nodeType" : "STEP",
                      "name" : "both numbers are multiplied",
                      "displayName" : "When both numbers are multiplied",
                      "language" : "en",
                      "keyword" : "When",
                      "properties" : {
                        "gherkinType" : "step"
                      }
                    }, {
                      "nodeType" : "STEP",
                      "name" : "the result is equals to:",
                      "displayName" : "Then the result is equals to:",
                      "language" : "en",
                      "keyword" : "Then",
                      "argument" : {
                        "dataTable" : {
                          "values" : [ [ "name", "value" ], [ "result", "72.18" ] ],
                          "rows" : 2,
                          "columns" : 2
                        }
                      },
                      "properties" : {
                        "gherkinType" : "step"
                      }
                    } ]
                  } ]
                }
            """);

        var expected = PlanNode.builder()
            .nodeType(NodeType.AGGREGATOR)
            .name("Test - Scenario with arguments")
            .language("en")
            .keyword("Feature")
            .properties(new TreeMap<>(Map.of("gherkinType","feature")))
            .children(List.of(
                PlanNode.builder()
                    .nodeType(NodeType.TEST_CASE)
                    .name("Test Scenario with document")
                    .language("en")
                    .keyword("Scenario")
                    .properties(new TreeMap<>(Map.of("gherkinType","scenario")))
                    .children(List.of(
                        PlanNode.builder()
                            .nodeType(NodeType.STEP)
                            .name("a number with value 8.02 and another number with value 9")
                            .displayName("Given a number with value 8.02 and another number with value 9")
                            .language("en")
                            .keyword("Given")
                            .properties(new TreeMap<>(Map.of("gherkinType","step")))
                            .build(),
                        PlanNode.builder()
                            .nodeType(NodeType.STEP)
                            .name("both numbers are multiplied")
                            .displayName("When both numbers are multiplied")
                            .language("en")
                            .keyword("When")
                            .properties(new TreeMap<>(Map.of("gherkinType","step")))
                            .build(),
                        PlanNode.builder()
                            .nodeType(NodeType.STEP)
                            .name("the result is equals to:")
                            .displayName("Then the result is equals to:")
                            .language("en")
                            .keyword("Then")
                            .properties(new TreeMap<>(Map.of("gherkinType","step")))
                            .argument(new PlanNode.PlanNodeArgument(
                                new Document("json","{\n  \"result\": 72.18\n}")
                            ))
                            .build()
                    ))
                    .build(),

                PlanNode.builder()
                    .nodeType(NodeType.TEST_CASE)
                    .name("Test Scenario with data table")
                    .language("en")
                    .keyword("Scenario")
                    .properties(new TreeMap<>(Map.of("gherkinType","scenario")))
                    .children(List.of(
                        PlanNode.builder()
                            .nodeType(NodeType.STEP)
                            .name("a number with value 8.02 and another number with value 9")
                            .displayName("Given a number with value 8.02 and another number with value 9")
                            .language("en")
                            .keyword("Given")
                            .properties(new TreeMap<>(Map.of("gherkinType","step")))
                            .build(),
                        PlanNode.builder()
                            .nodeType(NodeType.STEP)
                            .name("both numbers are multiplied")
                            .displayName("When both numbers are multiplied")
                            .language("en")
                            .keyword("When")
                            .properties(new TreeMap<>(Map.of("gherkinType","step")))
                            .build(),
                        PlanNode.builder()
                            .nodeType(NodeType.STEP)
                            .name("the result is equals to:")
                            .displayName("Then the result is equals to:")
                            .language("en")
                            .keyword("Then")
                            .properties(new TreeMap<>(Map.of("gherkinType","step")))
                            .argument(new PlanNode.PlanNodeArgument(
                                new DataTable(List.of(List.of("name","value"),List.of("result","72.18")))
                            ))
                            .build()
                    ))
                    .build()

            ))
        .build();
            
        assertEquals(actual.toString(),expected.toString());
    }

}
