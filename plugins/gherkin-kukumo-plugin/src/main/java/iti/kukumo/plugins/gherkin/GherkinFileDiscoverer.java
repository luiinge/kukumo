package iti.kukumo.plugins.gherkin;


import imconfig.Config;
import iti.kukumo.gherkin.parser.elements.GherkinDocument;
import java.util.stream.Stream;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.contributions.*;
import iti.kukumo.plugin.api.sources.*;
import org.jexten.*;

@Extension(
    extensionPoint = "iti.kukumo.plugin.api.contributions.SourceDiscoverer",
    extensionPointVersion = "2.0"
)
public class GherkinFileDiscoverer implements SourceDiscoverer<GherkinDocument> {


    @InjectedExtension
    Config config;

    @InjectedExtension("gherkin")
    ContentType<GherkinDocument> gherkinContentType;


    @Override
    public ContentType<GherkinDocument> contentType() {
        return gherkinContentType;
    }


    @Override
    public Stream<Source<GherkinDocument>> discoverSources() {
        return new FileLoader().discoverResourceFiles(new SourceCriteria<>(
            gherkinContentType,
            config.get(KukumoProperties.SOURCE_PATH,""),
            filename -> filename.endsWith(".feature")
        )).map(it -> it);
    }



}
