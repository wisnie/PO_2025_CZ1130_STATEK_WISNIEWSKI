package agh.darwin;

import agh.darwin.model.*;
import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.PlantPositionGenerator;
import agh.darwin.supplementary.Vector2i;

import java.util.HashMap;
import java.util.function.Supplier;


public class Simulation implements AnimalEventListener, MapEventListener, Runnable {
    private final HashMap<Animal, AnimalStatistic> statistics = new HashMap<>();

    private final GridMap simulationMap;
    private final Configuration configuration;
    private final HashMap<Animal, AnimalStatistic> animalStatistics = new HashMap<>();

    int simulationDay = 0;

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        this.simulationMap = new BasicMap(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), this::provideStatistics, configuration);

        addStartingEntities();
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            simulationMap.simulateDayProgression();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                IO.println("Simulation thread interrupted");
            }
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

        PlantPositionGenerator plantPositionGenerator = new PlantPositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.plantParams().startingPlants(), new HashMap<>());

        for (Vector2i plantPosition : plantPositionGenerator) {
            Plant plant = new Plant(plantPosition, new Energy(configuration.plantParams().plantEnergy()));
            simulationMap.place(plant);
        }
    }

    public AnimalAccessStatistics provideStatistics(Animal animal) {
        AnimalStatistic animalStatistic = statistics.get(animal);
        return new AnimalAccessStatistics() {
            @Override
            public int getAge() {
                return animalStatistic.getAge(simulationDay);
            }

            @Override
            public int getOffspringCount() {
                return animalStatistic.getOffspringCount();
            }
        };
    }

    @Override
    public void animalChanged(Animal animal, AnimalEvent event) {
        AnimalStatistic animalStats = statistics
                .computeIfAbsent(animal, k -> new AnimalStatistic(simulationDay));

        animalStats.handleEvent(event, simulationDay);
    }

    @Override
    public void mapChanged(GridMap gridMap, String message) {

    public GridMap getMap() {
        return simulationMap;
    }
}
