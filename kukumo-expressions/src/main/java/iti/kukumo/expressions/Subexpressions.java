package iti.kukumo.expressions;

import java.util.*;
import java.util.stream.*;

import org.jexten.ExtensionManager;

public class Subexpressions {

    public static Subexpressions of (Subexpression... subExpressions) {
        return new Subexpressions(Arrays.asList(subExpressions));
    }


    private final Map<String, Subexpression> byName;
    private final List<String> allNames;


    public Subexpressions(List<Subexpression> dataTypes) {
        this.byName = dataTypes.stream().collect(Collectors.toMap(Subexpression::name, e -> e));
        this.allNames = dataTypes.stream().map(Subexpression::name).sorted().toList();
    }


    Subexpressions(ExtensionManager extensionManager) {
        var dataTypes = extensionManager.getExtensions(Subexpression.class).toList();
        this.byName = dataTypes.stream().collect(Collectors.toMap(Subexpression::name, e -> e));
        this.allNames = dataTypes.stream().map(Subexpression::name).sorted().toList();
    }


    public Optional<Subexpression> getByName(String name) {
        return Optional.ofNullable(byName.get(name));
    }


    public List<String> allNames() {
        return allNames;
    }


    public Stream<Subexpression> stream() {
        return byName.values().stream();
    }

}
