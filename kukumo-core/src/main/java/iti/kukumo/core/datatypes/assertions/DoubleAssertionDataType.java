package iti.kukumo.core.datatypes.assertions;

import org.jexten.*;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import iti.kukumo.core.datatypes.assertions.adapters.*;
import iti.kukumo.plugin.api.Localizer;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "double-assertion"
)
public class DoubleAssertionDataType extends AssertionDataTypeAdapter {


    @InjectedExtension
    Localizer localizer;


    public DoubleAssertionDataType() {
        super(
            "double-assertion",
            "matcher.number"
        );
    }


    @PostConstructExtension
    public void init() {
        assertProviders(
            new UnaryNumberAssertProvider(
                localizer
            ),
            new BinaryNumberAssertProvider<>(
                localizer,
                locale -> NumberDataTypeAdapter.numericRegexPattern(locale, true),
                Number::doubleValue,
                locale -> NumberDataTypeAdapter.decimalFormat(locale, true)
            )
        );
    }


}
