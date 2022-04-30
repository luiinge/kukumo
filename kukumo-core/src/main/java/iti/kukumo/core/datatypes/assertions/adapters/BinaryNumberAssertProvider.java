package iti.kukumo.core.datatypes.assertions.adapters;

import iti.kukumo.plugin.api.Localizer;
import iti.kukumo.plugin.api.lang.ThrowableFunction;
import java.text.*;
import java.util.*;
import java.util.regex.Pattern;
import org.hamcrest.*;

public class BinaryNumberAssertProvider<T extends Comparable<T>> extends AbstractAssertProvider{

    public static final String EQUALS = "matcher.number.equals";
    public static final String GREATER = "matcher.number.greater";
    public static final String LESS = "matcher.number.less";
    public static final String GREATER_EQUALS = "matcher.number.greater.equals";
    public static final String LESS_EQUALS = "matcher.number.less.equals";

    public static final String NOT_EQUALS = "matcher.number.not.equals";
    public static final String NOT_GREATER = "matcher.number.not.greater";
    public static final String NOT_LESS = "matcher.number.not.less";
    public static final String NOT_GREATER_EQUALS = "matcher.number.not.greater.equals";
    public static final String NOT_LESS_EQUALS = "matcher.number.not.less.equals";

    private final ThrowableFunction<Locale, String> numberRegexProvider;
    private final ThrowableFunction<Number, T> mapper;
    private final ThrowableFunction<Locale, NumberFormat> formatter;


    public BinaryNumberAssertProvider(
        Localizer localizer,
        ThrowableFunction<Locale, String> numberRegexProvider,
        ThrowableFunction<Number, T> mapper,
        ThrowableFunction<Locale, NumberFormat> formatter
    ) {
        super(localizer);
        this.numberRegexProvider = numberRegexProvider;
        this.mapper = mapper;
        this.formatter = formatter;
    }


    @Override
    protected LinkedHashMap<String, Pattern> translatedExpressions(Locale locale) {
        String[] expressions = {
           EQUALS,
           GREATER,
           LESS,
           GREATER_EQUALS,
           LESS_EQUALS,
           NOT_EQUALS,
           NOT_GREATER,
           NOT_LESS,
           NOT_GREATER_EQUALS,
           NOT_LESS_EQUALS,
        };
        String regex = numberRegexProvider.apply(locale);
        LinkedHashMap<String, Pattern> translatedExpressions = new LinkedHashMap<>();
        for (String key : expressions) {
            translatedExpressions.put(
                key,
                Pattern.compile(translateBundleExpression(locale, key, regex))
            );
        }
        return translatedExpressions;
    }



    @Override
    protected Matcher<?> createMatcher(Locale locale, String key, String value) throws ParseException {
        T numericValue = mapper.apply(formatter.apply(locale).parse(value));
        return switch (key) {
            case EQUALS -> Matchers.comparesEqualTo(numericValue);
            case GREATER -> Matchers.greaterThan(numericValue);
            case LESS -> Matchers.lessThan(numericValue);
            case GREATER_EQUALS -> Matchers.greaterThanOrEqualTo(numericValue);
            case LESS_EQUALS -> Matchers.lessThanOrEqualTo(numericValue);
            case NOT_EQUALS -> Matchers.not(Matchers.comparesEqualTo(numericValue));
            case NOT_GREATER -> Matchers.not(Matchers.greaterThan(numericValue));
            case NOT_LESS -> Matchers.not(Matchers.lessThan(numericValue));
            case NOT_GREATER_EQUALS -> Matchers.not(Matchers.greaterThanOrEqualTo(numericValue));
            case NOT_LESS_EQUALS -> Matchers.not(Matchers.lessThanOrEqualTo(numericValue));
            default -> null;
        };
    }


}
