package iti.kukumo.core.expressions.internal;


import iti.kukumo.core.expressions.*;
import iti.kukumo.core.util.Regex;
import java.util.*;
import java.util.regex.Pattern;



public final class RegexFragment extends StepExpressionFragment
implements RegexContributor, StepExpression, EvaluableFragment {

    private final Pattern pattern;


    public RegexFragment(String regex) {
        this.pattern = Regex.of(regex);
    }


    public RegexFragment(List<RegexContributor> regexs) {
        RegexContributor current;
        RegexContributor previous = regexs.get(0);
        StringBuilder builder = new StringBuilder(previous.regex());
        for (int i = 1; i < regexs.size(); i++) {
            current = regexs.get(i);
            String regex = current.regex();
            String separator = " ";
            if (current instanceof WildcardFragment) {
                separator = "";
            } else if (current instanceof OptionalFragment optional) {
                separator = "";
                if (!optional.attach) {
                    if (optional.first() instanceof ChoiceFragment) {
                        regex = regex.replaceFirst("\\(\\(", "\\(\\( ");
                        regex = regex.replace("|(","|( ");
                    } else {
                        regex = regex.replaceFirst("\\(", "\\( ");
                    }
                }
            }
            builder.append(separator).append(regex);
            previous = current;
        }
        this.pattern = Regex.of(builder.toString());
    }


    @Override
    public String regex() {
        return pattern.pattern();
    }


    @Override
    protected StepExpressionFragment normalized() {
        return this;
    }


    @Override
    public boolean consumeFragment(ExpressionMatcher match) {
        var regexMatcher = pattern.matcher(match.pendingChars());
        if (regexMatcher.find() && regexMatcher.start() == 0) {
            match.consume(regexMatcher.end());
            return true;
        } else {
            match.reject();
            return false;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexFragment that = (RegexFragment) o;
        return Objects.equals(pattern, that.pattern);
    }


    @Override
    public int hashCode() {
        return Objects.hash(pattern);
    }

}
