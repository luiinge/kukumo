module iti.kukumo.plugins.io {
    exports iti.kukumo.plugins.io;
    opens iti.kukumo.plugins.io to org.jexten;
    requires org.jexten;
    requires iti.kukumo.plugin.api;
    uses iti.kukumo.plugins.io.IOStepsLocalization;
    provides iti.kukumo.plugin.api.contributions.StepContribution with iti.kukumo.plugins.io.IOSteps;
    provides iti.kukumo.plugins.io.IOStepsLocalization with iti.kukumo.plugins.io.IOStepsLocalization.Default;
}