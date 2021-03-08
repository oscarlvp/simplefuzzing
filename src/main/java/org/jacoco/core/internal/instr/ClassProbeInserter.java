package org.jacoco.core.internal.instr;


import org.jacoco.core.internal.flow.ClassProbesVisitor;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassProbeInserter extends ClassProbesVisitor {

    private String className;
    private int probeCount;

    private final String probeClassName;

    public ClassProbeInserter(String probeClassName, final ClassVisitor cv) {
        super(cv);
        //TODO: Validate
        this.probeClassName = probeClassName;
    }

    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public MethodProbesVisitor visitMethod(final int access, final String name,
                                           final String desc, final String signature,
                                           final String[] exceptions) {

        final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) { return null; }
        final MethodVisitor frameEliminator = new DuplicateFrameEliminator(mv);
        final MethodProbeInserter probeVariableInserter = new MethodProbeInserter(probeClassName, access, name, desc, frameEliminator);
        return new MethodInstrumenter(probeVariableInserter, probeVariableInserter);
    }

    @Override
    public void visitTotalProbeCount(final int count) {
        probeCount = count;
    }

    public String getClassName() {
        return className;
    }

    public int getProbeCount() {
        return probeCount;
    }
}