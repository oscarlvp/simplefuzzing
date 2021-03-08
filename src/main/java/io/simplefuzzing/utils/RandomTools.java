package io.simplefuzzing.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomTools {

    public static <T> T pick(T[] values, Random random) {
        if(values == null || values.length == 0) throw new IllegalArgumentException("Can not pick a value from an empty array");
        return values[random.nextInt(values.length)];
    }

    public static char pick(char[] values, Random random) {
        if(values == null || values.length == 0) throw new IllegalArgumentException("Can not pick a char from an empty array");
        return values[random.nextInt(values.length)];
    }

    public static char pick(String values, Random random) {
        if(values == null || values.isEmpty()) throw new IllegalArgumentException("Can not pick a char from an empty String");
        return values.charAt(random.nextInt(values.length()));
    }

    public static int pick(int[] values, Random random) {
        if(values == null || values.length == 0) throw new IllegalArgumentException("Can not pick a value from an empty array");
        return values[random.nextInt(values.length)];
    }


    public static <T> T pick(List<T> values, Random random) {
        if(values == null || values.isEmpty()) throw new IllegalArgumentException("Can not pick a value from an empty list");
        return values.get(random.nextInt(values.size()));
    }

    public static boolean chanceOf(double probability, Random random) {
        return random.nextDouble() < probability;
    }

    public static int pickFromDistribution(double[] values, Random random) {
        double[] dist = normalize(values);
        double chance = random.nextDouble();
        int index = 0;
        while(chance > 0 && index < dist.length - 1) {
            chance -= dist[index++];
        }
        return index;
    }

    public static double[] normalize(double[] array) {
        double sum = Arrays.stream(array).sum() + 0.00001;
        double[] normalized = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            normalized[i] = array[i]/sum;
        }
        return normalized;
    }

}
