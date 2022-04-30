package iti.kukumo.plugin.api.test;


import iti.kukumo.plugin.api.Log;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import iti.kukumo.plugin.api.contributions.ContentType;

public class TextContentType implements ContentType<String> {

    static Log log = Log.of();


    @Override
    public String name() {
        return "text";
    }


    @Override
    public List<String> aliases() {
        return List.of();
    }


    @Override
    public Optional<String> read(Supplier<InputStream> inputStream) {
        try {
            return Optional.of(new String(inputStream.get().readAllBytes(),StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }


    @Override
    public String toString() {
        return "text";
    }

}
