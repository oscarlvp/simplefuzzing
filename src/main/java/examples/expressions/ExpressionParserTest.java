package examples.expressions;

import examples.expressions.parser.ExpressionParser;
import examples.expressions.parser.ParsingException;
import io.simplefuzzing.coverage.Executable;

public class ExpressionParserTest implements Executable<String> {

    @Override
    public void execute(String input) throws Throwable {
        try {
            ExpressionParser parser = new ExpressionParser();
            parser.parse(input);
        } catch (ParsingException exc) {
            // Do nothing
        }
    }

}
