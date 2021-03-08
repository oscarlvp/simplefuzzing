package examples.metaphone;

import io.simplefuzzing.coverage.Executable;
import org.apache.commons.codec.language.Metaphone;

public class MetaphoneTest implements Executable<String> {

    public MetaphoneTest() {}

    @Override
    public void execute(String input) throws Throwable {
        Metaphone encoder = new Metaphone();
        encoder.metaphone(input);
    }
}
