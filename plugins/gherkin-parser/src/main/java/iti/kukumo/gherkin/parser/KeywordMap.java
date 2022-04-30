package iti.kukumo.gherkin.parser;

import java.util.*;

public interface KeywordMap {

    List<String> keywords(KeywordType keywordType);


    KeywordMap DEFAULT =
        category -> switch (category) {
            case GIVEN -> List.of("*", "Given", "Given that");
            case WHEN -> List.of("*", "When");
            case THEN -> List.of("*", "Then");
            case AND -> List.of("*", "And");
            case BUT -> List.of("*", "But");
            case FEATURE -> List.of("Feature", "Business Need", "Ability");
            case BACKGROUND -> List.of("Background");
            case SCENARIO -> List.of("Scenario");
            case SCENARIO_OUTLINE -> List.of("Scenario Outline", "Scenario Template");
            case EXAMPLES -> List.of("Examples", "Scenarios");
            default -> List.of();
         };

}
