package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

public class Scent {
    private final Vector2i origin;
    private int scentLongevityInDays;

    public Scent(Vector2i origin, int scentLongevityInDays) {
        this.origin = origin;
        this.scentLongevityInDays = scentLongevityInDays;
    }

    public Vector2i getOrigin() {
        return origin;
    }

    public int getScentLongevity() {
        return scentLongevityInDays;
    }

    public void decreaseLongevity() {
        scentLongevityInDays = Math.max(scentLongevityInDays - 1, 0);
    }

    public void increaseLongevity(int amount) {
        scentLongevityInDays += amount;
    }

    public boolean scentGone() {
        return scentLongevityInDays == 0;
    }
}
