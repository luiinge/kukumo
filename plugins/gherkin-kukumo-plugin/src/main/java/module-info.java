import iti.kukumo.plugin.api.contributions.*;
import iti.kukumo.plugins.gherkin.*;
import iti.kukumo.plugins.gherkin.GherkinLocalization;

module iti.kukumo.plugins.gherkin {

    opens iti.kukumo.plugins.gherkin to org.jexten;

    exports iti.kukumo.plugins.gherkin;

    requires iti.kukumo.plugin.api;
    requires iti.kukumo.gherkin.parser;
    requires lombok;

    uses GherkinLocalization;

    provides ContentType with GherkinContentType;
    provides SourceDiscoverer with GherkinFileDiscoverer;
    provides PlanAssembler with GherkinPlanAssembler;
    provides PlanMutationRules with GherkinMutationRules;
    provides GherkinLocalization with DefaultGherkinLocalization;
    provides ConfigContribution with GherkinConfig;

}