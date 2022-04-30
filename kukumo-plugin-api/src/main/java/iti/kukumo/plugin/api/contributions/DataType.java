package iti.kukumo.plugin.api.contributions;

import iti.kukumo.plugin.api.Contribution;
import java.util.*;
import java.util.regex.Matcher;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface DataType<T> extends Contribution {

    String name();
    Class<T> javaType();
    String regex(Locale locale);
    List<String> hints(Locale locale);
    T parse (Locale locale, String value);
    Matcher matcher(Locale locale, String value);

}
