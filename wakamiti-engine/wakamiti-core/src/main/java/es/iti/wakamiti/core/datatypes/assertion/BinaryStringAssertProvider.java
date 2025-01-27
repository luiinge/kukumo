/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package es.iti.wakamiti.core.datatypes.assertion;


import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;


public class BinaryStringAssertProvider extends AbstractAssertProvider {

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


    @Override
    protected LinkedHashMap<String, Pattern> translatedExpressions(Locale locale) {
        // binary numeric matchers
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
    protected Matcher<?> createMatcher(
        Locale locale,
        String key,
        String value
    ) throws ParseException {
        value = prepareString(value);
        Matcher<String> matcher = null;
        if (EQUALS.equals(key)) {
            matcher = Matchers.equalTo(value);
        } else if (EQUALS_IGNORE_CASE.equals(key)) {
            matcher = Matchers.equalToIgnoringCase(value);
        } else if (EQUALS_IGNORE_WHITESPACE.equals(key)) {
            matcher = Matchers.equalToCompressingWhiteSpace(value);
        } else if (STARTS_WITH.equals(key)) {
            matcher = Matchers.startsWith(value);
        } else if (STARTS_WITH_IGNORE_CASE.equals(key)) {
            matcher = Matchers.startsWithIgnoringCase(value);
        } else if (ENDS_WITH.equals(key)) {
            matcher = Matchers.endsWith(value);
        } else if (ENDS_WITH_IGNORE_CASE.equals(key)) {
            matcher = Matchers.endsWithIgnoringCase(value);
        } else if (CONTAINS.equals(key)) {
            matcher = Matchers.containsString(value);
        } else if (CONTAINS_IGNORE_CASE.equals(key)) {
            matcher = Matchers.containsStringIgnoringCase(value);
        } else if (NOT_EQUALS.equals(key)) {
            matcher = Matchers.not(Matchers.equalTo(value));
        } else if (NOT_EQUALS_IGNORE_CASE.equals(key)) {
            matcher = Matchers.not(Matchers.equalToIgnoringCase(value));
        } else if (NOT_EQUALS_IGNORE_WHITESPACE.equals(key)) {
            matcher = Matchers.not(Matchers.equalToCompressingWhiteSpace(value));
        } else if (NOT_STARTS_WITH.equals(key)) {
            matcher = Matchers.not(Matchers.startsWith(value));
        } else if (NOT_STARTS_WITH_IGNORE_CASE.equals(key)) {
            matcher = Matchers.not(Matchers.startsWithIgnoringCase(value));
        } else if (NOT_ENDS_WITH.equals(key)) {
            matcher = Matchers.not(Matchers.endsWith(value));
        } else if (NOT_ENDS_WITH_IGNORE_CASE.equals(key)) {
            matcher = Matchers.not(Matchers.endsWithIgnoringCase(value));
        } else if (NOT_CONTAINS.equals(key)) {
            matcher = Matchers.not(Matchers.containsString(value));
        } else if (NOT_CONTAINS_IGNORE_CASE.equals(key)) {
            matcher = Matchers.not(Matchers.containsStringIgnoringCase(value));
        }

        return matcher;
    }


    /* remove leading and tailing " or ' , and replace escaped characters */
    private static String prepareString(String input) {
        return input
            .substring(1, input.length() - 1)
            .replace("\\\"", "\"")
            .replace("\\'", "'");
    }
}