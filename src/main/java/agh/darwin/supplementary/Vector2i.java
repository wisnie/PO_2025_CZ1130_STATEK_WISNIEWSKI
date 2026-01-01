package agh.darwin.supplementary;

public record Vector2i(int x, int y) {
    public Vector2i justX() {
        return new Vector2i(x, 0);
    }

    public Vector2i justY() {
        return new Vector2i(0, y);
    }

    public Vector2i add(Vector2i other) {
        return new Vector2i(x + other.x, y + other.y);
    }

    public Vector2i subtract(Vector2i other) {
        return new Vector2i(x - other.x, y - other.y);
    }

    public boolean precedes(Vector2i other) {
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2i other) {
        return x >= other.x && y >= other.y;
    }

    public Vector2i opposite() {
        return new Vector2i(-x, -y);
    }
}