package iti.kukumo.core.datatypes.assertions.adapters;

import iti.kukumo.core.datatypes.assertions.MatcherAssertion;
import iti.kukumo.plugin.api.adapters.DataTypeAdapter;
import iti.kukumo.plugin.api.datatypes.Assertion;
import java.util.*;


public abstract class AssertionDataTypeAdapter extends DataTypeAdapter<Assertion> {

    private final String propertyPrefix;


    protected AssertionDataTypeAdapter(
        String name,
        String propertyPrefix
    ) {
        super(name, Assertion.class);
        this.propertyPrefix = propertyPrefix;
    }


    protected void assertProviders(AbstractAssertProvider... assertProviders) {
        regexProvider(locale -> ".*");
        hintProvider(locale -> hintProvider(locale, propertyPrefix, assertProviders));
        parserProvider(parserProvider(assertProviders));
    }




    protected static List<String> hintProvider(
        Locale locale,
        String propertyPrefix,
        AbstractAssertProvider[] assertProviders
    ) {
        List<String> hints = new ArrayList<>();
        for (var assertProvider : assertProviders) {
           var properties = assertProvider
               .propertiesFor(locale)
               .filtered(key -> key.startsWith(propertyPrefix));
           properties.keyStream().forEach(key -> hints.add(properties.get(key,"")));
        }
        return List.copyOf(hints);
    }




    protected static LocaleTypeParser<Assertion> parserProvider(
        AbstractAssertProvider[] assertProviders
    ) {
        return locale -> expression -> {
            for (AbstractAssertProvider assertProvider : assertProviders) {
                var matcher = assertProvider.matcherFromExpression(locale, expression);
                if (matcher.isPresent()) {
                    return MatcherAssertion.fromMatcher(matcher.get());
                }
            }
            return null;
        };
    }



}
