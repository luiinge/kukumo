package iti.kukumo.core.expressions.internal;


import iti.kukumo.core.exceptions.KukumoException;
import iti.kukumo.core.expressions.*;
import iti.kukumo.core.util.Regex;
import iti.kukumo.plugin.api.contributions.DataType;
import java.util.Objects;
import java.util.regex.*;

final class ArgumentFragment extends StepExpressionFragment implements EvaluableFragment {

    String name;
    String type;


    @Override
    protected StepExpressionFragment normalized() {
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgumentFragment that = (ArgumentFragment) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }



    @Override
    public boolean consumeFragment(ExpressionMatcher match) {
        DataType<?> dataType = match.dataTypes().getByName(type).orElseThrow(
            ()->new KukumoException(
                "Unknown data type {}. Valid data types are: {}\n    ",
                type,
                String.join("\n    ",match.dataTypes().allNames())
                )
        );
        Pattern regex = Regex.of(dataType.regex(match.locale()));
        Matcher regexMatcher = regex.matcher(match.pendingChars());
        if (regexMatcher.find() && regexMatcher.start() == 0) {
            String matchValue = regexMatcher.group();
            match.addArgument(new ExpressionArgument(name,matchValue,dataType,match.locale()));
            match.consume(regexMatcher.end());
            return true;
        } else {
            match.reject();
            return false;
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }



}
