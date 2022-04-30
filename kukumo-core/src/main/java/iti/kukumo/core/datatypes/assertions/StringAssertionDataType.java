package iti.kukumo.core.datatypes.assertions;

import org.jexten.*;

import iti.kukumo.core.datatypes.assertions.adapters.*;
import iti.kukumo.plugin.api.Localizer;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "string-assertion"
)
public class StringAssertionDataType extends AssertionDataTypeAdapter {


    @InjectedExtension
    Localizer localizer;


    public StringAssertionDataType() {
        super(
            "string-assertion",
            "matcher.string"
        );
    }


    @PostConstructExtension
    public void init() {
        assertProviders(
            new UnaryStringAssertProvider(
                localizer
            ),
            new BinaryStringAssertProvider<>(
                localizer
            )
        );
    }


}
