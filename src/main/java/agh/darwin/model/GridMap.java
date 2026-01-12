package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

import java.util.HashMap;
import java.util.List;

public interface GridMap {
    public void place(Animal animal);

    public void place(Plant plant);

    public void subscribe(MapEventListener observer);

    public void subscribeAnimal(AnimalEventListener observer);

    public void unsubscribe(MapEventListener observer);

    public HashMap<Vector2i, List<Animal>> getAnimals();

    public HashMap<Vector2i, Plant> getPlants();

    void simulateDayProgression();
}
