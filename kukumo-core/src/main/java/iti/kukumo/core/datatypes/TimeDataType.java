package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.TemporalDataTypeAdapter;
import java.time.*;

import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "time"
)
public class TimeDataType extends TemporalDataTypeAdapter<LocalTime> {

    public TimeDataType() {
        super("time", LocalTime.class, false, true, LocalTime::from);
    }

}
