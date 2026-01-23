package agh.darwin.model;

import agh.darwin.supplementary.OrdinalDirection;
import agh.darwin.supplementary.Setting;
import agh.darwin.supplementary.Vector2i;

public record SphereTopology(Boundary boundary) {
    Setting moveForward(Vector2i position, OrdinalDirection direction) {
        Vector2i targetField = position.add(direction.toUnitVector());
        OrdinalDirection targetDirection = direction;

        if (targetField.followsX(boundary.upperBound())) {
            targetField = targetField.justY().add(boundary.lowerBound().justX());
        }

        if (targetField.precedesX(boundary.lowerBound())) {
            targetField = targetField.justY().add(boundary.upperBound().justX());
        }

        if (targetField.followsY(boundary.upperBound())) {
            targetField = targetField.justX().add(boundary.upperBound().justY());
            targetDirection = switch (targetDirection) {
                case NORTH -> OrdinalDirection.SOUTH;
                case NORTHEAST -> OrdinalDirection.SOUTHEAST;
                case NORTHWEST -> OrdinalDirection.SOUTHWEST;
                default ->
                        throw new IllegalArgumentException(
                                "Inconsistent parameters: unit vector exceeds upper y bound," +
                                        " but direction not North!");
            };
        }

        if (targetField.precedesY(boundary.lowerBound())) {
            targetField = targetField.justX().add(boundary.lowerBound().justY());
            targetDirection = switch (targetDirection) {
                case SOUTH -> OrdinalDirection.NORTH;
                case SOUTHEAST -> OrdinalDirection.NORTHEAST;
                case SOUTHWEST -> OrdinalDirection.NORTHWEST;
                default ->
                        throw new IllegalArgumentException(
                                "Inconsistent parameters: unit vector below lower y bound," +
                                        " but direction not South!");
            };
        }

        return new Setting(targetField, targetDirection);
    }
}
