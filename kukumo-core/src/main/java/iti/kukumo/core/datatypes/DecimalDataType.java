package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import java.math.BigDecimal;
import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "decimal"
)
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
