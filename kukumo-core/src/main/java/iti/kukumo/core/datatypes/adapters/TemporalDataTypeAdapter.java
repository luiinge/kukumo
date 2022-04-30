/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.datatypes.adapters;


import iti.kukumo.plugin.api.contributions.DataType;
import java.time.temporal.*;
import java.util.*;
import iti.kukumo.core.util.DateTimeFormats;
import iti.kukumo.core.util.DateTimeFormats.Criteria;
import iti.kukumo.plugin.api.adapters.DataTypeAdapter;


public class TemporalDataTypeAdapter<T extends TemporalAccessor> extends DataTypeAdapter<T>
implements DataType<T> {

    private final boolean withDate;
    private final boolean withTime;


    protected TemporalDataTypeAdapter(
        String name,
        Class<T> javaType,
        boolean withDate,
        boolean withTime,
        TemporalQuery<T> temporalQuery
    ) {
        super(
            name, javaType,
            locale -> DateTimeFormats.dateTimeRegex(new Criteria(locale, withDate, withTime)),
            locale -> DateTimeFormats.dateTimePatterns(new Criteria(locale,withDate, withTime)),
            locale -> DateTimeFormats.dateTimeParser(new Criteria(locale, withDate, withTime), temporalQuery)
        );
        this.withDate = withDate;
        this.withTime = withTime;
    }


    public List<String> getDateTimeFormats(Locale locale) {
        return DateTimeFormats.dateTimePatterns(new Criteria(locale,withDate,withTime));
    }


}
