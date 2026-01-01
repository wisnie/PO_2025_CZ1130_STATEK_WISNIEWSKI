package agh.darwin.model;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public record Genotype(List<Integer> genes) implements Iterable<Integer> {
    public static Genotype randomGenotype(int n) {
        return new Genotype(new Random()
                .ints(n, 0, 8)
                .boxed()
                .toList());
    }

    @Override
    public Iterator<Integer> iterator() {
        return genes.iterator();
    }
}
