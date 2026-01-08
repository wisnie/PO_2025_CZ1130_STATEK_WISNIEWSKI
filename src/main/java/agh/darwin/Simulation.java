package agh.darwin;

import agh.darwin.model.*;
import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.Vector2i;

import java.util.HashMap;


public class Simulation {
    private final GridMap simulationMap;
    private final Configuration configuration;
    private final HashMap<Animal, AnimalStatistic> animalStatistics = new HashMap<>();

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        this.simulationMap = new BasicMap(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration);

        addStartingEntities();
    }

    void run() {
        for (int i = 0; i < 100; i++) {
            simulationMap.simulateDayProgression();
        }
    }

    void addStartingEntities() {
        FinitePositionGenerator positionGenerator = new FinitePositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.animalParams().startingAnimals());

        for (Vector2i position : positionGenerator) {
            Animal animal = new Animal(position, new Energy(configuration.animalParams().startingEnergy()),
                    Genotype.randomGenotype(configuration.genotypeParams().genomeLength()), configuration.animalParams());

            simulationMap.place(animal);
        }

        FinitePositionGenerator plantPositionGenerator = new FinitePositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.plantParams().plantsPerDay());

        for (Vector2i plantPosition : plantPositionGenerator) {
            Plant plant = new Plant(plantPosition, new Energy(configuration.plantParams().plantEnergy()));
            simulationMap.place(plant);
        }
    }
}
