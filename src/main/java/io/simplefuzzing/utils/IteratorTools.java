package io.simplefuzzing.utils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class IteratorTools {

    private IteratorTools() {}

    public static <T> Iterator<T> times(int count, Iterator<T> iterator) {
        return new Iterator<T>() {
            int counter = count;
            @Override
            public boolean hasNext() {
                return iterator.hasNext() && counter > 0;
            }

            @Override
            public T next() {
                counter--;
                return iterator.next();
            }
        };
    }

    @SafeVarargs
    public static <T> Iterator<T> concat(Iterator<T>... iterators) {
        return new Iterator<T>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                while(index < iterators.length && !iterators[index].hasNext()) {
                    index++;
                }
                return index < iterators.length;
            }

            @Override
            public T next() {
                return iterators[index].next();
            }
        };
    }
}

