package io.github.simengangstad.defendthecaves.pathfinding;

import com.badlogic.gdx.utils.Pool;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * @author simengangstad
 * @since 09/11/15
 */
public class PathfindingGrid {

    /**
     * The different directions.
     */
    private static final PathfindingCoordinate[] Directions = new PathfindingCoordinate[] {

        new PathfindingCoordinate(0, 1),
        new PathfindingCoordinate(-1, 0),
        new PathfindingCoordinate(0, -1),
        new PathfindingCoordinate(1, 0),
    };

    /**
     * The states of the tiles in the grid.
     */
    public enum State {

        Open, Closed
    }

    /**
     * The dimension of the grid.
     */
    public final int width, height;

    /**
     * The grid.
     */
    private final State[][] grid;

    /**
     * The queue used for the expanding ring (frontier) that determines the properties
     * of the tiles within the map.
     */
    public final PriorityQueue<PathfindingCoordinate> frontier = new PriorityQueue<>();

    /**
     * Temp value for the neigbours of a given tile.
     */
    private final PathfindingCoordinate[] tmpNeighbours = new PathfindingCoordinate[4];

    Pool<PathfindingCoordinate> pathfindingCoordinatePool = new Pool<PathfindingCoordinate>() {

        @Override
        protected PathfindingCoordinate newObject() {

            return new PathfindingCoordinate();
        }
    };

    Pool<Coordinate> coordinatePool = new Pool<Coordinate>() {

        @Override
        protected Coordinate newObject() {

            return new Coordinate();
        }
    };

    public PathfindingGrid(int width, int height) {

        this.width = width;
        this.height = height;

        grid = new State[width][height];

        clear();
    }

    public void clear() {

        for (int i = 0; i < width; i++) {

            for (int j = 0; j < height; j++) {

                grid[i][j] = State.Open;
            }
        }
    }

    public void set(int x, int y, State state) {

        grid[x][y] = state;
    }

    public State get(int x, int y) {

        return grid[x][y];
    }

    public Coordinate[][] performSearch(int x1, int y1, int x2, int y2)  {

        PathfindingCoordinate start = pathfindingCoordinatePool.obtain();

        start.set(x1, y1);

        PathfindingCoordinate end = pathfindingCoordinatePool.obtain();

        end.set(x2, y2);

        frontier.clear();

        frontier.add(start);

        Coordinate[][] cameFrom = new Coordinate[width][height];

        while (!frontier.isEmpty()) {

            PathfindingCoordinate frontierCoordinate = frontier.poll();

            if (frontierCoordinate.equals(end)) {

                pathfindingCoordinatePool.free(frontierCoordinate);

                break;
            }

            for (PathfindingCoordinate next : getNeighbours(frontierCoordinate)) {

                if (next != null) {

                    if (cameFrom[next.x][next.y] == null) {

                        next.origin = end;

                        frontier.add(next);

                        cameFrom[next.x][next.y] = new Coordinate(frontierCoordinate.x, frontierCoordinate.y);
                    }
                }
            }

            pathfindingCoordinatePool.free(frontierCoordinate);
        }

        pathfindingCoordinatePool.free(start);
        pathfindingCoordinatePool.free(end);

        return cameFrom;
    }

    private boolean inBounds(PathfindingCoordinate coordinate) {

        return  (0 <= coordinate.x && coordinate.x < width) &&
                (0 <= coordinate.y && coordinate.y < height);
    }

    /**
     * @return The neighbours of the given coordinate.
     */
    public PathfindingCoordinate[] getNeighbours(PathfindingCoordinate coordinate) {

        Arrays.fill(tmpNeighbours, null);

        for (int i = 0; i < Directions.length; i++) {

            PathfindingCoordinate neighbour = pathfindingCoordinatePool.obtain();

            neighbour.set(coordinate.x + Directions[i].x, coordinate.y + Directions[i].y);

            if (inBounds(neighbour) && grid[neighbour.x][neighbour.y] != State.Closed) {

                tmpNeighbours[i] = neighbour;
            }
            else {

                pathfindingCoordinatePool.free(neighbour);
            }
        }

        return tmpNeighbours;
    }

    /**
     * @author simengangstad
     * @since 09/11/15
     */
    private static class PathfindingCoordinate implements Comparable, Pool.Poolable {

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
        private int heuristic(PathfindingCoordinate a, PathfindingCoordinate b) {

            return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        }
    }
}
