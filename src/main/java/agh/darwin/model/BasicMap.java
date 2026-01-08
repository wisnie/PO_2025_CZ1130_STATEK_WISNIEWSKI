package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.MapEventListener;
import agh.darwin.supplementary.PlantPositionGenerator;
import agh.darwin.supplementary.Vector2i;

import java.util.*;

public class BasicMap implements GridMap {
    private final HashMap<Vector2i, List<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2i, Plant> plants = new HashMap<>();
    private final ArrayList<MapEventListener> observers = new ArrayList<>();

    private final Configuration configuration;
    private final SphereTopology topology;

    public BasicMap(int width, int height, Configuration configuration) {
        Boundary boundary = new Boundary(new Vector2i(0, 0), new Vector2i(width - 1, height - 1));
        this.topology = new SphereTopology(boundary);
        this.configuration = configuration;

    }

    @Override
    public void place(Animal animal) {
        addAnimal(animal);
    }

    @Override
    public void place(Plant plant) {
        plants.putIfAbsent(plant.position(), plant);
    }

    @Override
    public void simulateDayProgression() {
        // TODO Refactoring
        for (List<Animal> animalsOnField : List.copyOf(animals.values())) {
            for (Animal animal : List.copyOf(animalsOnField)) {
                if (animalIsDead(animal)) {
                    removeAnimal(animal.getPosition(), animal);
                    continue;
                }

                moveAnimal(animal);
                animal.exhaust();
            }
        }

        for (Map.Entry<Vector2i, List<Animal>> fieldAnimals : animals.entrySet()) {
            Vector2i field = fieldAnimals.getKey();
            List<Animal> animalsOnField = fieldAnimals.getValue();

            Comparator<Animal> strengthSorter = Comparator
                    .comparing(Animal::getEnergy)
                    .thenComparing(animal -> 0.0);

            animalsOnField.sort(strengthSorter);
            if (plants.containsKey(field)) {
               Animal winner = animalsOnField.getFirst();
               Plant plant = plants.get(field);
               winner.consumePlant(plant.plantEnergy());
               plants.remove(field);
            }

            List<Animal> animalPair = animalsOnField.stream()
                    .filter((animal) ->
                            animal.getEnergy().amount() > configuration.animalParams().reproductionThreshold())
                    .limit(2)
                    .toList();

            if (animalPair.size() == 2) {
                breedAnimals(animalPair);
            }
        }


        // TODO pareto
        PlantPositionGenerator plantPositionGenerator = new PlantPositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.plantParams().plantsPerDay());

        for (Vector2i plantPosition : plantPositionGenerator) {
            if (!plants.containsKey(plantPosition)) {
                Plant plant = new Plant(plantPosition,
                        new Energy(configuration.plantParams().plantEnergy()));

                place(plant);
            }
        }
    }

    private boolean animalIsDead(Animal animal) {
        return animal.getEnergy().noEnergy();
    }

    private void removeAnimal(Vector2i field, Animal animal) {
        List<Animal> animalsOnField = animals.get(field);
        animalsOnField.remove(animal);

        if (animalsOnField.isEmpty()) animals.remove(field);
    }

    private void addAnimal(Animal animal) {
        Vector2i field = animal.getPosition();
        if (animals.containsKey(field)) {
            animals.get(field).add(animal);
        } else {
            ArrayList<Animal> animalsOnField = new ArrayList<>();
            animalsOnField.add(animal);
            animals.put(field, animalsOnField);
        }
    }

    private void moveAnimal(Animal animal) {
        Vector2i initialField = animal.getPosition();
        animal.move(topology);

        removeAnimal(initialField, animal);
        addAnimal(animal);
    }

    private void breedAnimals(List<Animal> animalsPair) {
        if (animalsPair.size() != 2)
            throw new IllegalArgumentException("vetylf saldkjf alsdjf lajsdlf jasdlkf jasldjf ");

        Animal stronger = animalsPair.getFirst();
        Animal weaker = animalsPair.getLast();
        Vector2i field = stronger.getPosition();

        stronger.reproduce();
        weaker.reproduce();

        // TODO, genotype
        Animal newborn = new Animal(field,
                new Energy(2 * configuration.animalParams().reproductionCost()),
                Genotype.randomGenotype(configuration.genotypeParams().genomeLength()),
                configuration.animalParams()
        );

        animals.get(field).add(newborn);
    }
}
