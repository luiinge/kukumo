package iti.kukumo.core.datatypes;

import java.util.List;

import org.jexten.Extension;

import iti.kukumo.core.datatypes.adapters.StringDataTypeAdapter;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "text"
)
public class TextDataType extends StringDataTypeAdapter<String> {


    public TextDataType() {
        super(
            "text",
            "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'",
            String.class,
            x -> x,
            List.of("'text'","\"text\"")
        );
    }

}
