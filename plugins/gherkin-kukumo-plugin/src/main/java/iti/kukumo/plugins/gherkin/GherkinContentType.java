package iti.kukumo.plugins.gherkin;

import iti.kukumo.gherkin.parser.*;
import iti.kukumo.gherkin.parser.elements.GherkinDocument;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.annotations.LocalizableWith;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import org.jexten.*;
import iti.kukumo.plugin.api.contributions.ContentType;


@Extension(name = "gherkin", extensionPointVersion = "2.0")
@LocalizableWith(GherkinLocalization.class)
public class GherkinContentType implements ContentType<GherkinDocument> {

    final Log log = Log.of("plugins:gherkin");

    @InjectedExtension
    Localizer localizer;

    GherkinParser gherkinParser;


    @PostConstructExtension
    public void init() {
        KeywordMapProvider keywordMapProvider = locale -> localizer
            .localization(GherkinLocalization.class, locale)
            .map(it -> it.keywordMapForLocale(locale));
        this.gherkinParser = new GherkinParser(keywordMapProvider);
    }

    @Override
    public String name() {
        return "gherkin";
    }

    @Override
    public List<String> aliases() {
        return List.of("text/x-gherkin");
    }


    @Override
    public Optional<GherkinDocument> read(Supplier<InputStream> inputStream) {
        try (var reader = new InputStreamReader(inputStream.get(),StandardCharsets.UTF_8)) {
            return Optional.of(gherkinParser.parse(reader));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

}
