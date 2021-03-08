package io.simplefuzzing;

import java.util.*;

public abstract class Fuzzer<T> {

    protected final ExecutionMonitor<T> monitor;
    protected final List<FailureReport<T>> failures = new ArrayList<>();
    protected final Set<ExecutionPath> paths = new HashSet<>();

    public Fuzzer(ExecutionMonitor<T> monitor) {
        Objects.requireNonNull(monitor, "Execution monitor can not be null");
        this.monitor = monitor;
    }

    protected abstract T nextInput();

    protected void afterExecution(T input, Execution execution) {
        //Do nothing by default
    }

    public void fuzz() {
        T input = nextInput();
        Execution result = monitor.execute(input);
        if(result.hasPath()) {
            paths.add(result.getPath());
        }
        if(result.hasError()) {
            failures.add(new FailureReport<>(input, result.getError()));
        }
        afterExecution(input, result);
    }

    public void fuzz(int times) {
        for (int i = 0; i < times; i++) {
            fuzz();
        }
    }

    public boolean detectedAnyFailure() {
        return !failures.isEmpty();
    }

    public List<FailureReport<T>> getDetectedFailures() {
        return Collections.unmodifiableList(failures);
    }

    public Set<ExecutionPath> getObservedPaths() {
        return Collections.unmodifiableSet(paths);
    }
}