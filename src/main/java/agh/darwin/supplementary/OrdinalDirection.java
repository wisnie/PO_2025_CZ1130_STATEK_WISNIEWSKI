package agh.darwin.supplementary;

public enum OrdinalDirection {
    NORTH("północ", new Vector2i(0, 1)),
    NORTHEAST("północny wschód", new Vector2i(1, 1)),
    EAST("wschód", new Vector2i(1, 0)),
    SOUTHEAST("południowy wschód", new Vector2i(1, -1)),
    SOUTH("południe", new Vector2i(0, -1)),
    SOUTHWEST("południowy zachód", new Vector2i(-1, -1)),
    WEST("zachód", new Vector2i(-1, 0)),
    NORTHWEST("północny zachód", new Vector2i(-1, 1));

    private final String displayText;
    private final Vector2i unitVector;

    OrdinalDirection(String displayText, Vector2i unitVector) {
        this.displayText = displayText;
        this.unitVector = unitVector;
    }

    public Vector2i toUnitVector() {
        return unitVector;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
