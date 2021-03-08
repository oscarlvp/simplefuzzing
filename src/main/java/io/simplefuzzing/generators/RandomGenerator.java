package io.simplefuzzing.generators;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RandomGenerator<T> implements Supplier<T>, Iterator<T> {

    protected final Random random;

    public RandomGenerator(Random random) {
        Objects.requireNonNull(random, "Randomness source can not be null");
        this.random = random;
    }

    @Override
    public boolean hasNext() { return true; }

    @Override
    public T next() { return get(); }

    public List<T> next(int count) {
        if(count < 1) throw new IllegalArgumentException("Can not generate less than one value");
        return IntStream.range(0, count).mapToObj(i -> next()).collect(Collectors.toList());
    }

}