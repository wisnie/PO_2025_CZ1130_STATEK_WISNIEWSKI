package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.PlantPositionGenerator;
import agh.darwin.supplementary.Vector2i;

import java.util.*;
import java.util.function.Function;

public class BasicMap implements GridMap {
    protected final HashMap<Vector2i, List<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2i, Plant> plants = new HashMap<>();
    private final Comparator<Animal> animalComparator;


    protected final Configuration configuration;
    protected final Boundary boundary;
    private final SphereTopology topology;
    private final ArrayList<MapEventListener> observers = new ArrayList<>();
    private final ArrayList<AnimalEventListener> animalObservers = new ArrayList<>();

    private final Function<Animal, AnimalAccessStatistics> accessStats;

    public BasicMap(int width, int height, Function<Animal, AnimalAccessStatistics> accessStats, Configuration configuration) {
        this.boundary = new Boundary(new Vector2i(0, 0), new Vector2i(width - 1, height - 1));
        this.topology = new SphereTopology(boundary);
        this.accessStats = accessStats;
        this.configuration = configuration;

        this.animalComparator = Comparator
                .comparing(Animal::getEnergy)
                .thenComparing(a -> accessStats.apply(a).getAge())
                .thenComparing(a -> accessStats.apply(a).getOffspringCount())
                .reversed();
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
    public void subscribe(MapEventListener observer) {
        observers.add(observer);
    }

    @Override
    public void subscribeAnimal(AnimalEventListener animalObserver) {
        animalObservers.add(animalObserver);
    }

    @Override
    public void unsubscribe(MapEventListener observer) {
        observers.remove(observer);
    }

    @Override
    public void simulateDayProgression() {
        removeDeadAnimals();
        moveAnimals();
        feedAnimals();
        reproduceAnimals();
        growPlants();

        mapChanged("Mapa się zmieniła.");
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
        animalChanged(animal, AnimalEvent.REPLACEMENT);
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

        Animal newborn = new Animal(field,
                new Energy(2 * configuration.animalParams().reproductionCost()),
                Genotype.childGenotype(weaker, stronger),
                configuration.animalParams()
        );

        animals.get(field).add(newborn);
    }

    private void growPlants() {
        PlantPositionGenerator plantPositionGenerator = new PlantPositionGenerator(configuration.mapDimensions().width(),
                configuration.mapDimensions().height(), configuration.plantParams().plantsPerDay(), plants);

        for (Vector2i plantPosition : plantPositionGenerator) {
            if (!plants.containsKey(plantPosition)) {
                Plant plant = new Plant(plantPosition,
                        new Energy(configuration.plantParams().plantEnergy()));

                place(plant);
            }
        }
    }
    public void mapChanged(String message){
        for (MapEventListener observer : observers){
            observer.mapChanged(this,message);
        }
    }

    public void animalChanged(Animal animal, AnimalEvent event) {
        for (AnimalEventListener observer: animalObservers) {
            observer.animalChanged(animal, event);
        }
    }

    public HashMap<Vector2i, List<Animal>> getAnimals(){
        return animals;
    }

    public HashMap<Vector2i, Plant> getPlants(){
        return plants;
    }

}
