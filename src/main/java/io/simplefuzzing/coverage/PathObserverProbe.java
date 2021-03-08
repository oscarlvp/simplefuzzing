package io.simplefuzzing.coverage;

import java.util.ArrayList;
import java.util.Arrays;

public class PathObserverProbe {

    private static ArrayList<Integer> nodes;

    public final static int DEFAULT_CAPACITY = 1000;

    public static void init() { init(DEFAULT_CAPACITY); }

    public static void init(int capacity) {
        nodes = new ArrayList<>(capacity);
    }

    public static void block(int id) {
        nodes.add(id);
    }

    public static int[] path() {
        int[] result = new int[nodes.size()];
        Arrays.setAll(result, nodes::get);
        return result;
    }

}
