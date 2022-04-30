package iti.kukumo.plugin.api.adapters;


import java.util.function.Predicate;
import java.util.stream.Stream;
import imconfig.Config;
import iti.kukumo.plugin.api.KukumoProperties;
import iti.kukumo.plugin.api.contributions.*;
import iti.kukumo.plugin.api.sources.*;
import lombok.*;
import org.jexten.InjectedExtension;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class FileSourceDiscovererAdapter<T> implements SourceDiscoverer<T> {

    private final ContentType<T> contentType;
    private final Predicate<String> filenameFilter;

    @InjectedExtension
    Config config;


    @SuppressWarnings("unchecked")
    @Override
    public Stream<Source<T>> discoverSources() {
        return new FileLoader().discoverResourceFiles(new SourceCriteria<>(
            contentType,
            config.get(KukumoProperties.SOURCE_PATH,""),
            filenameFilter
        )).map(Source.class::cast);
    }

}
