package io.simplefuzzing.coverage;

import org.jacoco.core.internal.flow.ClassProbesAdapter;
import org.jacoco.core.internal.instr.ClassProbeInserter;
import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Predicate;

public class CoverageInstrumentation implements Instrumentation {

    private final Predicate<String> condition;
    private final String probeClassName;
    private int probeCount = 0;

    public CoverageInstrumentation(Predicate<String> condition, Class<?> probe) {
        Objects.requireNonNull(condition, "Predicate to select class instrumentation must not be null");
        Objects.requireNonNull(probe, "Probe class can not be null");
        if(!isProbeClass(probe))
            throw new IllegalArgumentException( probe.getName() + " is not public or does not contain a public static method block(int)");
        this.condition = condition;
        this.probeClassName = probe.getName();
    }

    @Override
    public boolean shouldInstrument(String name) {
        return condition.test(name);
    }

    @Override
    public byte[] instrument(byte[] code) {
        final ClassReader reader = InstrSupport.classReaderFor(code);
        final ClassWriter writer = new ClassWriter(reader, 0) {
            @Override
            protected String getCommonSuperClass(final String type1,
                                                 final String type2) {
                throw new IllegalStateException();
            }
        };
        ClassProbeInserter inserter = new ClassProbeInserter(probeClassName, writer);
        final int version = InstrSupport.getMajorVersion(reader);
        final ClassVisitor visitor = new ClassProbesAdapter(inserter, InstrSupport.needsFrames(version));
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        probeCount += inserter.getProbeCount();
        return writer.toByteArray();
    }

    public static boolean isProbeClass(Class<?> type) {
        // A probe class is just any class with a static public void method \
        // named block that receives a single integer
        try {
            if(!Modifier.isPublic(type.getModifiers())) {
                return false;
            }
            Method requiredMethod = type.getMethod("block", int.class);
            int modifiers = requiredMethod.getModifiers();
            return requiredMethod.getReturnType().equals(void.class)
                    && Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    ;
        } catch (NoSuchMethodException exc) {
            return false;
        }
    }

    public int getProbeCount() { return probeCount; }
}
