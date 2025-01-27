/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package es.iti.wakamiti.core.datatypes;


import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.iti.wakamiti.api.WakamitiException;
import es.iti.wakamiti.core.util.TokenParser;

import static es.iti.wakamiti.core.datatypes.WakamitiCoreTypes.PROPERTY_REGEX;


public class WakamitiDateDataType<T extends TemporalAccessor> extends WakamitiDataTypeBase<T> {

    private static final List<FormatStyle> FORMAT_STYLES = Collections.unmodifiableList(
        Arrays.asList(FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL)
    );

    private static final String[] ISO_8601_DATE_FORMATS = {
        "yyyy-MM-dd",
        PROPERTY_REGEX
    };

    private static final String[] ISO_8601_TIME_FORMATS = {
	    "hh:mm",
	    "hh:mm:ss",
	    "hh:mm:ss.SSS",
        PROPERTY_REGEX
    };

    private static final String[] ISO_8601_DATETIME_FORMATS = {
	    "yyyy-MM-dd'T'hh:mm",
	    "yyyy-MM-dd'T'hh:mm:ss",
	    "yyyy-MM-dd'T'hh:mm:ss.SSS",
        PROPERTY_REGEX + "'T'" + PROPERTY_REGEX,
        PROPERTY_REGEX
    };

    private static final String REGEX_ALPHAS = "[^\\s]+";
    private static final String REGEX_2_NUMBER = "[0-9]{2}";
    private static final String REGEX_3_NUMBER = "[0-9]{3}";
    private static final String REGEX_4_NUMBER = "[0-9]{4}";
    private static final String REGEX_1_2_NUMBER = "[0-9]{1,2}";
    private static final String REGEX_1_3_NUMBER = "[0-9]{1,3}";
    private static final String REGEX_2_3_NUMBER = "[0-9]{2,3}";
    private static final String REGEX_2_4_NUMBER = "[0-9]{2,4}";

    private static Map<String, String> regexSymbols = Stream.of(new String[][] {
        { "MMMM", REGEX_ALPHAS },
        { "EEEE", REGEX_ALPHAS },
        { "yyyy", REGEX_4_NUMBER },
        { "MMM", REGEX_ALPHAS },
        { "EEE", REGEX_ALPHAS },
        { "SSS", REGEX_3_NUMBER },
        { "MM", REGEX_2_NUMBER },
        { "EE", REGEX_ALPHAS },
        { "HH", REGEX_2_NUMBER },
        { "hh", REGEX_2_NUMBER },
        { "dd", REGEX_2_NUMBER },
        { "mm", REGEX_2_NUMBER },
        { "SS", REGEX_2_3_NUMBER },
        { "yy", REGEX_2_NUMBER },
        { "ss", REGEX_2_NUMBER },
        { "M", REGEX_1_2_NUMBER },
        { "d", REGEX_1_2_NUMBER },
        { "E", REGEX_ALPHAS },
        { "H", REGEX_1_2_NUMBER },
        { "h", REGEX_1_2_NUMBER },
        { "m", REGEX_1_2_NUMBER },
        { "s", REGEX_1_2_NUMBER },
        { "S", REGEX_1_3_NUMBER },
        { "a", REGEX_ALPHAS },
        { "z", REGEX_ALPHAS },
        { "y", REGEX_2_4_NUMBER }
     })
     .collect(Collectors.toMap(p -> p[0], p -> p[1], (p1, p2) -> p1, LinkedHashMap::new));

    private static final List<String> REGEX_SPECIAL_SYMBOLS = Arrays.asList(
        "\\",
        "[",
        "^",
        "$",
        ".",
        ",",
        "|",
        "?",
        "*",
        "+",
        "(",
        ")",
        "/",
        "-",
        ":"
    );


    private static String dateTimeRegex(Locale locale, boolean withDate, boolean withTime) {
        final Set<String> regexs;
        if (withDate && withTime) {
            regexs = regexsWithDateAndTime(locale);
        } else {
            regexs = regexsWithDateOrTime(locale, withDate, withTime);
        }
        regexs.addAll(dateTimeRegexISO(withDate, withTime));
        return "(" + regexs.stream().collect(Collectors.joining(")|(")) + ")";
    }


    private static Set<String> regexsWithDateOrTime(Locale locale, boolean withDate, boolean withTime) {
        Set<String> regexs = new HashSet<>();
        for (final FormatStyle formatStyle : FORMAT_STYLES) {
            regexs.add(
                dateTimeRegex(
                        locale,
                    withDate ? formatStyle : null,
                    withTime ? formatStyle : null
                )
            );
        }
        return regexs;
    }


    private static Set<String> regexsWithDateAndTime(Locale locale) {
        Set<String> regexs = new HashSet<>();
        for (final FormatStyle dateFormatStyle : FORMAT_STYLES) {
            for (final FormatStyle timeFormatStyle : FORMAT_STYLES) {
                regexs.add(dateTimeRegex(locale, dateFormatStyle, timeFormatStyle));
            }
        }
        return regexs;
    }


    private static List<String> dateTimeRegexISO(boolean withDate, boolean withTime) {
        String[] formats;
        if (withDate && withTime) {
            formats = ISO_8601_DATETIME_FORMATS;
        } else if (withDate) {
            formats = ISO_8601_DATE_FORMATS;
        } else {
            formats = ISO_8601_TIME_FORMATS;
        }
        return Stream.of(formats).map(WakamitiDateDataType::patternToRegex)
            .collect(Collectors.toList());
    }


    private static String patternToRegex(String formatPattern) {
        List<String> tokens = new ArrayList<>(regexSymbols.keySet());
        tokens.addAll(REGEX_SPECIAL_SYMBOLS);
        tokens.add(" ");
        TokenParser parser = new TokenParser(formatPattern, tokens, Arrays.asList("'[^']*'"));
        StringBuilder regex = new StringBuilder();
        while (parser.hasMoreTokens()) {
            String nextToken = parser.nextToken();
            if (nextToken.equals(" ")) {
                regex.append(" ");
            } else if (REGEX_SPECIAL_SYMBOLS.contains(nextToken)) {
                regex.append("\\" + nextToken);
            } else if (nextToken.startsWith("'")) {
                regex.append(nextToken.replace("'", ""));
            } else {
            	String regexSymbol = regexSymbols.get(nextToken);
            	if (regexSymbol == null) {
            		throw new WakamitiException("Date/time format symbol '"+nextToken+"' has no equivalent regex");
            	}
            	regex.append(regexSymbols.get(nextToken));
            }
        }
        return regex + "|" + PROPERTY_REGEX;
    }


    private static String dateTimeRegex(
        Locale locale,
        FormatStyle dateFormatStyle,
        FormatStyle timeFormatStyle
    ) {
        return patternToRegex(dateTimePattern(locale, dateFormatStyle, timeFormatStyle));
    }


    private static String dateTimePattern(
        Locale locale,
        FormatStyle dateFormatStyle,
        FormatStyle timeFormatStyle
    ) {
        return DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            dateFormatStyle,
            timeFormatStyle,
            IsoChronology.INSTANCE,
            locale
        );
    }


    private static List<String> dateTimePatterns(
        Locale locale,
        boolean withDate,
        boolean withTime
    ) {
        if (withDate && withTime) {
            return patternsWithDateAndTime(locale);
        } else {
            return patternsWithDateOrTime(locale, withDate, withTime);
        }
    }


    private static List<String> patternsWithDateOrTime(Locale locale, boolean withDate, boolean withTime) {
        List<String> patterns = new ArrayList<>();
        patterns
            .addAll(Arrays.asList(withDate ? ISO_8601_DATE_FORMATS : ISO_8601_TIME_FORMATS));
        for (final FormatStyle formatStyle : FORMAT_STYLES) {
            patterns.add(
                dateTimePattern(
                        locale,
                    withDate ? formatStyle : null,
                    withTime ? formatStyle : null
                )
            );
        }
        return patterns;
    }

    private static List<String> patternsWithDateAndTime(Locale locale) {
        List<String> patterns = new ArrayList<>();
        patterns.addAll(Arrays.asList(ISO_8601_DATETIME_FORMATS));
        for (final FormatStyle dateFormatStyle : FORMAT_STYLES) {
            for (final FormatStyle timeFormatStyle : FORMAT_STYLES) {
                patterns.add(dateTimePattern(locale, dateFormatStyle, timeFormatStyle));
            }
        }
        return patterns;
    }


    private static <T extends TemporalAccessor> TypeParser<T> dateTimeParser(
        Locale locale,
        boolean withDate,
        boolean withTime,
        TemporalQuery<T> temporalQuery
    ) {

        List<DateTimeFormatter> formatters;
        if (withDate && withTime) {
            formatters= formattersWithDateAndTime(locale);
        } else {
            formatters= formattersWithDateOrTime(locale, withDate, withTime);
        }
        return (String input) -> parse(formatters, input, temporalQuery);
    }


    private static List<DateTimeFormatter> formattersWithDateOrTime(Locale locale, boolean withDate, boolean withTime) {
        List<DateTimeFormatter> formatters = new ArrayList<>();
        for (final FormatStyle formatStyle : FORMAT_STYLES) {
            DateTimeFormatter formatter = formatter(
                    locale,
                withDate ? formatStyle : null,
                withTime ? formatStyle : null
            );
            formatters.add(formatter);
        }
        formatters.add(withDate ? DateTimeFormatter.ISO_DATE : DateTimeFormatter.ISO_TIME);
        return formatters;
    }


    private static List<DateTimeFormatter> formattersWithDateAndTime(Locale locale) {
        List<DateTimeFormatter> formatters = new ArrayList<>();
        for (final FormatStyle dateFormatStyle : FORMAT_STYLES) {
            for (final FormatStyle timeFormatStyle : FORMAT_STYLES) {
                DateTimeFormatter formatter = formatter(
                        locale,
                    dateFormatStyle,
                    timeFormatStyle
                );
                formatters.add(formatter);
            }
        }
        formatters.add(DateTimeFormatter.ISO_DATE_TIME);
        return formatters;
    }


    private static DateTimeFormatter formatter(
        Locale locale,
        FormatStyle dateFormatStyle,
        FormatStyle timeFormatStyle
    ) {
        return new DateTimeFormatterBuilder().parseCaseInsensitive().append(
            DateTimeFormatter.ofPattern(dateTimePattern(locale, dateFormatStyle, timeFormatStyle))
        ).toFormatter(locale);
    }


    private static <T extends TemporalAccessor> T parse(
        List<DateTimeFormatter> formatters,
        String input,
        TemporalQuery<T> temporalQuery
    ) {
        RuntimeException ex = new RuntimeException();
        for (DateTimeFormatter formatter : formatters) {
            try {
                return formatter.parse(input, temporalQuery);
            } catch (DateTimeParseException e) {
                ex = e;
            }
        }
        throw ex;
    }



    private final boolean withDate;
    private final boolean withTime;

    public WakamitiDateDataType(
                    String name, Class<T> javaType, boolean withDate, boolean withTime,
                    TemporalQuery<T> temporalQuery
    ) {
        super(
            name, javaType,
            locale -> dateTimeRegex(locale, withDate, withTime),
            locale -> dateTimePatterns(locale, withDate, withTime),
            locale -> dateTimeParser(locale, withDate, withTime, temporalQuery)
        );
        this.withDate = withDate;
        this.withTime = withTime;
    }



	public List<String> getDateTimeFormats(Locale locale) {
		return dateTimePatterns(locale,withDate,withTime);
	}

}