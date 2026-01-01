package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

public record Boundary(Vector2i lowerBound, Vector2i higherBound) {
    public Boundary {
        if (higherBound.precedes(lowerBound) || lowerBound.follows(higherBound)) {
            throw new java.lang.IllegalArgumentException(
                    String.format("Invalid boundary vectors; low: %s, high: %s", lowerBound, higherBound));
        }
    }
}
