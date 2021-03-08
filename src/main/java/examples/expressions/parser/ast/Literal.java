package examples.expressions.parser.ast;

public class Literal extends Expression {

    public Literal(String value) {
        super(value);
    }

    @Override
    public double evaluate(Scope scope) {
        return Double.parseDouble(getTag());
    }
}
