module iti.kukumo.expressions.test {
    requires iti.kukumo.expressions;
    requires iti.kukumo.plugin.api;
    requires org.junit.jupiter.params;
    requires org.assertj.core;
    opens iti.kukumo.expressions.test to org.junit.platform.commons;
}