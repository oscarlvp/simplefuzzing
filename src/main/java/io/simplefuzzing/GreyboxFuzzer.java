package io.simplefuzzing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.UnaryOperator;

public class GreyboxFuzzer<T> extends MutationBasedFuzzer<T> {

    private final Set<Branch> branches = new HashSet<>();
    public GreyboxFuzzer(Collection<T> seeds, UnaryOperator<T> mutation, Random random, ExecutionMonitor<T> monitor) {
        super(monitor, seeds, mutation, random);
    }

    @Override
    protected boolean shouldBecomeSeed(T input, Execution execution) {
        return execution.hasPath() && branches.addAll(execution.getPath().branches());
    }

}
