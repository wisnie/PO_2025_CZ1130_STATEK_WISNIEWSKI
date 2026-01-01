package agh.darwin.model;

import agh.darwin.supplementary.MapEventListener;
import agh.darwin.supplementary.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;

public class BasicMap implements GridMap {
    private final Boundary boundary;
    private final HashMap<Vector2i, Animal> animals = new HashMap<>();
    private final ArrayList<MapEventListener> observers = new ArrayList<>();

    public BasicMap(int width, int height) {
        this.boundary = new Boundary(new Vector2i(0, 0), new Vector2i(width - 1, height - 1));
    }

    @Override
    public void place(Animal animal) {

    }
}
