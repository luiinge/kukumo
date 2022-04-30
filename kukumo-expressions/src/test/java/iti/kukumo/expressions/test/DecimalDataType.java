package iti.kukumo.expressions.test;

import java.math.BigDecimal;


public class DecimalDataType extends NumberDataTypeAdapter<BigDecimal> {

    public DecimalDataType() {
        super(
            "decimal",
            BigDecimal.class,
            true,
            true,
            BigDecimal.class::cast
        );
    }

}
