package iti.kukumo.core.backend;

import imconfig.Config;
import iti.kukumo.plugin.api.Localizer;
import iti.kukumo.plugin.api.contributions.StepContribution;
import java.util.*;

public class StepLocalizer {
    
    private final Localizer localizer;
    private final StepContribution stepContribution;
    private final Map<Locale, Config> propertiesMap = new HashMap<>();


    StepLocalizer(StepContribution stepContribution, Localizer localizer) {
        this.localizer = localizer;
        this.stepContribution = stepContribution;
    }
    
    
    public Optional<String> localize(String stepKey, Locale locale) {
        if (!propertiesMap.containsKey(locale)) {
            propertiesMap.put(
                locale,
                localizer.localizedProperties(stepContribution.getClass(), locale).orElse(null));
        }
        return Optional.ofNullable(propertiesMap.get(locale)).flatMap(it->it.get(stepKey));
    }
    
}
