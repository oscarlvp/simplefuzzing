package io.simplefuzzing;

public class SeedInfo<T> {

    public final T input;
    public final ExecutionPath path;

    public SeedInfo(T input, ExecutionPath path) {
        this.input = input;
        this.path = path;
    }
}
