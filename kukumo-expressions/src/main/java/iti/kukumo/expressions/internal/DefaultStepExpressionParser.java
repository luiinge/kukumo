package iti.kukumo.core.expressions.internal;

import iti.kukumo.core.expressions.*;
import java.util.*;


public class DefaultStepExpressionParser implements StepExpressionParser {


    private static final Map<String, StepExpression> cache = new HashMap<>();

    public static StepExpression parse(String step) {
        return cache.computeIfAbsent(step, it->new DefaultStepExpressionParser(it).parse());
    }


    private enum State {
        DEFAULT,
        NEGATE_UNDETERMINED,
        NEGATE_WORD,
        NEGATE_PHRASE,
        OPTIONAL,
        OPTIONAL_CHOICE,
        WORD_CHOICE,
        GROUP_UNDETERMINED,
        GROUP_CHOICE,
        ARGUMENT,
        ARGUMENT_TYPE,
        SUBEXPRESSION
    }

    private static final char[] escapableSymbols = new char[] {
        '*','\\','(',')','{','}','^','[',']','|'
    };

    static {
        Arrays.sort(escapableSymbols);
    }

    private static boolean isEscapable(char c) {
        return Arrays.binarySearch(escapableSymbols,c) >= 0;
    }



    private final String text;
    private final StringBuilder buffer;
    private boolean escaped = false;
    private int position = 0;
    private char previous = 0;
    private char current = 0;
    private char next = 0;
    private final Deque<State> stateStack = new LinkedList<>();
    private final Deque<StepExpressionFragment> nodeStack = new LinkedList<>();
    private StepExpressionFragment currentFragment = null;
    private boolean optionalAttach;




    DefaultStepExpressionParser(String text) {
        this.text = text;
        this.buffer = new StringBuilder(text.length());

    }



    StepExpression parse() {
        pushState(State.DEFAULT, new SequenceFragment());
        while (hasNext()) {
            next();
        }
        if (stateStack.getLast() != State.DEFAULT) {
            abort("unexpected final state "+stateStack.getLast());
        }
        if (!buffer.isEmpty() && currentFragment instanceof CompoundFragment compoundFragment) {
          compoundFragment.add(StepExpressionFragment.literal(dumpBuffer()));
        }

        StepExpressionFragment finalFragment = currentFragment.normalized();
        StepExpression stepExpression;
        if (finalFragment instanceof StepExpression casted) {
            stepExpression = casted;
        } else if (finalFragment instanceof RegexContributor regexContributor) {
            stepExpression = new RegexFragment(regexContributor.regex());
        } else {
            stepExpression = new SequenceFragment(finalFragment);
        }
        return stepExpression;

    }


    private void next() {

        previous = current;
        current = text.charAt(position);
        next = (position == text.length()-1 ? 0 : text.charAt(position+1));

        if (escaped) {
            if (isEscapable(current)) {
                buffer.append(current);
                escaped = false;
                position++;
                return;
            } else {
                abort("unexpected escaped character "+current);
            }
        }

        if (current == '\\') {
            escaped = true;
            position++;
            return;
        }

        if (Character.isWhitespace(current) && Character.isWhitespace(next)) {
            position++;
            return;
        }

        switch (stateStack.getLast()) {
            case DEFAULT -> processDefault();
            case NEGATE_UNDETERMINED -> processNegateUndetermined();
            case NEGATE_WORD -> processNegateWord();
            case NEGATE_PHRASE -> processNegatePhrase();
            case OPTIONAL -> processOptional();
            case OPTIONAL_CHOICE -> processOptionalChoice();
            case WORD_CHOICE -> processWordChoice();
            case GROUP_UNDETERMINED -> processGroupUndetermined();
            case GROUP_CHOICE -> processGroupChoice();
            case ARGUMENT -> processArgument();
            case ARGUMENT_TYPE -> processArgumentType();
            case SUBEXPRESSION -> processSubexpression();
        }

        position++;

    }


    private void processDefault() {
        SequenceFragment parent = (SequenceFragment) currentFragment;
        if (current == '^') {
            if (!Character.isWhitespace(previous)) abort();
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.NEGATE_UNDETERMINED, new SequenceFragment());
        } else if (current == '(') {
            this.optionalAttach = !Character.isWhitespace(previous);
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.OPTIONAL, new SequenceFragment());
        } else if (current == '|') {
            String segmentValue = dumpBuffer();
            int index = segmentValue.lastIndexOf(' ');
            if (index == -1) {
                pushState(State.WORD_CHOICE, new ChoiceFragment());
                ((ChoiceFragment)currentFragment).add(StepExpressionFragment.literal(segmentValue));
            } else {
                String priorSegment = segmentValue.substring(0, index);
                String lastWord = segmentValue.substring(index);
                parent.add(StepExpressionFragment.literal(priorSegment));
                pushState(State.WORD_CHOICE, new ChoiceFragment());
                ((ChoiceFragment) currentFragment).add(StepExpressionFragment.literal(lastWord));
            }
        } else if (current == '[') {
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.GROUP_UNDETERMINED, new SequenceFragment());
        } else if (current == '*') {
            if (previous != 0 && !Character.isWhitespace(previous)) abort();
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            parent.add(StepExpressionFragment.wildcard());
        } else if (current == '{' && next == '{') {
            if (!Character.isWhitespace(previous)) abort();
            position++;
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.SUBEXPRESSION, new SubexpressionFragment());
        } else if (current == '{') {
            if (!Character.isWhitespace(previous)) abort();
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.ARGUMENT, new ArgumentFragment());
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }



    private void processGroupUndetermined() {
        SequenceFragment parent = (SequenceFragment) currentFragment;
        if (current == ']') {
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            popState();
        } else if (current == '|') {
            ChoiceFragment choice = new ChoiceFragment();
            choice.add(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.GROUP_CHOICE, choice);
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processGroupChoice() {
        ChoiceFragment parentNode = (ChoiceFragment) currentFragment;
        if (current == ']') {
            parentNode.add(StepExpressionFragment.literal(dumpBuffer()));
            popState();
            popState();
        } else if (current == '|') {
            parentNode.add(StepExpressionFragment.literal(dumpBuffer()));
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processNegateUndetermined() {
        if (current == '[') {
            if (previous != '^') abort();
            pushState(State.NEGATE_PHRASE, new SequenceFragment());
        } else if (isEscapable(current)) {
            abort();
        } else {
            pushState(State.NEGATE_WORD, new SequenceFragment());
            buffer.append(current);
        }
    }


    private void processNegateWord() {
        SequenceFragment parent = (SequenceFragment) currentFragment;
        if (Character.isWhitespace(current)) {
            parent.add(StepExpressionFragment.negate(StepExpressionFragment.literal(dumpBuffer()),true));
            popState();
            popState();
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processNegatePhrase() {
        SequenceFragment parent = (SequenceFragment) currentFragment;
        if (current == ']') {
            parent.add(StepExpressionFragment.negate(StepExpressionFragment.literal(dumpBuffer()),false));
            popState();
            popState();
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processOptional() {
        SequenceFragment parent = (SequenceFragment) currentFragment;
        if (current == ')') {
            parent.add(StepExpressionFragment.optional(
                StepExpressionFragment.literal(dumpBuffer()),
                optionalAttach
            ));
            popState();
        } else if (current == '|') {
            ChoiceFragment choice = new ChoiceFragment(StepExpressionFragment.literal(dumpBuffer()));
            pushState(State.OPTIONAL_CHOICE, StepExpressionFragment.optional(choice,optionalAttach));
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processOptionalChoice() {
        OptionalFragment optional = (OptionalFragment) currentFragment;
        ChoiceFragment parent = (ChoiceFragment) optional.first();
        if (current == ')') {
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
            popState();
            popState();
        } else if (current == '|') {
            parent.add(StepExpressionFragment.literal(dumpBuffer()));
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processWordChoice() {
        ChoiceFragment groupNode = (ChoiceFragment) currentFragment;
        if (current == '|') {
            groupNode.add(StepExpressionFragment.literal(dumpBuffer()));
        } else if (Character.isWhitespace(current)) {
            groupNode.add(StepExpressionFragment.literal(dumpBuffer()));
            popState();
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }




    private void processSubexpression() {
        SubexpressionFragment subexpressionNode = (SubexpressionFragment) currentFragment;
        if (current == '}') {
            if (buffer.isEmpty()) abort();
            if (next != '}') abort();
            subexpressionNode.type = dumpBuffer();
            popState();
            position++;
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processArgument() {
        ArgumentFragment argumentNode = (ArgumentFragment) currentFragment;
        if (current == ':') {
            if (buffer.isEmpty()) abort();
            argumentNode.name = dumpBuffer();
            this.stateStack.removeLast();
            this.stateStack.addLast(State.ARGUMENT_TYPE);
        } else if (current == '}') {
            if (buffer.isEmpty()) abort();
            argumentNode.type = dumpBuffer();
            argumentNode.name = argumentNode.type;
            popState();
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }


    private void processArgumentType() {
        ArgumentFragment argumentNode = (ArgumentFragment) currentFragment;
        if (current == '}') {
            if (buffer.isEmpty()) abort();
            argumentNode.type = dumpBuffer();
            popState();
        } else if (isEscapable(current)) {
            abort();
        } else {
            buffer.append(current);
        }
    }



    private boolean hasNext() {
        return position < text.length();
    }


    private void pushState(State newState, StepExpressionFragment newNode) {
        this.stateStack.addLast(newState);
        this.currentFragment = newNode;
        this.nodeStack.addLast(newNode);
    }


    private void popState() {
        if (this.nodeStack.isEmpty()) {
            return;
        }
        this.stateStack.removeLast();
        this.nodeStack.removeLast();
        StepExpressionFragment previousNode = this.nodeStack.getLast();
        if (previousNode instanceof CompoundFragment compoundFragment) {
            compoundFragment.add(this.currentFragment);
        }
        this.currentFragment = previousNode;
    }





    private String dumpBuffer() {
        String value = buffer.toString().strip();
        buffer.delete(0, buffer.length());
        return value;
    }


    private void abort(String message) {
        throw new ExpressionParsingException(text,position,message);
    }

    private void abort() {
        throw new ExpressionParsingException(text,position,"unexpected symbol "+current);
    }


    
}
