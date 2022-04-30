module iti.kukumo.core.test {

    requires org.jexten;
    requires org.jexten.plugin;
    requires iti.kukumo.plugin.api;
    requires iti.kukumo.core;
    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires org.junit.jupiter.params;
    requires org.hamcrest;

    opens iti.kukumo.core.test.expressions to org.junit.platform.commons;
    opens iti.kukumo.core.test.util to org.junit.platform.commons;
    opens iti.kukumo.core.test.datatypes to org.junit.platform.commons;
    opens iti.kukumo.core.test.datatypes.assertions to org.junit.platform.commons;


}