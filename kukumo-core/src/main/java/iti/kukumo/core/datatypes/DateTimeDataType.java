package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.TemporalDataTypeAdapter;
import java.time.*;

import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "date-time"
)
public class DateTimeDataType extends TemporalDataTypeAdapter<LocalDateTime> {

    public DateTimeDataType() {
        super("date-time", LocalDateTime.class, true, true, LocalDateTime::from);
    }

}