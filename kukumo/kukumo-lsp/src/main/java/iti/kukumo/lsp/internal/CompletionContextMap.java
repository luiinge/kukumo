package iti.kukumo.lsp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import iti.commons.gherkin.Comment;
import iti.commons.gherkin.CommentedNode;
import iti.commons.gherkin.Feature;
import iti.commons.gherkin.GherkinDocument;
import iti.commons.gherkin.Location;
import iti.commons.gherkin.Node;
import iti.commons.gherkin.ScenarioDefinition;
import iti.commons.gherkin.Step;

class CompletionContextMap {


    private enum Section {
        MAIN, COMMENT, STEP
    }

    public static class CompletionContext {

        public final Node node;
        public final Section section;

        public CompletionContext(Node node, Section section) {
            this.node = node;
            this.section = section;
        }
    }


    public static CompletionContextMap empty() {
        return new CompletionContextMap(null);
    }


    private final Map<Integer,Optional<CompletionContext>> map = new HashMap<>();


    public CompletionContextMap(GherkinDocument document) {
        if (document != null && document.getFeature() != null) {
            buildContextMap(document.getFeature());
        }
    }


    public Optional<CompletionContext> getContext(int line) {
        return map.getOrDefault(line, Optional.empty());
    }

    private void buildContextMap(CommentedNode node) {
        put(node.getLocation(), node, Section.MAIN);
        for (Comment comment : node.getComments()) {
            put(comment.getLocation(), node, Section.COMMENT);
        }
        if (node instanceof Feature) {
            ((Feature)node).getChildren().forEach(this::buildContextMap);
        }
        if (node instanceof ScenarioDefinition) {
            for (Step step : ((ScenarioDefinition)node).getSteps()) {
                put(step.getLocation(), node, Section.STEP);
            }
        }
    }




    private void put(Location location, CommentedNode node, Section section) {
        map.put(location.getLine(),Optional.of(new CompletionContext(node, section)));
    }

}