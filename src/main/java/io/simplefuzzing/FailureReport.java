package io.simplefuzzing;

import java.util.Objects;

public class FailureReport<T> {

    public final T input;
    public final Throwable failure;

    public FailureReport(T input, Throwable failure) {
        this.input = input;
        Objects.requireNonNull(failure, "Failure report must have an error");
        this.failure = failure;
    }
}
