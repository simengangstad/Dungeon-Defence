package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 13/11/15
 */
public class Map {

    // TODO: Can be turned into a game object.

    /**
     * Coordinates for the different types of tiles within the sprite sheet. These coordinates
     * are to be multiplied with
     */
    private static final Vector2[] Tiles = new Vector2[] {

            // Open
            new Vector2(0, 0),

            // Filling
            new Vector2(1, 0),

            // Wall facing left and down
            new Vector2(2, 0),
            new Vector2(2, 1),
            new Vector2(2, 2),

            // Wall facing down
            new Vector2(3, 0),
            new Vector2(3, 1),
            new Vector2(3, 2),

            // Wall facing right and down
            new Vector2(4, 0),
            new Vector2(4, 1),
            new Vector2(4, 2),

            // Wall facing left
            new Vector2(5, 0),
            new Vector2(5, 1),
            new Vector2(5, 2),

            // Wall facing left and up
            new Vector2(6, 0),
            new Vector2(6, 1),
            new Vector2(6, 2),

            // Wall facing up
            new Vector2(7, 0),
            new Vector2(7, 1),
            new Vector2(7, 2),

            // Wall facing right and up
            new Vector2(8, 0),
            new Vector2(8, 1),
            new Vector2(8, 2),

            // Wall facing right
            new Vector2(9, 0),
            new Vector2(9, 1),
            new Vector2(9, 2),

            // Wall facing down, left and up
            new Vector2(10, 0),
            new Vector2(10, 1),
            new Vector2(10, 2),

            // Wall facing down, right and up
            new Vector2(11, 0),
            new Vector2(11, 1),
            new Vector2(11, 2),

            // Wall facing down and up
            new Vector2(12, 0),
            new Vector2(12, 1),
            new Vector2(12, 2),

            // Wall facing left, up and right
            new Vector2(13, 0),
            new Vector2(13, 1),
            new Vector2(13, 2),

            // Wall facing left, down and right
            new Vector2(14, 0),
            new Vector2(14, 1),
            new Vector2(14, 2),

            // Spawn facing down
            new Vector2(15, 0),
            new Vector2(15, 1),
            new Vector2(15, 2),

            // Spawn facing up
            new Vector2(16, 0),
            new Vector2(16, 1),
            new Vector2(16, 2),

            // Spawn facing right
            new Vector2(17, 0),
            new Vector2(17, 1),
            new Vector2(17, 2),

            // Spawn facing left
            new Vector2(18, 0),
            new Vector2(18, 1),
            new Vector2(18, 2),

            // Up left corner
            new Vector2(19, 0),

            // Up right corner
            new Vector2(20, 0),

            // Down right corner
            new Vector2(21, 0),

            // Down left corner
            new Vector2(22, 0),

            // Edge facing up
            new Vector2(7, 5),

            // Edge facing right
            new Vector2(8, 5),

            // Edge facing right wall
            new Vector2(9, 5),

            // Edge facing left wall
            new Vector2(10, 5),

            // Wall facing left, down, right and up
            new Vector2(23, 0),
            new Vector2(23, 1),
            new Vector2(23, 2),

            // Wall filling

            new Vector2(0, 1),



            // UNBREAKABLE TILES

            // Wall facing left and down
            new Vector2(2, 15),

            // Wall facing down
            new Vector2(3, 15),

            // Wall facing right and down
            new Vector2(4, 15),

            // Wall facing left
            new Vector2(5, 15),

            // Wall facing left and up
            new Vector2(6, 15),

            // Wall facing up
            new Vector2(7, 15),

            // Wall facing right and up
            new Vector2(8, 15),

            // Wall facing right
            new Vector2(9, 15),

            // Wall facing down, left and up
            new Vector2(10, 15),

            // Wall facing down, right and up
            new Vector2(11, 15),

            // Wall facing down and up
            new Vector2(12, 15),

            // Wall facing left, up and right
            new Vector2(13, 15),

            // Wall facing left, down and right
            new Vector2(14, 15),

            // Up left corner
            new Vector2(19, 15),

            // Up right corner
            new Vector2(20, 15),

            // Down right corner
            new Vector2(21, 15),

            // Down left corner
            new Vector2(22, 15),

            // Edge facing up
            new Vector2(15, 15),

            // Edge facing right
            new Vector2(16, 15),

            // Edge facing right wall
            new Vector2(17, 15),

            // Edge facing left wall
            new Vector2(18, 15),


            // Wall facing left, down, right and up
            new Vector2(23, 15),
    };

    /**
     *  Identifiers for the different tiles.
     */
    private static final int

            Floor           = 0,
            Filling         = 1,
            WallFacingLeftDown0 = 2,
            WallFacingLeftDown1 = 3,
            WallFacingLeftDown2 = 4,
            WallFacingDown0 = 5,
            WallFacingDown1 = 6,
            WallFacingDown2 = 7,
            WallFacingRightDown0 = 8,
            WallFacingRightDown1 = 9,
            WallFacingRightDown2 = 10,
            WallFacingLeft0 = 11,
            WallFacingLeft1 = 12,
            WallFacingLeft2 = 13,
            WallFacingLeftUp0 = 14,
            WallFacingLeftUp1 = 15,
            WallFacingLeftUp2 = 16,
            WallFacingUp0 = 17,
            WallFacingUp1 = 18,
            WallFacingUp2 = 19,
            WallFacingRightUp0 = 20,
            WallFacingRightUp1 = 21,
            WallFacingRightUp2 = 22,
            WallFacingRight0 = 23,
            WallFacingRight1 = 24,
            WallFacingRight2 = 25,
            WallFacingDownLeftUp0 = 26,
            WallFacingDownLeftUp1 = 27,
            WallFacingDownLeftUp2 = 28,
            WallFacingUpDown0 = 29,
            WallFacingUpDown1 = 30,
            WallFacingUpDown2 = 31,
            WallFacingDownRightUp0 = 32,
            WallFacingDownRightUp1 = 33,
            WallFacingDownRightUp2 = 34,
            WallFacingLeftUpRight0 = 35,
            WallFacingLeftUpRight1 = 36,
            WallFacingLeftUpRight2 = 37,
            WallFacingLeftDownRight0 = 38,
            WallFacingLeftDownRight1 = 39,
            WallFacingLeftDownRight2 = 40,
            SpawnFacingDown0 = 41,
            SpawnFacingDown1 = 42,
            SpawnFacingDown2 = 43,
            SpawnFacingUp0 = 44,
            SpawnFacingUp1 = 45,
            SpawnFacingUp2 = 46,
            SpawnFacingRight0 = 47,
            SpawnFacingRight1 = 48,
            SpawnFacingRight2 = 49,
            SpawnFacingLeft0 = 50,
            SpawnFacingLeft1 = 51,
            SpawnFacingLeft2 = 52,
            CornerUpLeft = 53,
            CornerUpRight = 54,
            CornerDownLeft = 55,
            CornerDownRight = 56,
            EdgeFacingUp = 57,
            EdgeFacingRight = 58,
            EdgeFacingRightWall = 59,
            EdgeFacingLeft = 60,
            WallFacingAllSides0 = 61,
            WallFacingAllSides1 = 62,
            WallFacingAllSides2 = 63,
            WallFilling         = 64,

            UnbreakableWallFacingLeftDown = 65,
            UnbreakableWallFacingDown = 66,
            UnbreakableWallFacingRightDown = 67,
            UnbreakableWallFacingLeft = 68,
            UnbreakableWallFacingLeftUp = 69,
            UnbreakableWallFacingUp = 70,
            UnbreakableWallFacingRightUp = 71,
            UnbreakableWallFacingRight = 72,
            UnbreakableWallFacingDownLeftUp = 73,
            UnbreakableWallFacingUpDown = 74,
            UnbreakableWallFacingDownRightUp = 75,
            UnbreakableWallFacingLeftUpRight = 76,
            UnbreakableWallFacingLeftDownRight = 77,
            UnbreakableCornerUpLeft = 78,
            UnbreakableCornerUpRight = 79,
            UnbreakableCornerDownLeft = 80,
            UnbreakableCornerDownRight = 81,
            UnbreakableEdgeFacingUp = 82,
            UnbreakableEdgeFacingRight = 83,
            UnbreakableEdgeFacingRightWall = 84,
            UnbreakableEdgeFacingLeft = 85,
            UnbreakableWallFacingAllSides = 86;


    private final Vector2 tmpWall = new Vector2();

    /**
     * The states of the tiles.
     */
    public static final int Open = 0,
                            SolidIntact = 1,
                            SolidSlightlyBroken = 2,
                            SolidBroken = 3,
                            SolidUnbreakable = MapGenerator.Unbreakable,
                            SpawnIntact = 10,
                            SpawnSlightlyBroken = 11,
                            SpawnBroken = 12,
                            Door = MapGenerator.Door;

    /**
     * The renderable tile map.
     */
    private final int[][] tileMap;

    /**
     * The collidable tile map.
     */
    private final int[][] collidableMap;

    /**
     * The size of each tile in pixels.
     */
    public static final int TileSizeInPixelsInWorldSpace = 160;

    /**
     * The ratio between the (tile size and the cell size) * 2.
     */
    public final int Subdivision;

    /**
     * The tile size divided by the subdivision..
     */
    public final float CellSize;

    /**
     * The amount of cells there are in the map.
     */
    public final int GridSize;

    /**
     * The amount of steps we do per collions check.
     */
    public final float StepSize;

    /**
     * The collidable tile identifier.
     */
    private final int ExpandedObstacle = 99;

    /**
     * The spawn points.
     */
    private final ArrayList<Vector2> spawnPoints = new ArrayList<>();

    /**
     * Reference to the player position
     */
    public Vector2 playerPosition;

    private final MapGenerator.Room[] rooms;

    /**
     * Tmp values.
     */
    private final Vector2 tmpClosestPointInCell = new Vector2(),
                          tmpTargetPosition = new Vector2(),
                          tmpDelta = new Vector2(),
                          tmpDiff = new Vector2(),
                          tmpResult = new Vector2();

    public Map(int width, int height, int requestedAmountOfRooms, int lowerBoundry, int upperBoundry, int seed, Vector2 playerSize, int enemySpawnPoints, int spawnpointRadius) {

        MapGenerator mapGenerator = new MapGenerator(width, height, seed);

        mapGenerator.requestedAmountOfRooms = requestedAmountOfRooms;
        mapGenerator.lowerBoundary = lowerBoundry;
        mapGenerator.upperBoundary = upperBoundry;
        mapGenerator.chanceOfLockedRoom = 10;

        MathUtils.random.setSeed(seed);

        tileMap = mapGenerator.generate();

        if (TileSizeInPixelsInWorldSpace % playerSize.x != 0) {

            throw new RuntimeException("Invalid scale between player size and tile size.");
        }

        rooms = mapGenerator.getRooms();

        Subdivision = (int) (TileSizeInPixelsInWorldSpace / playerSize.x) * 2;

        CellSize = (float) TileSizeInPixelsInWorldSpace / Subdivision;
        GridSize = getWidth() * Subdivision;
        StepSize = CellSize * 0.25f;

        collidableMap = new int[GridSize][GridSize];

        updateCollidableMapWithinBoundry(0, 0, getWidth(), getHeight());

        int spawnPointsLeftToPlace = enemySpawnPoints;
        int amountOfTries = 3;

        while (0 < spawnPointsLeftToPlace && 0 < amountOfTries) {

            for (int y = 0; y < getHeight(); y++) {

                for (int x = 0; x < getWidth(); x++) {

                    if (tileMap[x][y] == MapGenerator.Unbreakable) {

                        continue;
                    }

                    if (isSolid(x, y) && surroundedByAtLeastOneNonSolids(x, y) && MathUtils.random(100) < 20 && !spawnpointIsCloseToPositionWithinRadius(x, y, spawnpointRadius)) {

                        if (    ((isValidTile(x - 1, y) && isSolid(x - 1, y)) && (isValidTile(x + 1, y) && isSolid(x + 1, y))) ||
                                ((isValidTile(x, y - 1) && isSolid(x, y - 1)) && (isValidTile(x, y + 1) && isSolid(x, y + 1)))  ) {

                            spawnPoints.add(new Vector2(x, y));

                            tileMap[x][y] = SpawnIntact;

                            for (int xs = x - 1; xs <= x + 1; xs++) {

                                for (int ys = y - 1; ys <= y + 1; ys++) {

                                    int tile = tileMap[xs][ys];

                                    if (tile == SolidIntact) {

                                        tileMap[xs][ys] = SolidUnbreakable;
                                    }
                                }
                            }

                            spawnPointsLeftToPlace--;

                            amountOfTries = 3;
                        }
                    }

                    if (spawnPointsLeftToPlace <= 0) {

                        break;
                    }
                }

                if (spawnPointsLeftToPlace <= 0) {

                    break;
                }
            }

            amountOfTries--;
        }
    }

    public MapGenerator.Room[] getRooms() {

        return rooms;
    }

    public int get(int x, int y) {

        if (!isValidTile(x, y)) {

            throw new RuntimeException("Position is out of boundry.");
        }

        return tileMap[x][y];
    }

    public void set(int x, int y, int value) {

        if (!isValidTile(x, y)) {

            throw new RuntimeException("Position is out of boundry.");
        }

        if (tileMap[x][y] == value) {

            return;
        }

        tileMap[x][y] = value;

        updateCollidableMapWithinBoundry(x - 2, y - 2, x + 2, y + 2);
    }

    private void updateCollidableMapWithinBoundry(int x1, int y1, int x2, int y2) {

        for (int xs = x1 * Subdivision; xs < x2 * Subdivision; xs++) {

            if (GridSize <= xs) {

                break;
            }

            for (int ys = y1 * Subdivision; ys < y2 * Subdivision; ys++) {

                if (GridSize <= ys) {

                    break;
                }

                if (cellIsValid(xs, ys)) collidableMap[xs][ys] = tileMap[(int) Math.ceil(xs / Subdivision)][(int) Math.ceil(ys / Subdivision)];
            }
        }

        for (int xs = x1 * Subdivision; xs < x2 * Subdivision; xs++) {

            if (GridSize <= xs) {

                break;
            }

            for (int ys = y1 * Subdivision; ys < y2 * Subdivision; ys++) {

                if (GridSize <= ys) {

                    break;
                }

                if (!cellIsSolid(xs, ys) && cellIsValid(xs, ys)) {

                    // Right
                    if (cellIsValid(xs + 1, ys) && cellIsSolid(xs + 1, ys)) {

                        collidableMap[xs][ys] = ExpandedObstacle;
                    }

                    // Left
                    else if (cellIsValid(xs - 1, ys) && cellIsSolid(xs - 1, ys)) {

                        collidableMap[xs][ys] = ExpandedObstacle;
                    }

                    // Up
                    else if (cellIsValid(xs, ys + 1) && cellIsSolid(xs, ys + 1)) {

                        collidableMap[xs][ys] = ExpandedObstacle;
                    }

                    // Down
                    else if (cellIsValid(xs, ys - 1) && cellIsSolid(xs, ys - 1)) {

                        collidableMap[xs][ys] = ExpandedObstacle;
                    }

                    // Corners
                    else if ((cellIsValid(xs + 1, ys + 1) && cellIsSolid(xs + 1, ys + 1)) ||
                             (cellIsValid(xs - 1, ys - 1) && cellIsSolid(xs - 1, ys - 1)) ||
                             (cellIsValid(xs + 1, ys - 1) && cellIsSolid(xs + 1, ys - 1)) ||
                             (cellIsValid(xs - 1, ys + 1) && cellIsSolid(xs - 1, ys + 1))) {

                        collidableMap[xs][ys] = ExpandedObstacle;
                    }
                }
            }
        }
    }

    public Vector2[] getSpawnPoints() {

        return spawnPoints.toArray(new Vector2[spawnPoints.size()]);
    }

    public boolean spawnpointIsCloseToPositionWithinRadius(int x, int y, int radius) {

        for (int xs = x - radius; xs < x + radius; xs++) {

            for (int ys = y - radius; ys < y + radius; ys++) {

                if (isValidTile(xs, ys) && tileMap[xs][ys] == SpawnIntact) {

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return If a given tile is solid.
     */
    public boolean isSolid(int x, int y) {

        if (!isValidTile(x, y)) {

            return false;
        }

        return tileMap[x][y] >= SolidIntact;
    }

    /**
     * @return If a given cell is solid.
     */
    private boolean cellIsSolid(int x, int y) {

        if (!cellIsValid(x, y)) {

            return false;
        }

        return collidableMap[x][y] >= SolidIntact && collidableMap[x][y] != ExpandedObstacle;
    }

    public boolean isValidTile(int x, int y) {

        return (0 <= x && x < getWidth()) && (0 <= y && y < getHeight());
    }

    public boolean surroundedByAtLeastOneNonSolids(int x, int y) {

        return  (isValidTile(x    , y + 1) && !isSolid(x    , y + 1)) ||
                (isValidTile(x    , y - 1) && !isSolid(x    , y - 1)) ||
                (isValidTile(x + 1, y    ) && !isSolid(x + 1, y    )) ||
                (isValidTile(x - 1, y    ) && !isSolid(x - 1, y    ));
    }

    /**
     * @return The width of the tile map.
     */
    public int getWidth() {

        return tileMap.length;
    }

    /**
     * @return The height of the tile map.
     */
    public int getHeight() {

        return tileMap[0].length;
    }

    public boolean retrieveCollisionPoint(Vector2 position, Vector2 direction, float speed, Vector2 point) {

        tmpDelta.set(direction).scl(speed * Gdx.graphics.getDeltaTime());
        tmpTargetPosition.set(position).add(tmpDelta);

        // Find the cell corresponding to the target position.
        final int cellX = (int)(tmpTargetPosition.x / CellSize);
        final int cellY = (int)(tmpTargetPosition.y / CellSize);

        if (cellIsValid(cellX, cellY) && collidableMap[cellX][cellY] != Open) {

            point.set(tmpTargetPosition);

            return true;
        }

        System.out.println("1: " + position);

        position.set(tmpTargetPosition);

        System.out.println("2: " + position);

        return false;
    }

    /**
     * Resolves the collision with the map (if any).
     *
     * @param position The current position (this variable will be changed and returned by reference)
     * @param direction The direction of the movement.
     * @param speed The speed of the movement.
     *
     * @return If there was a collision.
     */
    public boolean resolveCollision(Vector2 position, Vector2 direction, float speed) {

        //direction.nor();

        // Subdivide the move into steps and process each step in turn.
        float remainingDistance = speed * Gdx.graphics.getDeltaTime();

        while (remainingDistance > 0.0f) {

            final float distance = Math.min(remainingDistance, StepSize);
            remainingDistance -= distance;

            // Compute the target position for this step.
            tmpTargetPosition.set(position);
            tmpDelta.set(direction);

            tmpDelta.scl(distance);
            tmpTargetPosition.add(tmpDelta);

            // Find the cell corresponding to the target position.
            final int cellX = (int)(tmpTargetPosition.x / CellSize);
            final int cellY = (int)(tmpTargetPosition.y / CellSize);

            // If the cell is empty, we just move to the new position and
            // continue.
            if (isValidTile(cellX, cellY) && collidableMap[cellX][cellY] == Open) {

                position.set(tmpTargetPosition);
            }
            else {

                // This is the tricky part. Here, we need to project the
                // target point into an empty cell over the shortest
                // distance possible.
                float minDistanceSquared = Float.MAX_VALUE;

                for (int x = cellX - 1; x <= cellX + 1; x++) {

                    for (int y = cellY - 1; y <= cellY + 1; y++) {

                        if (cellIsValid(x, y) && collidableMap[x][y] == Open) {

                            final Vector2 closest = closestPointInCell(tmpTargetPosition, x, y);
                            tmpDiff.set(closest);
                            tmpDiff.sub(tmpTargetPosition);

                            final float distanceSquared = tmpDiff.len2();

                            if (distanceSquared < minDistanceSquared) {

                                minDistanceSquared = distanceSquared;
                                tmpResult.set(closest);
                            }
                        }
                    }
                }

                // If our step size is small enough, 'result' should never
                // be null, but you'd probably want to check for that and
                // handle failure more gracefully here.
                position.set(tmpResult);

                return true;
            }
        }

        return false;
    }

    private Vector2 closestPointInCell(final Vector2 p, final int x, final int y) {

        final float minX = x * CellSize;
        final float minY = y * CellSize;
        final float maxX = minX + CellSize;
        final float maxY = minY + CellSize;

        tmpClosestPointInCell.set(

                Math.min(Math.max(p.x, minX), maxX),
                Math.min(Math.max(p.y, minY), maxY)
        );

        return tmpClosestPointInCell;
    }

    /**
     * @return If the specified cell is valid.
     */
    private boolean cellIsValid(final int x, final int y) {

        return x >= 0 && x < GridSize && y >= 0 && y < GridSize;
    }

    /**
     * Draws the map with the given batch. It will only draw the visible tiles.
     */
    public void draw(SpriteBatch batch, float scaleFactor) {

        for (int y = (int) (playerPosition.y + (Gdx.graphics.getHeight() / 2.0f) * scaleFactor) / TileSizeInPixelsInWorldSpace; y >= (playerPosition.y - (Gdx.graphics.getHeight() / 2.0f) * scaleFactor - TileSizeInPixelsInWorldSpace) / TileSizeInPixelsInWorldSpace; y--) {

            for (int x = (int) (playerPosition.x - (Gdx.graphics.getWidth() / 2.0f) * scaleFactor) / TileSizeInPixelsInWorldSpace; x < (playerPosition.x + (Gdx.graphics.getWidth() / 2.0f) * scaleFactor) / TileSizeInPixelsInWorldSpace; x++) {

                // Outside bounds
                if (x < 0 || tileMap.length <= x || y < 0 || tileMap[0].length <= y) {

                    continue;
                }

                boolean outsideBoundsHorisontallyUpper = tileMap.length <= (x + 1);
                boolean outsideBoundsHorisontallyLower = (x - 1) < 0;
                boolean outsideBoundsVerticallyUpper = tileMap[0].length <= (y + 1);
                boolean outsideBoundsVerticallyLower = (y - 1) < 0;

                switch (tileMap[x][y]) {

                    case Open:

                        drawOpenTile(x, y, batch);

                        break;

                    case SolidIntact:
                    case SolidSlightlyBroken:
                    case SolidBroken:

                        drawSolidTile(x, y, batch, outsideBoundsHorisontallyUpper, outsideBoundsHorisontallyLower, outsideBoundsVerticallyUpper, outsideBoundsVerticallyLower);

                        break;

                    case SolidUnbreakable:

                        drawSolidUnbreakable(x, y, batch, outsideBoundsHorisontallyUpper, outsideBoundsHorisontallyLower, outsideBoundsVerticallyUpper, outsideBoundsVerticallyLower);

                        break;

                    case SpawnIntact:
                    case SpawnBroken:
                    case SpawnSlightlyBroken:

                        drawSpawnTile(x, y, batch, outsideBoundsHorisontallyUpper, outsideBoundsHorisontallyLower, outsideBoundsVerticallyUpper, outsideBoundsVerticallyLower);

                        break;

                    case Door:

                        batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 96, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);

                        break;

                }
            }
        }

        if (Game.DebubDraw) {

            for (int y = (int) ((playerPosition.y + Gdx.graphics.getHeight() / 2.0f) / CellSize); y >= (playerPosition.y - Gdx.graphics.getHeight() / 2.0f - CellSize) / CellSize; y--) {

                for (int x = (int) ((playerPosition.x - Gdx.graphics.getWidth() / 2.0f) / CellSize); x < playerPosition.x + (Gdx.graphics.getWidth() / 2.0f) / CellSize; x++) {

                    // Outside bounds
                    if (!cellIsValid(x, y)) {

                        continue;
                    }

                    if (collidableMap[x][y] == ExpandedObstacle) {

                        batch.draw(Game.SpriteSheet, x * CellSize, y * CellSize, CellSize, CellSize, 48, 80, 16, 16, false, false);
                    }
                }
            }
        }
    }

    private void drawOpenTile(int x, int y, SpriteBatch batch) {

        batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 0, 0, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);

        // Shadows
        if (tileMap[x][y + 1] != Open && tileMap[x - 1][y + 1] != Open && tileMap[x + 1][y + 1] != Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 48, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x][y + 1] != Open && tileMap[x - 1][y + 1] == Open && tileMap[x + 1][y + 1] != Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 32, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x][y + 1] != Open && tileMap[x - 1][y + 1] == Open && tileMap[x + 1][y + 1] == Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 208, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x - 1][y] != Open && tileMap[x][y + 1] == Open && tileMap[x - 1][y + 1] != Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 144, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x - 1][y] != Open && tileMap[x][y + 1] != Open && tileMap[x - 1][y + 1] != Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 144, 64, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x - 1][y] != Open && tileMap[x - 1][y + 1] == Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 128, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x][y + 1] != Open && tileMap[x + 1][y + 1] == Open && tileMap[x - 1][y + 1] != Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 64, 64, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x - 1][y + 1] != Open && tileMap[x][y + 1] == Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 80, 64, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }

        if (tileMap[x][y + 1] != Open && tileMap[x - 1][y + 1] == Open && tileMap[x + 1][y + 1] == Open) {

            batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 224, 48, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);
        }
    }

    private void drawSolidTile(int x, int y, SpriteBatch batch, boolean outsideBoundsHorisontallyUpper, boolean outsideBoundsHorisontallyLower, boolean outsideBoundsVerticallyUpper, boolean outsideBoundsVerticallyLower) {

        batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 0, 0, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);


        tmpWall.set(Tiles[WallFilling]);

        drawWall(batch, x, y);

        // Walls facing down and up
        if ((!outsideBoundsHorisontallyLower && tileMap[x - 1][y] != Open) && (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] != Open)) {

            if (!outsideBoundsVerticallyLower && tileMap[x][y - 1] == Open) {

                tmpWall.set(Tiles[WallFacingDown0 + (tileMap[x][y] != SolidUnbreakable ? (tileMap[x][y] - SolidIntact) : 0)]);
            }
            else if (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] == Open) {

                tmpWall.set(Tiles[WallFacingUp0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);
            }
        }

        // Walls facing left and right
        if ((!outsideBoundsVerticallyLower && tileMap[x][y - 1] != Open) && (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Open)) {

            if (!outsideBoundsHorisontallyLower && !outsideBoundsHorisontallyUpper && tileMap[x - 1][y] == Open && tileMap[x + 1][y] == Open) {

                tmpWall.set(Tiles[WallFacingLeft0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                drawWall(batch, x, y);

                tmpWall.set(Tiles[EdgeFacingRight]);

                drawWall(batch, x, y);

                return;
            }
            else if (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] == Open) {

                tmpWall.set(Tiles[WallFacingRight0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);
            }
            else if (!outsideBoundsHorisontallyLower && tileMap[x - 1][y] == Open) {

                tmpWall.set(Tiles[WallFacingLeft0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);
            }
        }

        // Corners
        if (!outsideBoundsHorisontallyUpper && !outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Open && tileMap[x + 1][y] != Open && tileMap[x + 1][y + 1] == Open && (outsideBoundsVerticallyLower || tileMap[x][y - 1] != Open)) {

            tmpWall.set(Tiles[CornerDownLeft]);

            if (tileMap[x - 1][y] == Open) {

                drawWall(batch, x,  y);

                tmpWall.set(Tiles[EdgeFacingLeft]);

                drawWall(batch, x, y);

                return;
            }
        }

        if (!outsideBoundsHorisontallyLower && !outsideBoundsVerticallyUpper && !outsideBoundsVerticallyLower && tileMap[x][y + 1] != Open && tileMap[x - 1][y] != Open && tileMap[x - 1][y + 1] == Open && tileMap[x][y - 1] != Open && (outsideBoundsHorisontallyUpper || tileMap[x + 1][y] != Open)) {

            tmpWall.set(Tiles[CornerDownRight]);
        }

        if (!outsideBoundsHorisontallyUpper && !outsideBoundsVerticallyLower && tileMap[x][y - 1] != Open && tileMap[x + 1][y] != Open && tileMap[x + 1][y - 1] == Open && (x - 1 < 0 || tileMap[x - 1][y] != Open)) {

            tmpWall.set(Tiles[CornerUpLeft]);

            if (tileMap[x][y + 1] == Open) {

                drawWall(batch, x,  y);

                tmpWall.set(Tiles[EdgeFacingUp]);

                drawWall(batch, x, y);

                return;
            }
        }

        if (!outsideBoundsHorisontallyLower && !outsideBoundsVerticallyLower && tileMap[x][y - 1] != Open && tileMap[x - 1][y] != Open && tileMap[x - 1][y - 1] == Open) {

            tmpWall.set(Tiles[CornerUpRight]);

            if (tileMap[x + 1][y] == Open || tileMap[x][y + 1] == Open) {

                drawWall(batch, x, y);

                if (tileMap[x + 1][y] == Open) {

                    tmpWall.set(Tiles[EdgeFacingRight]);

                    drawWall(batch, x, y);
                }

                if (tileMap[x][y + 1] == Open) {

                    tmpWall.set(Tiles[EdgeFacingUp]);

                    drawWall(batch, x, y);
                }

                return;
            }
        }

        if (!outsideBoundsHorisontallyLower && !outsideBoundsHorisontallyUpper) {

            if (!outsideBoundsVerticallyLower) {

                if (!outsideBoundsVerticallyUpper) {

                    if (    tileMap[x][y + 1] == Open &&
                            tileMap[x + 1][y] == Open &&
                            tileMap[x + 1][y + 1] == Open &&
                            tileMap[x - 1][y] != Open &&
                            tileMap[x][y - 1] != Open) {

                        tmpWall.set(Tiles[WallFacingRightUp0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                    }

                    if (    tileMap[x][y + 1] == Open &&
                            tileMap[x - 1][y] == Open &&
                            tileMap[x - 1][y + 1] == Open &&
                            tileMap[x + 1][y] != Open &&
                            tileMap[x][y - 1] != Open) {

                        tmpWall.set(Tiles[WallFacingLeftUp0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                    }

                    if (    tileMap[x][y - 1] == Open &&
                            tileMap[x + 1][y] == Open &&
                            tileMap[x + 1][y - 1] == Open &&
                            tileMap[x][y + 1] != Open &&
                            tileMap[x - 1][y] != Open) {

                        tmpWall.set(Tiles[WallFacingRightDown0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                    }

                    if (    tileMap[x][y - 1] == Open &&
                            tileMap[x - 1][y] == Open &&
                            tileMap[x - 1][y - 1] == Open &&
                            tileMap[x + 1][y] != Open &&
                            tileMap[x][y + 1] != Open) {

                        tmpWall.set(Tiles[WallFacingLeftDown0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                    }

                    if (    tileMap[x - 1][y    ] == Open &&
                            tileMap[x + 1][y    ] == Open &&
                            tileMap[x    ][y - 1] == Open &&
                            tileMap[x    ][y + 1] == Open) {

                        tmpWall.set(Tiles[WallFacingAllSides0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                        drawWall(batch, x, y);

                        return;
                    }
                }

                if (    tileMap[x - 1][y    ] == Open &&
                        tileMap[x + 1][y    ] == Open &&
                        tileMap[x    ][y - 1] == Open) {

                    tmpWall.set(Tiles[WallFacingLeftDownRight0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                }
            }

            if (!outsideBoundsVerticallyUpper) {

                if (    tileMap[x - 1][y    ] == Open &&
                        tileMap[x + 1][y    ] == Open &&
                        tileMap[x    ][y + 1] == Open) {

                    tmpWall.set(Tiles[WallFacingLeftUpRight0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                }
            }
        }


        if (!outsideBoundsVerticallyLower && !outsideBoundsVerticallyUpper) {

            if (!outsideBoundsHorisontallyLower) {

                if (!outsideBoundsHorisontallyUpper) {

                    if (    tileMap[x - 1][y] != Open &&
                            tileMap[x + 1][y] != Open &&
                            tileMap[x][y + 1] == Open &&
                            tileMap[x][y - 1] == Open) {

                        tmpWall.set(Tiles[WallFacingUpDown0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                    }
                }

                if (    tileMap[x - 1][y] == Open &&
                        tileMap[x][y + 1] == Open &&
                        tileMap[x][y - 1] == Open) {

                    tmpWall.set(Tiles[WallFacingDownLeftUp0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                }
            }

            if (!outsideBoundsHorisontallyUpper) {

                if (    tileMap[x + 1][y] == Open &&
                        tileMap[x][y + 1] == Open &&
                        tileMap[x][y - 1] == Open) {

                    tmpWall.set(Tiles[WallFacingDownRightUp0 + (tileMap[x][y] == SolidUnbreakable ? 0  : (tileMap[x][y] - SolidIntact))]);

                }
            }

            if (tmpWall.equals(Tiles[WallFilling])) {

                tmpWall.set(Tiles[Filling]);
            }

            drawWall(batch, x, y);

            if (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] != Open && tileMap[x][y - 1] != Open && tileMap[x + 1][y - 1] == Open) {

                tmpWall.set(Tiles[EdgeFacingRightWall]);

                drawWall(batch, x, y);
            }
        }
    }

    private void drawSolidUnbreakable(int x, int y, SpriteBatch batch, boolean outsideBoundsHorisontallyUpper, boolean outsideBoundsHorisontallyLower, boolean outsideBoundsVerticallyUpper, boolean outsideBoundsVerticallyLower) {

        tmpWall.set(Tiles[WallFilling]);

        drawWall(batch, x, y);

        batch.draw(Game.SpriteSheet, x * TileSizeInPixelsInWorldSpace, y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, 0, 0, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet, false, false);

        // Walls facing down and up
        if ((!outsideBoundsHorisontallyLower && tileMap[x - 1][y] != Open) && (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] != Open)) {

            if (!outsideBoundsVerticallyLower && tileMap[x][y - 1] == Open) {

                tmpWall.set(Tiles[UnbreakableWallFacingDown]);
            }
            else if (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] == Open) {

                tmpWall.set(Tiles[UnbreakableWallFacingUp]);
            }
        }

        // Walls facing left and right
        if ((!outsideBoundsVerticallyLower && tileMap[x][y - 1] != Open) && (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Open)) {

            if (!outsideBoundsHorisontallyLower && !outsideBoundsHorisontallyUpper && tileMap[x - 1][y] == Open && tileMap[x + 1][y] == Open) {

                tmpWall.set(Tiles[UnbreakableWallFacingLeft]);

                drawWall(batch, x, y);

                tmpWall.set(Tiles[UnbreakableEdgeFacingRight]);

                drawWall(batch, x, y);

                return;
            }
            else if (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] == Open) {

                tmpWall.set(Tiles[UnbreakableWallFacingRight]);
            }
            else if (!outsideBoundsHorisontallyLower && tileMap[x - 1][y] == Open) {

                tmpWall.set(Tiles[UnbreakableWallFacingLeft]);
            }
        }

        // Corners
        if (!outsideBoundsHorisontallyUpper && !outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Open && tileMap[x + 1][y] != Open && tileMap[x + 1][y + 1] == Open && (outsideBoundsVerticallyLower || tileMap[x][y - 1] != Open)) {

            tmpWall.set(Tiles[UnbreakableCornerDownLeft]);

            if (tileMap[x - 1][y] == Open) {

                drawWall(batch, x,  y);

                tmpWall.set(Tiles[UnbreakableEdgeFacingLeft]);

                drawWall(batch, x, y);

                return;
            }
        }

        if (!outsideBoundsHorisontallyLower && !outsideBoundsVerticallyUpper && !outsideBoundsVerticallyLower && tileMap[x][y + 1] != Open && tileMap[x - 1][y] != Open && tileMap[x - 1][y + 1] == Open && tileMap[x][y - 1] != Open && (outsideBoundsHorisontallyUpper || tileMap[x + 1][y] != Open)) {

            tmpWall.set(Tiles[UnbreakableCornerDownRight]);
        }

        if (!outsideBoundsHorisontallyUpper && !outsideBoundsVerticallyLower && tileMap[x][y - 1] != Open && tileMap[x + 1][y] != Open && tileMap[x + 1][y - 1] == Open && (x - 1 < 0 || tileMap[x - 1][y] != Open)) {

            tmpWall.set(Tiles[UnbreakableCornerUpLeft]);

            if (tileMap[x][y + 1] == Open) {

                drawWall(batch, x,  y);

                tmpWall.set(Tiles[UnbreakableEdgeFacingUp]);

                drawWall(batch, x, y);

                return;
            }
        }

        if (!outsideBoundsHorisontallyLower && !outsideBoundsVerticallyLower && tileMap[x][y - 1] != Open && tileMap[x - 1][y] != Open && tileMap[x - 1][y - 1] == Open) {

            tmpWall.set(Tiles[UnbreakableCornerUpRight]);

            if (tileMap[x + 1][y] == Open || tileMap[x][y + 1] == Open) {

                drawWall(batch, x, y);

                if (tileMap[x + 1][y] == Open) {

                    tmpWall.set(Tiles[UnbreakableEdgeFacingRight]);

                    drawWall(batch, x, y);
                }

                if (tileMap[x][y + 1] == Open) {

                    tmpWall.set(Tiles[UnbreakableEdgeFacingUp]);

                    drawWall(batch, x, y);
                }

                return;
            }
        }

        if (!outsideBoundsHorisontallyLower && !outsideBoundsHorisontallyUpper) {

            if (!outsideBoundsVerticallyLower) {

                if (!outsideBoundsVerticallyUpper) {

                    if (    tileMap[x][y + 1] == Open &&
                            tileMap[x + 1][y] == Open &&
                            tileMap[x + 1][y + 1] == Open &&
                            tileMap[x - 1][y] != Open &&
                            tileMap[x][y - 1] != Open) {

                        tmpWall.set(Tiles[UnbreakableWallFacingRightUp]);

                    }

                    if (    tileMap[x][y + 1] == Open &&
                            tileMap[x - 1][y] == Open &&
                            tileMap[x - 1][y + 1] == Open &&
                            tileMap[x + 1][y] != Open &&
                            tileMap[x][y - 1] != Open) {

                        tmpWall.set(Tiles[UnbreakableWallFacingLeftUp]);

                    }

                    if (    tileMap[x][y - 1] == Open &&
                            tileMap[x + 1][y] == Open &&
                            tileMap[x + 1][y - 1] == Open &&
                            tileMap[x][y + 1] != Open &&
                            tileMap[x - 1][y] != Open) {

                        tmpWall.set(Tiles[UnbreakableWallFacingRightDown]);

                    }

                    if (    tileMap[x][y - 1] == Open &&
                            tileMap[x - 1][y] == Open &&
                            tileMap[x - 1][y - 1] == Open &&
                            tileMap[x + 1][y] != Open &&
                            tileMap[x][y + 1] != Open) {

                        tmpWall.set(Tiles[UnbreakableWallFacingLeftDown]);

                    }

                    if (    tileMap[x - 1][y    ] == Open &&
                            tileMap[x + 1][y    ] == Open &&
                            tileMap[x    ][y - 1] == Open &&
                            tileMap[x    ][y + 1] == Open) {

                        tmpWall.set(Tiles[UnbreakableWallFacingAllSides]);

                        drawWall(batch, x, y);

                        return;
                    }
                }

                if (    tileMap[x - 1][y    ] == Open &&
                        tileMap[x + 1][y    ] == Open &&
                        tileMap[x    ][y - 1] == Open) {

                    tmpWall.set(Tiles[UnbreakableWallFacingLeftDownRight]);
                }
            }

            if (!outsideBoundsVerticallyUpper) {

                if (    tileMap[x - 1][y    ] == Open &&
                        tileMap[x + 1][y    ] == Open &&
                        tileMap[x    ][y + 1] == Open) {

                    tmpWall.set(Tiles[UnbreakableWallFacingLeftUpRight]);
                }
            }
        }


        if (!outsideBoundsVerticallyLower && !outsideBoundsVerticallyUpper) {

            if (!outsideBoundsHorisontallyLower) {

                if (!outsideBoundsHorisontallyUpper) {

                    if (    tileMap[x - 1][y] != Open &&
                            tileMap[x + 1][y] != Open &&
                            tileMap[x][y + 1] == Open &&
                            tileMap[x][y - 1] == Open) {

                        tmpWall.set(Tiles[UnbreakableWallFacingUpDown]);
                    }
                }

                if (    tileMap[x - 1][y] == Open &&
                        tileMap[x][y + 1] == Open &&
                        tileMap[x][y - 1] == Open) {

                    tmpWall.set(Tiles[UnbreakableWallFacingDownLeftUp]);
                }
            }

            if (!outsideBoundsHorisontallyUpper) {

                if (    tileMap[x + 1][y] == Open &&
                        tileMap[x][y + 1] == Open &&
                        tileMap[x][y - 1] == Open) {

                    tmpWall.set(Tiles[UnbreakableWallFacingDownRightUp]);

                }
            }

            if (tmpWall.equals(Tiles[WallFilling])) {

                tmpWall.set(Tiles[Filling]);
            }

            drawWall(batch, x, y);

            if (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] != Open && tileMap[x][y - 1] != Open && tileMap[x + 1][y - 1] == Open) {

                tmpWall.set(Tiles[UnbreakableEdgeFacingRightWall]);

                drawWall(batch, x, y);
            }
        }
    }

    private void drawSpawnTile(int x, int y, SpriteBatch batch, boolean outsideBoundsHorisontallyUpper, boolean outsideBoundsHorisontallyLower, boolean outsideBoundsVerticallyUpper, boolean outsideBoundsVerticallyLower) {

        tmpWall.set(Tiles[WallFilling]);

        drawWall(batch, x, y);

        // Up and down
        if ((!outsideBoundsHorisontallyLower && tileMap[x - 1][y] != Open) && (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] != Open)) {

            if (!outsideBoundsVerticallyLower && tileMap[x][y - 1] == Open) {

                tmpWall.set(Tiles[SpawnFacingDown0 + (tileMap[x][y] - SpawnIntact)]);

                drawWall(batch, x, y);
            }

            if (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] == Open) {

                tmpWall.set(Tiles[SpawnFacingUp0 + (tileMap[x][y] - SpawnIntact)]);

                drawWall(batch, x, y);
            }
        }
        // Left and right
        else  {

            if (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] == Open) {

                tmpWall.set(Tiles[SpawnFacingRight0 + (tileMap[x][y] - SpawnIntact)]);

                drawWall(batch, x, y);
            }

            if (!outsideBoundsHorisontallyLower && tileMap[x - 1][y] == Open) {

                tmpWall.set(Tiles[SpawnFacingLeft0 + (tileMap[x][y] - SpawnIntact)]);

                drawWall(batch, x, y);
            }
        }
    }

    public int toTileCoordinate(float value) {

        return (int) (value / Map.TileSizeInPixelsInWorldSpace);
    }

    /**
     * @return The length between two tiles.
     */
    public float lengthBetweenTiles(int x0, int y0, int x1, int y1) {

        return (float) Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
    }

    /**
     * @return The length between two coordinates which initially are in pixels.
     */
    public float lengthBetweenCoordinates(Vector2 start, Vector2 end) {

        float startX = toTileCoordinate(start.x);
        float startY = toTileCoordinate(start.y);

        float endX = toTileCoordinate(end.x);
        float endY = toTileCoordinate(end.y);

        return (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    /**
     * Draws the wall at the given location.
     */
    private void drawWall(SpriteBatch batch, int x, int y) {

        batch.draw(

                Game.SpriteSheet,
                x * TileSizeInPixelsInWorldSpace,
                y * TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace, TileSizeInPixelsInWorldSpace,
                (int) (tmpWall.x * Game.SizeOfTileInPixelsInSpritesheet),
                (int) (tmpWall.y * Game.SizeOfTileInPixelsInSpritesheet),
                Game.SizeOfTileInPixelsInSpritesheet,
                Game.SizeOfTileInPixelsInSpritesheet,
                false,
                false
        );
    }
}
