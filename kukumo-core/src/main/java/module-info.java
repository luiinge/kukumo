import iti.kukumo.core.datatypes.*;
import iti.kukumo.core.datatypes.assertions.*;
import iti.kukumo.plugin.api.contributions.DataType;

module iti.kukumo.core {

    exports iti.kukumo.core.util;
    exports iti.kukumo.core.datatypes;
    exports iti.kukumo.core.datatypes.assertions;
    exports iti.kukumo.core.backend;
    exports iti.kukumo.core;

    requires iti.kukumo.plugin.api;
    requires org.opentest4j;
    requires lombok;
    requires org.jexten.plugin;
    requires org.jexten.plugin.distributed;
    requires org.hamcrest;

    opens iti.kukumo.core.datatypes to org.jexten;
    opens iti.kukumo.core.datatypes.assertions to org.jexten;


    provides DataType with
        DateDataType,
        DateTimeDataType,
        DecimalDataType,
        DoubleDataType,
        PathDataType,
        FloatDataType,
        IdDataType,
        IntegerDataType,
        LongDataType,
        TextDataType,
        TimeDataType,
        URIDataType,
        WordDataType,
        DecimalAssertionDataType,
        DoubleAssertionDataType,
        FloatAssertionDataType,
        IntegerAssertionDataType,
        LongAssertionDataType,
        StringAssertionDataType;

    provides AssertionLocalizationProvider with AssertionLocalizationProvider.Default;
}