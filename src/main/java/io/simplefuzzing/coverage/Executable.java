package io.simplefuzzing.coverage;

@FunctionalInterface
public interface Executable<T> {

    void execute(T input) throws Throwable;

}
