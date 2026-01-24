package agh.darwin.model;

public record SimulationStatistics(long aliveNumber, long plantNumber, long freeFields, Genotype mostPopularGenotype,
                                   double averageEnergy, double averageAge, double averageChildren) {

}
