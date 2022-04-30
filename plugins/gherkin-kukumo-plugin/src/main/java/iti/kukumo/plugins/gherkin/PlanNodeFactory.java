package iti.kukumo.plugins.gherkin;

import static iti.kukumo.plugin.api.lang.Functions.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import iti.kukumo.gherkin.parser.*;
import iti.kukumo.gherkin.parser.elements.*;
import iti.kukumo.gherkin.parser.elements.DataTable;
import iti.kukumo.plugin.api.plan.*;
import static iti.kukumo.plugins.gherkin.GherkinConstants.*;

class PlanNodeFactory {

    private final Pattern propertyRegex = Pattern.compile(
        "\\s*#+\\s*([^\\s]+)\\s*:\\s*([^\\s]+)\\s*"
    );


    private final Feature feature;
    private final String relativePath;
    private final String scenarioKeyword;
    private final Background background;
    private final Pattern idTagPattern;


    PlanNodeFactory(
        Feature feature,
        String relativePath,
        KeywordMapProvider keywordMapProvider,
        String idTagPattern
    ) {
        this.feature = feature;
        this.relativePath = relativePath;
        this.scenarioKeyword = new GherkinDialectFactory(keywordMapProvider,"en")
            .dialectFor(feature.language())
            .keywords(KeywordType.SCENARIO)
            .get(0);
        this.background = cast(first(feature.children()), Background.class);
        this.idTagPattern = Pattern.compile(idTagPattern);
    }


    public MutablePlanNode createTestPlan() {
        return featureNode();
    }


    private MutablePlanNode featureNode() {

        var node = new MutablePlanNode(NodeType.AGGREGATOR)
            .id(idFromTags(feature))
            .name(feature.name())
            .language(feature.language())
            .keyword(feature.keyword())
            .description(lines(feature.description()))
            .tags(tags(feature))
            .source(nodeLocation(feature))
            .underlyingModel(feature)
        ;
        node.properties().putAll(propertiesFromComments(feature));
        node.properties().put(GHERKIN_TYPE, GHERKIN_TYPE_FEATURE);

        for (var child : feature.children()) {
            if (child instanceof Scenario scenario) {
                node.addChild(scenarioNode(scenario));
            } else if (child instanceof ScenarioOutline scenarioOutline) {
                node.addChild(scenarioOutlineNode(scenarioOutline));
            }
        }

        return node;
    }





    private MutablePlanNode scenarioNode(Scenario scenario) {
        return scenarioNode(scenario, scenario.name(), idFromTags(scenario), scenario.keyword());
    }


    private MutablePlanNode scenarioNode(
        ScenarioOutline scenarioOutline,
        int example
    ) {
        return scenarioNode(
            scenarioOutline,
            "%s [%s]".formatted(scenarioOutline.name(), example),
            idFromTags(scenarioOutline)+"_"+example,
            scenarioKeyword
        );
    }


    private MutablePlanNode scenarioNode(
        ScenarioDefinition scenarioDefinition,
        String name,
        String id,
        String keyword
    ) {
        var node = new MutablePlanNode(NodeType.TEST_CASE)
            .id(id)
            .name(name)
            .language(feature.language())
            .keyword(keyword)
            .description(lines(scenarioDefinition.description()))
            .tags(tags(scenarioDefinition))
            .source(nodeLocation(scenarioDefinition))
            .underlyingModel(scenarioDefinition)
            .addProperties(propertiesFromComments(scenarioDefinition))
            .addProperty(GHERKIN_TYPE, GHERKIN_TYPE_SCENARIO);
        ifPresent(createBackgroundStepsNode(), node::addChild);
        scenarioDefinition.children().forEach( step -> node.addChild(stepNode(step)));
        return node;
    }


    private MutablePlanNode scenarioOutlineNode(ScenarioOutline scenarioOutline) {
        var node = new MutablePlanNode(NodeType.AGGREGATOR)
            .id(idFromTags(scenarioOutline))
            .name(scenarioOutline.name())
            .displayNamePattern("{keyword}: {name}")
            .language(feature.language())
            .keyword(scenarioOutline.keyword())
            .description(lines(scenarioOutline.description()))
            .tags(tags(scenarioOutline))
            .source(nodeLocation(scenarioOutline))
            .underlyingModel(scenarioOutline)
            .argument(let(first(scenarioOutline.examples()),this::tableOf))
            .addProperties(propertiesFromComments(scenarioOutline))
            .addProperty(GHERKIN_TYPE, GHERKIN_TYPE_SCENARIO_OUTLINE);
        scenarioOutline.examples().stream()
            .flatMap(examples -> createScenariosFromExamples(scenarioOutline, examples).stream())
            .forEach(node::addChild);
        return node;
    }


    private MutablePlanNode stepNode(Step step) {
        var node = new MutablePlanNode(NodeType.STEP)
            .name(step.text())
            .language(feature.language())
            .keyword(step.keyword())
            .displayNamePattern("{keyword} {name}")
            .source(nodeLocation(step))
            .underlyingModel(step)
            .addProperty(GHERKIN_TYPE,GHERKIN_TYPE_STEP);
        if (step.argument() instanceof DataTable dataTable) {
            node.argument(tableOf(dataTable));
        } else if (step.argument() instanceof DocString docString) {
            node.argument(documentOf(docString));
        }
        return node;
    }


    public MutablePlanNode createBackgroundStepsNode() {
        if (background == null) {
            return null;
        }
        var node = new MutablePlanNode(NodeType.STEP_AGGREGATOR)
            .name(background.name())
            .language(feature.language())
            .keyword(background.keyword())
            .description(lines(background.description()))
            .tags(tags(background))
            .source(nodeLocation(background))
            .underlyingModel(background)
            .addProperties(propertiesFromComments(background))
            .addProperty(GHERKIN_TYPE,GHERKIN_TYPE_BACKGROUND);
        background.children().forEach(step -> node.addChild(stepNode(step)));
        return node;
    }


    public List<MutablePlanNode> createScenariosFromExamples(
        ScenarioOutline scenarioOutline,
        Examples examples
    ) {
        return indexMapped(substitutions(examples), (number, substitution) ->
            also(
                scenarioNode(scenarioOutline, number+1),
                scenarioNode -> scenarioNode.children().forEach(
                  step -> step.name( let(step.name(),substitution))
                )
            )
        );
    }


    private String nodeLocation(Node node) {
        return "%s[%s,%s]".formatted(relativePath,node.location().line(),node.location().column());
    }


    private Map<String,String> propertiesFromComments(Commented node) {
        return node.comments().stream()
            .map(Comment::text)
            .map(propertyRegex::matcher)
            .filter(Matcher::find)
            .collect(Collectors.toMap(it -> it.group(1),it -> it.group(2)));
    }


    private List<String> tags(Tagged node) {
        return node.tags().stream()
            .map(it -> it.name().substring(1))
            .filter(idTagPattern.asPredicate().negate())
            .toList();
    }


    private List<String> lines(String string) {
        return string.lines().map(String::strip).toList();
    }


    private iti.kukumo.plugin.api.plan.DataTable tableOf(DataTable dataTable) {
        return new iti.kukumo.plugin.api.plan.DataTable(
            mapped(dataTable.rows(), row -> mapped(row.cells(), TableCell::value))
        );
    }


    private iti.kukumo.plugin.api.plan.DataTable tableOf(Examples examples) {
        return new iti.kukumo.plugin.api.plan.DataTable(concat(
            List.of(mapped(examples.tableHeader().cells(),TableCell::value)),
            mapped(examples.tableBody(), row -> mapped(row.cells(),TableCell::value))
        ));
    }


    private Document documentOf(DocString docString) {
        return new Document(docString.contentType(), docString.content());
    }


    private List<Function<String,String>> substitutions(Examples examples) {
        var variables = mapped(
            examples.tableHeader().cells(),
            cell->"<"+cell.value()+">"
        );
        var values = mapped(
            examples.tableBody(),
            row -> mapped(row.cells(),TableCell::value)
        );
        return mapped(values, example ->
            indexMapped(example, (index,value) -> Map.entry(variables.get(index),value) )
                .stream()
                .map(this::toReplaceFunction)
                .reduce(Function::andThen)
                .orElseThrow()
        );
    }


    private Function<String,String> toReplaceFunction(Map.Entry<String,String> pair) {
        return ( input -> input.replace(pair.getKey(), pair.getValue()) );
    }


    private String idFromTags(Tagged tagged) {
        for (Tag tag : List.copyOf(tagged.tags())) {
            var matcher = this.idTagPattern.matcher(tag.name().substring(1));
            if (matcher.find()) {
                return matcher.groupCount() > 0 ? matcher.group(1) : matcher.group(0);
            }
        }
        return null;
    }

}
