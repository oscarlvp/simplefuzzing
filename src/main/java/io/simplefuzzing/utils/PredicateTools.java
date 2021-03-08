package io.simplefuzzing.utils;

import java.util.function.Predicate;

public class PredicateTools {

    private PredicateTools() {}

    public static <T> Predicate<T> and(Predicate<T> first, Predicate<T> second) {
        return x -> first.test(x) && second.test(x);
    }


    public static <T> Predicate<T> or(Predicate<T> first, Predicate<T> second) {
        return x -> first.test(x) || second.test(x);
    }
}
