package io.simplefuzzing;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExecutionPath {

    public static final ExecutionPath EMPTY = new ExecutionPath(new int[0]);

    private final int[] nodes;

    public ExecutionPath(int[] nodes) {
        Objects.requireNonNull(nodes);
        this.nodes = Arrays.copyOf(nodes, nodes.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(nodes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nodes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutionPath path = (ExecutionPath) o;

        return Arrays.equals(nodes, path.nodes);
    }

    public boolean isEmpty() {
        return nodes.length == 0;
    }

    public Stream<Branch> branchSequence() {
        return IntStream.range(0, nodes.length - 1).mapToObj(i -> new Branch(nodes[i], nodes[i+1]));
    }

    public Set<Branch> branches() {
        return branchSequence().collect(Collectors.toSet());
    }

    public Set<Integer> blocks() {
        return IntStream.of(nodes).boxed().collect(Collectors.toSet());
    }

    public static Set<Integer> allBlocks(Collection<ExecutionPath> paths) {
        return paths.stream().map(ExecutionPath::blocks).flatMap(Set::stream).collect(Collectors.toSet());
    }

    public static Set<Branch> allBranches(Collection<ExecutionPath> paths) {
        return paths.stream().map(ExecutionPath::branches).flatMap(Set::stream).collect(Collectors.toSet());
    }

}
