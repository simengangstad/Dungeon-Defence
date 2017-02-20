package io.github.simengangstad.dungeondefence.pathfinding;

/**
 * Class for representing coordinates within the map.
 *
 * @author simengangstad
 * @since 26/11/15
 */
public class Coordinate {

    public int x = 0, y = 0;

    public Coordinate(int x, int y) {

        set(x, y);
    }

    public Coordinate() {

        this(0, 0);
    }

    public void set(int x, int y) {

        this.x = x;
        this.y = y;
    }

    public void set(Coordinate coordinate) {

        set(coordinate.x, coordinate.y);
    }

    final String divider = ", ";

    @Override
    public String toString() {

        return x + divider + y;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Coordinate)) {

            return false;
        }

        Coordinate coordinate = (Coordinate) obj;

        return x == coordinate.x && y == coordinate.y;
    }
}
