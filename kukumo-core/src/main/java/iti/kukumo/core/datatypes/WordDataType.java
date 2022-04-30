package iti.kukumo.core.datatypes;

import iti.kukumo.core.datatypes.adapters.StringDataTypeAdapter;
import java.util.List;
import org.jexten.Extension;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "word"
)
public class WordDataType extends StringDataTypeAdapter<String> {

    public WordDataType() {
        super("word", "[\\w-]+", String.class, x -> x, List.of("<word>"));
    }

}
