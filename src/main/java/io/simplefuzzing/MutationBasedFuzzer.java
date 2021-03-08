package io.simplefuzzing;

import java.util.*;
import java.util.function.UnaryOperator;

public class MutationBasedFuzzer<T> extends Fuzzer<T> {

    private final List<T> originalSeeds;
    private final UnaryOperator<T> mutation;
    private int index = -1;

    protected final Random random;
    protected final List<SeedInfo<T>> pool;

    public MutationBasedFuzzer(ExecutionMonitor<T> monitor,
                               Collection<T> seeds,
                               UnaryOperator<T> mutation,
                               Random random) {
        super(monitor);

        Objects.requireNonNull(seeds, "Seed collection can not be null");
        if(seeds.isEmpty()) {
            throw new IllegalArgumentException("Seed collection can not be empty");
        }
        Objects.requireNonNull(mutation, "Input mutation can not be null");
        Objects.requireNonNull(random, "Pseudo-random number generator can not be null");

        originalSeeds = new ArrayList<>(seeds);
        pool = new ArrayList<>();
        this.mutation = mutation;
        this.random = random;

    }

    protected boolean seeding() { return index < originalSeeds.size(); }

    private void advance() { index = Math.min(originalSeeds.size(), ++index); }

    @Override
    protected T nextInput() {
        advance();
        if(seeding()) {
            return originalSeeds.get(index);
        }
        return mutation.apply(pool.get(selectSeedIndex()).input);
    }

    protected int selectSeedIndex () {
        return random.nextInt(pool.size());
    }

    protected boolean shouldBecomeSeed(T input, Execution execution) {
        return seeding();
    }

    @Override
    protected void afterExecution(T input, Execution execution) {
        if(execution.hasError()) return;
        if(shouldBecomeSeed(input, execution)) {
            pool.add(new SeedInfo<>(input, execution.getPath()));
        }
    }
}
