package io.simplefuzzing.mutations;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.UnaryOperator;

import static io.simplefuzzing.utils.RandomTools.pick;

public class RandomMutation<T> implements UnaryOperator<T> {

    private final Random random;
    private final int min, max;
    private final UnaryOperator<T>[] mutations;

     @SafeVarargs
     private RandomMutation(Random random, int min, int max, UnaryOperator<T>... mutations) {
        this.random = random;
        this.min = min;
        this.max = max;
        this.mutations = mutations;
    }

    @Override
    public T apply(T input) {
        int numberOfMutations = min + random.nextInt(max - min + 1);
        for(int i = 0; i < numberOfMutations; i++) {
            input = pick(mutations, random).apply(input);
        }
        return input;
    }

    @SafeVarargs
    public static <T> Builder<T> apply(UnaryOperator<T>... mutations) {
         return new Builder<>(mutations);
    }

    public static class Builder<T> {
        private int min = 1, max = 5;
        private UnaryOperator<T>[] mutations;

        @SafeVarargs
        public Builder(UnaryOperator<T>... mutations) {
            Objects.requireNonNull(mutations, "Mutation operators array can not be null");
            if(mutations.length == 0)
                throw new IllegalArgumentException("There should be at least one mutation operator");
            this.mutations = Arrays.copyOf(mutations, mutations.length);
        }

        public Builder<T> atLeast(int min) {
            if(min <= 0)
                throw new IllegalArgumentException("Minimum number of mutations should be at least 1");
            this.min = min;
            return this;
        }

        public Builder<T> atMost(int max) {
            if(max < min)
                throw new IllegalArgumentException("Maximum number of mutations should be at least " + min);
            this.max = max;
            return this;
        }

        public RandomMutation<T> using(Random random) {
            Objects.requireNonNull(random, "Pseudo-random number generator can not be null");
            return new RandomMutation<>(random, min, max, mutations);
        }
    }

}
