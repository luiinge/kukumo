package iti.kukumo.core.datatypes.adapters;

import iti.kukumo.plugin.api.KukumoPluginException;
import iti.kukumo.plugin.api.contributions.DataType;
import iti.kukumo.plugin.api.lang.ThrowableFunction;
import java.util.*;
import java.util.regex.Matcher;

public abstract class StringDataTypeAdapter<T> implements DataType<T> {

    private final String name;
    private final String regex;
    private final Class<T> javaType;
    private final ThrowableFunction<String,T> parser;
    private final List<String> hints;


    protected StringDataTypeAdapter(
        String name,
        String regex,
        Class<T> javaType,
        ThrowableFunction<String, T> parser,
        List<String> hints
    ) {
        this.name = name;
        this.regex = regex;
        this.javaType = javaType;
        this.parser = parser;
        this.hints = hints;
    }


    @Override
    public String name() {
        return name;
    }


    @Override
    public Class<T> javaType() {
        return javaType;
    }


    @Override
    public String regex(Locale locale) {
        return regex;
    }


    @Override
    public List<String> hints(Locale locale) {
        return hints;
    }


    @Override
    public T parse(Locale locale, String value) {
        try {
            return parser.apply(value);
        } catch (Exception e) {
            throw new KukumoPluginException(e);
        }
    }


    @Override
    public Matcher matcher(Locale locale, String value) {
        return null;
    }

}
