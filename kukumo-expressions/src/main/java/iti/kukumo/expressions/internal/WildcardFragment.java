package iti.kukumo.expressions.internal;


final class WildcardFragment extends StepExpressionFragment implements RegexContributor {

    static final WildcardFragment INSTANCE = new WildcardFragment();


    private WildcardFragment() {
        super();
    }

    @Override
    public String regex() {
        return "(.*)";
    }


    @Override
    protected StepExpressionFragment normalized() {
        return this;
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof WildcardFragment && this == INSTANCE);
    }


    @Override
    public int hashCode() {
        return 33;
    }

}
