package io.simplefuzzing;

import java.util.Objects;
import java.util.function.Supplier;

public class BlackboxFuzzer<T> extends Fuzzer<T> {

    private final Supplier<T> inputs;
    public BlackboxFuzzer(Supplier<T> inputs, ExecutionMonitor<T> monitor) {
        super(monitor);
        Objects.requireNonNull(inputs, "Input supplier can not be null");
        this.inputs = inputs;
    }

    @Override
    protected T nextInput() {
        return inputs.get();
    }
}
