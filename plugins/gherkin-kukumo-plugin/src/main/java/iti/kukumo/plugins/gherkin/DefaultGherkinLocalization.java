package iti.kukumo.plugins.gherkin;

import java.util.*;
import iti.kukumo.gherkin.parser.KeywordMap;
import org.jexten.Extension;

@Extension(extensionPointVersion = "2.0")
public class DefaultGherkinLocalization implements GherkinLocalization {

    private final List<Locale> locales = List.of(Locale.ENGLISH);


    @Override
    public List<Locale> locales() {
        return locales;
    }


    @Override
    public KeywordMap keywordMapForLocale(Locale locale) {
        return KeywordMap.DEFAULT;
    }

}
