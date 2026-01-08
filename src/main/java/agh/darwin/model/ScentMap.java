package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.Vector2i;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ScentMap extends BasicMap {
    private final HashMap<Vector2i, List<Scent>> scents = new HashMap<>();

    public ScentMap(int width, int height, Configuration configuration) {
        super(width, height, configuration);
    }

    @Override
    protected void moveAnimals() {
        List<Animal> allAnimals = animals.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();

        for (Animal animal : allAnimals) {
            moveAnimal(animal);
            animal.exhaust();
        }
    }
}
