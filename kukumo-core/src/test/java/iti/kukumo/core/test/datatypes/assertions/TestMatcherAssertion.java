package iti.kukumo.core.test.datatypes.assertions;

import iti.kukumo.core.datatypes.assertions.MatcherAssertion;
import iti.kukumo.plugin.api.datatypes.Assertion;
import static org.assertj.core.api.Assertions.assertThat;
import org.hamcrest.*;
import org.junit.jupiter.api.Test;

class TestMatcherAssertion {

    @Test
    void asMatcher() {
        Assertion<String> assertion = MatcherAssertion.fromMatcher(Matchers.endsWith("yz"));
        Matcher<String> matcher = MatcherAssertion.asMatcher(assertion);
        assertThat(matcher).isNotNull();
        assertThat(matcher.matches("xyz")).isTrue();
        assertThat(matcher.matches("abc")).isFalse();
    }


    @Test
    void fromMatcher() {
        Matcher<String> matcher = Matchers.endsWith("yz");
        Assertion<String> assertion = MatcherAssertion.fromMatcher(matcher);
        assertThat(assertion).isNotNull();
        assertThat(assertion.test("xyz")).isTrue();
        assertThat(assertion.test("abc")).isFalse();
    }



    @Test
    void description() {
        Matcher<String> matcher = Matchers.endsWith("yz");
        Assertion<String> assertion = MatcherAssertion.fromMatcher(matcher);
        assertThat(assertion.description()).isEqualTo("a string ending with \"yz\"");
    }


    @Test
    void describeFailure() {
        Matcher<String> matcher = Matchers.endsWith("yz");
        Assertion<String> assertion = MatcherAssertion.fromMatcher(matcher);
        assertThat(assertion.describeFailure("abc")).isEqualTo("expecting a string ending with \"yz\" but was \"abc\"");
    }

}