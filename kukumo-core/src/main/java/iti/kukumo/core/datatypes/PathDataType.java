package iti.kukumo.core.datatypes;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.jexten.Extension;
import iti.kukumo.core.datatypes.adapters.StringDataTypeAdapter;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.DataType",
    extensionPointVersion = "2.0",
    name = "path"
)
public class PathDataType extends StringDataTypeAdapter<Path> {

    public PathDataType() {
        super(
            "path",
            "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'",
            Path.class,
            Path::of,
            List.of("path/file")
        );
    }

}
