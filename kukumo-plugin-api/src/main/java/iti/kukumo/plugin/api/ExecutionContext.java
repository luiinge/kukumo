package iti.kukumo.plugin.api;

import imconfig.Config;
import iti.kukumo.plugin.api.adapters.LocalizationProvider;
import iti.kukumo.plugin.api.plan.PlanNode;
import java.util.*;

public record ExecutionContext(
    PlanNode testCase,
    Config config,
    Locale stepLocale,
    Locale dataLocale,
    Contributions contributions
) {

    public ExecutionContext(PlanNode testCase, Config config, Contributions contributions) {
        this(
            testCase,
            config,
            LocalizationProvider.localeFor(
                Optional.ofNullable(testCase.language())
                    .or( ()->config.get(KukumoProperties.LANGUAGE) )
                    .orElseThrow()
            ),
            config.get(KukumoProperties.DATA_FORMAT_LANGUAGE)
                .map(LocalizationProvider::localeFor).orElse(null),
            contributions
        );
    }


    @Override
    public Locale dataLocale() {
        return dataLocale != null ? dataLocale : stepLocale;
    }

}


