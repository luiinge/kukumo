package iti.kukumo.plugins.io;

import iti.kukumo.plugin.api.adapters.*;
import java.util.*;
import org.jexten.*;

@ExtensionPoint(version = "2.0")
public interface IOStepsLocalization extends LocalizationProvider {

    @Extension(extensionPointVersion = "2.0")
    class Default extends ResourceLocalizationAdapter implements IOStepsLocalization {
        public Default() {
            super("steps.properties", List.of("en"));
        }
    }

}
