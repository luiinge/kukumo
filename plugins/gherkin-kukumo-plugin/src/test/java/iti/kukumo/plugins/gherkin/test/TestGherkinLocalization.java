package iti.kukumo.plugins.gherkin.test;

import iti.kukumo.gherkin.parser.*;
import iti.kukumo.plugins.gherkin.GherkinLocalization;
import java.util.*;
import org.jexten.Extension;

@Extension(extensionPointVersion = "2.0")
public class TestGherkinLocalization implements GherkinLocalization {

    @Override
    public List<Locale> locales() {
        return List.of(Locale.forLanguageTag("es"));
    }


    @Override
    public KeywordMap keywordMapForLocale(Locale locale) {
        return category -> switch (category) {
            case GIVEN -> List.of("*", "Dado", "Dada", "Dados", "Dadas", "Dado que");
            case WHEN -> List.of("*", "Cuando");
            case THEN -> List.of("*", "Entonces");
            case AND -> List.of("*", "Y", "Y que");
            case BUT -> List.of("*", "Pero");
            case FEATURE -> List.of("Característica");
            case BACKGROUND -> List.of("Antecedentes");
            case SCENARIO -> List.of("Escenario");
            case SCENARIO_OUTLINE -> List.of("Esquema del escenario");
            case EXAMPLES -> List.of("Ejemplos");
            default -> List.of();
        };
    }

}
