package iti.kukumo.core.expressions;


public interface ExpressionMatch {


    boolean matches();

    ExpressionArgument argument(String name);

}
