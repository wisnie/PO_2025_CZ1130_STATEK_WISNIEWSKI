package agh.darwin.model;

import agh.darwin.supplementary.MoveDirection;
import agh.darwin.supplementary.OrdinalDirection;
import agh.darwin.supplementary.Vector2i;

public class Animal implements MapEntity {
    private OrdinalDirection orientation = OrdinalDirection.NORTH;
    private Vector2i position;
    private Energy energy;
    private Genotype genotype;

    public Animal(Vector2i position, Energy energy, Genotype genotype) {
        this.position = position;
        this.energy = energy;
        this.genotype = genotype;
    }

    public void move() {
      return;
    }

    public Vector2i getPosition() {
        return position;
    }
}
