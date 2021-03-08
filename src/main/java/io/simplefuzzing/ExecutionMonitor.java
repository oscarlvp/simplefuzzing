package io.simplefuzzing;

public interface ExecutionMonitor<T> {
    Execution execute(T input);
}
