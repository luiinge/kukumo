package iti.kukumo.plugin.api.sources;

import iti.kukumo.plugin.api.contributions.ContentType;
import java.util.Optional;


public interface Source<T> {

    ContentType<T> contentType();

    Optional<T> read();

}
