package io.github.simengangstad.defendthecaves.pathfinding;

import com.badlogic.gdx.utils.Pool;

/**
 * @author simengangstad
 * @since 09/11/15
 */
public class PathfindingCoordinate implements Comparable, Pool.Poolable {

    /**
     * The origin in the pathfinding algorithm
     */
    public PathfindingCoordinate origin;

    public int x, y;

    public PathfindingCoordinate(int x, int y) {

        set(x, y);
    }

    public PathfindingCoordinate(PathfindingCoordinate pathfindingCoordinate) {

        this(pathfindingCoordinate.x, pathfindingCoordinate.y);
    }

    public PathfindingCoordinate() {

        this(0, 0);
    }

    public PathfindingCoordinate set(int x, int y) {

        this.x = x;
        this.y = y;

        return this;
    }

    public PathfindingCoordinate set(PathfindingCoordinate pathfindingCoordinate) {

        return set(pathfindingCoordinate.x, pathfindingCoordinate.y);
    }

    @Override
    /**
     * Compares how far this object is to the origin in comparison to
     * the passed argument.
     */
    public int compareTo(Object o) {

        if (!(o instanceof PathfindingCoordinate)) {

            return 0;
        }

        PathfindingCoordinate pathfindingCoordinate = (PathfindingCoordinate) o;

        return heuristic(this, origin) - heuristic(pathfindingCoordinate, origin);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PathfindingCoordinate)) {

            return false;
        }

        PathfindingCoordinate pathfindingCoordinate = (PathfindingCoordinate) obj;

        return pathfindingCoordinate.x == x && pathfindingCoordinate.y == y;
    }

    @Override
    public String toString() {

        return "(" + x + ", " + y + ")";
    }

    @Override
    public void reset() {

        set(0, 0);
    }

    /**
     * @return Returns the length from coordinate a to b.
     */
    private static int heuristic(PathfindingCoordinate a, PathfindingCoordinate b) {

        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
