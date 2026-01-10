package agh.darwin.model;

import java.util.*;

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

    public static Genotype childGenotype(Animal weaker, Animal stronger) {
        List<Integer> childGenes = new ArrayList<>();
        List<Integer> strongerGenes = stronger.getGenotype().genes;
        List<Integer> weakerGenes = weaker.getGenotype().genes;
        int strongerEnergy = stronger.getEnergy().amount();
        int weakerEnergy = weaker.getEnergy().amount();
        int genomeLength = strongerGenes.size();
        int genesFromStronger = (int) ((double) strongerEnergy / (weakerEnergy + strongerEnergy) * genomeLength);
        final Random random = new Random();
        boolean strongerLeftSide = random.nextBoolean();
        if (strongerLeftSide) {
            childGenes.addAll(strongerGenes.subList(0, genesFromStronger));
            childGenes.addAll(weakerGenes.subList(genesFromStronger, genomeLength));
        } else {
            childGenes.addAll(weakerGenes.subList(0, genomeLength - genesFromStronger));
            childGenes.addAll(strongerGenes.subList(genomeLength - genesFromStronger, genomeLength));
        }
        int mutationGeneIndex = random.nextInt(childGenes.size());
        int newGene = (childGenes.get(mutationGeneIndex) + random.nextInt(1,8)) % 8;
        childGenes.set(mutationGeneIndex, newGene);
        return new Genotype(childGenes);

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
