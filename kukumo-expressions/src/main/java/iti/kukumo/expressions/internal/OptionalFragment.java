package iti.kukumo.expressions.internal;


import java.util.Objects;

final class OptionalFragment extends CompoundFragment implements RegexContributor {

    final boolean attach;

    OptionalFragment(StepExpressionFragment fragment, boolean attach) {
        this.fragments.add(fragment);
        this.attach = attach;
    }


    @Override
    public String regex() {
        if (fragments.getFirst().normalized() instanceof RegexContributor regex) {
            return regex.regex()+"?";
        }
        throw new IllegalArgumentException("Cannot build a simple regex from "+ fragments.getFirst());
    }


    @Override
    protected StepExpressionFragment normalized() {
        return new OptionalFragment(fragments.getFirst().normalized(),attach);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OptionalFragment that = (OptionalFragment) o;
        return attach == that.attach;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attach);
    }

}
