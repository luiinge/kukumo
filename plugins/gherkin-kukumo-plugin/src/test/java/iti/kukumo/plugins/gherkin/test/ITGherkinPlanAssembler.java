package iti.kukumo.plugins.gherkin.test;


import imconfig.Config;
import iti.kukumo.plugin.api.plan.MutablePlanNode;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import iti.kukumo.plugin.api.*;
import org.jexten.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ITGherkinPlanAssembler {

    static {
        System.out.println("---------------------- using module ----------------------");
        System.out.println(ITGherkinPlanAssembler.class.getModule());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "simpleScenario",
        "scenarioOutline",
        "background",
        "arguments"
    })
    void assembleSingleFeatureTests(String filename) throws IOException {
        System.out.println(filename);
        var conf = Config.factory().fromMap(Map.of(
            KukumoProperties.SOURCE_TYPES, "gherkin",
            KukumoProperties.SOURCE_PATH, "src/test/resources/features/"+filename+".feature"
        ));
        var extensionManager = ExtensionManager.create();
        var contributions = new Contributions(extensionManager, conf);
        System.out.println(contributions.contributedConfiguration());
        var gherkinContentType = contributions.contentType("gherkin").orElseThrow();
        var gherkinPlanAssembler = contributions.planAssembler(gherkinContentType).orElseThrow();
        var gherkinDiscoverer = contributions.sourceDiscoverers(gherkinContentType).findAny().orElseThrow();

        var source = gherkinDiscoverer.discoverSources().findAny().orElseThrow();
        System.out.println(source);
        var plan = gherkinPlanAssembler.assembleTestPlan(source).orElseThrow();

        for (var mutationRules : contributions.planMutationRules()) {
            mutationRules.rules().forEach(rule -> rule.mutate(plan.stream()));
        }

        var immutablePlan = plan.toImmutable();
        var assembledJson = immutablePlan.toJSON();
        System.out.println(assembledJson);
        var json = Files.readString(Path.of("src/test/resources/features/"+filename+".json"));
        assertEquals(json,assembledJson);
    }



    @Test
    void assembleRedefiningFeatureTest() throws IOException {
        var conf = Config.factory().fromMap(Map.of(
            KukumoProperties.SOURCE_TYPES, "gherkin",
            KukumoProperties.SOURCE_PATH, "src/test/resources/features/redefining"
        ));
        var extensionManager = ExtensionManager.create();
        var contributions = new Contributions(extensionManager, conf);
        System.out.println(contributions.contributedConfiguration());
        var gherkinContentType = contributions.contentType("gherkin").orElseThrow();
        var gherkinPlanAssembler = contributions.planAssembler(gherkinContentType).orElseThrow();
        var gherkinDiscoverer = contributions.sourceDiscoverers(gherkinContentType).findAny().orElseThrow();

        var sources = gherkinDiscoverer.discoverSources().toList();
        var nodes = sources.stream()
            .map(gherkinPlanAssembler::assembleTestPlan)
            .flatMap(Optional::stream).toList();

        var plan = MutablePlanNode.compose(nodes);
        System.out.println(plan.toImmutable().toJSON());

        for (var rules : contributions.planMutationRules()) {
            for (int i = 0; i < rules.rules().size(); i++) {
                rules.rules().get(i).mutate(plan);
                System.out.println("===========================================");
                System.out.println("  ---  AFTER RULE "+i);
                System.out.println("===========================================");
                System.out.println(plan.toImmutable().toJSON());
            }
        }


        var immutablePlan = plan.normalized().orElseThrow().toImmutable();
        var assembledJson = immutablePlan.toJSON();
        System.out.println(assembledJson);
        var json = Files.readString(Path.of("src/test/resources/features/redefining/redefining_plan.json"));
        assertEquals(json,assembledJson);
    }




}
