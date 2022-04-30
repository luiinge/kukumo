package iti.kukumo.gherkin.parser.internal;

import java.util.Locale;
import iti.kukumo.gherkin.parser.*;
import iti.kukumo.gherkin.parser.elements.Location;

public class NoSuchLanguageException extends ParserException {

    public NoSuchLanguageException(Locale locale, Location location) {
        super("Language not supported: " + locale, location);
    }

}
