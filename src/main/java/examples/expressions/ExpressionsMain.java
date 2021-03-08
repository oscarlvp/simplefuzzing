package examples.expressions;

import examples.expressions.parser.ExpressionParser;
import io.simplefuzzing.*;
import io.simplefuzzing.coverage.PathExecutionMonitor;
import io.simplefuzzing.generators.DictionaryBasedGenerator;
import io.simplefuzzing.generators.RandomStringGenerator;
import io.simplefuzzing.mutations.RandomMutation;
import io.simplefuzzing.utils.RandomTools;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static examples.Experiments.averageAmongExecutions;
import static examples.Experiments.xticks;
import static io.simplefuzzing.generators.RandomStringGenerator.strings;
import static io.simplefuzzing.mutations.RandomMutation.apply;
import static io.simplefuzzing.mutations.RandomStringMutation.*;
import static io.simplefuzzing.utils.ClassSelectors.selectClass;
import static io.simplefuzzing.utils.ClassSelectors.selectPackage;
import static io.simplefuzzing.utils.RandomTools.pick;

public class ExpressionsMain {

    static Set<String> dictionary = Set.of("pow", "min", "sin", "cos", "1", ".5", "1e-5", "(", ")", "+", "-", "*", "/", "PI", "E", " ");

    static String alphabet = " \tabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+-*/(),.!@;[]{}";

    static List<String> seeds = List.of(" ", "1", "1 + 2", "min(1, 2)", "-1");

    static int SEED = 1000;
    static int MAX_LENGTH = 100;
    static int MAX_TERMS = 10;
    static int INPUTS = 200;
    static int TRIALS = 30;

    public static void main(String[] args) throws IOException {
        System.out.println("Blackbox fuzzing");
        blackboxExperiment();
        System.out.println("Fuzing with an empty seed");
        seedingExperiment(List.of(" "), "expressions-seeding-empty");
        System.out.println("Fuzzing with selected seeds");
        seedingExperiment(seeds, "expression-valid-seeds");
    }

    static void blackboxExperiment() throws IOException {
        double[] x = xticks(INPUTS);
        XYChart chart = createChart();
        System.out.println("Random");
        chart.addSeries("Random",  x, series(ExpressionsMain::createBlackBox));
        System.out.println("Dictionary");
        chart.addSeries("Dictionary",x, series(ExpressionsMain::createDictionaryBased));
        System.out.println("Grammar");
        chart.addSeries("Grammar",   x, series(ExpressionsMain::createGrammarBased));
        System.out.println("Mutation");
        chart.addSeries("Mutation", x, series(() -> createMutationBased(seeds)));
        BitmapEncoder.saveBitmap(chart, "./expressions-blackbox", BitmapEncoder.BitmapFormat.PNG);
        saveCSV("blackbox.csv",
                averageAmongExecutions(ExpressionsMain::createDictionaryBased, INPUTS, TRIALS),
                averageAmongExecutions(ExpressionsMain::createDictionaryBased, INPUTS, TRIALS),
                averageAmongExecutions(ExpressionsMain::createGrammarBased, INPUTS, TRIALS)
        );
    }

    static void seedingExperiment(Collection<String> seeds, String name) throws IOException {
        double[] x = xticks(INPUTS);
        XYChart chart = createChart();
        chart.addSeries("Mutation", series(() -> createMutationBased(seeds)));
        chart.addSeries("Greybox",x, series(() -> createGreybox(seeds)));
        chart.addSeries("Power schedule", x, series(() -> createPowerSchedule(seeds)));
        BitmapEncoder.saveBitmap(chart, "./" + name, BitmapEncoder.BitmapFormat.PNG);
        saveCSV(name + ".csv",
                averageAmongExecutions(() -> createMutationBased(seeds), INPUTS, TRIALS),
                averageAmongExecutions(() -> createGreybox(seeds), INPUTS, TRIALS),
                averageAmongExecutions(() -> createPowerSchedule(seeds), INPUTS, TRIALS)
        );
    }

    static double[] series(Supplier<Fuzzer<?>> supplier) {
        return averageAmongExecutions(supplier, INPUTS, TRIALS);
    }

    static XYChart createChart() {
        XYChart chart = new XYChartBuilder().width(800).height(600).xAxisTitle("Inputs").yAxisTitle("Branches").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line)
                .setXAxisMin(0d)
                .setXAxisMax((double) INPUTS)
                .setPlotMargin(0)
                .setPlotContentSize(.95)
                .setSeriesMarkers(new Marker[]{SeriesMarkers.NONE})
                .setChartBackgroundColor(Color.WHITE)
        ;
        return chart;
    }

    static void saveCSV(String path, double[]... values) throws FileNotFoundException {
        try(PrintWriter writer = new PrintWriter(new File(path))) {
            int rows = values[0].length;
            for(int row = 0; row < rows; row++) {
                StringBuilder builder = new StringBuilder();
                for(int column = 0; column < values.length; column++) {
                    builder.append(values[column][row]);
                    builder.append(',');
                }
                writer.println(builder.toString());
            }
        }
    }

    static PathExecutionMonitor<String> monitor() {
        return new PathExecutionMonitor<>(
                selectClass(ExpressionParser.class),
                ExpressionParserTest.class,
                selectPackage("examples.expressions.parser")
        );
    }

    static Random random() { return new Random(SEED); }

    static Fuzzer<String> createBlackBox() {
        Random random = new Random(SEED);
        RandomStringGenerator generator = strings()
                .fromAlphabet(alphabet)
                .withLengthsBetween(0, MAX_LENGTH)
                .withRandom(random)
                .generator();
        return new BlackboxFuzzer<>(generator, monitor());
    }

    static Fuzzer<String> createDictionaryBased() {
        Random random = new Random(SEED);
        DictionaryBasedGenerator generator = new DictionaryBasedGenerator(dictionary, MAX_TERMS, random);
        return new BlackboxFuzzer<>(generator, monitor());
    }

    static Fuzzer<String> createGrammarBased() {
        Random random = new Random(SEED);
        RandomValidExpression generator = new RandomValidExpression(MAX_TERMS, random);
        return new BlackboxFuzzer<>(generator, monitor());
    }

    static UnaryOperator<String> mutation(Random random) {
        return apply(
                removeChar(random),
                replaceCharFromAlphabet(alphabet, random),
                addCharFromAlphabet(alphabet, random)
        ).atMost(5).using(random);
    }

    static Fuzzer<String> createMutationBased(Collection<String> seeds) {
        return new MutationBasedFuzzer<>(monitor(), seeds, mutation(random()), random());
    }

    static Fuzzer<String> createGreybox(Collection<String> seeds) {
        return new GreyboxFuzzer<>(seeds, mutation(random()), random(), monitor());
    }

    static Fuzzer<String> createPowerSchedule(Collection<String> seeds) {
        return new PowerScheduleFuzzer<>(seeds, mutation(random()), random(), monitor());
    }

}
