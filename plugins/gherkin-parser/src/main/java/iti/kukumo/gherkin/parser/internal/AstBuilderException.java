package iti.kukumo.gherkin.parser.internal;

import iti.kukumo.gherkin.parser.*;
import iti.kukumo.gherkin.parser.elements.Location;

public class AstBuilderException extends ParserException {

    public AstBuilderException(String message, Location location) {
        super(message, location);
    }

}
