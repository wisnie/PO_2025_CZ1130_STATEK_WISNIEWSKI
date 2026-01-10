package agh.darwin.model;

import agh.darwin.supplementary.OrdinalDirection;
import agh.darwin.supplementary.Rotation;
import agh.darwin.supplementary.Setting;
import agh.darwin.supplementary.Vector2i;

public record SphereTopology(Boundary boundary) {
    Setting moveForward(Vector2i position, OrdinalDirection direction) {
        OrdinalDirection targetDirection = direction;

        Vector2i moveVector = direction.toUnitVector();
        Vector2i targetX = switch (direction) {
            case NORTH, SOUTH -> position.justX();
            case NORTHEAST, EAST, SOUTHEAST -> {
                if (position.justX().equals(boundary.higherBound().justX())) {
                    yield boundary.higherBound().justX();
                }
                yield position.justX().add(moveVector.justX());
            }

            case SOUTHWEST, WEST, NORTHWEST -> {
                if (position.justX().equals(boundary.lowerBound().justX())) {
                    yield boundary.lowerBound().justX();
                }
                yield position.justX().add(moveVector.justX());
            }
        };

        Vector2i targetY = switch (direction) {
            case EAST, WEST -> position.justY();
            case NORTHWEST, NORTH, NORTHEAST -> {
                if (position.justY().equals(boundary.higherBound().justY())) {
                    targetDirection = targetDirection.rotate(Rotation.ONE_HUNDRED_EIGHTY);
                    yield boundary.higherBound().justY();
                }
                yield position.justY().add(moveVector.justY());
            }

            case SOUTHEAST, SOUTH, SOUTHWEST -> {
                if (position.justY().equals(boundary.lowerBound().justY())) {
                    targetDirection = targetDirection.rotate(Rotation.ONE_HUNDRED_EIGHTY);
                    yield boundary.lowerBound().justY();
                }
                yield position.justY().add(moveVector.justY());
            }
        };

        return new Setting(targetX.add(targetY), targetDirection);
    }
}
