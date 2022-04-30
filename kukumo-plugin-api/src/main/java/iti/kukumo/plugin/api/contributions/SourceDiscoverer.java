package iti.kukumo.plugin.api.contributions;


import java.util.stream.Stream;
import iti.kukumo.plugin.api.Contribution;
import iti.kukumo.plugin.api.sources.Source;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface SourceDiscoverer<T> extends Contribution {

    ContentType<T> contentType();
    Stream<Source<T>> discoverSources();

    default boolean accepts(ContentType<?> contentType) {
        return this.contentType().getClass() == contentType.getClass();
    }

}
