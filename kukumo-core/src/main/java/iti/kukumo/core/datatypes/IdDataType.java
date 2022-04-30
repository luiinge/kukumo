package iti.kukumo.core.datatypes;

import java.util.List;

import org.jexten.Extension;

import iti.kukumo.core.datatypes.adapters.StringDataTypeAdapter;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "id"
)
public class IdDataType extends StringDataTypeAdapter<String> {

    public IdDataType() {
        super("id", "\\w[\\w_]+", String.class, x -> x, List.of("<id>"));
    }

}
