package io.simplefuzzing.coverage;

public class CannotExecuteException extends RuntimeException {
    public CannotExecuteException(Class<?> executable, Exception exc) {
        super("Could not execute class " + executable.getName(), exc);
    }
}
