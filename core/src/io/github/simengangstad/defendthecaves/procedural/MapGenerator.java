package io.github.simengangstad.defendthecaves.procedural;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingCoordinate;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingGrid;

import java.util.*;

/**
 * A tool for procedurally generating maps.
 *
 * @author simengangstad
 * @since 07/11/15
 */
public class MapGenerator {

    private static final int RectangularRoom = 0,
                             RectanglularRoomWithCentre = 1,
                             CircularRoom = 2;

    public static final int Floor = 0,
                            Solid = 10,
                            CorridorWall = 10,
                            Wall = 11,
                            Stone = 12;

    private final int[][] map;

    private final ArrayList<Rectangle> rooms = new ArrayList<>();

    private static final Random random = new Random();

    /**
     * The array used for determining where an arbitrary point is in relative to an
     * origin in terms of direction.
     */
    private PathfindingCoordinate[][] cameFrom;

    /**
     * The amount of rooms the generator will try to place.
     */
    public int requestedAmountOfRooms = 10;

    /**
     * The range of the rooms; they can be of a size between lower boundry and
     * upper boundry (inclusive).
     */
    public int lowerBoundry = 5, upperBoundry = 9;

    /**
     * The space in tiles between the edge of the sides of the map and the content
     * within it.
     */
    public int sizeOfEdge = 3;

    /**
     * The amount of times the algorithm will try to place a room within the map if it
     * doesn't find a suitable place for one straight away. Increasing this value will
     * make the algorithm slower, but the chance that it will manage to place all of
     * the rooms requested is higher.
     *
     * @see MapGenerator#requestedAmountOfRooms
     */
    public int amountOfRetries = 50;

    /**
     * The amount of times the algorithm has tried placing a room under a certain state.
     * If this value exceeds {@link MapGenerator#amountOfRetries} it will decrease the
     * {@link MapGenerator#upperBoundry} with one. This process is repeated until the
     * upper boundry is equal to the lower boundry. When this happens, it will stop the
     * process.
     */
    private int tries = 0;

    public MapGenerator(int width, int height, int seed) {

        map = new int[width][height];

        random.setSeed(seed);

        cameFrom = new PathfindingCoordinate[width][height];
    }

    public MapGenerator(int width, int height) {

        this(width, height, random.nextInt());
    }

    public int[][] generate() {

        placeRooms();
        constructCorridors();

        return map;
    }

    /**
     * Places rooms up to the amount requested, {@link MapGenerator#requestedAmountOfRooms}. A range of
     * different types of rooms can be placed.
     */
    private void placeRooms() {

        clearMap();

        if (sizeOfEdge + upperBoundry >= map.length || sizeOfEdge + upperBoundry >= map[0].length) {

            throw new RuntimeException("Distance to sides plus upper boundry can't be more than the boundries of the rectangle.");
        }

        for (int roomCount = 0; roomCount < requestedAmountOfRooms;) {

            int width = random.nextInt(upperBoundry - lowerBoundry) + lowerBoundry;
            int height = random.nextInt(upperBoundry - lowerBoundry) + lowerBoundry;

            // Check for even numbers and turn them odd if they are.
            width += (width % 2 == 0) ? 1 : 0;
            height += (height % 2 == 0) ? 1 : 0;

            int x = random.nextInt((map[0].length - 1) - sizeOfEdge * 2 - (width - 1)) + sizeOfEdge + (width - 1) / 2;
            int y = random.nextInt((map.length - 1) - sizeOfEdge * 2 - (height - 1)) + sizeOfEdge + (height - 1) / 2;

            // If the calculated x, y, width and height values end up intersecting
            // with one of the already added rooms, step will be set to false
            // and the process will be repeated.
            boolean step = false;

            for (Rectangle addedRoom : rooms) {

                if (addedRoom.intersect(x, y, width, height)) {

                    tries++;

                    if (tries > amountOfRetries && upperBoundry > lowerBoundry) {

                        upperBoundry--;

                        tries = 0;
                    }

                    if (upperBoundry == lowerBoundry) {

                        // Stop trying to add new rooms; this will end the whole loop.
                        roomCount = requestedAmountOfRooms;
                    }

                    step = true;

                    break;
                }
            }

            if (step) {

                continue;
            }

            // If we passed the intersection test without any result, tries will be set
            // to zero and by randomness a type of room will be placed with the calculated
            // coordinate and size.
            tries = 0;

            rooms.add(new Rectangle(x, y, width, height));

            switch (random.nextInt(3)) {

                case RectangularRoom:

                    constructRectangularRoom(x, y, width, height);

                    break;

                case RectanglularRoomWithCentre:

                    if (width == 3 || height == 3) {

                        constructRectangularRoom(x, y, width, height);
                    }
                    else {

                        int centreWidth = random.nextInt(width - 4);
                        int centreHeight = random.nextInt(height - 4);

                        centreWidth += (centreWidth % 2 == 0 ? 1 : 0);
                        centreHeight += (centreHeight % 2 == 0 ? 1 : 0);

                        constructRectangularRoomWithCentre(x, y, width, height, centreWidth, centreHeight);
                    }

                    break;

                case CircularRoom:

                    constructCircularRoom(x, y, width, height);

                    break;
            }

            roomCount++;
        }
    }

    /**
     * Constructs a rectangular room.
     */
    private void constructRectangularRoom(int x, int y, int width, int height) {

        for (int xs = x - (width - 1) / 2; xs <= x + (width - 1) / 2; xs++) {

            for (int ys = y - (height - 1) / 2; ys <= y + (height - 1) / 2; ys++) {

                if ((xs == (x - (width - 1) / 2) || xs == x + (width - 1) / 2) || ((ys == (y - (height - 1) / 2)) || ys == (y + (height - 1) / 2))) {

                    map[xs][ys] = Wall;
                }
                else {

                    map[xs][ys] = Floor;
                }
            }
        }

        {
            PathfindingCoordinate coordinate = new PathfindingCoordinate((x - (width - 1) / 2) + random.nextInt(width - 2) + 1, random.nextBoolean() == true ? y + (height - 1) / 2 : y - (height - 1) / 2);

            rooms.get(rooms.size() - 1).entrances.add(coordinate);

            map[coordinate.x][coordinate.y] = Stone;
        }

        {
            PathfindingCoordinate coordinate = new PathfindingCoordinate(random.nextBoolean() == true ? x + (width - 1) / 2 : x - (width - 1) / 2, (y - (height - 1) / 2) + random.nextInt(height - 2) + 1);

            rooms.get(rooms.size() - 1).entrances.add(coordinate);

            map[coordinate.x][coordinate.y] = Stone;
        }
    }

    /**
     * Constructs a rectangular room with a centre of an arbitrary size.
     */
    private void constructRectangularRoomWithCentre(int x, int y, int width, int height, int centreWidth, int centreHeight) {

        if (centreWidth >= width - 1 || centreHeight >= height - 1) {

            throw new RuntimeException("Centre diameter can't be larger than or equal to (side diamter - 1)");
        }

        if (centreWidth % 2 == 0 || centreHeight % 2 == 0) {

            throw new RuntimeException("Centre diameter has to be an odd number.");
        }

        constructRectangularRoom(x, y, width, height);

        if (centreWidth == 1 && centreHeight == 1) {

            map[x][y] = Wall;
        }
        else {

            for (int xs = x - (centreWidth - 1) / 2; xs <= x + (centreWidth - 1) / 2; xs++) {

                for (int ys = y - (centreHeight - 1) / 2; ys <= y + (centreHeight - 1) / 2; ys++) {

                    map[xs][ys] = Wall;
                }
            }
        }
    }

    /**
     * Constructs a circular room.
     */
    private void constructCircularRoom(int x, int y, int width, int height) {

        final int initialStep = Math.min(width, height);

        int steps = initialStep;

        while (steps > 0) {

            for (int i = 0; i < 360; i += 5) {

                int xs = x + (int) (MathUtils.cosDeg(i) * (width - (initialStep - steps)) / 2.0f);
                int ys = y + (int) (MathUtils.sinDeg(i) * (height - (initialStep - steps)) / 2.0f);

                if (steps >= initialStep - 1) {

                    map[xs][ys] = Wall;
                }
                else {

                    map[xs][ys] = Floor;
                }
            }

            steps--;
        }

        {

            int xs = x + (random.nextBoolean() == true ? (width - 1) / 2 : -(width - 1) / 2);
            int ys = y;

            PathfindingCoordinate coordinate = new PathfindingCoordinate(xs, ys);

            map[xs][ys] = Stone;

            rooms.get(rooms.size() - 1).entrances.add(coordinate);
        }

        {
            int xs = x;
            int ys = y + (random.nextBoolean() == true ? (height - 1) / 2 : -(height - 1) / 2);

            PathfindingCoordinate coordinate = new PathfindingCoordinate(xs, ys);

            map[xs][ys] = Stone;

            rooms.get(rooms.size() - 1).entrances.add(coordinate);
        }

        Vector2 spawnPoint = new Vector2();

        spawnPoint.x = x + (int) (MathUtils.cosDeg(random.nextInt(360)) * width / 2.0f - 1);
        spawnPoint.y = y + (int) (MathUtils.sinDeg(random.nextInt(360)) * height / 2.0f - 1);

    }

    /**
     * Constructs corridors by going from room to room and binding them together
     * with the use of an algorithm.
     */
    private void constructCorridors() {

        PathfindingGrid grid = new PathfindingGrid(map.length, map[0].length);
        PathfindingCoordinate current = new PathfindingCoordinate();

        for (int i = 0; i < rooms.size(); i++) {

            grid.clear();

            for (int x = 0; x < grid.width; x++) {

                for (int y = 0; y < grid.height; y++) {

                    if (map[x][y] == Wall) {

                        grid.set(x, y, PathfindingGrid.State.Closed);
                    }
                }
            }

            // Determine the directions from the different tiles to the current room.

            Rectangle startRoom = rooms.get(i);
            Rectangle endRoom = rooms.get((i + 1) % rooms.size());

            PathfindingCoordinate startCoordinate = startRoom.entrances.poll();
            PathfindingCoordinate endCoordinate = endRoom.entrances.poll();

            map[startCoordinate.x][startCoordinate.y] = Floor;

            cameFrom = grid.performSearch(startCoordinate, endCoordinate);

            current.set(endCoordinate);

            while (!current.equals(startCoordinate)) {

                current.set(cameFrom[current.x][current.y]);

                map[current.x][current.y] = Floor;

                if (map[current.x][current.y + 1] != Floor) {

                    map[current.x][current.y + 1] = CorridorWall;
                }

                if (map[current.x][current.y - 1] != Floor) {

                    map[current.x][current.y - 1] = CorridorWall;
                }

                if (map[current.x + 1][current.y] != Floor) {

                    map[current.x + 1][current.y] = CorridorWall;
                }

                if (map[current.x - 1][current.y] != Floor) {

                    map[current.x - 1][current.y] = CorridorWall;
                }
            }
        }
    }

    private void clearMap() {

        for (int i = 0; i < map.length; i++) {

            Arrays.fill(map[i], Stone);
        }
    }

    @Override
    public String toString() {

        StringBuilder output = new StringBuilder();

        for (int y = map.length - 1; y >= 0; y--) {

            for (int x = 0; x < map[0].length; x++) {

                switch (map[x][y]) {

                    case Floor:

                        output.append('.');

                        break;

                    case Wall:

                        output.append('#');

                        break;

                    case Stone:

                        output.append('X');

                        break;

                }

                output.append('\t');
            }

            output.append('\n');
        }

        return output.toString();
    }

    /**
     * A class for representing the different rooms within the map.
     */
    public class Rectangle {

        /**
         * The centre of the rectangle.
         */
        public final int x, y;

        public final int width, height;

        public Queue<PathfindingCoordinate> entrances = new LinkedList<>();

        Rectangle(int x, int y, int width, int height) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * @return If the given coordinate and size isSolid with this rectangle.
         */
        public boolean intersect(int x, int y, int width, int height) {

            int x1 = x - (width - 1) / 2,      y1 = y - (height - 1) / 2;
            int x2 = this.x - (this.width - 1) / 2, y2 = this.y - (this.height - 1) / 2;

            return  (x1 <= x2 + this.width) && (x1 + width >= x2) &&
                    (y1 <= y2 + this.height) && (y1 + height >= y2);
        }

        /**
         * @return If the given rectangle isSolid with this rectangle.
         */
        public boolean intersect(Rectangle rectangle) {

            return intersect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }
}
