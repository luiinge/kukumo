package iti.kukumo.plugins.gherkin;

import imconfig.*;
import iti.kukumo.plugin.api.contributions.ConfigContribution;
import java.util.List;
import org.jexten.Extension;

@Extension(extensionPointVersion = "2.0")
public class GherkinConfig implements ConfigContribution {

    /**
     * Tag used for annotate a node as a definition
     */
    public static final String REDEFINITION_DEFINITION_TAG = "gherkin.definitionTag";

    /**
     * Tag used for annotate a node as an implementation
     */
    public static final String REDEFINITION_IMPLEMENTATION_TAG = "gherkin.implementationTag";

    /**
     * Regex for tags used as identifier
     */
    public static final String ID_TAG_PATTERN = "gherkin.idTagPattern";

    /**
     * Dash-separated number list that indicates how many implementation steps
     * correspond to each definition step
     */
    public static final String STEP_MAP = "gherkin.stepMap";



    @Override
    public Config config() {
        return Config.factory().accordingDefinitions(List.of(

            PropertyDefinition.builder(ID_TAG_PATTERN)
                .textType()
                .description("Regex for tags used as identifier. If it contains a regex group, only the group value would be used as identifier.")
                .defaultValue("ID-(\\w+)")
                .build(),

            PropertyDefinition.builder(REDEFINITION_DEFINITION_TAG)
                .textType("\\w+")
                .defaultValue("definition")
                .description("Tag used for annotate a node as a definition")
                .build(),

            PropertyDefinition.builder(REDEFINITION_IMPLEMENTATION_TAG)
                .textType("\\w+")
                .defaultValue("implementation")
                .description("Tag used for annotate a node as an implementation")
                .build()

        ));
    }

}
