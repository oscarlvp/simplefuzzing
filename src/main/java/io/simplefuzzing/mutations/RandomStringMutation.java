package io.simplefuzzing.mutations;

import java.util.Objects;
import java.util.Random;
import java.util.function.UnaryOperator;

import static io.simplefuzzing.utils.RandomTools.pick;

public abstract class RandomStringMutation implements UnaryOperator<String> {

    protected final Random random;
    protected RandomStringMutation(Random random) {
        Objects.requireNonNull(random);
        this.random = random;
    }

    public static RandomStringMutation removeChar(Random random) {
        return new RandomStringMutation(random) {
            @Override
            public String apply(String source) {
                if(source.isEmpty())
                    return source;
                int pos = random.nextInt(source.length());
                if(pos == 0)
                    return source.substring(1);
                if (pos == source.length() - 1)
                    return source.substring(0, source.length() - 2);
                return source.substring(0, pos - 1) + source.substring(pos + 1);
            }
        };
    }

    public static RandomStringMutation replaceCharFromAlphabet(String alphabet, Random random) {
        return  new RandomStringMutation(random) {
            @Override
            public String apply(String source) {
                if(source == null || source.isEmpty()) return source;
                char[] buffer = source.toCharArray();
                buffer[random.nextInt(source.length())] = pick(alphabet, random);
                return new String(buffer);
            }
        };
    }

    public static RandomStringMutation addCharFromAlphabet(String alphabet, Random random) {
        return new RandomStringMutation(random) {
            @Override
            public String apply(String source) {
                if (source == null) source = "";
                if(source.isEmpty()) return Character.toString(pick(alphabet, random));
                int pos = random.nextInt(source.length());
                char toInsert = pick(alphabet, random);
                if (pos == 0)
                    return toInsert + source;
                if (pos == source.length() - 1)
                    return source + toInsert;
                return source.substring(0, pos - 1) + toInsert + source.substring(pos + 1);
            }
        };
    }

}
