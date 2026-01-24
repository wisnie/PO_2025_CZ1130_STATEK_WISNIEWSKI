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

    public boolean weakPrecedes(Vector2i other) {
        return x <= other.x && y <= other.y;
    }

    public boolean precedesX(Vector2i other) {
        return  x < other.x;
    }

    public boolean precedesY(Vector2i other) {
        return y < other.y;
    }

    public boolean weakFollows(Vector2i other) {
        return x >= other.x && y >= other.y;
    }

    public boolean followsX(Vector2i other) {
        return x > other.x;
    }

    public boolean followsY(Vector2i other) {
        return y > other.y;
    }

    public Vector2i upperRight(Vector2i other) {
        return new Vector2i(Math.max(x, other.x), Math.max(y, other.y));
    }

    public Vector2i lowerLeft(Vector2i other) {
        return new Vector2i(Math.min(x, other.x), Math.min(y, other.y));
    }

    public Vector2i chebyshevUnitVector() {
        return new Vector2i(x == 0 ? 0 : x / Math.abs(x), y == 0 ? 0 : y / Math.abs(y));
    }

    public static int calcChebyshevMetric(Vector2i a, Vector2i b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    public Vector2i opposite() {
        return new Vector2i(-x, -y);
    }
}