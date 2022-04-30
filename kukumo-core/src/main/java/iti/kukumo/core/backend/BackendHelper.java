package iti.kukumo.core.backend;

import iti.kukumo.core.util.*;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.annotations.Step;
import iti.kukumo.plugin.api.contributions.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BackendHelper {

    private final List<String> allStepDefinitions;
    private final List<String> allStepDefinitionsWithVariants;
    private final Map<DataType<?>, Pattern> types;


    public BackendHelper(
        ExecutionContext context,
        Map<Class<? extends StepContribution>, StepLocalizer> localizers,
        DataTypes dataTypes
    ) {
        this.types = dataTypes.stream()
            .collect(Collectors.toMap(
                x -> x,
                type -> Regex.of("\\{[^:]*:?" + type.name() + "}")
            ));

        this.allStepDefinitions = new ArrayList<>();
        localizers.forEach((stepContribution, localizer) -> {
            for (var method : stepContribution.getMethods()) {
                if (!method.isAnnotationPresent(Step.class)) {
                    continue;
                }
                var key = method.getAnnotation(Step.class).value();
                allStepDefinitions.addAll(localizers
                    .get(stepContribution)
                    .localize(key, context.stepLocale())
                    .stream()
                    .toList()
                );
            }
        });

        this.allStepDefinitionsWithVariants = allStepDefinitions.stream()
            .map(hint -> populateStepHintWithTypeHints(hint, context.dataLocale()))
            .flatMap(List::stream)
            .toList();

    }


    public List<String> suggestionsForInvalidStep(
        String invalidStep,
        int hints,
        boolean includeVariations
    ) {
        if (includeVariations)
            return StringDistance.closerStrings(invalidStep, allStepDefinitionsWithVariants, hints);
        else
            return StringDistance.closerStrings(invalidStep, allStepDefinitions, hints);
    }



    private List<String> populateStepHintWithTypeHints(String stepHint, Locale dataLocale) {
        List<String> variants = new ArrayList<>();
        for (Map.Entry<? extends DataType<?>, Pattern> type : types.entrySet()) {
            if (type.getValue().matcher(stepHint).find()) {
                for (String typeHint : type.getKey().hints(dataLocale)) {
                    String variant = stepHint.replaceFirst(type.getValue().pattern(), typeHint);
                    variants.addAll(populateStepHintWithTypeHints(variant, dataLocale));
                }
            }
        }
        if (variants.isEmpty()) {
            variants.add(stepHint);
        }
        return variants;
    }


}
