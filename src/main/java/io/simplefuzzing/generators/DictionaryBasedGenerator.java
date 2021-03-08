package io.simplefuzzing.generators;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DictionaryBasedGenerator extends RandomGenerator<String> {

    private final String[] dictionary;
    private final int maxTerms;

    public DictionaryBasedGenerator(Set<String> dictionary, int maxTerms, Random random) {
        super(random);

        this.dictionary = dictionary.toArray(String[]::new);
        this.maxTerms = maxTerms;
    }

    @Override
    public String get() {
        return IntStream
                .range(0, random.nextInt(maxTerms))
                .map(i -> random.nextInt(dictionary.length))
                .mapToObj(i -> dictionary[i])
                .collect(Collectors.joining())
                ;
    }
}
