package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

public record Boundary(Vector2i lowerBound, Vector2i upperBound) {
    public Boundary {
        if (upperBound.weakPrecedes(lowerBound) || lowerBound.weakFollows(upperBound)) {
            throw new java.lang.IllegalArgumentException(
                    String.format("Invalid boundary vectors; low: %s, high: %s", lowerBound, upperBound));
        }
    }
}
