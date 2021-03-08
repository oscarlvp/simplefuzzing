package io.simplefuzzing.coverage;

public interface Instrumentation {

    default boolean shouldInstrument(String name) { return true; }

    byte[] instrument(byte[] code);

}
