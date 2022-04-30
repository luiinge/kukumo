/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.datatypes.assertions.adapters;


import imconfig.Config;
import iti.kukumo.core.backend.StepExpression;
import iti.kukumo.core.datatypes.assertions.AssertionLocalizationProvider;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.annotations.LocalizableWith;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.hamcrest.Matcher;

@LocalizableWith(AssertionLocalizationProvider.class)
public abstract class AbstractAssertProvider {

    protected static final String VALUE_GROUP = "x";
    protected static final String VALUE_WILDCARD = "_";

    private final Localizer localizer;
    private final Map<Locale, Map<String, Pattern>> translatedExpressions = new HashMap<>();
    private final Map<Locale, Config> propertiesMap = new HashMap<>();


    protected AbstractAssertProvider(Localizer localizer) {
        this.localizer = localizer;
    }


    public Optional<Matcher<?>> matcherFromExpression(Locale locale, String expression) {

        Map<String, Pattern> expressions = translatedExpressions.computeIfAbsent(
            locale,
            this::translatedExpressions
        );

        String key = null;
        String value = null;
        boolean found = false;

        // locate the proper _expression
        for (Map.Entry<String, Pattern> e : expressions.entrySet()) {
            key = e.getKey();
            Pattern pattern = e.getValue();
            java.util.regex.Matcher patternMatcher = pattern.matcher(expression);
            if (patternMatcher.find()) {
                found = true;
                value = pattern.pattern().contains("<" + VALUE_GROUP + ">") ?
                    patternMatcher.group(VALUE_GROUP) :
                    null
                ;
                break;
            }
        }

        Matcher<?> matcher = null;
        if (found) {
            try {
                matcher = createMatcher(locale, key, value);
            } catch (Exception e) {
                throw new KukumoPluginException(e);
            }
        }
        return Optional.ofNullable(matcher);

    }


    protected abstract LinkedHashMap<String, Pattern> translatedExpressions(Locale locale);


    protected abstract Matcher<?> createMatcher(
        Locale locale,
        String key,
        String value
    ) throws ParseException;


    protected String translateBundleExpression(
        Locale locale,
        String expression,
        String valueGroupReplacing
    ) {
        String translatedExpression = propertiesFor(locale).get(expression,expression);
        translatedExpression = StepExpression.computeRegularExpression(translatedExpression);
        String regexGroupExpression = "(?<%s>%s)".formatted(VALUE_GROUP,valueGroupReplacing);
        translatedExpression = translatedExpression.replace(VALUE_WILDCARD, regexGroupExpression);
        return "^" + translatedExpression + "$";
    }



    public static List<String> getAllExpressions(Properties properties, String prefix) {
        return properties.keySet()
            .stream()
            .map(String.class::cast)
            .filter(key->key.startsWith(prefix))
            .map(properties::getProperty)
            .collect(Collectors.toList());
    }



    protected Config propertiesFor(Locale locale) {
        return propertiesMap.computeIfAbsent(
            locale,
            it -> localizer.localizedProperties(AbstractAssertProvider.class,it).orElseThrow(
                ()->new KukumoPluginException("Cannot found localization for assertion expressions")
            )
        );
    }

}
