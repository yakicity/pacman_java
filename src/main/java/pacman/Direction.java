package pacman;

public enum Direction {
    O(0, 0), // center / no direction
    L(-1, 0), // left / west
    U(0, -1), // up / north
    R(1, 0), // right / east
    D(0, 1); // down / south

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public final int dx;
    public final int dy;

    public static final Direction[] FLIPPED = {O, R, D, L, U};

    public Direction flip() {
        return FLIPPED[this.ordinal()];
    }
}
