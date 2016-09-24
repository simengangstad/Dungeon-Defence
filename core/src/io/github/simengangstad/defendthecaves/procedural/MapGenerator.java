package io.github.simengangstad.defendthecaves.procedural;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.pathfinding.Coordinate;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingGrid;
import io.github.simengangstad.defendthecaves.scene.items.Key;

import java.util.*;

/**
 * A tool for procedurally generating maps.
 *
 * @author simengangstad
 * @since 07/11/15
 */
public class MapGenerator {

    private final int RectangularRoom = 0,
                      RectangularRoomWithCentre = 1,
                      CircularRoom = 2;

    /**
     * Free - The floor within the map.
     * Solid - Everything besides the floor within the map.
     * Unbreakable - Solid unbreakable.
     * Door - No need for explanation, huh?
     */
    public static final int Free = 0,
                            Solid = 1,
                            Unbreakable = 300,
                            Door = 1043;

    /**
     * The map.
     */
    private int[][] map;

    /**
     * The rooms in the map.
     */
    private final ArrayList<Room> rooms = new ArrayList<Room>();

    /**
     * The amount of rooms the generator will try to place.
     */
    public int requestedAmountOfRooms = 10;

    /**
     * The range of the rooms; they can be of a size between lower boundry and
     * upper boundry (inclusive).
     */
    public int lowerBoundary = 5, upperBoundary = 9;

    /**
     * The space in tiles between the edge of the sides of the map and the content
     * within it.
     */
    public int sizeOfEdge = 2;

    /**
     * The chance of the generator creating a locked room.
     */
    public int chanceOfLockedRoom = 20;

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
     * {@link MapGenerator#upperBoundary} with one. This process is repeated until the
     * upper boundry is equal to the lower boundry. When this happens, it will stop the
     * process.
     */
    private int tries = 0;

    /**
     * Initialises the map generator.
     */
    public MapGenerator(int width, int height, int seed) {

        map = new int[width][height];

        MathUtils.random.setSeed(seed);
    }

    public MapGenerator(int width, int height) {

        this(width, height, MathUtils.randomSign());
    }

    /**
     * Generates the map with a specific seed.
     *
     * @return The procedurally generated map.
     */
    public int[][] generate() {

        rooms.clear();

        clearMap();

        placeRooms();
        constructCorridors();

        for (int x = 0; x < map.length; x++) {

            for (int y = 0; y < map[0].length; y++) {

                if ((x < sizeOfEdge || map.length - sizeOfEdge <= x)/* || (y < sizeOfEdge || map[0].length - sizeOfEdge <= y)*/) {

                    map[x][y] = Unbreakable;
                }
            }
        }

        return map;
    }

    public ArrayList<Room> getRooms() {

        return rooms;
    }

    /**
     * Places rooms up to the amount requested, {@link MapGenerator#requestedAmountOfRooms}. A range of
     * different types of rooms can be placed.
     */
    private void placeRooms() {

        if (sizeOfEdge + upperBoundary >= map.length || sizeOfEdge + upperBoundary >= map[0].length) {

            throw new RuntimeException("Distance to sides plus upper boundry can't be more than the boundries of the rectangle.");
        }

        for (int roomCount = 0; roomCount < requestedAmountOfRooms;) {

            int width = MathUtils.random.nextInt(upperBoundary - lowerBoundary) + lowerBoundary;
            int height = MathUtils.random.nextInt(upperBoundary - lowerBoundary) + lowerBoundary;

            // Check for even numbers and turn them odd if they are.
            width += (width % 2 == 0) ? 1 : 0;
            height += (height % 2 == 0) ? 1 : 0;

            int x = MathUtils.random.nextInt((map[0].length - 1) - sizeOfEdge * 2 - (width - 1)) + sizeOfEdge + (width - 1) / 2;
            int y = MathUtils.random.nextInt((map.length - 1) - sizeOfEdge * 2 - (height - 1)) + sizeOfEdge + (height - 1) / 2;

            // If the calculated x, y, width and height values end up intersecting
            // with one of the already added rooms, step will be set to false
            // and the process will be repeated.
            boolean step = false;

            for (Room addedRoom : rooms) {

                if (addedRoom.intersect(x, y, width, height)) {

                    tries++;

                    if (tries > amountOfRetries && upperBoundary > lowerBoundary) {

                        upperBoundary--;

                        tries = 0;
                    }

                    if (upperBoundary == lowerBoundary) {

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

            Room room = new Room(x, y, width, height, MathUtils.random(100) < chanceOfLockedRoom);

            rooms.add(room);

            if (room.isLocked()) {

                constructRectangualarLockedRoom(x, y, width, height, MathUtils.random(100));

                room.key = new Key(new Vector2(), new Coordinate(room.getEntrance(0).x, room.getEntrance(0).y));
            }
            else {

                switch (MathUtils.random.nextInt(3)) {

                    case RectangularRoom:

                        constructRectangularRoom(x, y, width, height);

                        break;

                    case RectangularRoomWithCentre:

                        if (width == 3 || height == 3) {

                            constructRectangularRoom(x, y, width, height);
                        }
                        else {

                            int centreWidth = MathUtils.random.nextInt(width - 4);
                            int centreHeight = MathUtils.random.nextInt(height - 4);

                            centreWidth += (centreWidth % 2 == 0 ? 1 : 0);
                            centreHeight += (centreHeight % 2 == 0 ? 1 : 0);

                            constructRectangularRoomWithCentre(x, y, width, height, centreWidth, centreHeight);
                        }

                        break;

                    case CircularRoom:

                        constructCircularRoom(x, y, width, height);

                        break;
                }
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

                    map[xs][ys] = Solid;
                }
                else {

                    map[xs][ys] = Free;
                }
            }
        }

        for (int i = 0; i < MathUtils.random(2); i++) {

            Coordinate coordinate = new Coordinate((x - (width - 1) / 2) + MathUtils.random.nextInt(width - 2) + 1, MathUtils.random.nextBoolean() ? y + (height - 1) / 2 : y - (height - 1) / 2);

            rooms.get(rooms.size() - 1).addEntrance(coordinate);
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

            map[x][y] = Solid;
        }
        else {

            for (int xs = x - (centreWidth - 1) / 2; xs <= x + (centreWidth - 1) / 2; xs++) {

                for (int ys = y - (centreHeight - 1) / 2; ys <= y + (centreHeight - 1) / 2; ys++) {

                    map[xs][ys] = Solid;
                }
            }
        }
    }

    private void constructRectangualarLockedRoom(int x, int y, int width, int height, int rareness) {

        for (int xs = x - (width - 1) / 2; xs <= x + (width - 1) / 2; xs++) {

            for (int ys = y - (height - 1) / 2; ys <= y + (height - 1) / 2; ys++) {

                if ((xs == (x - (width - 1) / 2) || xs == x + (width - 1) / 2) || ((ys == (y - (height - 1) / 2)) || ys == (y + (height - 1) / 2))) {

                    map[xs][ys] = Unbreakable;
                }
                else {

                    map[xs][ys] = Free;
                }
            }
        }

        Coordinate coordinate = new Coordinate((x - (width - 1) / 2) + MathUtils.random.nextInt(width - 2) + 1, MathUtils.random.nextBoolean() ? y + (height - 1) / 2 : y - (height - 1) / 2);

        rooms.get(rooms.size() - 1).addEntrance(coordinate);

        map[coordinate.x][coordinate.y] = Door;
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

                    map[xs][ys] = Solid;
                }
                else {

                    map[xs][ys] = Free;
                }
            }

            steps--;
        }

        for (int i = 0; i < MathUtils.random(2); i++) {

            int xs = x + (MathUtils.random.nextBoolean() ? (width - 1) / 2 : -(width - 1) / 2);
            int ys = y + (MathUtils.random.nextBoolean() ? (height - 1) / 2 : -(height - 1) / 2);

            Coordinate coordinate = new Coordinate(xs, ys);

            rooms.get(rooms.size() - 1).addEntrance(coordinate);
        }

        Vector2 spawnPoint = new Vector2();

        spawnPoint.x = x + (int) (MathUtils.cosDeg(MathUtils.random.nextInt(360)) * width / 2.0f - 1);
        spawnPoint.y = y + (int) (MathUtils.sinDeg(MathUtils.random.nextInt(360)) * height / 2.0f - 1);

    }

    /**
     * Constructs corridors by going from room to room and binding them together
     * with the use of an algorithm.
     */
    private void constructCorridors() {

        if (Game.Debug) System.out.println("\n---- Constructing corridors ----");

        PathfindingGrid grid = new PathfindingGrid(map.length, map[0].length);
        Coordinate current = new Coordinate();

        int index = 0;

        while (index < rooms.size()) {

            for (int x = 0; x < grid.width; x++) {

                for (int y = 0; y < grid.height; y++) {

                    if (map[x][y] == Unbreakable) {

                        grid.set(x, y, PathfindingGrid.State.Closed);
                    }
                    else {

                        grid.set(x, y, PathfindingGrid.State.Open);
                    }
                }
            }

            // Determine the directions from the different tiles to the current room.

            Room startRoom = rooms.get(index);
            Room endRoom = rooms.get((index + 1) % rooms.size());

            if (Game.Debug) System.out.println("Start room is locked locked: " + startRoom.locked);

            int a = index + 1;

            while (endRoom.locked) {

                endRoom = rooms.get((a++ + 1) % rooms.size());
            }

            if (startRoom.getEntrancesLeftInQueue() == 0) {

                boolean changed = false;

                for (int j = 0; j < rooms.size(); j++) {

                    Room newRoom = rooms.get(j);

                    if (newRoom == startRoom || newRoom == endRoom) {

                        continue;
                    }

                    if (newRoom.getEntrancesLeftInQueue() > 0) {

                        startRoom = newRoom;

                        changed = true;
                    }
                }

                // No rooms with entrances left in the queues
                if (!changed) {

                    break;
                }
            }

            Coordinate startCoordinate = startRoom.pollEntrance();
            Coordinate endCoordinate = new Coordinate(endRoom.centreX, endRoom.centreY);

            if (Game.Debug) System.out.println("Constructing corridor between " + endCoordinate + " and " + startCoordinate);

            map[endCoordinate.x][endCoordinate.y] = Free;

            Coordinate[][] cameFrom = new Coordinate[grid.width][grid.height];

            for (int x = 0; x < cameFrom.length; x++) {

                for (int y = 0; y < cameFrom[0].length; y++) {

                    cameFrom[x][y] = new Coordinate(-1, -1);
                }
            }

            grid.performSearch(endCoordinate.x, endCoordinate.y, startCoordinate.x, startCoordinate.y, cameFrom);

            current.set(endCoordinate.x, endCoordinate.y);

            while (!current.equals(startCoordinate)) {

                Coordinate cameFromCoordinate = cameFrom[current.x][current.y];

                if (cameFromCoordinate.x == -1) {

                    System.err.println("Current came from null...");

                    break;
                }

                current.set(cameFromCoordinate);

                if (!startRoom.locked && map[current.x][current.y] == Free && !current.equals(endCoordinate) && !endRoom.isInside(current.x, current.y)) {

                    if (Game.Debug) System.out.println("Corridor found another free tile before the end destination. Stopped at " + current + ".");

                    break;
                }


                if (map[current.x][current.y] != Unbreakable && map[current.x][current.y] != Door) {

                    map[current.x][current.y] = Free;
                }

                // Walls for the corridor
                if (validCoordinate(current.x, current.y + 1) && map[current.x][current.y + 1] != Free && map[current.x][current.y + 1] != Unbreakable && map[current.x][current.y + 1] != Door) {

                    map[current.x][current.y + 1] = Solid;
                }

                if (validCoordinate(current.x, current.y - 1) && map[current.x][current.y - 1] != Free && map[current.x][current.y - 1] != Unbreakable && map[current.x][current.y - 1] != Door) {

                    map[current.x][current.y - 1] = Solid;
                }

                if (validCoordinate(current.x + 1, current.y) && map[current.x + 1][current.y] != Free && map[current.x + 1][current.y] != Unbreakable && map[current.x + 1][current.y] != Door) {

                    map[current.x + 1][current.y] = Solid;
                }

                if (validCoordinate(current.x - 1, current.y) && map[current.x - 1][current.y] != Free && map[current.x - 1][current.y] != Unbreakable && map[current.x - 1][current.y] != Door) {

                    map[current.x - 1][current.y] = Solid;
                }
            }

            System.out.println();

            index++;
        }

        if (Game.Debug) System.out.println("---- Finished constructing corridors ----\n");
    }

    private boolean validCoordinate(int x, int y) {

        return (0 <= x && x < map.length) && (0 <= y && y < map[0].length);
    }

    /**
     * Clears the map to a solid state.
     */
    private void clearMap() {

        for (int[] list : map) {

            Arrays.fill(list, Solid);
        }
    }

    /**
     * A class for representing the different rooms within the map.
     */
    public static class Room {

        /**
         * The centre of the rectangle.
         */
        public final int centreX, centreY;

        /**
         * The dimensions of the whole room.
         */
        public final int width, height;

        private Queue<Coordinate> entrancesQueue = new LinkedList<Coordinate>();

        private List<Coordinate> entrancesList = new ArrayList<Coordinate>();

        private final boolean locked;

        /**
         * A key that points to this rooms entrance if it's locked.
         */
        private Key key;

        Room(int centreX, int centreY, int width, int height, boolean locked) {

            this.centreX = centreX;
            this.centreY = centreY;
            this.width = width;
            this.height = height;
            this.locked = locked;
        }

        public void addEntrance(Coordinate entrance) {

            entrancesQueue.add(entrance);
            entrancesList.add(entrance);
        }

        public int getEntrancesLeftInQueue() {

            return entrancesQueue.size();
        }

        public Coordinate pollEntrance() {

            return entrancesQueue.poll();
        }

        public int getAmountOfEntrances() {

            return entrancesList.size();
        }

        public Coordinate getEntrance(int index) {

            return entrancesList.get(index);
        }

        public Key getKey() {

            return key;
        }

        public boolean isLocked() {

            return locked;
        }

        /**
         * @return If the given room intersects with this room.
         */
        public boolean intersect(int x, int y, int width, int height) {

            int x1 = x - (width - 1) / 2,      y1 = y - (height - 1) / 2;
            int x2 = this.centreX - (this.width - 1) / 2, y2 = this.centreY - (this.height - 1) / 2;

            return  (x1 <= x2 + this.width) && (x1 + width >= x2) &&
                    (y1 <= y2 + this.height) && (y1 + height >= y2);
        }

        public boolean isInside(int x0, int y0) {

            int x = this.centreX - width / 2;
            int y = this.centreY - height / 2;

            return (x <= x0 && x0 <= x + width) && (y <= y0 && y0 <= y + height);
        }

        /**
         * @return If the given room intersects with this room.
         */
        public boolean intersect(Room room) {

            return intersect(room.centreX, room.centreY, room.width, room.height);
        }

        @Override
        public String toString() {

            return "Centre (" + centreX + ", " + centreY + ")" + ", dimension (" + width + ", " + height + ")";
        }
    }
}
