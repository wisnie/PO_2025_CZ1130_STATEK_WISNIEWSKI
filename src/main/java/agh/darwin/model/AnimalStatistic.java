package agh.darwin.model;

import java.util.Objects;

public final class AnimalStatistic {
    private final int daysOfLife;
    private final int plantsEaten;
    private final int childrenCount;
    private final int descendantsCount;
    private final int deathDay;

    public AnimalStatistic(int daysOfLife,
                           int plantsEaten,
                           int childrenCount,
                           int descendantsCount,
                           int deathDay) {
        this.daysOfLife = daysOfLife;
        this.plantsEaten = plantsEaten;
        this.childrenCount = childrenCount;
        this.descendantsCount = descendantsCount;
        this.deathDay = deathDay;
    }

    public int daysOfLife() {
        return daysOfLife;
    }

    public int plantsEaten() {
        return plantsEaten;
    }

    public int childrenCount() {
        return childrenCount;
    }

    public int descendantsCount() {
        return descendantsCount;
    }

    public int deathDay() {
        return deathDay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AnimalStatistic) obj;
        return this.daysOfLife == that.daysOfLife &&
                this.plantsEaten == that.plantsEaten &&
                this.childrenCount == that.childrenCount &&
                this.descendantsCount == that.descendantsCount &&
                this.deathDay == that.deathDay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(daysOfLife, plantsEaten, childrenCount, descendantsCount, deathDay);
    }

    @Override
    public String toString() {
        return "AnimalStatistic[" +
                "daysOfLife=" + daysOfLife + ", " +
                "plantsEaten=" + plantsEaten + ", " +
                "childrenCount=" + childrenCount + ", " +
                "descendantsCount=" + descendantsCount + ", " +
                "deathDay=" + deathDay + ']';
    }

}
