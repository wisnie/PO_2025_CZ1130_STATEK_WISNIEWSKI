package agh.darwin.model;

public interface GridMap {
    public void place(Animal animal);

    public void place(Plant plant);

    public void subscribe(MapEventListener observer);

    public void subscribeAnimal(AnimalEventListener observer);

    public void unsubscribe(MapEventListener observer);

    void simulateDayProgression();
}
