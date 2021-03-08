package examples.expressions;

import io.simplefuzzing.generators.RandomGenerator;
import io.simplefuzzing.utils.RandomTools;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.simplefuzzing.utils.RandomTools.pick;

public class RandomValidExpression extends RandomGenerator<String> {


    private final int maxTerms;

    private final static char[] LETTERS = "abcdefghijkmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final static char[] DIGITS = "0123456789".toCharArray();
    private final static char[] SYMBOLS = "+-*/".toCharArray();


    public RandomValidExpression(int maxTerms, Random random) {
        super(random);
        this.maxTerms = maxTerms;
    }


    private char letter() {
        return pick(LETTERS, random);
    }

    private char digit() {
        return pick(DIGITS, random);
    }

    private char symbol() { return pick(SYMBOLS, random); }

    private String term() {
        return withProbability(.5) ? number() : id();
    }


    private String id() {
        //TODO: Generate better ids
        return Character.toString(letter());
    }

    private String number() {
        //TODO: Generate better numbers
        return Character.toString(digit());
    }

    private String invocation(String... arguments) {
        return id() + "(" + Arrays.stream(arguments).collect(Collectors.joining(", ")) + ")";
    }

    private boolean withProbability(double probability) {
        return RandomTools.chanceOf(probability, random);
    }

    @Override
    public String get() {

        LinkedList<String> queue = IntStream.range(1, maxTerms).mapToObj(i -> term()).collect(Collectors.toCollection(LinkedList::new));
        while (queue.size() > 1) {

            if (withProbability(.5)) {
                int pos = random.nextInt(queue.size());
                queue.set(pos, decorate(queue.get(pos)));
            }

            if (queue.size() >= 4 && withProbability(.3)) {
                String[] arguments = new String[random.nextInt(3) + 1];
                for (int i = 0; i < arguments.length; i++) {
                    arguments[i] = queue.removeFirst();
                }
                queue.addLast(invocation(arguments));
            } else if (queue.size() >= 2) {
                String first = queue.removeFirst();
                String second = queue.removeFirst();
                queue.addLast(first + " " + symbol() + " " + second);
            }

        }
        return decorate(queue.removeFirst());
    }

    private String decorate(String term) {
        if (withProbability(.2)) {
            if (term.startsWith("+") || term.startsWith("-") || term.startsWith("*") || term.startsWith("/"))
                return "-(" + term + ")";
            return "-" + term;
        }
        if (withProbability(.2)) {
            return "(" + term + ")";
        }
        return term;
    }

}
