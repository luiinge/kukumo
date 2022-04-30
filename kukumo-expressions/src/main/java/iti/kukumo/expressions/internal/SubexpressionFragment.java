package iti.kukumo.expressions.internal;


import iti.kukumo.expressions.Subexpression;
import iti.kukumo.plugin.api.KukumoPluginException;
import java.util.Objects;
import java.util.regex.*;


final class SubexpressionFragment extends StepExpressionFragment implements EvaluableFragment {

    String type;


    SubexpressionFragment() {
        //
    }


    SubexpressionFragment(String type) {
        this.type = type;
    }


    @Override
    protected StepExpressionFragment normalized() {
        return this;
    }


    @Override
    public boolean consumeFragment(ExpressionMatcher match) {
        Subexpression subexpression = match.subexpressions().getByName(type).orElseThrow(
            ()->new KukumoPluginException(
                "Unknown subexpression {}. Valid subexpressions are: {}\n    ",
                type,
                String.join("\n    ",match.subexpressions().allNames())
            )
        );
        Matcher regexMatcher = subexpression.regexs(match.locale()).stream()
            .map(Regex::of)
            .map(it -> it.matcher(match.pendingChars()))
            .filter(it->it.find() && it.start() == 0)
            .findAny()
            .orElse(null);

        if (regexMatcher == null) {
            match.reject();
            return false;
        } else {
            match.consume(regexMatcher.end());
            return true;
        }

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubexpressionFragment that = (SubexpressionFragment) o;
        return Objects.equals(type, that.type);
    }


    @Override
    public int hashCode() {
        return Objects.hash(type);
    }




}
