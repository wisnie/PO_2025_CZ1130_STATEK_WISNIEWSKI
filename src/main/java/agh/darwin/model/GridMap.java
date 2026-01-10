package agh.darwin.model;

import agh.darwin.supplementary.MapEventListener;

public interface GridMap {
    public void place(Animal animal);

    public void place(Plant plant);

    public void subscribe(MapEventListener observer);

    public void unsubscribe(MapEventListener observer);

    void simulateDayProgression();
}
