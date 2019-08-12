package iti.kukumo.api;


import iti.commons.configurer.Configuration;
import iti.commons.configurer.ConfigurationBuilder;
import iti.commons.configurer.ConfigurationException;
import iti.commons.configurer.Configurator;
import iti.commons.configurer.Property;

public class KukumoConfiguration {

    public static final String PREFIX = "kukumo";

    // basic configuration

    /** Types of resources to be discovered and processed */
    public static final String RESOURCE_TYPES = "resourceTypes";

    /** Language used by a resource */
    public static final String LANGUAGE = "language";

    /** Path (file-based or classpath-based) where the resource files are located */
    public static final String RESOURCE_PATH = "resourcePath";

    /** List of names of the modules required */
    public static final String MODULES = "modules";

    /** List of full-qualified classes implementing KukumoStepProvider that are not declared as module */
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

    /** Dash-separated number list that indicates how many implementation steps correspond to each definition step */
    public static final String REDEFINITION_STEP_MAP = "redefinition.stepMap";

    /** Tag used for annotate a feature as a definition */
    public static final String REDEFINITION_DEFINITION_TAG = "redefinition.definitionTag";

    /** Tag used for annotate a feature as an implementation */
    public static final String REDEFINITION_IMPLEMENTATION_TAG = "redefinition.implementationTag";







    @Configurator(properties={
        @Property(key=RESOURCE_PATH,value="."),
        @Property(key=OUTPUT_FILE_PATH,value=Defaults.DEFAULT_OUTPUT_FILE_PATH),
        @Property(key=REPORT_GENERATION,value="true"),
        @Property(key=REPORT_SOURCE,value=Defaults.DEFAULT_OUTPUT_FILE_PATH),
        @Property(key=ID_TAG_PATTERN,value=Defaults.DEFAULT_ID_TAG_PATTERN),
        @Property(key=REDEFINITION_ENABLED,value=Defaults.DEFAULT_REDEFINITION_ENABLED),
        @Property(key=REDEFINITION_DEFINITION_TAG,value=Defaults.DEFAULT_REDEFINITION_DEFINITION_TAG),
        @Property(key=REDEFINITION_IMPLEMENTATION_TAG,value=Defaults.DEFAULT_REDEFINITION_IMPLEMENTATION_TAG)
    })
    public static class Defaults {
        public static final String DEFAULT_OUTPUT_FILE_PATH = "kukumo.json";
        public static final String DEFAULT_ID_TAG_PATTERN = "ID-(.*)";
        public static final String DEFAULT_REDEFINITION_ENABLED = "true";
        public static final String DEFAULT_REDEFINITION_DEFINITION_TAG = "definition";
        public static final String DEFAULT_REDEFINITION_IMPLEMENTATION_TAG = "implementation";

        private Defaults() { /* avoid instantiation*/ }
    }



    public static Configuration defaultConfiguration() throws ConfigurationException {
        return ConfigurationBuilder.instance()
                .buildFromEnvironment(false)
                .filtered(PREFIX)
                .appendFromAnnotation(Defaults.class);
        // TODO append from file
    }


    private KukumoConfiguration() {
        // avoid instantation
    }
}
