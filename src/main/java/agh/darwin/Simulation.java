package agh.darwin;

import agh.darwin.model.*;
import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.PlantPositionGenerator;
import agh.darwin.supplementary.Vector2i;

import java.util.*;


public class Simulation implements AnimalEventListener, MapEventListener, Runnable {
    private final HashMap<Animal, AnimalStatistic> statistics = new HashMap<>();
    private final int dayLength;
    private final GridMap simulationMap;
    private final Configuration configuration;
    private boolean paused = false;
    private boolean active = true;
    private SimulationStatistics simulationStatistics;
    int simulationDay = 0;

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        this.dayLength = configuration.dayLength().time();
        this.simulationMap = switch (configuration.mapVariant().mapVariant()) {
            case "Scent Map" -> new ScentMap(configuration.mapDimensions().width(),
                    configuration.mapDimensions().height(), this::provideStatistics, configuration);
            case "Basic Map" -> new BasicMap(configuration.mapDimensions().width(),
                    configuration.mapDimensions().height(), this::provideStatistics, configuration);
            default -> throw new IllegalArgumentException("Invalid map variant");
        };

        this.simulationMap.subscribeAnimal(this);

        addStartingEntities();
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        IO.println("Simulation interrupted");
                        return;
                    }
                }
            }
            if (!active) {
                break;
            }
            synchronized (this) {
                simulationMap.simulateDayProgression();
                simulationStatistics = makeStatistics();
            }

            simulationDay++;
            try {
                Thread.sleep(dayLength);
            } catch (InterruptedException e) {
                IO.println("Simulation thread interrupted");
                active = false;
            }
        }
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    public synchronized void stop() {
        active = false;
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

            @Override
            public int getPlantsEaten() { return animalStatistic.getPlantsEaten(); }

            @Override
            public int getEnergyAmount(){ return animal.getEnergy().amount(); }

            @Override
            public int getActiveGene(){ return animal.getGenotype().activeGene();}

            @Override
            public String getGenotype(){ return animal.getGenotype().toString(); }

            @Override
            public String getDeathDay(){
                return animalStatistic.getDeathDay() != -1 ? String.valueOf(animalStatistic.getDeathDay()) : "Animal is alive";}

            @Override
            public String toString() {
                return String.format("""
                        Age: %d,
                        Death Day: %s,
                        Offspring Count: %d,
                        Plants Eaten: %d,
                        Energy Amount: %d,
                        Active Gene: %d
                        Genotype: %s""",
                         getAge(), getDeathDay() ,getOffspringCount(), getPlantsEaten(), getEnergyAmount(), getActiveGene(), getGenotype());
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
    public GridMap mapChanged(GridMap gridMap, String message) {
        return gridMap;
    }

    public synchronized SimulationStatistics makeStatistics() {
        HashMap<Vector2i, List<Animal>> animals = simulationMap.getAnimals();
        HashMap<Vector2i, Plant> plants = simulationMap.getPlants();
        Map<Genotype, Integer> genotypeCounts = new HashMap<>();
        long aliveNumber = 0;
        double totalEnergy = 0;
        double totalChildren = 0;

        for (List<Animal> animalList : animals.values()) {
            if (animalList == null) continue;
            for (Animal animal : animalList) {
                aliveNumber++;
                totalEnergy += animal.getEnergy().amount();

                AnimalStatistic stat = statistics.get(animal);
                if (stat != null) {
                    totalChildren += stat.getOffspringCount();
                }

                Genotype genotype = animal.getGenotype();
                genotypeCounts.put(genotype, genotypeCounts.getOrDefault(genotype, 0) + 1);
            }
        }

        Genotype mostPopular = null;
        int maxCount = 0;
        for (Map.Entry<Genotype, Integer> entry : genotypeCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopular = entry.getKey();
            }
        }

        double ageSum = 0;
        int deadCount = 0;
        for (AnimalStatistic stat : statistics.values()) {
            if (stat.getDeathDay() != -1) {
                ageSum += (stat.getDeathDay() - stat.getBirthDay());
                deadCount++;
            }
        }

        double avgEnergy = (aliveNumber > 0) ? totalEnergy / aliveNumber : 0;
        double averageChildrenNumber = (aliveNumber > 0) ? totalChildren / aliveNumber : 0;
        double averageAge = (deadCount > 0) ? ageSum / deadCount : 0;

        int width = configuration.mapDimensions().width();
        int height = configuration.mapDimensions().height();
        Set<Vector2i> occupiedPositions = new HashSet<>();
        occupiedPositions.addAll(animals.keySet());
        occupiedPositions.addAll(plants.keySet());
        long freeFields = (long) width * height - occupiedPositions.size();
        long plantNumber = plants.size();

        return new SimulationStatistics(aliveNumber, plantNumber, freeFields, mostPopular, avgEnergy, averageAge,
                averageChildrenNumber);
    }



    public synchronized String getStatistics () {
                return String.format("""
                        Day: %d
                        Animal Number: %d
                        Plant Number: %d
                        Free Fields: %d
                        Most Popular Genotype: %s
                        Average energy: %.1f
                        Average Age (Dead): %.1f
                        Average Children Number (Alive): %.1f
                        """,
                simulationDay, simulationStatistics.aliveNumber(), simulationStatistics.plantNumber(),
                simulationStatistics.freeFields() ,simulationStatistics.mostPopularGenotype(),
                simulationStatistics.averageEnergy(), simulationStatistics.averageAge(), simulationStatistics.averageChildren());
    }

    public synchronized Genotype getMostPopularGenotype() {
        return simulationStatistics.mostPopularGenotype();
    }

    public GridMap getMap() {
        return simulationMap;
    }
}
