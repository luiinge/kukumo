package iti.kukumo.core.expressions.internal;


import java.util.*;


abstract class CompoundFragment extends StepExpressionFragment {

    protected final LinkedList<StepExpressionFragment> fragments = new LinkedList<>();


    protected CompoundFragment() {
       //
    }

    protected CompoundFragment(Iterable<StepExpressionFragment> fragments) {
       fragments.forEach(this::add);
    }


    void add(StepExpressionFragment node) {
        if (node != null) this.fragments.add(node);
    }


    StepExpressionFragment first() {
        return this.fragments.getFirst();
    }

    StepExpressionFragment last() {
        return this.fragments.getLast();
    }


    public int size() {
        return fragments.size();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundFragment that = (CompoundFragment) o;
        return Objects.equals(fragments, that.fragments);
    }


    @Override
    public int hashCode() {
        return Objects.hash(fragments);
    }

}
