import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.contributions.*;

module iti.kukumo.plugin.api {

    exports iti.kukumo.plugin.api;
    exports iti.kukumo.plugin.api.annotations;
    exports iti.kukumo.plugin.api.adapters;
    exports iti.kukumo.plugin.api.contributions;
    exports iti.kukumo.plugin.api.plan.mutators;
    exports iti.kukumo.plugin.api.plan;
    exports iti.kukumo.plugin.api.sources;

    requires transitive org.jexten;
    requires transitive imconfig;

    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.slf4j;
    requires slf4jansi;


    opens iti.kukumo.plugin.api.plan to com.fasterxml.jackson.databind;
    exports iti.kukumo.plugin.api.lang;
    exports iti.kukumo.plugin.api.datatypes;

    uses Log;

    uses ContentType;
    uses PlanAssembler;
    uses PlanMutationRules;
    uses SourceDiscoverer;


}