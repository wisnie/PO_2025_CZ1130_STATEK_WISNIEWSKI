package agh.darwin.model;

import java.util.function.Supplier;

public interface AnimalAccessStatistics {
    int getAge();
    int getOffspringCount();
    int getPlantsEaten();
    int getEnergyAmount();
    int getActiveGene();
    String getGenotype();
    @Override
    String toString();
    String getDeathDay();
}