package iti.kukumo.plugin.api.contributions;

import iti.kukumo.plugin.api.Contribution;
import java.util.*;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface Subexpression extends Contribution {

    String name();

    List<String> regexs(Locale locale);

}
