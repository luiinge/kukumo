package iti.kukumo.plugins.gherkin;

import imconfig.Config;
import iti.kukumo.gherkin.parser.*;
import java.util.Optional;
import iti.kukumo.gherkin.parser.elements.GherkinDocument;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.contributions.*;
import iti.kukumo.plugin.api.plan.MutablePlanNode;
import iti.kukumo.plugin.api.sources.*;
import org.jexten.*;

@Extension(extensionPointVersion = "2.0")
public class GherkinPlanAssembler implements PlanAssembler {

    @InjectedExtension
    Localizer localizer;

    @InjectedExtension
    Config config;

    KeywordMapProvider keywordMapProvider;


    @PostConstructExtension
    public void init() {
        this.keywordMapProvider = GherkinKeywordProvider.fromLocalizer(localizer);
    }


    @Override
    public boolean accepts(ContentType<?> contentType) {
        return contentType.getClass() == GherkinContentType.class;
    }


    @Override
    @SuppressWarnings("unchecked")
    public Optional<MutablePlanNode> assembleTestPlan(Source<?> input) {
        var source = (Source<GherkinDocument>) input;
        String relativePath = input instanceof FileSource<?> fileSource ?
            fileSource.relativePath().toString() :
            "";
        var idTagPattern = config.get(GherkinConfig.ID_TAG_PATTERN).orElseThrow();

        return source.read()
            .map(GherkinDocument::feature)
            .map(it -> new PlanNodeFactory(it, relativePath, keywordMapProvider, idTagPattern))
            .map(PlanNodeFactory::createTestPlan);
    }



}
