package io.simplefuzzing.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

public class ClassSelectors {

    private ClassSelectors() {}

    public static Predicate<String> select(String... names) {
        Set<String> targets = Set.of(names);
        return targets::contains;
    }

    public static Predicate<String> select(Class<?>... classes) {
        return select(Arrays.stream(classes).map(Class::getName).toArray(String[]::new));
    }

    public static Predicate<String> selectClass(String name) {
        return (str) -> str.equals(name);
    }

    public static Predicate<String> selectClass(Class<?> type) {
        return selectClass(type.getName());
    }

    public static Predicate<String> selectPackage(String packageName) {
        return (str) -> str.startsWith(packageName + ".");
    }


}
