package iti.kukumo.core.datatypes;

import java.math.BigDecimal;

import org.jexten.Extension;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "float"
)
public class FloatDataType extends NumberDataTypeAdapter<Float> {

    public FloatDataType() {
        super(
            "float",
            Float.class,
            true,
            false,
            Number::floatValue
        );
    }

}
