package iti.kukumo.expressions;


import iti.kukumo.plugin.api.*;
import java.util.Locale;

public interface ExpressionMatch {

    static ExpressionMatchBuilder builder(String step) {
        return new ExpressionMatchBuilder(step, DataTypes.of(), Subexpressions.of(), Locale.ENGLISH);
    }

    boolean matches();

    ExpressionArgument argument(String name);

}
