package iti.kukumo.core.expressions;

import iti.kukumo.plugin.api.contributions.DataType;
import java.util.Locale;

public record ExpressionArgument(
    String name,
    String value,
    DataType<?> dataType,
    Locale locale
) {

    public Object javaValue() {
        return dataType.parse(locale,value);
    }

    public Class<?> javaType() {
        return dataType.javaType();
    }


}
