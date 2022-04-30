package iti.kukumo.core.datatypes.assertions;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import iti.kukumo.core.datatypes.assertions.adapters.*;
import iti.kukumo.plugin.api.Localizer;
import org.jexten.*;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "integer-assertion"
)
public class IntegerAssertionDataType extends AssertionDataTypeAdapter {


    @InjectedExtension
    Localizer localizer;


    public IntegerAssertionDataType() {
        super(
            "integer-assertion",
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
                locale -> NumberDataTypeAdapter.numericRegexPattern(locale, false),
                Number::intValue,
                locale -> NumberDataTypeAdapter.decimalFormat(locale, false)
            )
        );
    }


}
