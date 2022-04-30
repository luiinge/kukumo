package iti.kukumo.plugins.gherkin;

import iti.kukumo.gherkin.parser.KeywordMapProvider;
import iti.kukumo.plugin.api.Localizer;

public final class GherkinKeywordProvider {

    private GherkinKeywordProvider() { }


    public static KeywordMapProvider fromLocalizer(Localizer localizer) {
        return locale -> localizer
            .localization(GherkinLocalization.class, locale)
            .map(it -> it.keywordMapForLocale(locale));
    }

}
