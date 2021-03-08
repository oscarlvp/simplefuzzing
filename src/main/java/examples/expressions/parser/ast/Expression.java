package examples.expressions.parser.ast;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Expression {

    private final String tag;

    private final Expression[] children;

    public Expression(String tag, Expression... children) {
        this.tag = tag;
        this.children = children;
    }

    public String getTag() {
        return tag;
    }

    public double evaluate(Scope scope) throws EvaluationException {

        Optional<Symbol> symbol = scope.get(tag);
        if(symbol.isEmpty()) throw new EvaluationException("Unknown symbol: " + tag);
        Symbol op = symbol.get();
        if(!op.testArity(children.length))
            throw new EvaluationException(String.format("Operator %s can not evaluate %d operands", tag, children.length));

        double[] arguments = new double[children.length];
        for(int i = 0 ; i < children.length; i++) {
            arguments[i] = children[i].evaluate(scope);
        }

        return op.evaluate(arguments);
    }

    public static Expression constant(String name) {
        return new Expression(name);
    }

    @Override
    public String toString() {
        if(children == null || children.length == 0) {
            return tag;
        }
        return "( " + tag + " " + Arrays.stream(children).map(Expression::toString).collect(Collectors.joining(" ")) + " )";
    }
}
