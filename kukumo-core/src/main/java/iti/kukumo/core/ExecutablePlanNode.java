package iti.kukumo.core;

import imconfig.Config;
import iti.kukumo.plugin.api.plan.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import lombok.Getter;

@Getter
public class ExecutablePlanNode {

    private final NodeType nodeType;
    private final String name;
    private final String displayName;
    private final String language;
    private final String id;
    private final String source;
    private final String keyword;
    private final NodeArgument argument;
    private final List<String> description;
    private final List<String> tags;
    private final Map<String,String> properties;
    private final List<ExecutablePlanNode> children;
    private final Config configuration;

    private Instant startInstant;
    private Instant finishInstant;
    private Result result;
    private Throwable error;


    public ExecutablePlanNode(MutablePlanNode planNode, Config conf) {
        this.nodeType = planNode.nodeType();
        this.name = planNode.name();
        this.displayName = planNode.displayName();
        this.language = planNode.language();
        this.id = planNode.id();
        this.source = planNode.source();
        this.keyword = planNode.keyword();
        this.argument = planNode.argument();
        this.description = List.copyOf(planNode.description());
        this.tags = planNode.allTags().toList();
        this.properties = Map.copyOf(planNode.properties());
        this.configuration = conf.append(Config.factory().fromMap(properties));
        this.children = planNode.children().stream()
            .map(it -> new ExecutablePlanNode(it, this.configuration))
            .toList();
    }


    public void markStarted(Instant instant) {
        if (startInstant != null) throw new IllegalStateException("Already started");
        this.startInstant = instant;
    }


    public void markFinished(Instant instant, Result result) {
        markFinished(instant,result,null);
    }


    public void markFinished(Instant instant, Result result, Throwable error) {
        if (startInstant == null) throw new IllegalStateException("Not started yet");
        if (finishInstant != null) throw new IllegalStateException("Already finished");
        this.finishInstant = instant;
        this.result = result;
        this.error = error;
    }


    public Instant startInstant() {
        if (startInstant != null) return startInstant;
        return children.stream()
            .map(ExecutablePlanNode::startInstant)
            .filter(Objects::nonNull)
            .min(Instant::compareTo).orElse(null);
    }


    public Instant finishInstant() {
        if (finishInstant != null) return finishInstant;
        return children.stream()
            .map(ExecutablePlanNode::finishInstant)
            .filter(Objects::nonNull)
            .max(Instant::compareTo).orElse(null);
    }


    public Duration duration() {
        var finish = finishInstant();
        if (finish == null) {
            return null;
        }
        var start = startInstant();
        return Duration.between(start,finish);
    }


    public Result result() {
        if (result != null) return result;
        return children.stream()
            .map(ExecutablePlanNode::result)
            .filter(Objects::nonNull)
            .max(Result::compareTo)
            .orElse(null);
    }


    public Stream<ExecutablePlanNode> descendants() {
        return Stream.concat(
            children.stream(),
            children.stream().flatMap(ExecutablePlanNode::descendants)
        );
    }



    public PlanNode snapshot() {
        return snapshot(false);
    }

    public PlanNode treeSnapshot() {
        return snapshot(true);
    }


    private PlanNode snapshot(boolean includeChildren) {
        return PlanNode.builder()
            .nodeType(nodeType)
            .name(name)
            .displayName(displayName)
            .language(language)
            .id(id)
            .source(source)
            .keyword(keyword)
            .argument(new PlanNode.PlanNodeArgument(argument))
            .description(description)
            .tags(tags)
            .properties(new TreeMap<>(properties))
            .result(result())
            .startInstant(startInstant())
            .finishInstant(finishInstant())
            .duration(duration())
            .childrenResultCount(new TreeMap<>(children.stream().collect(
                Collectors.groupingBy(ExecutablePlanNode::result, Collectors.counting()))
            ))
            .children( includeChildren ?
                children.stream().map(ExecutablePlanNode::treeSnapshot).toList() :
                List.of()
            )
            .build();
    }


}
