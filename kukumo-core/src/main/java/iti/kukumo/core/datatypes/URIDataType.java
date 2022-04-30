package iti.kukumo.core.datatypes;

import java.net.*;
import java.util.List;

import org.jexten.Extension;

import iti.kukumo.core.datatypes.adapters.StringDataTypeAdapter;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "url"
)
public class URIDataType extends StringDataTypeAdapter<URI> {

    public URIDataType() {
        super("url", "\\w+:(\\/?\\/?)[^\\s]+", URI.class, URI::new,  List.of("<protocol://host/path>"));
    }

}
