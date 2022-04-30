/** @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com */
package iti.kukumo.core.datatypes.assertions.adapters;


import iti.kukumo.plugin.api.Localizer;
import java.util.*;
import java.util.regex.Pattern;

import org.hamcrest.*;


public class UnaryNumberAssertProvider extends AbstractAssertProvider {

    public static final String NULL = "matcher.generic.null";
    public static final String NOT_NULL = "matcher.generic.not.null";


    public UnaryNumberAssertProvider(Localizer localizer) {
        super(localizer);
    }


    @Override
    protected LinkedHashMap<String, Pattern> translatedExpressions(Locale locale) {
        String[] expressions = {
            NULL,
            NOT_NULL,
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
            case NULL -> Matchers.nullValue();
            case NOT_NULL -> Matchers.notNullValue();
            default -> null;
        };
    }


}