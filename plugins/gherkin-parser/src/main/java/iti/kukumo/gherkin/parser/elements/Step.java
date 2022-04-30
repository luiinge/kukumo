package iti.kukumo.gherkin.parser.elements;

import java.util.*;

public record Step (
    Location location,
    List<Comment> comments,
    String keyword,
    String text,
    StepArgument argument
) implements Node, Commented { }