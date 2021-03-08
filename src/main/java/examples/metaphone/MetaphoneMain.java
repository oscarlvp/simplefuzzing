package examples.metaphone;

import io.simplefuzzing.BlackboxFuzzer;
import io.simplefuzzing.Fuzzer;
import io.simplefuzzing.coverage.PathExecutionMonitor;
import io.simplefuzzing.generators.RandomStringGenerator;
import org.apache.commons.codec.language.Metaphone;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

import static examples.Experiments.averageAmongExecutions;
import static io.simplefuzzing.generators.RandomStringGenerator.strings;
import static io.simplefuzzing.utils.ClassSelectors.selectClass;
import static io.simplefuzzing.utils.ClassSelectors.selectPackage;

public class MetaphoneMain {

    static int SEED = 1000;
    static int INPUTS = 100;
    static int TRIALS = 30;
    static int MAX_LENGTH = 100;

    public static void main(String[] args) throws IOException {
        double[] x = IntStream.range(0, INPUTS + 1).asDoubleStream().toArray();
        XYChart chart = QuickChart.getChart("", "Inputs", "Edges", "Blackbox",
                x, averageAmongExecutions(MetaphoneMain::createFuzzer, INPUTS, TRIALS));
        BitmapEncoder.saveBitmap(chart, "./metaphone", BitmapEncoder.BitmapFormat.PNG);
    }

    static Fuzzer<String> createFuzzer() {
        Random random = new Random(SEED);
        RandomStringGenerator generator = strings()
                .fromAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                .withLengthsBetween(0, MAX_LENGTH)
                .withRandom(random)
                .generator();
        PathExecutionMonitor<String> monitor = new PathExecutionMonitor<>(
                selectClass(Metaphone.class),
                MetaphoneTest.class,
                selectPackage("org.apache.commons.codec")
        );
        return new BlackboxFuzzer<>(generator, monitor);
    }

}
