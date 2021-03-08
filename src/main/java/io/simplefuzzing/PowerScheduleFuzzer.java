package io.simplefuzzing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;

import static io.simplefuzzing.utils.RandomTools.pickFromDistribution;

public class PowerScheduleFuzzer<T> extends GreyboxFuzzer<T> {

    public PowerScheduleFuzzer(Collection<T> seeds, UnaryOperator<T> mutation, Random random, ExecutionMonitor<T> monitor) {
        super(seeds, mutation, random, monitor);
    }

    @Override
    protected int selectSeedIndex() {
        HashMap<Branch, Integer> frequency = edgeFrequency();
        ToDoubleFunction<Branch> edgeProbability = e -> frequency.get(e).doubleValue() / pool.size();
        ToDoubleFunction<ExecutionPath> pathProbability = (path) -> path.branches().stream().
                mapToDouble(edgeProbability).
                reduce(1.0, (x, y) -> x*y);
        ToDoubleFunction<SeedInfo<T>> energy = seed -> 1.0 / pathProbability.applyAsDouble(seed.path);

        double[] poolProbability = pool.stream().mapToDouble(energy).toArray();
        return pickFromDistribution(poolProbability, random);
    }

    private HashMap<Branch, Integer> edgeFrequency() {
        HashMap<Branch, Integer> counter = new HashMap<>();
        for (SeedInfo<T> info : pool) {
            for(Branch branch : info.path.branches()) {
                counter.put(branch, counter.getOrDefault(branch, 0) + 1);
            }
        }
        return counter;
    }
}
