package iti.kukumo.core.datatypes;

import org.jexten.Extension;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "double"
)
public class DoubleDataType extends NumberDataTypeAdapter<Double> {

    public DoubleDataType() {
        super(
            "dobule",
            Double.class,
            true,
            false,
            Number::doubleValue
        );
    }

}
