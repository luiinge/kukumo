package iti.kukumo.core.expressions.internal;



abstract class StepExpressionFragment {

    static StepExpressionFragment literal(String text) {
        return new LiteralFragment(text);
    }

    static StepExpressionFragment optional(StepExpressionFragment fragment, boolean attach) {
        return new OptionalFragment(fragment,attach);
    }


    static StepExpressionFragment wildcard() {
        return WildcardFragment.INSTANCE;
    }


    static StepExpressionFragment negate(StepExpressionFragment fragment, boolean word) {
        return new NegatedFragment(fragment,word);
    }


    protected abstract StepExpressionFragment normalized();

}
