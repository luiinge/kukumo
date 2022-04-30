package iti.kukumo.core.expressions.internal;


import java.util.Objects;

final class NegatedFragment extends CompoundFragment implements RegexContributor {

    final boolean word;

    NegatedFragment(StepExpressionFragment node, boolean word) {
        this.fragments.add(node);
        this.word = word;
    }


    @Override
    public String regex() {
        if (fragments.getFirst().normalized() instanceof RegexContributor regex) {
            return "(?!"+regex.regex()+")" + (word ? "\\S+" : ".*");
        }
        throw new IllegalArgumentException("Cannot build a simple regex from "+ fragments.getFirst());
    }



    @Override
    protected StepExpressionFragment normalized() {
        return new NegatedFragment(fragments.getFirst().normalized(),word);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NegatedFragment that = (NegatedFragment) o;
        return word == that.word;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), word);
    }

}
