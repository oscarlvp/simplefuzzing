package io.simplefuzzing.generators;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import static io.simplefuzzing.utils.RandomTools.pick;

public class RandomStringGenerator extends RandomGenerator<String> {


    private final int minLength;
    private final int maxLength;
    private final char[] alphabet;

    public RandomStringGenerator(int minLength, int maxLength, char[] alphabet, Random random) {
        super(random);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.alphabet = alphabet;

    }

    @Override
    public String get() {
        int length = random.nextInt(maxLength - minLength);
        int[] selection = random.ints(length, 0, alphabet.length).toArray();
        char[] buffer = new char[length];
        for(int i = 0; i < length; i++) {
            buffer[i] = alphabet[selection[i]];
        }
        return new String(buffer);
    }

    public static Builder strings() {
        return new Builder();
    }

    static char[] toAlphabet(Charset charset) {
        CharsetEncoder encoder = charset.newEncoder();
        ArrayList<Character> characters = new ArrayList<>();
        for(int i = 0; i < Character.MAX_CODE_POINT; i++) {
            if(encoder.canEncode((char)i))
                characters.add((char)i);
        }
        char[] alphabet = new char[characters.size()];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = characters.get(i);
        }
        return alphabet;
    }

    public static class Builder {

        private Random random = new Random();
        private char[] alphabet = toAlphabet(StandardCharsets.UTF_8);
        private int minLength = 0;
        private int maxLength = 100;

        public Builder fromCharset(Charset charset) {
            this.alphabet = toAlphabet(charset);
            return this;
        }

        public Builder fromAlphabet(String alphabet) {
            this.alphabet = alphabet.toCharArray();
            return this;
        }

        public Builder withLengthsBetween(int min, int max) {
            this.minLength = min;
            this.maxLength = max;
            return this;
        }

        public Builder withRandom(Random random) {
            this.random = random;
            return this;
        }

        public RandomStringGenerator generator() {
            return new RandomStringGenerator(minLength, maxLength, alphabet, random);
        }



    }

}