package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

public record Plant(Vector2i position, Energy plantEnergy) implements MapEntity {
    @Override
    public String toString() {
        return "*";
    }
}
