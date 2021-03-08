package io.simplefuzzing.coverage;


import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Predicate;

public class InstrumentationLoader extends ClassLoader {

    private Instrumentation instrumentation;
    private final Predicate<String> classPath;

    public InstrumentationLoader(ClassLoader parent, Instrumentation instrumentation) {
        this(parent, instrumentation, x -> true);
    }

    public InstrumentationLoader(ClassLoader parent, Instrumentation instrumentation, Predicate<String> classPath) {
        super(parent);
        setInstrumentation(instrumentation);
        Objects.requireNonNull(classPath, "Class path definition should not be null");
        this.classPath = classPath;
    }

    private void setInstrumentation(Instrumentation instrumentation) {
        Objects.requireNonNull(instrumentation, "Instrumentation can not be null");
        this.instrumentation = instrumentation;
    }

    InputStream tryGetStream(String name) {
        String resourceLocation = name.replace('.', '/');
        InputStream result = getResourceAsStream(resourceLocation);
        if(result == null) {
            result = getResourceAsStream(resourceLocation + ".class");
        }
        return result;
    }

    byte[] tryGetCode(String name) {
        InputStream stream = tryGetStream(name);
        if(stream == null)
            return null;
        try {
            return stream.readAllBytes();
        } catch (IOException exc) {
            return null;
        }
    }

//    protected Class<?> tryToInstrument(String name) {
//        InputStream stream = tryGetStream(name);
//        if (stream == null)
//            return null;
//        byte[] originalCode;
//        try {
//            originalCode = stream.readAllBytes();
//        } catch (IOException e) {
//            return null;
//        }
//        byte[] instrumentedCode = instrumentation.instrument(originalCode);
//        return define(name, instrumentedCode);
//    }

    private boolean isSystem(String name) {
        return name.startsWith("java.") || name.startsWith("javax.");
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if(isSystem(name) || !classPath.test(name)) {
                return super.loadClass(name, resolve);
            }
            Class<?> classToReturn = findLoadedClass(name);
            if (classToReturn != null) {
                return classToReturn;
            }
            byte[] code = tryGetCode(name);
            if (code == null) {
                return super.loadClass(name, resolve);
            }
            if (instrumentation.shouldInstrument(name)) {
                code = instrumentation.instrument(code);
            }
            classToReturn = define(name, code);
            if (resolve) {
                resolveClass(classToReturn);
            }
            return classToReturn;
        }
    }

    public Class<?> define(String name, byte[] code) {
        return defineClass(name, code, 0, code.length);
    }

    public Class<?> loadClass(Class<?> classToLoad) {
        try {
            return loadClass(classToLoad.getName(), false);
        } catch (ClassNotFoundException e) {
            throw new ClassReloadingException(classToLoad, e);
        }
    }
}
