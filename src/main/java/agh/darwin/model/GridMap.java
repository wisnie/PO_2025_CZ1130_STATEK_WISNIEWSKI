package agh.darwin.model;

public interface GridMap {
    public void place(Animal animal);

    public void place(Plant plant);

    void simulateDayProgression();
}
