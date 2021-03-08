package examples.expressions.parser.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;

public class Scope {

    private final HashMap<String, Symbol> symbols = new HashMap<>();

    public Scope constant(String name, double value) {
        symbols.put(name, new Symbol(name, args -> value, ar -> ar == 0));
        return this;
    }

    public Scope binaryOperator(String name, DoubleBinaryOperator operator) {
        symbols.put(name, new Symbol(name, args -> operator.applyAsDouble(args[0], args[1]), ar -> ar == 2));
        return this;
    }

    public Scope unaryOperator(String name, DoubleUnaryOperator operator) {
        symbols.put(name, new Symbol(name, args -> operator.applyAsDouble(args[0]), ar -> ar == 1));
        return this;
    }

    public Scope associativeBinaryOperator(String name, DoubleBinaryOperator operator) {
        symbols.put(name, new Symbol(name, args -> Arrays.stream(args).reduce(operator).getAsDouble(), ar -> ar >= 2));
        return this;
    }

    public Scope naryOperator(String name, ToDoubleFunction<double[]> operator) {
        symbols.put(name, new Symbol(name, operator, ar -> ar >= 1));
        return this;
    }

    public Scope symbol(Symbol symbol) { symbols.put(symbol.getName(), symbol); return this; }

    public Optional<Symbol> get(String name) {
        Symbol result = symbols.getOrDefault(name, null);
        return (result == null)? Optional.empty() : Optional.of(result);
    }

    public static Scope defaultScope() {

        return new Scope()
                .constant("E", Math.E)
                .constant("PI", Math.PI)
                .constant("TAU", 2*Math.PI)
                .associativeBinaryOperator("+", Double::sum)
                .associativeBinaryOperator("*", (x, y) -> x * y)
                .associativeBinaryOperator("/", (x, y) -> x / y)
                .symbol(new Symbol(
                        "-",
                        args -> (args.length == 1)? - args[0] : Arrays.stream(args).reduce((x, y) -> x - y).getAsDouble(),
                        ar -> ar >= 1 ))
                .unaryOperator("abs", Math::abs)
                .unaryOperator("sin", Math::sin)
                .unaryOperator("cos", Math::cos)
                .binaryOperator("pow", Math::pow)
                .naryOperator("max", args -> Arrays.stream(args).min().getAsDouble())
                .naryOperator("min", args -> Arrays.stream(args).min().getAsDouble())
                ;
    }
}
