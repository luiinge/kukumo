package iti.kukumo.core.datatypes.assertions.adapters;

import java.util.*;
import java.util.regex.Pattern;
import org.hamcrest.*;
import iti.kukumo.plugin.api.Localizer;



public class BinaryStringAssertProvider<T extends Comparable<T>> extends AbstractAssertProvider{

    public static final String EQUALS = "matcher.string.equals";
    public static final String EQUALS_IGNORE_CASE = "matcher.string.equals.ignore.case";
    public static final String EQUALS_IGNORE_WHITESPACE = "matcher.string.equals.ignore.whitespace";
    public static final String STARTS_WITH = "matcher.string.starts.with";
    public static final String STARTS_WITH_IGNORE_CASE = "matcher.string.starts.with.ignore.case";
    public static final String ENDS_WITH = "matcher.string.ends.with";
    public static final String ENDS_WITH_IGNORE_CASE = "matcher.string.ends.with.ignore.case";
    public static final String CONTAINS = "matcher.string.contains";
    public static final String CONTAINS_IGNORE_CASE = "matcher.string.contains.ignore.case";

    public static final String NOT_EQUALS = "matcher.string.not.equals";
    public static final String NOT_EQUALS_IGNORE_CASE = "matcher.string.not.equals.ignore.case";
    public static final String NOT_EQUALS_IGNORE_WHITESPACE = "matcher.string.not.equals.ignore.whitespace";
    public static final String NOT_STARTS_WITH = "matcher.string.not.starts.with";
    public static final String NOT_STARTS_WITH_IGNORE_CASE = "matcher.string.not.starts.with.ignore.case";
    public static final String NOT_ENDS_WITH = "matcher.string.not.ends.with";
    public static final String NOT_ENDS_WITH_IGNORE_CASE = "matcher.string.not.ends.with.ignore.case";
    public static final String NOT_CONTAINS = "matcher.string.not.contains";
    public static final String NOT_CONTAINS_IGNORE_CASE = "matcher.string.not.contains.ignore.case";




    public BinaryStringAssertProvider(Localizer localizer) {
        super(localizer);
    }


    @Override
    protected LinkedHashMap<String, Pattern> translatedExpressions(Locale locale) {
        String[] expressions = {
            EQUALS,
            EQUALS_IGNORE_CASE,
            EQUALS_IGNORE_WHITESPACE,
            STARTS_WITH,
            STARTS_WITH_IGNORE_CASE,
            ENDS_WITH,
            ENDS_WITH_IGNORE_CASE,
            CONTAINS,
            CONTAINS_IGNORE_CASE,
            NOT_EQUALS,
            NOT_EQUALS_IGNORE_CASE,
            NOT_EQUALS_IGNORE_WHITESPACE,
            NOT_STARTS_WITH,
            NOT_STARTS_WITH_IGNORE_CASE,
            NOT_ENDS_WITH,
            NOT_ENDS_WITH_IGNORE_CASE,
            NOT_CONTAINS,
            NOT_CONTAINS_IGNORE_CASE
        };
        LinkedHashMap<String, Pattern> translatedExpressions = new LinkedHashMap<>();
        for (String expression : expressions) {
            translatedExpressions.put(
                expression,
                Pattern.compile(
                    translateBundleExpression(
                        locale,
                        expression,
                        "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"
                    )
                )
            );
        }
        return translatedExpressions;
    }



    @Override
    protected Matcher<?> createMatcher(Locale locale, String key, String value) {
        value = prepareString(value);
        return switch (key) {
            case EQUALS -> Matchers.equalTo(value);
            case EQUALS_IGNORE_CASE -> Matchers.equalToIgnoringCase(value);
            case EQUALS_IGNORE_WHITESPACE -> Matchers.equalToCompressingWhiteSpace(value);
            case STARTS_WITH -> Matchers.startsWith(value);
            case STARTS_WITH_IGNORE_CASE -> Matchers.startsWithIgnoringCase(value);
            case ENDS_WITH -> Matchers.endsWith(value);
            case ENDS_WITH_IGNORE_CASE -> Matchers.endsWithIgnoringCase(value);
            case CONTAINS -> Matchers.containsString(value);
            case CONTAINS_IGNORE_CASE -> Matchers.containsStringIgnoringCase(value);
            case NOT_EQUALS -> Matchers.not(Matchers.equalTo(value));
            case NOT_EQUALS_IGNORE_CASE -> Matchers.not(Matchers.equalToIgnoringCase(value));
            case NOT_EQUALS_IGNORE_WHITESPACE -> Matchers.not(Matchers.equalToCompressingWhiteSpace(value));
            case NOT_STARTS_WITH -> Matchers.not(Matchers.startsWith(value));
            case NOT_STARTS_WITH_IGNORE_CASE -> Matchers.not(Matchers.startsWithIgnoringCase(value));
            case NOT_ENDS_WITH -> Matchers.not(Matchers.endsWith(value));
            case NOT_ENDS_WITH_IGNORE_CASE -> Matchers.not(Matchers.endsWithIgnoringCase(value));
            case NOT_CONTAINS -> Matchers.not(Matchers.containsString(value));
            case NOT_CONTAINS_IGNORE_CASE -> Matchers.not(Matchers.containsStringIgnoringCase(value));
            default -> null;
        };
    }


    /* remove leading and tailing " or ' , and replace escaped characters */
    private static String prepareString(String input) {
        return input
            .substring(1, input.length() - 1)
            .replace("\\\\\"", "\"")
            .replace("\\\\'", "'");
    }

}
