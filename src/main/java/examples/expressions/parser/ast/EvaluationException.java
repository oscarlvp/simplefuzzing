package examples.expressions.parser.ast;

public class EvaluationException extends Exception {

    public EvaluationException(String message) { super(message); }

    public EvaluationException(String message, Throwable cause) { super(message, cause); }

}
