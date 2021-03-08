package io.simplefuzzing.coverage;

import io.simplefuzzing.Execution;
import io.simplefuzzing.ExecutionMonitor;
import io.simplefuzzing.ExecutionPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;

import static io.simplefuzzing.utils.ClassSelectors.selectClass;
import static io.simplefuzzing.utils.PredicateTools.or;

public class PathExecutionMonitor<T> implements ExecutionMonitor<T> {

    private final CoverageInstrumentation instrumentation;
    private final InstrumentationLoader loader;
    private final Class<? extends Executable<T>> executableClass;

    public PathExecutionMonitor(Predicate<String> condition, Class<? extends Executable<T>> executable, Predicate<String> classPath) {
        //TODO: Validate executable having a default public constructor
        instrumentation = new CoverageInstrumentation(condition, PathObserverProbe.class);
        loader = new InstrumentationLoader(getClass().getClassLoader(), instrumentation, or(classPath, selectClass(executable)));
        this.executableClass = (Class<? extends Executable<T>>) loader.loadClass(executable);
    }

    public Execution execute(T input) {
        try {
            PathObserverProbe.init();
            Constructor<?> constructor = executableClass.getConstructor();
            Executable<T> executable = (Executable<T>) constructor.newInstance();
            Throwable error = null;
            try {
                executable.execute(input);
            } catch (Throwable exc) {
                error = exc;
            }
            return new Execution(new ExecutionPath(PathObserverProbe.path()), error);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exc) {
            throw new CannotExecuteException(executableClass, exc);
        }
    }

    public int getBlocks() { return instrumentation.getProbeCount(); }
}
