package iti.kukumo.plugin.api;

public class KukumoProperties {

    private KukumoProperties() { }


    public static final String PREFIX = "kukumo";

    /** Types of sources to be discovered and processed */
    public static final String SOURCE_TYPES = "sourceTypes";


    /** Language used by the current source */
    public static final String LANGUAGE = "language";

    /**
     * Path (file-based or classpath-based) where the source files are located
     */
    public static final String SOURCE_PATH = "sourcePath";

    /** List of plugins (in form 'groupId:artifactId' enabled for the current execution */
    public static final String PLUGINS = "plugins";


    /** Tag Expression filter */
    public static final String TAG_FILTER = "tagFilter";


    /** Overriden locale for data formatting */
    public static final String DATA_FORMAT_LANGUAGE = "dataFormatLanguage";


    /**
     * Filter step contributions according their name.
     * Used when step definitions from several step contributions collide.
     */
    public static final String STEPS = "steps";
}
