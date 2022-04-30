package iti.kukumo.expressions;


import iti.kukumo.plugin.api.KukumoPluginException;

public class ExpressionParsingException extends KukumoPluginException {

    public ExpressionParsingException(String text, int position, String error) {
        super(
            "Invalid Kukumo expression '{}' at position {}: {}",
            format(text, position),
            position,
            error
        );
    }


    private static String format(String text, int position) {
        if (position <= text.length() - 1) {
            return "%s[<%s>]%s".formatted(
                text.substring(0, position),
                text.charAt(position),
                text.substring(position + 1)
            );
        } else {
            return text;
        }
    }

}
