package agh.darwin;

import agh.darwin.model.*;
import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.Vector2i;

import java.util.stream.IntStream;

public class Simulation {
    private final GridMap simulationMap = new BasicMap();
    private final Configuration configuration = new Configuration(
            new Configuration.MapDimensions(30, 20),
            new Configuration.MapVariant(null),
            new Configuration.PlantParameters(5, 10),
            new Configuration.AnimalParameters(10, 20, 5, 10, 8),
            new Configuration.GenotypeParameters(0, 2, 10)
    );

    void run() {
        FinitePositionGenerator positionGenerator = new FinitePositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.animalParams().startingAnimals());

        for (Vector2i position : positionGenerator) {
            Animal animal = new Animal(position, new Energy(configuration.animalParams().energyUponBirth()),
                    Genotype.randomGenotype(configuration.genotypeParams().genomeLength()));


        }
    }
}
