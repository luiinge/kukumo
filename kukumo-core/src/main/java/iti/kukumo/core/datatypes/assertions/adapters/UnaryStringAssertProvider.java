/** @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com */
package iti.kukumo.core.datatypes.assertions.adapters;


import java.util.*;
import java.util.regex.Pattern;
import org.hamcrest.*;
import iti.kukumo.plugin.api.Localizer;


public class UnaryStringAssertProvider extends AbstractAssertProvider {

    public static final String NULL = "matcher.generic.null";
    public static final String EMPTY = "matcher.generic.empty";
    public static final String NULL_EMPTY = "matcher.generic.null.empty";

    public static final String NOT_NULL = "matcher.generic.not.null";
    public static final String NOT_EMPTY = "matcher.generic.not.empty";
    public static final String NOT_NULL_EMPTY = "matcher.generic.not.null.empty";



    public UnaryStringAssertProvider(Localizer localizer) {
        super(localizer);
    }


    @Override
    protected LinkedHashMap<String, Pattern> translatedExpressions(Locale locale) {
        String[] expressions = {
            NULL,
            EMPTY,
            NOT_EMPTY,
            NOT_NULL,
            NOT_EMPTY,
            NOT_NULL_EMPTY
        };
        LinkedHashMap<String, Pattern> translatedExpressions = new LinkedHashMap<>();
        for (String key : expressions) {
            translatedExpressions
                .put(key, Pattern.compile(translateBundleExpression(locale, key, "")));
        }
        return translatedExpressions;
    }


    @Override
    protected Matcher<?> createMatcher(Locale locale, String expression, String value) {
        return switch (expression) {
            case NULL ->  Matchers.nullValue();
            case EMPTY -> Matchers.anyOf(
                single(Matchers.emptyString()),
                collection(Matchers.empty())
            );
            case NULL_EMPTY -> Matchers.anyOf(
                single(Matchers.emptyOrNullString()),
                collection(Matchers.empty())
            );
            case NOT_NULL ->  Matchers.not(Matchers.nullValue());
            case NOT_EMPTY -> Matchers.not(Matchers.anyOf(
                single(Matchers.emptyString()),
                collection(Matchers.empty())
            ));
            case NOT_NULL_EMPTY -> Matchers.not(Matchers.anyOf(
                single(Matchers.emptyOrNullString()),
                collection(Matchers.empty())
            ));
            default -> null;
        };
    }


    @SuppressWarnings("unchecked")
    private <T> Matcher<? super Object> single(Matcher<? super T> matcher) {
        return (Matcher<? super Object>) Matchers.allOf(Matchers.instanceOf(String.class), matcher);
    }


    @SuppressWarnings("unchecked")
    private Matcher<? super Object> collection(Matcher<? super Collection<?>> matcher) {
        return (Matcher<? super Object>) Matchers.allOf(Matchers.instanceOf(Collection.class), matcher);
    }


}