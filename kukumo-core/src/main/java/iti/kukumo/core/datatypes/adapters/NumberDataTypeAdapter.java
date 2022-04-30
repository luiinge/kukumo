package iti.kukumo.core.datatypes.adapters;

import iti.kukumo.plugin.api.KukumoPluginException;
import iti.kukumo.plugin.api.adapters.DataTypeAdapter;
import iti.kukumo.plugin.api.contributions.DataType;
import java.text.*;
import java.util.*;
import java.util.function.Function;

public class NumberDataTypeAdapter<T extends Number> extends DataTypeAdapter<T>
implements DataType<T> {


    protected NumberDataTypeAdapter(
        String name,
        Class<T> javaType,
        boolean includeDecimals,
        boolean useBigDecimal,
        Function<Number,T> converter
    ) {
        super(
            name,
            javaType,
            locale -> numericRegexPattern(locale, includeDecimals),
            locale -> List.of(decimalFormat(locale, useBigDecimal).toLocalizedPattern()),
            locale -> parser(locale, includeDecimals, converter)
        );
    }



    private static <T> TypeParser<T> parser(
        Locale locale,
        boolean includeDecimals,
        Function<Number, T> converter
    ) {
        return source -> {
            try {
               var number = decimalFormat(locale, includeDecimals).parse(source);
               return converter.apply(number);
            } catch (ParseException e) {
                throw new KukumoPluginException(e);
            }
        };
    }



    public static String numericRegexPattern(Locale locale, boolean includeDecimals) {
        DecimalFormat format = decimalFormat(locale, includeDecimals);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        StringBuilder pattern = new StringBuilder("-?")
            .append("\\d{1,").append(format.getGroupingSize()).append("}")
            .append("(\\").append(symbols.getGroupingSeparator()).append("?")
            .append("\\d{1,").append(format.getGroupingSize()).append("})*");
        if (includeDecimals) {
            pattern.append("\\").append(symbols.getDecimalSeparator()).append("\\d+?");
        }
        return pattern.toString();
    }



    public static DecimalFormat decimalFormat(Locale locale, boolean useBigDecimal) {
        DecimalFormat format;
        if (useBigDecimal) {
            format = (DecimalFormat) NumberFormat.getNumberInstance(locale);
            format.setParseBigDecimal(true);
        } else {
            format = (DecimalFormat) NumberFormat.getIntegerInstance(locale);
        }
        return format;
    }


}
