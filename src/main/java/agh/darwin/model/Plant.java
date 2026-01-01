package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

public record Plant(Vector2i position) implements MapEntity {
    @Override
    public String toString() {
        return "*";
    }
}
