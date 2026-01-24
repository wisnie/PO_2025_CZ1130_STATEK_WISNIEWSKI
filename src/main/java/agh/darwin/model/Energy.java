package agh.darwin.model;

public record Energy(int amount) implements Comparable<Energy> {
    Energy loseEnergy(Energy energyLost) {
        return new Energy(Math.max(0, amount - energyLost.amount));
    }

    Energy gainEnergy(Energy energyGain) {
        return new Energy(amount + energyGain.amount);
    }

    public boolean noEnergy() {
        return amount == 0;
    }

    @Override
    public int compareTo(Energy o) {
        return Integer.compare(amount, o.amount);
    }
}
