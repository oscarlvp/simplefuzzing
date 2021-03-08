package io.simplefuzzing.coverage;

public class ClassReloadingException extends RuntimeException {
    public ClassReloadingException(Class<?> type, Throwable reason) {
        super("Can not reload class: " + type.getName(), reason);
    }
}
