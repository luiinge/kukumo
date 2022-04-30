package iti.kukumo.gherkin.parser.internal;

import java.util.*;
import iti.kukumo.gherkin.parser.*;

public class AggregateKeywordMapProvider implements KeywordMapProvider {

    private final List<KeywordMapProvider> aggregates;


    public AggregateKeywordMapProvider(List<KeywordMapProvider> aggregates) {
        this.aggregates = List.copyOf(aggregates);
    }


    @Override
    public Optional<KeywordMap> keywordMap(Locale locale) {
        return aggregates.stream().flatMap(it -> it.keywordMap(locale).stream()).findAny();
    }

}
