package iti.kukumo.core.datatypes.assertions;

import iti.kukumo.core.datatypes.assertions.adapters.*;
import org.jexten.*;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import iti.kukumo.plugin.api.Localizer;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "float-assertion"
)
public class FloatAssertionDataType extends AssertionDataTypeAdapter {


    @InjectedExtension
    Localizer localizer;


    public FloatAssertionDataType() {
        super(
            "float-assertion",
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
                Number::floatValue,
                locale -> NumberDataTypeAdapter.decimalFormat(locale, true)
            )
        );
    }


}
