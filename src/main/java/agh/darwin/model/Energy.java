package agh.darwin.model;

public record Energy(int energy) {
    Energy consumeEnergy(int amountConsumed) {
        return new Energy(Math.max(0, energy - amountConsumed));
    }
}
