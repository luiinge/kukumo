package iti.kukumo.core.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class LogicExpression {

    private final String value;


    private enum TokenType {
        VALUE, AND, OR, XOR, NOT, OPEN_BRACKET, CLOSE_BRACKET
    }

    private record Token(String literal, TokenType type) { }


    private static sealed class Predicate {
        private final String text;
        protected Predicate(String text) { this.text = text;  }
        public String toString() { return text; }
    }

    private static final class Tautology extends Predicate {
        private final Boolean value;
        Tautology(Boolean value) {
            super(value.toString().toUpperCase());
            this.value = value;
        }
    }

    private static final class UnaryOperation extends Predicate {
        private final UnaryOperator<Boolean> operation;
        UnaryOperation(UnaryOperator<Boolean> operation, String text) {
            super(text);
            this.operation = operation;
        }
    }

    private static final class BinaryOperation extends Predicate {
        private final BinaryOperator<Boolean> operation;
        BinaryOperation(BinaryOperator<Boolean> operation, String text) {
            super(text);
            this.operation = operation;
        }
    }

    private static final class Expression extends Predicate {
        private final List<Predicate> predicates;
        Expression(List<Predicate> predicates) {
            super(predicates.stream().map(String::valueOf).collect(Collectors.joining(" "," ( "," ) ")));
            this.predicates = predicates;
        }
    }

    private final Map<String,TokenType> symbols = Map.ofEntries(
        Map.entry("and", TokenType.AND),
        Map.entry("or", TokenType.OR),
        Map.entry("xor", TokenType.XOR),
        Map.entry("not", TokenType.NOT),
        Map.entry("(", TokenType.OPEN_BRACKET),
        Map.entry(")", TokenType.CLOSE_BRACKET)
    );


    private final List<Token> tokens;


    public LogicExpression(String value) {
        this.value = value;
        try {
            var separators = List.of(" ");
            var delimiters = Stream.concat(
                separators.stream(),
                symbols.keySet().stream().filter(it -> it.length() == 1)
            ).toList();
            var tokenizer = new StringTokenizer(value, String.join("", delimiters), true);
            this.tokens = Collections.list(tokenizer).stream()
                .map(Object::toString)
                .filter(it -> !separators.contains(it))
                .map( it -> new Token(it, typeOf(it)))
                .toList();
        } catch (Exception e) {
            throw exception(e);
        }
    }


    public boolean evaluate(List<String> values) {
        try {
            return evaluate(simplify(resolve(values)));
        } catch (Exception e) {
            throw exception(e);
        }
    }


    private boolean evaluate(Predicate predicate) {
        if (predicate instanceof Tautology tautology) {
            return tautology.value;
        } else if (predicate instanceof Expression expression) {
            var predicates = expression.predicates;
            var first = predicates.get(0);
            if (first instanceof Tautology tautology && predicates.size() == 1) {
                return tautology.value;
            } else if (first instanceof Expression && predicates.size() == 1) {
                return evaluate(first);
            } else if (first instanceof UnaryOperation unaryOperation && predicates.size() == 2) {
                return unaryOperation.operation.apply(evaluate(predicates.get(1)));
            } else if (first instanceof BinaryOperation binaryOperation && predicates.size() == 3) {
                return binaryOperation.operation.apply(evaluate(predicates.get(1)),(evaluate(predicates.get(2))));
            } else {
                throw exception(null);
            }
        } else {
            return false;
        }
     }


    private Predicate simplify(Predicate predicate) {
        if (predicate instanceof Expression expression) {
            return simplifyBinaryOperation(simplifyUnaryOperation(expression));
        } else {
            return predicate;
        }
    }

    private Expression simplifyUnaryOperation(Expression expression) {
        if (expression.predicates.size() <= 1) {
            return expression;
        }
        List<Predicate> simplified = new ArrayList<>();
        UnaryOperation operation = null;
        for (var next : expression.predicates) {
            if (next instanceof UnaryOperation unaryOperation) {
                operation = unaryOperation;
            } else if (operation != null) {
                simplified.add(new Expression(List.of(operation, simplify(next))));
                operation = null;
            } else {
                simplified.add(next);
            }
        }
        return new Expression(simplified);
    }


    private Expression simplifyBinaryOperation(Expression expression) {
        if (expression.predicates.size() <= 2) {
            return expression;
        }
        List<Predicate> simplified = new ArrayList<>();
        Predicate left = null;
        BinaryOperation operation = null;
        for (var next : expression.predicates) {
            if (left == null) {
                left = next;
            } else if (next instanceof BinaryOperation binaryOperation) {
                operation = binaryOperation;
            } else if (operation != null) {
                left = new Expression(List.of(operation, simplify(left), simplify(next)));
                operation = null;
            } else {
                simplified.add(left);
                left = null;
                simplified.add(next);
            }
        }
        if (operation == null && left != null) {
            simplified.add(left);
        }
        return new Expression(simplified);
    }



    private Expression resolve(List<String> values) {

        List<List<Predicate>> expressionStack = new ArrayList<>();
        List<Predicate> currentExpression = new ArrayList<>();
        expressionStack.add(currentExpression);

        for(var token : tokens) {
            switch (token.type) {
                case VALUE -> currentExpression.add(new Tautology(values.contains(token.literal)));
                case AND -> currentExpression.add(new BinaryOperation((a, b) -> a && b, "AND"));
                case OR -> currentExpression.add(new BinaryOperation((a, b) -> a || b, "OR"));
                case XOR -> currentExpression.add(new BinaryOperation((a, b) -> a ^ b, "AND"));
                case NOT -> currentExpression.add(new UnaryOperation(a -> !a, "NOT"));
                case OPEN_BRACKET -> {
                    currentExpression = new ArrayList<>();
                    expressionStack.add(currentExpression);
                }
                case CLOSE_BRACKET -> {
                    var closedExpression = expressionStack.remove(expressionStack.size()-1);
                    currentExpression = expressionStack.get(expressionStack.size()-1);
                    currentExpression.add(new Expression(closedExpression));
                }
            }
        }
        return new Expression(currentExpression);
    }


    private TokenType typeOf(String literal) {
        return symbols.getOrDefault(literal.toLowerCase(), TokenType.VALUE);
    }


    private IllegalArgumentException exception(Exception e) {
        throw new IllegalArgumentException("Invalid logic expression: "+value, e);
    }

}
