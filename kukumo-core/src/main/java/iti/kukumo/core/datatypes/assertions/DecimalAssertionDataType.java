package iti.kukumo.core.datatypes.assertions;

import java.math.BigDecimal;
import org.jexten.*;

import iti.kukumo.core.datatypes.adapters.NumberDataTypeAdapter;
import iti.kukumo.core.datatypes.assertions.adapters.*;
import iti.kukumo.plugin.api.Localizer;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "decimal-assertion"
)
public class DecimalAssertionDataType extends AssertionDataTypeAdapter {


    @InjectedExtension
    Localizer localizer;


    public DecimalAssertionDataType() {
        super(
            "decimal-assertion",
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
                number -> new BigDecimal(number.toString()),
                locale -> NumberDataTypeAdapter.decimalFormat(locale, true)
            )
        );
    }


}
