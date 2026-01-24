package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.OrdinalDirection;
import agh.darwin.supplementary.Rotation;
import agh.darwin.supplementary.Setting;
import agh.darwin.supplementary.Vector2i;

public class Animal implements MapEntity {
    private final Genotype genotype;
    private OrdinalDirection orientation = OrdinalDirection.NORTH;
    private Vector2i position;
    private Energy energy;

    private final Configuration.AnimalParameters animalConf;

    public Animal(Vector2i position, Energy energy, Genotype genotype, Configuration.AnimalParameters animalConf) {
        this.position = position;
        this.energy = energy;
        this.genotype = genotype;
        this.animalConf = animalConf;
    }

    public void moveTowardsScent(Scent scent) {
        Vector2i scentOrigin = scent.getOrigin();
        Vector2i shift = scentOrigin
                .subtract(position)
                .chebyshevUnitVector();

        position = position.add(shift);
    }

    public void move(SphereTopology topology) {
        Rotation rotation = switch (genotype.activeGene()) {
            case 0 -> Rotation.ZERO;
            case 1 -> Rotation.FORTY_FIVE;
            case 2 -> Rotation.NINETY;
            case 3 -> Rotation.ONE_HUNDRED_THIRTY_FIVE;
            case 4 -> Rotation.ONE_HUNDRED_EIGHTY;
            case 5 -> Rotation.TWO_HUNDRED_TWENTY_FIVE;
            case 6 -> Rotation.TWO_HUNDRED_SEVENTY;
            case 7 -> Rotation.THREE_HUNDRED_FIFTEEN;
            default -> throw new IllegalStateException("Unexpected gene value: " + genotype.activeGene());
        };
        orientation = orientation.rotate(rotation);

        Setting setting = topology.moveForward(position, orientation);
        position = setting.position();
        orientation = setting.direction();

        genotype.nextGene();
    }

    public void exhaust() {
        energy = energy.loseEnergy(new Energy(animalConf.dailyEnergyConsumption()));
    }

    public void consumePlant(Energy plantEnergy) {
        energy = energy.gainEnergy(plantEnergy);
    }

    public void reproduce() {
        energy = energy.loseEnergy(new Energy(animalConf.reproductionCost()));
    }

    public Energy getEnergy() {
        return energy;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    public Vector2i getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Animal with " + energy.amount() + " energy.";
    }
}
