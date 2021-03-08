package examples;

import io.simplefuzzing.Fuzzer;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import static io.simplefuzzing.ExecutionPath.allBranches;

public class Experiments {

    public static  double[] averageAmongExecutions(Supplier<Fuzzer<?>> ctor, int inputs, int trials) {
        double[] result = new double[inputs + 1];
        for (int i = 1; i <= inputs; i++) {
            for (int j = 0; j < trials; j++) {
                Fuzzer<?> fuzzer = ctor.get();
                fuzzer.fuzz(i);
                result[i] += allBranches(fuzzer.getObservedPaths()).size();
            }
            result[i] /= trials;
        }
        return result;
    }

    public static double[] xticks(int inputs) {
       return IntStream.range(0, inputs + 1).asDoubleStream().toArray();
    }



}
