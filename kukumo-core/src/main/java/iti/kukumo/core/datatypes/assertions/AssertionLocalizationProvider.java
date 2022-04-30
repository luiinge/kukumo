package iti.kukumo.core.datatypes.assertions;

import imconfig.Config;
import iti.kukumo.plugin.api.adapters.*;
import java.util.List;
import org.jexten.*;

@ExtensionPoint(version = "2.0")
public interface AssertionLocalizationProvider extends ResourceLocalizationProvider {

    @Extension(
        extensionPoint = "iti.kukumo.core.datatypes.assertions.AssertionLocalizationProvider",
        extensionPointVersion = "2.0"
    )
    class Default extends ResourceLocalizationAdapter implements AssertionLocalizationProvider {
        public Default() {
            super("assertions.properties", List.of("en") );
        }
    }


}
