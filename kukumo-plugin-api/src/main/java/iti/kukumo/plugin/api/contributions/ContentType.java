package iti.kukumo.plugin.api.contributions;

import java.io.*;
import java.util.*;
import iti.kukumo.plugin.api.Contribution;
import java.util.function.Supplier;
import org.jexten.ExtensionPoint;

/**
 *
 * @param <T> A Java type that represents an instance of a resource with this content type
 */
@ExtensionPoint(version = "2.0")
public interface ContentType<T> extends Contribution {

    /** The main identifier of this content type */
    String name();


    List<String> aliases();

    Optional<T> read(Supplier<InputStream> inputStream);



}
