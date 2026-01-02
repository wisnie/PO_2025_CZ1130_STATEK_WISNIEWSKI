package agh.darwin.model;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class Genotype implements Iterable<Integer> {
    private final List<Integer> genes;
    private final int genomeLength;
    private int activeGeneIndex;

    public Genotype(List<Integer> genes) {
        this.genes = genes;
        this.genomeLength = genes.size();
        this.activeGeneIndex = 0;
    }

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

    public int activeGene() {
        return genes.get(activeGeneIndex);
    }

    public void setActiveGene(int geneIndex) {
        activeGeneIndex = geneIndex;
    }

    public void nextGene() {
        activeGeneIndex = (activeGeneIndex + 1) % genomeLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Genotype) obj;
        return Objects.equals(this.genes, that.genes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genes);
    }

    @Override
    public String toString() {
        return "Genotype[" +
                "genes=" + genes + ']';
    }

}
