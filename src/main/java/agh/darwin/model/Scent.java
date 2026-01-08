package agh.darwin.model;

import agh.darwin.supplementary.Vector2i;

public class Scent {
    private final Vector2i source;
    private int daysToDisappear;

    public Scent(Vector2i source) {
        this.source = source;
    }
}
