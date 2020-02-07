package iti.kukumo.api;

import iti.commons.configurer.Configuration;
import iti.commons.configurer.Configurer;
import iti.commons.jext.Extension;
import iti.kukumo.api.extensions.ConfigContributor;

@Extension(name = "core-properties")
public class KukumoConfiguration implements ConfigContributor<Void> {

    public static final String PREFIX = "kukumo";

      /** Types of resources to be discovered and processed */
    public static final String RESOURCE_TYPES = "resourceTypes";

    /** Language used by a resource */
    public static final String LANGUAGE = "language";

    /**
     * Path (file-based or classpath-based) where the resource files are located
     */
    public static final String RESOURCE_PATH = "resourcePath";

    /** List of names of the modules required */
    public static final String MODULES = "modules";

    /**
     * List of full-qualified classes implementing KukumoStepProvider that are
     * not declared as module
     */
    public static final String NON_REGISTERED_STEP_PROVIDERS = "nonRegisteredStepProviders";

    /** Output file path */
    public static final String OUTPUT_FILE_PATH = "outputFilePath";

    /** Report sources */
    public static final String REPORT_SOURCE = "report.source";

    /** Enable / disable the report generation */
    public static final String REPORT_GENERATION = "report.generation";

    /** Tag Expression filter */
    public static final String TAG_FILTER = "tagFilter";

    /** Pattern for use specific tag as an identifier */
    public static final String ID_TAG_PATTERN = "idTagPattern";

    /** Overriden locale for data formatting */
    public static final String DATA_FORMAT_LANGUAGE = "dataFormatLanguage";

    /** Set if the redefinition feature is enabled */
    public static final String REDEFINITION_ENABLED = "redefinition.enabled";

    /**
     * Dash-separated number list that indicates how many implementation steps
     * correspond to each definition step
     */
    public static final String REDEFINITION_STEP_MAP = "redefinition.stepMap";

    /** Tag used for annotate a feature as a definition */
    public static final String REDEFINITION_DEFINITION_TAG = "redefinition.definitionTag";

    /** Tag used for annotate a feature as an implementation */
    public static final String REDEFINITION_IMPLEMENTATION_TAG = "redefinition.implementationTag";

    /** Use Ansi characters in the logs */
    public static final String LOGS_ANSI_ENABLED = "logs.ansi.enabled";

    /** Use Ansi styles in the logs */
    public static final String LOGS_ANSI_STYLES = "logs.ansi.styles";

    /** Show the Kukumo logo in the logs */
    public static final String LOGS_SHOW_LOGO = "logs.showLogo";

    /** Show the source for each step in the logs */
    public static final String LOGS_SHOW_STEP_SOURCE = "logs.showStepSource";

    /** Show the elapsed time for each step in the logs */
    public static final String LOGS_SHOW_ELAPSED_TIME = "logs.showElapsedTime";

    /** Set if the steps are treated as tests. */
    public static final String TREAT_STEPS_AS_TESTS = "junit.treatStepsAsTests";


    static final Configuration DEFAULTS = Configuration
        .fromEnvironment()
        .appendFromSystem()
        .filtered(PREFIX)
        .appendFromPairs(
            RESOURCE_PATH, ".",
            OUTPUT_FILE_PATH, "kukumo.json",
            REPORT_GENERATION, "true",
            ID_TAG_PATTERN, "ID-(.*)",
            REDEFINITION_ENABLED, "true",
            REDEFINITION_DEFINITION_TAG, "definition",
            REDEFINITION_IMPLEMENTATION_TAG, "implementation",
            LOGS_SHOW_LOGO, "true",
            LOGS_SHOW_STEP_SOURCE, "false",
            LOGS_SHOW_ELAPSED_TIME, "true",
            TREAT_STEPS_AS_TESTS, "false",
            "logs.ansi.styles.keyword", "blue",
            "logs.ansi.styles.source", "faint",
            "logs.ansi.styles.time", "faint",
            "logs.ansi.styles.resourceType", "cyan",
            "logs.ansi.styles.contributor", "green",
            "logs.ansi.styles.stepResult.PASSED", "green,bold",
            "logs.ansi.styles.stepResult.SKIPPED", "faint",
            "logs.ansi.styles.stepResult.UNDEFINED", "yellow",
            "logs.ansi.styles.stepResult.FAILED", "red,bold",
            "logs.ansi.styles.stepResult.ERROR", "red,bold"
        );


    @Override
    public Configuration defaultConfiguration() {
        return DEFAULTS;
    }


    @Override
    public boolean accepts(Object contributor) {
        return false;
    }


    @Override
    public Configurer<Void> configurer() {
        return (x,y)->{};
    }
}
