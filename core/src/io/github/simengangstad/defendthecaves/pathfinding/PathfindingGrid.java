package io.github.simengangstad.defendthecaves.pathfinding;

import com.badlogic.gdx.utils.Pool;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

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
    private final Queue<PathfindingCoordinate> frontier = new LinkedList<>();

    /**
     * The priority queue used for the expanding ring (frontier) which also takes in account distance
     * to the goal.
     */
    private final PriorityQueue<PathfindingCoordinate> priorityFrontier = new PriorityQueue<>();

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

    PathfindingCoordinate start = new PathfindingCoordinate(), end = new PathfindingCoordinate();

    /**
     * Performs a flood from the given position and marks all the solid tiles where 0 is defined as a
     * non-solid tile and 1 is a solid tile.
     */
    public void performFlood(int x0, int y0, int[][] map) {

        start.set(x0, y0);

        for (int i = 0; i < width; i++) {

            for (int j = 0; j < height; j++) {

                map[i][j] = -1;
            }
        }

        frontier.clear();

        frontier.add(start);

        while (!frontier.isEmpty()) {

            PathfindingCoordinate frontierCoordinate = frontier.poll();

            for (PathfindingCoordinate next : getNeighbours(frontierCoordinate, false)) {

                if (next != null && map[next.x][next.y] == -1) {

                    if (get(next.x, next.y) == State.Closed) {

                        System.out.println(next);

                        map[next.x][next.y] = 1;
                    }
                    else {

                        map[next.x][next.y] = 0;

                        frontier.add(next);
                    }
                }
            }

            pathfindingCoordinatePool.free(frontierCoordinate);
        }
    }

    /**
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param cameFrom The reference to the coordinates which builds the path.
     *
     * @return Whether the search for a path was successful or not.
     */
    public boolean performSearch(int x1, int y1, int x2, int y2, Coordinate[][] cameFrom)  {

        start.set(x2, y2);
        end.set(x1, y1);

        priorityFrontier.clear();

        priorityFrontier.add(start);

        cameFrom[start.x][start.y].set(start.x, start.y);

        while (!priorityFrontier.isEmpty()) {

            PathfindingCoordinate frontierCoordinate = priorityFrontier.poll();

            for (PathfindingCoordinate next : getNeighbours(frontierCoordinate, true)) {

                if (next != null) {

                    Coordinate came = cameFrom[next.x][next.y];

                    if (came.x == -1 && came.y == -1) {

                        next.origin = end;

                        priorityFrontier.add(next);

                        cameFrom[next.x][next.y].set(frontierCoordinate.x, frontierCoordinate.y);

                        if (cameFrom[end.x][end.y].x != -1) {

                            priorityFrontier.clear();

                            break;
                        }
                    }
                }
            }

            if (priorityFrontier.isEmpty() && (cameFrom[end.x][end.y].x != frontierCoordinate.x || cameFrom[end.x][end.y].y != frontierCoordinate.y)) {

                return false;
            }

            pathfindingCoordinatePool.free(frontierCoordinate);
        }

        return true;
    }

    private boolean inBounds(PathfindingCoordinate coordinate) {

        return  (0 <= coordinate.x && coordinate.x < width) &&
                (0 <= coordinate.y && coordinate.y < height);
    }

    /**
     * @return The neighbours of the given coordinate.
     */
    public PathfindingCoordinate[] getNeighbours(PathfindingCoordinate coordinate, boolean checkAgainstClosedState) {

        Arrays.fill(tmpNeighbours, null);

        for (int i = 0; i < Directions.length; i++) {

            PathfindingCoordinate neighbour = pathfindingCoordinatePool.obtain();

            neighbour.set(coordinate.x + Directions[i].x, coordinate.y + Directions[i].y);

            if (inBounds(neighbour)) {

                if (checkAgainstClosedState) {

                    if (grid[neighbour.x][neighbour.y] != State.Closed) {

                        tmpNeighbours[i] = neighbour;
                    }
                }
                else {

                    tmpNeighbours[i] = neighbour;
                }
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
