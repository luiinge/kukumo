package iti.kukumo.plugin.api.plan;

import static iti.kukumo.plugin.api.lang.Functions.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.*;
import lombok.experimental.*;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@ToString(exclude = "parent")
public class MutablePlanNode {

    public static MutablePlanNode compose(List<MutablePlanNode> parts) {
        return switch (parts.size()) {
            case 0 -> null;
            case 1 -> parts.get(0);
            default -> new MutablePlanNode(parts);
        };
    }

    private NodeType nodeType;
    private String name;
    private String displayNamePattern;
    private String language;
    private String id;
    private String source;
    private String keyword;
    private Object underlyingModel;
    private NodeArgument argument;
    private final List<String> description = new ArrayList<>();
    private final List<String> tags = new ArrayList<>();
    private final Map<String,String> properties = new LinkedHashMap<>();
    private MutablePlanNode parent;
    private final List<MutablePlanNode> children = new ArrayList<>();


    public MutablePlanNode(NodeType nodeType) {
        this.nodeType = nodeType;
    }


    private MutablePlanNode(List<MutablePlanNode> nodes) {
        this(NodeType.AGGREGATOR);
        this.name = "Test Plan";
        this.children.addAll(nodes);
        nodes.forEach(it -> it.parent(this));
    }


    public MutablePlanNode description(Collection<String> lines) {
        this.description.clear();
        this.description.addAll(lines);
        return this;
    }


    public MutablePlanNode tags(Collection<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        return this;
    }

    public MutablePlanNode properties(Map<String,String> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
        return this;
    }


    public MutablePlanNode addProperties(Map<String,String> properties) {
        this.properties.putAll(properties);
        return this;
    }


    public MutablePlanNode addProperty(String key, String value) {
        this.properties.put(key,value);
        return this;
    }


    public String displayName() {
        return or(displayNamePattern,"")
            .replace("{id}", or(id,""))
            .replace("{name}", or(name,""))
            .replace("{keyword}", or(keyword,""));
    }

    public boolean hasId() {
        return id != null;
    }

    public boolean hasNoId() {
        return id == null;
    }


    public Optional<MutablePlanNode> parent() {
        return Optional.ofNullable(parent);
    }


    public Stream<MutablePlanNode> children(Predicate<MutablePlanNode> filter) {
        return children.stream().filter(filter);
    }


    public Stream<MutablePlanNode> descendants() {
        return Stream.concat(
            children.stream(),
            children.stream().flatMap(MutablePlanNode::descendants)
        );
    }

    public Stream<MutablePlanNode> descendantsOfType(NodeType nodeType) {
        return descendants().filter(it -> it.nodeType == nodeType);
    }


    public Stream<MutablePlanNode> ancestors() {
        return Stream.concat(
            parent().stream(),
            parent().stream().flatMap(MutablePlanNode::ancestors)
        );
    }


    public Stream<MutablePlanNode> siblings() {
        return parent()
            .map(it -> it.children.stream().filter(child->!child.equals(this)) )
            .orElseGet(Stream::empty);
   }


    public int siblingPosition() {
        return parent().map(it -> it.children.indexOf(this)).orElse(-1);
    }


    public boolean hasChildren() {
        return !children.isEmpty();
    }


    public MutablePlanNode root() {
        return parent().map(MutablePlanNode::root).orElse(this);
    }


    public boolean chop() {
        return parent().map(it->it.removeChild(this)).orElse(false);
    }


    public void chopChildren() {
        this.children.forEach(it -> it.parent(null));
        this.children.clear();
    }


    public void addChild(MutablePlanNode child) {
        children.add(child);
        child.parent(this);
    }


    public void addChildFirst(MutablePlanNode child) {
        children.add(0, child);
        child.parent(this);
    }


    public boolean removeChild(MutablePlanNode child) {
        if (children.remove(child)) {
            child.parent(null);
            return true;
        } else {
            return false;
        }
    }


    public int numChildren() {
        return children.size();
    }


    public boolean hasTag(String tag) {
        return allTags().anyMatch(tag::equals);
    }


    public boolean hasPropertyValue(String property, String value) {
        return value.equals(properties.get(property));
    }


    public Stream<String> allTags() {
        return parent()
            .map( it -> Stream.concat(tags.stream(), it.allTags()) )
            .orElseGet(tags::stream);
    }


    public Optional<MutablePlanNode> normalized() {
        if (nodeType == NodeType.AGGREGATOR) {
            if (children.isEmpty()) return Optional.empty();
            if (children.size() == 1) return Optional.of(children.get(0));
        }
        var oldChildren = List.copyOf(children);
        children.clear();
        oldChildren.stream()
            .map(MutablePlanNode::normalized)
            .flatMap(Optional::stream)
            .forEach(this::addChild);
        return Optional.of(this);
    }



    public PlanNode toImmutable() {
        return PlanNode.builder()
            .nodeType(nodeType)
            .name(name)
            .displayName(displayName())
            .language(language)
            .id(id)
            .source(source)
            .keyword(keyword)
            .argument(new PlanNode.PlanNodeArgument(argument))
            .description(List.copyOf(description))
            .tags(allTags().toList())
            .properties(new TreeMap<>(properties))
            .children(children.stream().map(MutablePlanNode::toImmutable).toList())
            .build();
    }



    public Stream<MutablePlanNode> stream() {
        return Stream.concat(
            Stream.of(this),
            children.stream().flatMap(MutablePlanNode::stream)
        ).toList().stream(); // necessary to allow children removals
    }



}
