package iti.kukumo.expressions.internal;


import iti.kukumo.expressions.*;
import iti.kukumo.plugin.api.*;
import java.util.*;


public class ExpressionMatcher implements ExpressionMatch {


    public static ExpressionMatchBuilder builder (String text) {
        return new ExpressionMatchBuilder(text,DataTypes.of(), Subexpressions.of(),Locale.ENGLISH);
    }



    private final String text;
    private final Locale locale;
    private int position = 0;
    private final Map<String, ExpressionArgument> arguments = new HashMap<>();
    private boolean rejected = false;
    private final DataTypes dataTypes;
    private final Subexpressions subExpressions;

    public ExpressionMatcher(String text, DataTypes dataTypes, Subexpressions subExpressions, Locale locale) {
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

    Subexpressions subexpressions() {
        return subExpressions;
    }

    void addArgument(ExpressionArgument argument) {
        if (arguments.containsKey(argument.name())) {
            throw new KukumoPluginException("Argument name {} is already used",argument.name());
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
