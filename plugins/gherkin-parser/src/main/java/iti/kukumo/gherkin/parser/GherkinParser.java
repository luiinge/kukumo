package iti.kukumo.gherkin.parser;

import java.io.*;
import java.util.*;
import iti.kukumo.gherkin.parser.elements.GherkinDocument;
import iti.kukumo.gherkin.parser.internal.*;

public class GherkinParser {

    private final Parser parser;

    public GherkinParser(KeywordMapProvider keywordMapProvider) {
        this.parser = new Parser(keywordMapProvider);
    }

    public GherkinParser(List<KeywordMapProvider> keywordMapProviders) {
        this.parser = new Parser(new AggregateKeywordMapProvider(keywordMapProviders));
    }

    public GherkinParser(KeywordMap keywordMap) {
        this.parser = new Parser(it -> Optional.of(keywordMap));
    }

    public GherkinDocument parse(Reader reader) {
        return parser.parse(reader);
    }


}
