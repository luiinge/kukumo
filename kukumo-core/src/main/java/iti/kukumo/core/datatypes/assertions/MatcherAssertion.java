package iti.kukumo.core.datatypes.assertions;

import iti.kukumo.plugin.api.datatypes.Assertion;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;



public class MatcherAssertion<T> implements Assertion<T> {

    public static <T> Matcher<T> asMatcher(Assertion<T> assertion) {
        if (assertion instanceof MatcherAssertion) {
            return ((MatcherAssertion<T>) assertion).matcher;
        } else {
            return new BaseMatcher<>() {
                @Override
                public boolean matches(Object actual) {
                    return assertion.test(actual);
                }
                @Override
                public void describeTo(Description description) {
                    description.appendText(assertion.description());
                }
            };
        }
    }


    public static <T> Assertion<T> fromMatcher(Matcher<T> matcher) {
        return new MatcherAssertion<>(matcher);
    }



    private final Matcher<T> matcher;

    private MatcherAssertion(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean test(Object actualValue) {
        return matcher.matches(actualValue);
    }

    @Override
    public String description() {
        StringDescription description = new StringDescription();
        matcher.describeTo(description);
        return description.toString();
    }

    @Override
    public String describeFailure(Object actualValue) {
        StringDescription description = new StringDescription();
        description.appendText("expecting ");
        matcher.describeTo(description);
        description.appendText(" but ");
        matcher.describeMismatch(actualValue, description);
        return description.toString();
    }



}