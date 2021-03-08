package io.simplefuzzing;

public class Branch {

    public final int from;
    public final int to;

    public Branch(int to, int from) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return from + " --> " + to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;

        if (from != branch.from) return false;
        return to == branch.to;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        return result;
    }

}
