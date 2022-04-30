package iti.kukumo.expressions.internal;

import java.util.Objects;
import java.util.regex.Pattern;


final class LiteralFragment extends StepExpressionFragment implements RegexContributor {

     private static final Pattern specialRegexSymbols = Regex.of(
         "[\\*\\\\\\(\\)\\{\\}\\^\\[\\]\\|]"
     );


    private final String value;


    LiteralFragment(String value) {
        this.value = value.strip();
    }


    public String value() {
        return value;
    }



    @Override
    public String regex() {
        return "("+ specialRegexSymbols.matcher(value).replaceAll("\\\\$0") +")";
    }


    @Override
    protected StepExpressionFragment normalized() {
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralFragment that = (LiteralFragment) o;
        return Objects.equals(value, that.value);
    }


    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
