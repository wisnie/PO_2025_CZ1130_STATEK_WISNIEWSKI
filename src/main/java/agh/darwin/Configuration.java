package agh.darwin;

import agh.darwin.model.GridMap;

public record Configuration(
    MapDimensions mapDimensions,
    MapVariant mapVariant,
    PlantParameters plantParams,
    AnimalParameters animalParams,
    GenotypeParameters genotypeParams
) {
    public record MapDimensions(int width, int height) {}

    public record MapVariant(GridMap mapVariant) {}

    public record PlantParameters(int plantsPerDay, int plantEnergy) {}

    public record AnimalParameters(int startingAnimals,
                                   int energyUponBirth,
                                   int dailyEnergyConsumption,
                                   int reproductionThreshold,
                                   int reproductionCost) {}

    public record GenotypeParameters(int minimumMutations,
                                     int maximumMutations,
                                     int genomeLength) {}
}
