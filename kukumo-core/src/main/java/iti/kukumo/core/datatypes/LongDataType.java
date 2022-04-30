package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "long"
)
public class LongDataType extends NumberDataTypeAdapter<Long> {

    public LongDataType() {
        super(
            "long",
            Long.class,
            false,
            false,
            Number::longValue
        );
    }

}
