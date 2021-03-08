package examples.expressions.parser.ast;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public class Symbol {

    private final String name;
    private final ToDoubleFunction<double[]> operator;
    private final Predicate<Integer> arity;

    public Symbol(String name, ToDoubleFunction<double[]> operator, Predicate<Integer> arity) {
        this.name = name;
        this.operator = operator;
        this.arity = arity;
    }

    public String getName() {
        return name;
    }

    public ToDoubleFunction<double[]> getOperator() {
        return operator;
    }

    public Predicate<Integer> getArity() {
        return arity;
    }

    public double evaluate(double[] arguments) throws EvaluationException {
        return operator.applyAsDouble(arguments);
    }

    public boolean testArity(int value) {
        return arity.test(value);
    }

}
