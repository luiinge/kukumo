package iti.kukumo.core.backend;

import iti.kukumo.core.exceptions.WrongStepDefinition;
import iti.kukumo.core.util.Regex;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.contributions.DataType;
import java.util.*;
import java.util.regex.*;

public class StepExpression {

    public static final String UNNAMED = "X0";

    private static final Log log = Log.of();
    private static final String NAMED_ARGUMENT_REGEX = "\\{(\\w+)\\:(\\w+\\-?\\w+)\\}";
    private static final String UNNAMED_ARGUMENT_REGEX = "\\{(\\w+\\-?\\w+)\\}";

    public static String computeRegularExpression(String stepDefinition) {
        String regex = regexPriorAdjustments(stepDefinition);
        regex = regexFinalAdjustments(regex);
        log.debug("Expression Matcher: {} ==> {}", stepDefinition, regex);
        return regex;
    }



    private final String definition;
    private final Locale stepLocale;
    private final Locale dataLocale;
    private final Map<String, DataType<?>> arguments = new HashMap<>();
    private final Pattern pattern;


    public StepExpression(String definition, Locale stepLocale, Locale dataLocale, DataTypes dataTypes) {
        this.definition = definition;
        this.stepLocale = stepLocale;
        this.dataLocale = dataLocale;
        this.pattern = computeRegularExpression(dataTypes);
    }



    public Matcher matcherFor(String step) {
        return pattern.matcher(step);
    }


    public boolean matches(String step) {
        return pattern.matcher(step).matches();
    }


    public Map<String,DataType<?>> arguments() {
        return Map.copyOf(arguments);
    }


    public String definition() {
        return definition;
    }


    public Locale stepLocale() {
        return stepLocale;
    }


    public Locale dataLocale() {
        return dataLocale;
    }


    public Object parseArgument(String argumentName, String textualValue) {
        return arguments.get(argumentName).parse(dataLocale, textualValue);
    }


    private Pattern computeRegularExpression(DataTypes dataTypes) {
        String regex = regexPriorAdjustments(definition);
        regex = regexArgumentSubstitution(regex,dataTypes);
        regex = regexFinalAdjustments(regex);
        log.debug("Expression Matcher: {} ==> {}", definition, regex);
        return Pattern.compile(regex);
    }


    private static String regexPriorAdjustments(String sourceExpression) {
        String regex = sourceExpression;
        // a|b|c -> (a|b|c)
        regex = Regex.replace(regex, "[^ |(]*(\\|[^ |)]+)+", "\\($0\\)");
        // (( -> ( and )) -> )
        regex = regex.replace("((", "(").replace("))", ")");
        // * -> any value
        regex = Regex.replace(regex, "(?<!\\\\)\\*", "(.*)");
        // ( ) -> optional
        regex = Regex.replace(regex, "(?<!\\\\)\\(([^!][^)]*)\\)", "(?:$1)?");
        // (...)?_ -> (?:(...)?_)?
        regex = Regex.replace(regex, "\\(\\?:[^)]+\\)\\? ", "(?:$0)?");
        // _(?:.*)? -> (?:.*)?
        regex = regex.replace(" (?:.*)?", "(?:.*)?");
        // (!a) -> ((?!a).)*
        regex = Regex.replace(regex, "(?<!\\\\)\\(!([^)]*)\\)", "((?!$1).)*");
        return regex;
    }



    private String regexArgumentSubstitution(String computingRegex, DataTypes dataTypes) {

        String regex = computingRegex;

        // unnamed arguments
        Matcher unnamedArgs = Regex.match(regex, UNNAMED_ARGUMENT_REGEX);
        while (unnamedArgs.find()) {
            String typeName = unnamedArgs.group(1);
            DataType<?> type = dataType(dataTypes,typeName);
            regex = regex.replace(
                "{" + typeName + "}",
                "(?<"+UNNAMED+">" + type.regex(dataLocale) + ")"
            );
            this.arguments.put(UNNAMED,type);
        }

        // named arguments
        Matcher namedArgs = Regex.match(regex, NAMED_ARGUMENT_REGEX);
        while (namedArgs.find()) {
            String argName = namedArgs.group(1);
            if (argName.equals(UNNAMED)) {
                throw new WrongStepDefinition(
                    "Argument name '{}' is reserved. Consider changing it for something else.",
                    UNNAMED
                );
            }
            String argType = namedArgs.group(2);
            DataType<?> type = dataType(dataTypes,argType);
            regex = regex.replace(
                "{" + argName + ":" + argType + "}",
                "(?<" + argName + ">" + type.regex(dataLocale) + ")"
            );
            this.arguments.put(argName, type);
        }
        return regex;
    }



    static String regexFinalAdjustments(String computingRegex) {
        String regex = computingRegex;
        regex = regex.replace(" $", "$");
        regex = regex.replace("$", "\\s*$");
        return regex;
    }


    private WrongStepDefinition unknownArgumentType(DataTypes dataTypes, String typeName) {
        throw new WrongStepDefinition(
            "Wrong step definition '{}' : unknown argument type '{}'\nAvailable types are: {}",
            definition,
            typeName,
            String.join(", ",dataTypes.allNames())
        );
    }



    private DataType<?> dataType(DataTypes dataTypes, String typeName) {
        return dataTypes.getByName(typeName).orElseThrow(
            () -> unknownArgumentType(dataTypes,typeName)
        );
    }


    @Override
    public String toString() {
        return "[%s]:%s".formatted(stepLocale,definition);
    }




}
