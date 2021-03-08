package examples.base64;

import io.simplefuzzing.BlackboxFuzzer;
import io.simplefuzzing.FailureReport;
import io.simplefuzzing.coverage.PathExecutionMonitor;
import io.simplefuzzing.generators.ByteArrayGenerator;
import org.apache.commons.codec.binary.Base64;

import java.util.Random;

import static io.simplefuzzing.ExecutionPath.allBlocks;
import static io.simplefuzzing.utils.ClassSelectors.selectPackage;
import static io.simplefuzzing.utils.ClassSelectors.selectClass;

public class Base64Main {

    public static void main(String[] args) {

        Random random = new Random();
        ByteArrayGenerator generator = new ByteArrayGenerator(100, random);
        PathExecutionMonitor<byte[]> monitor = new PathExecutionMonitor<>(
                selectClass(Base64.class),
                Base64Test.class,
                selectPackage("org.apache.commons.codec"));
        BlackboxFuzzer<byte[]> fuzzer = new BlackboxFuzzer<>(generator, monitor);
        fuzzer.fuzz(10000);
        if(fuzzer.detectedAnyFailure()) {
            for (FailureReport<byte[]> failure : fuzzer.getDetectedFailures()) {
                System.out.println(failure.failure.getMessage());
            }
            return;
        }

        System.out.printf("Covered %d/%d\n", allBlocks(fuzzer.getObservedPaths()).size(), monitor.getBlocks());
    }

}
