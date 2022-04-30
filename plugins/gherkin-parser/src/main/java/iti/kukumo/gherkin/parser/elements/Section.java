package iti.kukumo.gherkin.parser.elements;

public interface Section extends Node, Tagged, Commented {
    String keyword();
    String name();
    String description();
}
