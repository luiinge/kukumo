package iti.kukumo.plugin.api.sources;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import iti.kukumo.plugin.api.contributions.ContentType;


public record FileSource<T> (
    ContentType<T> contentType,
    Path absolutePath,
    Path relativePath,
    Supplier<InputStream> inputStreamSupplier
) implements Source<T> {


    @Override
    public Optional<T> read() {
        return contentType.read(inputStreamSupplier);
    }


    @Override
    public String toString() {
        return "FileSource[contentType=%s, absolutePath=%s, relativePath=%s]"
            .formatted(contentType,absolutePath,relativePath);
    }

}
