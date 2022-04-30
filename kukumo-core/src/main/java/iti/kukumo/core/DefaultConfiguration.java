package iti.kukumo.core;

import imconfig.*;
import java.util.List;
import static iti.kukumo.plugin.api.KukumoProperties.*;


public class DefaultConfiguration {

    public static final Config CONFIGURATION = Config.factory()
        .accordingDefinitions(List.of(

            PropertyDefinition.builder(SOURCE_TYPES)
                .textType()
                .multivalue()
                .required()
                .description("Types of sources to be discovered and processed")
                .build(),

            PropertyDefinition.builder(LANGUAGE)
                .textType()
                .description("Language used by the current source")
                .defaultValue("es")
                .build(),

            PropertyDefinition.builder(DATA_FORMAT_LANGUAGE)
                .textType()
                .description("Language used for dataLocale-dependant values (if absent, source dataLocale would be used)")
                .build(),

            PropertyDefinition.builder(SOURCE_PATH)
                .textType()
                .description("Path (file-based or classpath-based) where the source files are located")
                .defaultValue(".")
                .build(),

            PropertyDefinition.builder(PLUGINS)
                .textType("[\\w-]+:[\\w-]+")
                .multivalue()
                .required()
                .description("List of plugins (in form 'groupId:artifactId') enabled for the current execution")
                .build(),

            PropertyDefinition.builder(TAG_FILTER)
                .textType()
                .description("Tag expression filter")
                .build(),

            PropertyDefinition.builder(STEPS)
                .textType()
                .multivalue()
                .description("""
                    Filter step contributions according their name.
                    Used when step definitions from several step contributions collide."""
                ).build()

        ))
        ;

}
