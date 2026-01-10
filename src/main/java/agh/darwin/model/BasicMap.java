package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.MapEventListener;
import agh.darwin.supplementary.Vector2i;

import java.util.*;

public class BasicMap implements GridMap {
    protected final HashMap<Vector2i, List<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2i, Plant> plants = new HashMap<>();
    private final ArrayList<MapEventListener> observers = new ArrayList<>();
    private final Comparator<Animal> animalComparator = Comparator
            .comparing(Animal::getEnergy)
            .thenComparing(animal -> 0.0);


    protected final Configuration configuration;
    protected final Boundary boundary;
    private final SphereTopology topology;

    public BasicMap(int width, int height, Configuration configuration) {
        boundary = new Boundary(new Vector2i(0, 0), new Vector2i(width - 1, height - 1));
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
        removeDeadAnimals();
        moveAnimals();
        feedAnimals();
        reproduceAnimals();
        growPlants();
    }

    private void removeDeadAnimals() {
        animals.values().forEach( a -> a.removeIf(this::animalIsDead));
        animals.values().removeIf(List::isEmpty);
    }

    private boolean animalIsDead(Animal animal) {
        return animal.getEnergy().noEnergy();
    }

    private void moveAnimals() {
        for (Animal animal : animals.values()
                .stream()
                .flatMap(Collection::stream)
                .toList()) {
            moveAnimal(animal);
            animal.exhaust();
        }
    }

    void moveAnimal(Animal animal) {
        Vector2i initialField = animal.getPosition();
        animal.move(topology);

        removeAnimal(initialField, animal);
        addAnimal(animal);
    }

    protected void removeAnimal(Vector2i field, Animal animal) {
        List<Animal> animalsOnField = animals.get(field);
        animalsOnField.remove(animal);

        if (animalsOnField.isEmpty()) animals.remove(field);
    }

    protected void addAnimal(Animal animal) {
        Vector2i field = animal.getPosition();
        animals.computeIfAbsent(field, k -> new ArrayList<>()).add(animal);
    }

    private void feedAnimals() {
        for (Map.Entry<Vector2i, List<Animal>> fieldAnimals : animals.entrySet()) {
            Vector2i field = fieldAnimals.getKey();
            List<Animal> animalsOnField = fieldAnimals.getValue();

            if (plants.containsKey(field)) {
                Animal winner = Collections.min(animalsOnField, animalComparator);
                Plant plant = plants.get(field);

                winner.consumePlant(plant.plantEnergy());
                plants.remove(field);
            }
        }
    }

    private void reproduceAnimals() {
        for (List<Animal> animalsOnField : animals.values()) {
            List<Animal> animalPair = animalsOnField.stream()
                    .filter((animal) ->
                            animal.getEnergy().amount() > configuration.animalParams().reproductionThreshold())
                    .sorted(animalComparator)
                    .limit(2)
                    .toList();

            if (animalPair.size() == 2) {
                reproducePair(animalPair);
            }
        }
    }

    protected void reproducePair(List<Animal> animalPair) {
        if (animalPair.size() != 2)
            throw new IllegalArgumentException("Functions must be called with List<Animal> of size 2.");

        Animal stronger = animalPair.getFirst();
        Animal weaker = animalPair.getLast();
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

    private void growPlants() {
        // TODO pareto
        FinitePositionGenerator plantPositionGenerator = new FinitePositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.plantParams().plantsPerDay());

        for (Vector2i plantPosition : plantPositionGenerator) {
            if (!plants.containsKey(plantPosition)) {
                Plant plant = new Plant(plantPosition,
                        new Energy(configuration.plantParams().plantEnergy()));

                place(plant);
            }
        }
    }
}
