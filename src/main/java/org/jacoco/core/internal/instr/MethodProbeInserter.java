package org.jacoco.core.internal.instr;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.InstructionAdapter;

public class MethodProbeInserter extends MethodVisitor implements IProbeInserter {


    // For the moment we are wiring the execution to fuzz.Probe
    private final String probeClassName;
    private static final String BLOCK = "block";

    public MethodProbeInserter(
            String probeClassName,
            final int access, final String name, final String desc,
            final MethodVisitor mv) {
        super(InstrSupport.ASM_API_VERSION, mv);

        //TODO: Validate
        this.probeClassName = probeClassName.replace('.', '/');

    }

    public void insertProbe(final int id) {
        InstructionAdapter adapter = new InstructionAdapter(mv);
        adapter.iconst(id);
        adapter.invokestatic(probeClassName, BLOCK, "(I)V", false);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        int increasedStack = maxStack + 1;//?? Check the size of the stack for the probe
        mv.visitMaxs(increasedStack, maxLocals);
    }

}