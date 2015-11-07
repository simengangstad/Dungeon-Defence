package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import java.util.*;

public class Game extends ApplicationAdapter {

    SpriteBatch batch;
    Camera camera;
    Texture spriteSheet;

	@Override
	public void create () {

        generate();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        spriteSheet = new Texture("assets/spritesheet.png");

        camera.translate(map.length * size / 2, map[0].length * size / 2, 0.0f);
        camera.update();
    }

    int size = 10;

    @Override
    public void resize(int width, int height) {

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
	public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

            dig();
        }

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        for (int y = map.length - 1; y >= 0; y--) {

            for (int x = 0; x < map[0].length; x++) {

                switch (map[x][y]) {

                    case Wall:

                        batch.draw(spriteSheet, x * size, y * size, size, size, 0, 0, 2, 2, false, false);

                        break;

                    case Stone:

                        batch.draw(spriteSheet, x * size, y * size, size, size, 4, 0, 2, 2, false, false);

                        break;

                    case Floor:

                        batch.draw(spriteSheet, x * size, y * size, size, size, 2, 0, 2, 2, false, false);

                        break;

                    case Test:

                        batch.draw(spriteSheet, x * size, y * size, size, size, 6, 0, 2, 2, false, false);

                        break;
                }
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
    }

    public class Room {

        public final int x, y, width, height;

        Room(int x, int y, int width, int height) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean intersect(int x, int y, int width, int height) {

            int x1 = x - (width - 1) / 2,      y1 = y - (height - 1) / 2;
            int x2 = this.x - (this.width - 1) / 2, y2 = this.y - (this.height - 1) / 2;

            return  (x1 <= x2 + this.width) && (x1 + width >= x2) &&
                    (y1 <= y2 + this.height) && (y1 + height >= y2);
        }

        public boolean intersect(Room room) {

            return intersect(room.x, room.y, room.width, room.height);
        }
    }

    final Random random = new Random(12L);
    final int[][] map = new int[30][30];
    final int Stone = 0, Floor = 1, Wall = 2, Test = 3;
    Array<Room> rooms;

    private void generate() {

        placeRooms();

        digCooridors();
    }

    private void placeRooms() {

        // TOOD:
        //
        // Time for algorithm to complete
        // More types of rooms
        // Clean up

        for (int i = 0; i < map[0].length; i++) {

            Arrays.fill(map[i], Wall);
        }

        // Filling up the area with rooms

        int roomsToPlace = 15;
        int lower = 3, upper = 9;                      // The boundries of the rooms; how big the rooms can be.
        int distanceToSides = 2;                        // The distance to the sides in the rectangle; determines how close to the side we can place a room.
        int amountOfTimesToTry = 50;                    // Amount of times the algorithm will look for a room with the given boundries before decreasing the upper boundry by 1.
        int tries = 0;

        if (distanceToSides + upper >= map.length || distanceToSides + upper >= map[0].length) {

            throw new RuntimeException("Distance to sides plus upper boundry can't be more than the boundries of the rectangle.");
        }

        rooms = new Array(roomsToPlace);

        for (int roomCount = 0; roomCount < roomsToPlace;) {

            int width = random.nextInt(upper - lower) + lower;
            int height = random.nextInt(upper - lower) + lower;

            // Only want odd numbers
            width += (width % 2 == 0) ? 1 : 0;
            height += (height % 2 == 0) ? 1 : 0;

            int x = random.nextInt((map[0].length - 1) - distanceToSides * 2 - (width - 1)) + distanceToSides + (width - 1) / 2;
            int y = random.nextInt((map.length - 1) - distanceToSides * 2 - (height - 1)) + distanceToSides + (height - 1) / 2;

            boolean step = false;

            //System.out.println("Trying; " + x + ", " + y + "\t-\t" + width + ", " + height);

            for (Room addedRoom : rooms) {

                if (addedRoom.intersect(x, y, width, height)) {

                    tries++;

                    if (tries > amountOfTimesToTry && upper > lower) {

                        upper--;

                        tries = 0;
                    }

                    if (upper == lower) {

                        // Stop trying to add new rooms.
                       roomCount = roomsToPlace;
                    }

                    step = true;

                    break;
                }
            }

            if (step) {

                continue;
            }

            tries = 0;

            rooms.add(new Room(x, y, width, height));

            int roomToConstruct = random.nextInt(3);

            switch (roomToConstruct) {

                case 0:

                    constructRectangularRoom(x, y, width, height);

                    break;

                case 1:

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

                case 2:

                    constructCircularRoom(x, y, width, height);

                    break;
            }

            //printMap();

            //System.out.println(x + ", " + y + "\t-\t" + width + ", " + height);

            roomCount++;
        }

        //printMap();
    }

    Pool<Vector2> vectorPool = new Pool<Vector2>() {

        @Override
        protected Vector2 newObject() {

            return new Vector2();
        }
    };
    Vector2[][] came_from = new Vector2[map.length][map[0].length];
    Queue<Vector2> frontier = new LinkedList<>();
    Vector2 current, start = new Vector2(), goal = new Vector2();

    private void digCooridors() {

        for (int i = 0; i < 1; i++) {

            Room room = rooms.get(0);

            Vector2 front = new Vector2();
            front.set(room.x, room.y);
            frontier.add(front);

            for (int j = 0; j < came_from.length; j++) {

                Arrays.fill(came_from[j], null);
            }

            came_from[room.x][room.y] = null;

            while (!frontier.isEmpty()) {

                stepAlgorithm();
            }
        }


        start.set(rooms.get(1).x, rooms.get(1).y);
        goal.set(rooms.get(0).x, rooms.get(0).y);

        map[(int) start.x][(int) start.y] = Test;
        map[(int) goal.x][(int) goal.y] = Test;

        /*
        for (int i = 0; i < came_from.length; i++) {

            System.out.println(Arrays.toString(came_from[i]));
        }*/

    }

    private void stepAlgorithm() {

        // TOOD: This fails and isn't correct for some reason (the came from variables)

        current = frontier.poll();

        Array<Vector2> neighbours = getNeighbours(current, map.length, map[0].length);

        for (Vector2 next : neighbours) {

            if (came_from[(int) next.x][(int) next.y] == null) {

                frontier.add(next);

                came_from[(int) next.x][(int) next.y] = current;

                //System.out.println(next.x + ", " + next.y + " came from: " + current.x + ", " + current.y);

                //map[(int) next.x][(int) next.y] = Test;
            }
        }
    }

    private void dig() {

        Vector2 came = came_from[(int) start.x][(int) start.y];

        start = came;

        map[(int) start.x][(int) start.y] = Test;
    }

    private Array<Vector2> getNeighbours(Vector2 centre, int boundryWidth, int boundryHeight) {

        Array<Vector2> neighbours = new Array<>();

        // TODO: This is a problem with rectangular rooms with centres, as the coordinate of the room is inside stone

        // Up
        if (centre.y + 1 < boundryHeight) {

            if (map[(int) centre.x][(int) (centre.y + 1)] != Stone) {

                neighbours.add(new Vector2(centre.x, centre.y + 1));
            }
        }

        // Left
        if (centre.x - 1 >= 0) {

            if (map[(int) centre.x - 1][(int) (centre.y)] != Stone) {

                neighbours.add(new Vector2(centre.x - 1, centre.y));
            }
        }

        // Down
        if (centre.y - 1 >= 0) {

            if (map[(int) centre.x][(int) (centre.y - 1)] != Stone) {

                neighbours.add(new Vector2(centre.x, centre.y - 1));
            }
        }

        // Right
        if (centre.x + 1 < boundryWidth) {

            if (map[(int) centre.x + 1][(int) (centre.y)] != Stone) {

                neighbours.add(new Vector2(centre.x + 1, centre.y));
            }
        }

        return neighbours;
    }

    private void constructRectangularRoom(int x, int y, int width, int height) {

        boolean placedEntrance = false;

        for (int xs = x - (width - 1) / 2; xs <= x + (width - 1) / 2; xs++) {

            for (int ys = y - (height - 1) / 2; ys <= y + (height - 1) / 2; ys++) {

                if ((xs == (x - (width - 1) / 2) || xs == x + (width - 1) / 2) || ((ys == (y - (height - 1) / 2)) || ys == (y + (height - 1) / 2))) {

                    boolean leftOrRightCorner = (((xs == (x - (width - 1) / 2) || xs == x + (width - 1) / 2) && ys == y - (height - 1) / 2) && !(((xs == (x - (width - 1) / 2) || xs == (x + (width - 1) / 2)) && ys == y + (height - 1) / 2)));

                    if (random.nextInt(100) <= 10 && !leftOrRightCorner) {

                        map[xs][ys] = Wall;

                        placedEntrance = true;
                    }
                    else {

                        map[xs][ys] = Stone;
                    }
                }
                else {

                    map[xs][ys] = Floor;
                }
            }
        }

        if (!placedEntrance) {

            map[(x - (width - 1) / 2) + random.nextInt(width - 2) + 1][random.nextBoolean() == true ? y + (height - 1) / 2 : y - (height - 1) / 2] = Wall;
        }
    }

    private void constructRectangularRoomWithCentre(int x, int y, int width, int height, int centreWidth, int centreHeight) {

        if (centreWidth >= width - 1 || centreHeight >= height - 1) {

            throw new RuntimeException("Centre diameter can't be larger than or equal to (side diamter - 1)");
        }

        if (centreWidth % 2 == 0 || centreHeight % 2 == 0) {

            throw new RuntimeException("Centre diameter has to be an odd number.");
        }

        constructRectangularRoom(x, y, width, height);

        if (centreWidth == 1 && centreHeight == 1) {

            map[x][y] = Stone;
        }
        else {

            for (int xs = x - (centreWidth - 1) / 2; xs <= x + (centreWidth - 1) / 2; xs++) {

                for (int ys = y - (centreHeight - 1) / 2; ys <= y + (centreHeight - 1) / 2; ys++) {

                    map[xs][ys] = Stone;
                }
            }
        }
    }

    private void constructCircularRoom(int x, int y, int width, int height) {

        final int initialStep  = Math.min(width, height);

        int steps = initialStep;

        while (steps > 0) {

            for (int i = 0; i < 360; i += 5) {

                int xs = x + (int) (MathUtils.cosDeg(i) * (width - (initialStep - steps)) / 2.0f);
                int ys = y + (int) (MathUtils.sinDeg(i) * (height - (initialStep - steps)) / 2.0f);

                if (steps >= initialStep - 1) {

                    map[xs][ys] = Stone;
                }
                else {

                    map[xs][ys] = Floor;
                }
            }

            steps--;
        }

        int amountOfEntrances = random.nextInt(4) + 1;

        for (int entranceIndex = 0; entranceIndex < amountOfEntrances; entranceIndex++) {

            int degree = random.nextInt(360);

            int xs = x + (int) (MathUtils.cosDeg(degree) * width / 2.0f);
            int ys = y + (int) (MathUtils.sinDeg(degree) * height / 2.0f);

            map[xs][ys] = Wall;
        }
    }

    private void printMap() {

        for (int y = map.length - 1; y >= 0; y--) {

            for (int x = 0; x < map[0].length; x++) {

                switch (map[x][y]) {

                    case Wall:

                        System.out.print("#");

                        break;

                    case Stone:

                        System.out.print("X");

                        break;

                    case Floor:

                        System.out.print(".");

                        break;
                }

                System.out.print("\t");
            }

            System.out.println();
        }
    }
}
