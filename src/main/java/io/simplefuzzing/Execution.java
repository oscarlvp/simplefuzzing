package io.simplefuzzing;

public class Execution {

    private final ExecutionPath path;
    private final Throwable error;

    public Execution(Throwable error) {
        this(ExecutionPath.EMPTY, error);
    }

    public Execution(ExecutionPath path, Throwable error) {
        this.path = path == null? ExecutionPath.EMPTY:path;
        this.error = error;
    }

    public boolean hasError() { return error != null; }

    public boolean hasPath() { return !path.isEmpty(); }

    public ExecutionPath getPath() {
        return path;
    }

    public Throwable getError() {
        if(error == null)
            throw new NullPointerException("No detected error in execution");
        return error;
    }
}
