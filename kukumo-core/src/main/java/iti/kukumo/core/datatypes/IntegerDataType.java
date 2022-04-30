package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "integer"
)
public class IntegerDataType extends NumberDataTypeAdapter<Integer> {

    public IntegerDataType() {
        super(
            "integer",
            Integer.class,
            false,
            false,
            Number::intValue
        );
    }

}
