package io.simplefuzzing.generators;

import java.util.Random;

public class ByteArrayGenerator extends RandomGenerator<byte[]> {


    private final int maxLength;

    public ByteArrayGenerator(int maxLength, Random random) {
        super(random);

        if(maxLength < 0) throw new IllegalArgumentException("Maximum length can not be negative");
        this.maxLength = maxLength;
    }

    @Override
    public byte[] get() {
        byte[] result = new byte[random.nextInt(maxLength)];
        random.nextBytes(result);
        return result;
    }
}
