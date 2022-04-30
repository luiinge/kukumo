import iti.kukumo.plugins.gherkin.GherkinLocalization;
import iti.kukumo.plugins.gherkin.test.TestGherkinLocalization;

open module iti.kukumo.plugins.gherkin.test {

    requires iti.kukumo.plugins.gherkin;
    requires iti.kukumo.plugin.api;
    requires transitive org.junit.jupiter.api;
    requires transitive org.junit.jupiter.params;
    requires transitive org.junit.jupiter.engine;
    requires imconfig;
    requires iti.kukumo.gherkin.parser;

    exports iti.kukumo.plugins.gherkin.test to org.jexten, org.junit.platform.commons;
    provides GherkinLocalization with TestGherkinLocalization;

}