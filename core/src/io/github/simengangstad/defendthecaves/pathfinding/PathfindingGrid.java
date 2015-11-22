package io.github.simengangstad.defendthecaves.pathfinding;

import com.badlogic.gdx.utils.Pool;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * @author simengangstad
 * @since 09/11/15
 */
public class PathfindingGrid {

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

    public final int width, height;

    private final State[][] grid;

    /**
     * The queue used for the expanding ring (frontier) that determines the properties
     * of the tiles within the map.
     */
    public final PriorityQueue<PathfindingCoordinate> frontier = new PriorityQueue<>();

    private final PathfindingCoordinate[] tmpNeighbours = new PathfindingCoordinate[4];

    Pool<PathfindingCoordinate> coordinatePool = new Pool<PathfindingCoordinate>() {

        @Override
        protected PathfindingCoordinate newObject() {

            return new PathfindingCoordinate();
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

    public PathfindingCoordinate[][] performSearch(PathfindingCoordinate start, PathfindingCoordinate end) {

        frontier.clear();

        frontier.add(start);

        PathfindingCoordinate[][] cameFrom = new PathfindingCoordinate[width][height];

        while (!frontier.isEmpty()) {

            PathfindingCoordinate frontierCoordinate = frontier.poll();

            if (frontierCoordinate.equals(end)) {

                //coordinatePool.free(frontierCoordinate);

                break;
            }

            for (PathfindingCoordinate next : getNeighbours(frontierCoordinate)) {

                if (next != null) {

                    if (cameFrom[next.x][next.y] == null) {

                        next.origin = end;

                        frontier.add(next);

                        cameFrom[next.x][next.y] = new PathfindingCoordinate(frontierCoordinate);
                    }
                }
            }

            //System.out.println("freeing");

            //coordinatePool.free(frontierCoordinate);
        }

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

            //PathfindingCoordinate neighbour = coordinatePool.obtain();

            PathfindingCoordinate neighbour = new PathfindingCoordinate();

            neighbour.set(coordinate.x + Directions[i].x, coordinate.y + Directions[i].y);

            if (inBounds(neighbour) && grid[neighbour.x][neighbour.y] != State.Closed) {

                tmpNeighbours[i] = neighbour;
            }
            else {

                coordinatePool.free(neighbour);
            }
        }

        return tmpNeighbours;
    }
}
