package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Random;

public class Game extends ApplicationAdapter {

    public Game() {

        generate();
    }

	@Override
	public void create () {

	}

	@Override
	public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

            return (((x - (width - 1) / 2 <= this.x + (this.width - 1) / 2)   && (x - (width - 1) / 2) >= this.x - (this.width - 1) / 2) ||
                    ((x + (width - 1) / 2 <= this.x + (this.width - 1) / 2)   && (x + (width - 1) / 2) >= this.x - (this.width - 1) / 2)) &&
                   (((y - (height - 1) / 2 <= this.y + (this.height - 1) / 2) && (y - (height - 1) / 2) >= this.y - (this.height - 1) / 2) ||
                    ((y + (height - 1) / 2 <= this.y + (this.height - 1) / 2) && (y + (height - 1) / 2) >= this.y - (this.height - 1) / 2));
        }
    }

    final Random random = new Random(5L);
    final int[][] map = new int[40][40];
    final int Stone = 0, Floor = 1, Wall = 2;

    private void generate() {

        // TOOD:
        //
        // Time for algorithm to complete
        // More types of rooms
        // Clean up

        for (int i = 0; i < map[0].length; i++) {

            Arrays.fill(map[i], Wall);
        }

        // Filling up the area with rooms

        int roomsToPlace = 20;
        int lower = 3, upper = 11;                      // The boundries of the rooms; how big the rooms can be.
        int distanceToSides = 1;                        // The distance to the sides in the rectangle; determines how close to the side we can place a room.
        int amountOfTimesToTry = 100;                   // Amount of times the algorithm will look for a room with the given boundries before decreasing the upper boundry by 1.
        int tries = 0;

        if (distanceToSides + upper >= map.length || distanceToSides + upper >= map[0].length) {

            throw new RuntimeException("Distance to sides plus upper boundry can't be more than the boundries of the rectangle.");
        }

        Array<Room> rooms = new Array(roomsToPlace);

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


            if (random.nextBoolean()) {

                constructCircularRoom(x, y, width, height);
            }
            else {

                constructRectangularRoom(x, y, width, height);
            }


            System.out.println(x + ", " + y + "\t-\t" + width + ", " + height);

            roomCount++;
        }

        printMap();
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
