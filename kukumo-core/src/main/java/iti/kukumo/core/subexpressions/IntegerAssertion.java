package iti.kukumo.core.subexpressions;

import iti.kukumo.plugin.api.contributions.Subexpression;
import java.util.*;
import org.jexten.Extension;

@Extension(extensionPointVersion = "2.0", name = "integer-assertion")
public class IntegerAssertion implements Subexpression {

    @Override
    public String name() {
        return "integer-assertion";
    }


    @Override
    public List<String> regexs(Locale locale) {
        return List.of(
            "is equal to {{int}}"
        );
    }

}
