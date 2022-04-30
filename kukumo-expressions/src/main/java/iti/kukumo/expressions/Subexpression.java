package iti.kukumo.expressions;

import java.util.*;

import org.jexten.ExtensionPoint;

import iti.kukumo.plugin.api.Contribution;

@ExtensionPoint(version = "2.0")
public interface Subexpression extends Contribution {

    String name();

    List<String> regexs(Locale locale);

}
