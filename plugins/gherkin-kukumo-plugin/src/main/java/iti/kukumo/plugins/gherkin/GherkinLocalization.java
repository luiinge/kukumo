package iti.kukumo.plugins.gherkin;

import java.util.Locale;
import iti.kukumo.gherkin.parser.KeywordMap;
import iti.kukumo.plugin.api.adapters.LocalizationProvider;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface GherkinLocalization extends LocalizationProvider {

    KeywordMap keywordMapForLocale(Locale locale);

}
