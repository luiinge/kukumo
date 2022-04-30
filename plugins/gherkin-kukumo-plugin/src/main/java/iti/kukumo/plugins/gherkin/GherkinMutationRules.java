package iti.kukumo.plugins.gherkin;

import iti.kukumo.plugin.api.*;
import static iti.kukumo.plugin.api.lang.Functions.*;
import static iti.kukumo.plugins.gherkin.GherkinConfig.*;
import java.util.*;
import imconfig.Config;
import iti.kukumo.gherkin.parser.KeywordMapProvider;
import iti.kukumo.gherkin.parser.elements.*;
import iti.kukumo.plugin.api.contributions.PlanMutationRules;
import iti.kukumo.plugin.api.plan.*;
import static iti.kukumo.plugin.api.plan.mutators.Mutators.forEach;
import static iti.kukumo.plugins.gherkin.GherkinConstants.*;
import java.util.function.*;
import org.jexten.*;

@Extension(extensionPointVersion = "2.0")
public class GherkinMutationRules implements PlanMutationRules {

    @InjectedExtension
    Localizer localizer;

    @InjectedExtension
    Config config;

    KeywordMapProvider keywordMapProvider;

    @PostConstructExtension
    public void init() {
        keywordMapProvider = GherkinKeywordProvider.fromLocalizer(localizer);
    }


    @Override
    public List<PlanNodeMutationRule> rules() {
        return let(new Rules(config), it -> List.of(
            it.chopDefinitionBackground,
            it.chopChildrenOfImplementationScenarioOutlines,
            it.populateImplementationScenarioOutlinesWithExamples,//
            it.addBackgroundToRedefinedImplementationScenarios,
            it.changeNodeTypeOfDefinitionSteps,
            it.attachImplementationStepsToDefinitionStepAggregators,
            it.attachImplementationBackgroundToDefinitionScenarios,
            it.changeTheNodeTypeOfStepAggregatorsWithNoChildrenToVirtualStep,
            it.copyPropertiesFromDefinitionToImplementation,
            it.chopImplementationFeature,
            it.nodesWithNoIdWouldInheritParentId
        ));
    }


    private class Rules {

        String definitionTag;
        String implementationTag;
        String idTagPattern;

        Rules(Config conf) {
            this.definitionTag = conf.get(REDEFINITION_DEFINITION_TAG).orElse(null);
            this.implementationTag = conf.get(REDEFINITION_IMPLEMENTATION_TAG).orElse(null);
            this.idTagPattern = conf.get(ID_TAG_PATTERN).orElseThrow();
        }


        final PlanNodeMutationRule chopDefinitionBackground =
            forEach(this::isDefinition).and(this::isBackground)
            .then(MutablePlanNode::chop);

        final PlanNodeMutationRule chopChildrenOfImplementationScenarioOutlines =
            forEach(this::isImplementation).and(this::isScenarioOutline)
            .then(MutablePlanNode::chopChildren);

        final PlanNodeMutationRule populateImplementationScenarioOutlinesWithExamples =
            forEach(this::isImplementation).and(this::isScenarioOutline)
            .givenOptional(this::anyDefinitionScenarioOutlineWithSameId)
            .then(this::populateWithExamples);

        final PlanNodeMutationRule addBackgroundToRedefinedImplementationScenarios =
            forEach(this::isImplementation).and(this::isScenario)
            .and(it -> it.parent().map(this::isScenarioOutline).orElse(Boolean.FALSE))
            .then(this::addBackgroundNode);

        final PlanNodeMutationRule changeNodeTypeOfDefinitionSteps =
            forEach(this::isDefinition).and(this::isStep)
            .then(it -> it.nodeType(NodeType.STEP_AGGREGATOR));

        final PlanNodeMutationRule attachImplementationStepsToDefinitionStepAggregators =
            forEach(this::isDefinition).and(it -> it.nodeType() == NodeType.STEP_AGGREGATOR)
                .givenOptional(this::implementationScenarioWhichParentHasSameId)
                .then(this::attachImplementationSteps);


        final PlanNodeMutationRule attachImplementationBackgroundToDefinitionScenarios =
            forEach(this::isImplementation).and(this::isBackground)
                .givenOptional(this::definitionScenarioWhichParentHasSameId)
                .then(this::attachImplementationBackgroundToDefinitionScenario);


        final PlanNodeMutationRule changeTheNodeTypeOfStepAggregatorsWithNoChildrenToVirtualStep =
            forEach(it -> it.nodeType() == NodeType.STEP_AGGREGATOR)
                .and( it -> !it.hasChildren())
                .then ( it -> it.nodeType(NodeType.VIRTUAL_STEP));

        final PlanNodeMutationRule copyPropertiesFromDefinitionToImplementation =
            forEach(this::isDefinition).and(this::isScenario)
                .givenOptional(this::anyImplementationScenarioWithSameId)
                .then( (def,impl) -> impl.properties(def.properties()));


        final PlanNodeMutationRule nodesWithNoIdWouldInheritParentId =
            forEach(MutablePlanNode::hasNoId)
            .givenOptional(MutablePlanNode::parent)
            .and( (parent,node) -> parent.hasId())
            .then( (parent,node) -> node.id("%s_%s".formatted(parent.id(), node.siblingPosition() + 1)));


        final PlanNodeMutationRule chopImplementationFeature =
            forEach(this::isFeature).and(this::isImplementation)
            .then(MutablePlanNode::chop);




        private Optional<MutablePlanNode> implementationScenarioWhichParentHasSameId(MutablePlanNode node) {
            return anyOtherNode(
                it -> it.nodeType() == NodeType.TEST_CASE && it.hasTag(implementationTag),
                sharing(MutablePlanNode::parent, Optional::of, MutablePlanNode::id)
            ).apply(node);
        }


        private Optional<MutablePlanNode> definitionScenarioWhichParentHasSameId(MutablePlanNode node) {
            return anyOtherNode(
                it -> it.nodeType() == NodeType.TEST_CASE && it.hasTag(definitionTag),
                sharing(MutablePlanNode::parent, Optional::of, MutablePlanNode::id)
            ).apply(node);
        }


        private void addBackgroundNode(MutablePlanNode node) {
            var backgroundStepsNode = new PlanNodeFactory(
                parentFeature(node),
                or( node.source(), ""),
                keywordMapProvider,
                idTagPattern
            )
            .createBackgroundStepsNode();
            if (backgroundStepsNode != null) {
                node.addChildFirst(backgroundStepsNode
                    .name("<preparation>")
                    .displayNamePattern("{name}")
                );
            }
        }


        private Optional<MutablePlanNode> anyDefinitionScenarioOutlineWithSameId(MutablePlanNode node) {
            return node.root().descendants().filter(this::isDefinition)
                .filter(this::isScenarioOutline)
                .filter(MutablePlanNode::hasId)
                .filter(other -> other.id().equals(node.id()) )
                .findFirst();
        }


        private Optional<MutablePlanNode> anyImplementationScenarioWithSameId(MutablePlanNode node) {
            return node.root().descendants().filter(this::isImplementation)
                .filter(this::isScenario)
                .filter(other -> or( let( other.id(), id -> id.equals(node.id())) , Boolean.FALSE) )
                .findFirst();
        }

        private void populateWithExamples(
            MutablePlanNode implementation,
            MutablePlanNode definition
        ) {
            new PlanNodeFactory(
                parentFeature(implementation),
                or(implementation.source(), ""),
                keywordMapProvider,
                idTagPattern
            ).createScenariosFromExamples(
                (ScenarioOutline) implementation.underlyingModel(),
                ((ScenarioOutline) definition.underlyingModel()).examples().get(0)
            ).forEach(implementation::addChild);
        }



        private record StepMap(int start, int end) { }

        public void attachImplementationSteps(
            MutablePlanNode defStepNode,
            MutablePlanNode impScenarioNode
        ) {
            StepMap[] stepMaps = computeStepMap(
                defStepNode.parent().map(MutablePlanNode::numChildren).orElse(0),
                impScenarioNode
            );
            var impStepNodes = impScenarioNode.children(it->it.nodeType()==NodeType.STEP).toList();
            int stepNumber = defStepNode.siblingPosition();
            StepMap stepMap = stepMaps[stepNumber];
            for (int i = stepMap.start; i <= stepMap.end; i++) {
                defStepNode.addChild(impStepNodes.get(i));
            }
        }

        private StepMap[] computeStepMap(int numDefChildren, MutablePlanNode implNode) {
            StepMap[] stepMap = new StepMap[numDefChildren];
            String stepMapProperty = implNode.properties().get(STEP_MAP);
            try {
                if (stepMapProperty != null) {
                    String[] stepMapArray = stepMapProperty.split("-");
                    int base = 0;
                    for (int i = 0; i < stepMapArray.length; i++) {
                        int size = Integer.parseInt(stepMapArray[i]);
                        stepMap[i] = new StepMap(base, base+size-1);
                        base += size;
                    }
                } else {
                    for (int i = 0; i < numDefChildren; i++) {
                        stepMap[i] = new StepMap(i, i);
                    }
                }
                return stepMap;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new KukumoPluginException(
                    e,
                    "Bad definition of step map in {} : {}",
                    implNode.source(),
                    stepMapProperty
                );
            }
        }


        public static <T> BiPredicate<MutablePlanNode, MutablePlanNode> sharing(
            Function<MutablePlanNode, Optional<MutablePlanNode>> leftNodeGetter,
            Function<MutablePlanNode, Optional<MutablePlanNode>> rightNodeGetter,
            Function<MutablePlanNode, T> method
        ) {
            return (leftNode, rightNode) -> {
                Optional<MutablePlanNode> actualLeftNode = leftNodeGetter.apply(leftNode);
                Optional<MutablePlanNode> actualRightNode = rightNodeGetter.apply(rightNode);
                return actualLeftNode.isPresent() && actualRightNode.isPresent() &&
                    Objects.equals(method.apply(actualLeftNode.get()), method.apply(actualRightNode.get()));
            };
        }



        public void attachImplementationBackgroundToDefinitionScenario(
            MutablePlanNode impBackground,
            MutablePlanNode defScenario
        ) {
            defScenario.addChildFirst(
                impBackground
                    .name("<preparation>")
                    .keyword(null)
                    .displayNamePattern("{name}")
            );
        }



        public static Function<MutablePlanNode, Optional<MutablePlanNode>> anyOtherNode(
            Predicate<MutablePlanNode> predicate,
            BiPredicate<MutablePlanNode, MutablePlanNode> biPredicate
        ) {
            return leftNode -> leftNode.root().descendants()
                .filter(predicate)
                .filter(rightNode -> biPredicate.test(leftNode, rightNode))
                .findAny();
        }



        private boolean isImplementation(MutablePlanNode node) {
            return node.hasTag(implementationTag);
        }

        private boolean isDefinition(MutablePlanNode node) {
            return node.hasTag(definitionTag);
        }

        private boolean isFeature(MutablePlanNode node) {
            return node.hasPropertyValue(GHERKIN_TYPE, GHERKIN_TYPE_FEATURE);
        }

        private boolean isBackground(MutablePlanNode node) {
            return node.hasPropertyValue(GHERKIN_TYPE, GHERKIN_TYPE_BACKGROUND);
        }

        private boolean isScenarioOutline(MutablePlanNode node) {
            return node.hasPropertyValue(GHERKIN_TYPE, GHERKIN_TYPE_SCENARIO_OUTLINE);
        }

        private boolean isScenario(MutablePlanNode node) {
            return node.hasPropertyValue(GHERKIN_TYPE, GHERKIN_TYPE_SCENARIO);
        }

        private boolean isStep(MutablePlanNode node) {
            return node.hasPropertyValue(GHERKIN_TYPE, GHERKIN_TYPE_STEP);
        }


        private Feature parentFeature(MutablePlanNode implementation) {
            return (Feature) implementation
                .ancestors()
                .filter(this::isFeature)
                .findFirst()
                .orElseThrow()
                .underlyingModel();
        }

    }






}
