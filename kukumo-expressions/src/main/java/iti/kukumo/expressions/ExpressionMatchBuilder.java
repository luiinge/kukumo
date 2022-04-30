package iti.kukumo.expressions;

import iti.kukumo.expressions.internal.ExpressionMatcher;
import iti.kukumo.plugin.api.*;
import java.util.*;
import lombok.*;

@Setter
@AllArgsConstructor
public class ExpressionMatchBuilder {

    private String text;
    private DataTypes dataTypes;
    private Subexpressions subExpressions;
    private Locale locale;


    public ExpressionMatch build() {
        return new ExpressionMatcher(
            Objects.requireNonNull(text),
            Objects.requireNonNull(dataTypes),
            Objects.requireNonNull(subExpressions),
            Objects.requireNonNull(locale)
        );
    }

}
