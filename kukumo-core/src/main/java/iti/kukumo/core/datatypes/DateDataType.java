package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.TemporalDataTypeAdapter;
import java.time.LocalDate;
import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "date"
)
public class DateDataType extends TemporalDataTypeAdapter<LocalDate> {

    public DateDataType() {
        super("date", LocalDate.class, true, false, LocalDate::from);
    }

}
