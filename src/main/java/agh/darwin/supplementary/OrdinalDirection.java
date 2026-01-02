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

    private static final OrdinalDirection[] directions = values();

    OrdinalDirection(String displayText, Vector2i unitVector) {
        this.displayText = displayText;
        this.unitVector = unitVector;
    }

    public OrdinalDirection rotate(Rotation rotation) {
        int offset = switch (rotation) {
            case ZERO -> 0;
            case FORTY_FIVE -> 1;
            case NINETY -> 2;
            case ONE_HUNDRED_THIRTY_FIVE -> 3;
            case ONE_HUNDRED_EIGHTY -> 4;
            case TWO_HUNDRED_TWENTY_FIVE -> 5;
            case TWO_HUNDRED_SEVENTY -> 6;
            case THREE_HUNDRED_FIFTEEN -> 7;
        };

        return directions[(this.ordinal() + offset) % directions.length];
    };

    public Vector2i toUnitVector() {
        return unitVector;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
