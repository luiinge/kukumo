package iti.kukumo.gherkin.parser;

import java.util.*;

@FunctionalInterface
public interface KeywordMapProvider {

    Optional<KeywordMap> keywordMap(Locale locale);

}
