package iti.kukumo.core.datatypes.assertion;

import iti.kukumo.api.Kukumo;
import iti.kukumo.api.KukumoException;
import iti.kukumo.core.backend.ExpressionMatcher;
import iti.kukumo.util.ResourceLoader;
import org.hamcrest.Matcher;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractAssertProvider {

    public static final String MATCHERS_RESOURCE = "iti_kukumo_core-matchers";
    protected static final String VALUE_GROUP = "x";
    protected static final String VALUE_WILDCARD = "~x~";
    protected static final ResourceLoader resourceLoader = Kukumo.instance().resourceLoader();

    private final Map<Locale,ResourceBundle> bundles = new HashMap<>();
    private final Map<Locale,Map<String,Pattern>> translatedExpressions = new HashMap<>();


    public AbstractAssertProvider() {

    }

    protected ResourceBundle bundle(Locale locale) {
        return bundles.computeIfAbsent(locale,
                bundleLocale -> resourceLoader.resourceBundle(MATCHERS_RESOURCE, bundleLocale));
    }



    public Optional<Matcher<?>> matcherFromExpression(Locale locale, String expression) {

        Map<String, Pattern> expressions = translatedExpressions.computeIfAbsent(locale, this::translatedExpressions);
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
                value = (pattern.pattern().contains("<"+VALUE_GROUP+">") ?
                        patternMatcher.group(VALUE_GROUP) : null);
                break;
            }
        }

        Matcher<?> matcher = null;
        if (found) {
            try {
                matcher = createMatcher(locale, key, value);
            } catch (Exception e) {
                throw new KukumoException(e);
            }
        }
        return Optional.ofNullable(matcher);

    }



    protected abstract LinkedHashMap<String,Pattern> translatedExpressions(Locale locale);


    protected abstract Matcher<?> createMatcher(Locale locale, String key, String value) throws Exception;




    protected String translateBundleExpression(Locale locale, String expression, String valueGroupReplacing) {
        String translatedExpression = bundle(locale).getString(expression);
        translatedExpression = ExpressionMatcher.computeRegularExpression(translatedExpression);
        translatedExpression =
                translatedExpression.replace(VALUE_WILDCARD, "(?<"+VALUE_GROUP+">" + valueGroupReplacing + ")");
        return "^" + translatedExpression + "$";
    }



    public static List<String> getAllExpressions(Locale locale) {
        ResourceBundle bundle = resourceLoader.resourceBundle(MATCHERS_RESOURCE, locale);
        return bundle.keySet().stream().map(bundle::getString).collect(Collectors.toList());
    }



}
