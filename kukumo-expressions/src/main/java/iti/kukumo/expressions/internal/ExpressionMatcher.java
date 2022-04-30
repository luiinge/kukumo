package iti.kukumo.core.expressions.internal;

import iti.kukumo.core.exceptions.KukumoException;
import iti.kukumo.core.expressions.*;
import iti.kukumo.plugin.api.*;
import java.util.*;

import iti.kukumo.core.util.Regex;
import lombok.*;

public class ExpressionMatcher implements ExpressionMatch {




    @Setter @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ExpressionMatchBuilder {

        private String text;
        private DataTypes dataTypes;
        private SubExpressions subExpressions;
        private Locale locale;

        ExpressionMatcher build() {
            return new ExpressionMatcher(
                Objects.requireNonNull(text),
                Objects.requireNonNull(dataTypes),
                Objects.requireNonNull(subExpressions),
                Objects.requireNonNull(locale)
            );
        }
    }

    public static ExpressionMatchBuilder builder (String text) {
        return new ExpressionMatchBuilder(text,DataTypes.of(),SubExpressions.of(),Locale.ENGLISH);
    }



    private final String text;
    private final Locale locale;
    private int position = 0;
    private final Map<String, ExpressionArgument> arguments = new HashMap<>();
    private boolean rejected = false;
    private final DataTypes dataTypes;
    private final SubExpressions subExpressions;

    ExpressionMatcher(String text, DataTypes dataTypes, SubExpressions subExpressions, Locale locale) {
        this.text = Regex.replace(text.strip(), "\\s+", " ");
        this.dataTypes = dataTypes;
        this.subExpressions = subExpressions;
        this.locale = locale;
    }


    public String pendingChars() {
        return text.substring(position);
    }


    public void consume(int length) {
        this.position += length;
    }

    public void reject() {
        this.rejected = true;
    }

    boolean totallyConsumed() {
        return position >= text.length();
    }


    Locale locale() {
        return locale;
    }


    DataTypes dataTypes() {
        return dataTypes;
    }

    SubExpressions subexpressions() {
        return subExpressions;
    }

    void addArgument(ExpressionArgument argument) {
        if (arguments.containsKey(argument.name())) {
            throw new KukumoException("Argument name {} is already used",argument.name());
        }
        arguments.put(argument.name(),argument);
    }


    public boolean matches() {
        return !rejected && totallyConsumed();
    }


    public ExpressionArgument argument(String name) {
        return arguments.get(name);
    }

}
