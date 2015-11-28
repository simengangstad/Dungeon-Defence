package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;

/**
 * @author simengangstad
 * @since 13/11/15
 */
public class Map {

    /**
     * The size of the sprites from the sprite sheet in pixels.
     */
    private final int SpriteSheetTileSize = 16;

    /**
     * Locations for the different types of walls within the sprite sheet.
     */
    private static final Vector2[] Walls = new Vector2[] {

            new Vector2(-4, 2),
            new Vector2(0, 0),
            new Vector2(1, 0),
            new Vector2(2, 0),
            new Vector2(2, 1),
            new Vector2(2, 2),
            new Vector2(1, 2),
            new Vector2(0, 2),
            new Vector2(0, 1),

            new Vector2(3, 0),
            new Vector2(5, 0),
            new Vector2(5, 2),
            new Vector2(3, 2),

            new Vector2(6, 0),
            new Vector2(7, 0),

            new Vector2(9, 0),
            new Vector2(9, 1)
    };

    /**
     *  Identifiers for the different walls.
     */
    private static final int

            Fill            = 0,
            LeftUp          = 1,
            MidUp           = 2,
            RightUp         = 3,
            RightMid        = 4,
            RightDown       = 5,
            MidDown         = 6,
            LeftDown        = 7,
            LeftMid         = 8,

            LeftUpCorner    = 9,
            RightUpCorner   = 10,
            RightDownCorner = 11,
            LeftDownCorner  = 12,

            LeftUpDown      = 13,
            RightUpDown     = 14,

            UpLeftRight     = 15,
            DownLeftRight   = 16;

    private final Vector2 tmpWall = new Vector2();

    /**
     * The location where the walls are.
     */
    private final Vector2 SpriteSheetWallPosition = new Vector2(80, 16);

    /**
     * Reference to the sprite sheet.
     */
    private final Texture spriteSheet;

    /**
     * The states of the map.
     */
    private static final int Floor = 0,
                             Solid = 1;

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
    public final int TileSize = 160;

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
     * The current player
     */
    public Vector2 playerPosition;

    /**
     * Tmp values.
     */
    private final Vector2 tmpClosestPointInCell = new Vector2(),
                          tmpTargetPosition = new Vector2(),
                          tmpDelta = new Vector2(),
                          tmpDiff = new Vector2(),
                          tmpResult = new Vector2();


    public Map(Vector2 playerSize) {

        MapGenerator.requestedAmountOfRooms = 25;
        MapGenerator.lowerBoundry = 5;
        MapGenerator.upperBoundry = 11;

        tileMap = MapGenerator.generate(50, 50);

        if (TileSize % playerSize.x != 0) {

            throw new RuntimeException("Invalid scale between player size and tile size.");
        }

        Subdivision = (int) (TileSize / playerSize.x) * 2;

        CellSize = (float) TileSize / Subdivision;
        GridSize = getWidth() * Subdivision;
        StepSize = CellSize * 0.25f;

        collidableMap = new int[GridSize][GridSize];

        for (int x1 = 0; x1 < GridSize; x1++) {

            for (int y1 = 0; y1 < GridSize; y1++) {

                collidableMap[x1][y1] = tileMap[(int) Math.ceil(x1 / Subdivision)][(int) Math.ceil(y1 / Subdivision)];

                if (isSolid(x1, y1)) {

                    for (int x2 = x1 - 2; x2 <= x1; x2++) {

                        for (int y2 = y1 - 2; y2 <= y1; y2++) {

                            if (cellIsValid(x2, y2) && !isSolid(x2, y2) && collidableMap[x2][y2] == Floor) {

                                collidableMap[x2][y2] = ExpandedObstacle;
                            }
                        }
                    }
                }
            }
        }

        this.spriteSheet = Game.spriteSheet;
    }

    /**
     * @return If a tile is solid.
     */
    public boolean isSolid(int x, int y) {

        return collidableMap[x][y] >= Solid;
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

    /**
     * Resolves the collision with the map (if any).
     *
     * @param position The current position (this variable will be changed and returned by reference)
     * @param direction The direction of the movement.
     * @param speed The speed of the movement.
     *
     * @return The new position.
     */
    public Vector2 resolveCollision(Vector2 position, Vector2 direction, float speed) {

        direction.nor();

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
            if (!isSolid(cellX, cellY)) {

                position.set(tmpTargetPosition);
            }
            else {

                // This is the tricky part. Here, we need to project the
                // target point into an empty cell over the shortest
                // distance possible.
                float minDistanceSquared = Float.MAX_VALUE;

                for (int x = cellX - 1; x <= cellX + 1; x++) {

                    for (int y = cellY - 1; y <= cellY + 1; y++) {

                        if (cellIsValid(x, y) && !isSolid(x, y)) {

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
            }
        }

        return position;
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
    public void draw(SpriteBatch batch) {

        for (int y = (int) (playerPosition.y + Gdx.graphics.getHeight() / 2.0f) / TileSize; y >= (playerPosition.y - Gdx.graphics.getHeight() / 2.0f - TileSize) / TileSize; y--) {

            for (int x = (int) (playerPosition.x - Gdx.graphics.getWidth() / 2.0f) / TileSize; x < (playerPosition.x + Gdx.graphics.getWidth() / 2.0f) / TileSize; x++) {

                // Outside bounds
                if (x < 0 || tileMap.length <= x || y < 0 || tileMap[0].length <= y) {

                    continue;
                }

                switch (tileMap[x][y]) {

                    case Floor:

                        batch.draw(spriteSheet, x * TileSize, y * TileSize, TileSize, TileSize, 16, 32, 16, 16, false, false);

                        if (tileMap[x][y + 1] != Floor) {

                            batch.draw(spriteSheet, x * TileSize, y * TileSize, TileSize, TileSize, 16, 0, 16, 16, false, false);
                        }

                        break;

                    case Solid:

                        tmpWall.set(Walls[Fill]);

                        drawWall(batch, x, y);

                        boolean outsideBoundsHorisontallyUpper = tileMap.length <= (x + 1);
                        boolean outsideBoundsHorisontallyLower = (x - 1) < 0;
                        boolean outsideBoundsVerticallyUpper = tileMap[0].length <= (y + 1);
                        boolean outsideBoundsVerticallyLower = (y - 1) < 0;

                        // Up and down
                        if ((!outsideBoundsHorisontallyLower && tileMap[x - 1][y] != Floor) || (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] != Floor)) {

                            if (!outsideBoundsVerticallyLower && tileMap[x][y - 1] == Floor) {

                                tmpWall.set(Walls[MidDown]);

                                drawWall(batch, x, y);
                            }

                            if (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] == Floor) {

                                tmpWall.set(Walls[MidUp]);

                                drawWall(batch, x, y);
                            }
                        }

                        // Left and right
                        if ((!outsideBoundsVerticallyLower && tileMap[x][y - 1] != Floor) || (!outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Floor)) {

                            if (!outsideBoundsHorisontallyUpper && tileMap[x + 1][y] == Floor) {

                                tmpWall.set(Walls[RightMid]);

                                drawWall(batch, x, y);
                            }

                            if (!outsideBoundsHorisontallyLower && tileMap[x - 1][y] == Floor) {

                                tmpWall.set(Walls[LeftMid]);

                                drawWall(batch, x, y);
                            }
                        }

                        // Corners
                        if (!outsideBoundsHorisontallyUpper && !outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Floor && tileMap[x + 1][y] != Floor && tileMap[x + 1][y + 1] == Floor) {

                            tmpWall.set(Walls[LeftDownCorner]);

                            drawWall(batch, x, y);
                        }

                        if (!outsideBoundsHorisontallyLower && !outsideBoundsVerticallyUpper && tileMap[x][y + 1] != Floor && tileMap[x - 1][y] != Floor && tileMap[x - 1][y + 1] == Floor) {

                            tmpWall.set(Walls[RightDownCorner]);

                            drawWall(batch, x, y);
                        }

                        if (!outsideBoundsHorisontallyUpper && !outsideBoundsVerticallyLower && tileMap[x][y - 1] != Floor && tileMap[x + 1][y] != Floor && tileMap[x + 1][y - 1] == Floor) {

                            tmpWall.set(Walls[LeftUpCorner]);

                            drawWall(batch, x, y);
                        }

                        if (!outsideBoundsHorisontallyLower && !outsideBoundsVerticallyLower && tileMap[x][y - 1] != Floor && tileMap[x - 1][y] != Floor && tileMap[x - 1][y - 1] == Floor) {

                            tmpWall.set(Walls[RightUpCorner]);

                            drawWall(batch, x, y);
                        }


                        if (!outsideBoundsHorisontallyLower && !outsideBoundsHorisontallyUpper) {

                            if (!outsideBoundsVerticallyLower) {

                                if (!outsideBoundsVerticallyUpper) {

                                    if (    tileMap[x][y + 1] == Floor &&
                                            tileMap[x + 1][y] == Floor &&
                                            tileMap[x + 1][y + 1] == Floor &&
                                            tileMap[x - 1][y] != Floor &&
                                            tileMap[x][y - 1] != Floor) {

                                        tmpWall.set(Walls[RightUp]);

                                        drawWall(batch, x, y);
                                    }

                                    if (    tileMap[x][y + 1] == Floor &&
                                            tileMap[x - 1][y] == Floor &&
                                            tileMap[x - 1][y + 1] == Floor &&
                                            tileMap[x + 1][y] != Floor &&
                                            tileMap[x][y - 1] != Floor) {

                                        tmpWall.set(Walls[LeftUp]);

                                        drawWall(batch, x, y);
                                    }

                                    if (    tileMap[x][y - 1] == Floor &&
                                            tileMap[x + 1][y] == Floor &&
                                            tileMap[x + 1][y - 1] == Floor &&
                                            tileMap[x][y + 1] != Floor &&
                                            tileMap[x - 1][y] != Floor) {

                                        tmpWall.set(Walls[RightDown]);

                                        drawWall(batch, x, y);
                                    }

                                    if (    tileMap[x][y - 1] == Floor &&
                                            tileMap[x - 1][y] == Floor &&
                                            tileMap[x - 1][y - 1] == Floor &&
                                            tileMap[x + 1][y] != Floor &&
                                            tileMap[x][y + 1] != Floor) {

                                        tmpWall.set(Walls[LeftDown]);

                                        drawWall(batch, x, y);
                                    }
                                }

                                if (    tileMap[x - 1][y    ] == Floor &&
                                        tileMap[x + 1][y    ] == Floor &&
                                        tileMap[x    ][y - 1] == Floor) {

                                    tmpWall.set(Walls[DownLeftRight]);

                                    drawWall(batch, x, y);
                                }
                            }

                            if (!outsideBoundsVerticallyUpper) {

                                if (    tileMap[x - 1][y    ] == Floor &&
                                        tileMap[x + 1][y    ] == Floor &&
                                        tileMap[x    ][y + 1] == Floor) {

                                    tmpWall.set(Walls[UpLeftRight]);

                                    drawWall(batch, x, y);
                                }
                            }
                        }


                        if (!outsideBoundsVerticallyLower && !outsideBoundsVerticallyUpper) {

                            if (!outsideBoundsHorisontallyLower) {

                                if (    tileMap[x - 1][y] == Floor &&
                                        tileMap[x][y + 1] == Floor &&
                                        tileMap[x][y - 1] == Floor) {

                                    tmpWall.set(Walls[LeftUpDown]);

                                    drawWall(batch, x, y);
                                }
                            }

                            if (!outsideBoundsHorisontallyUpper) {

                                if (    tileMap[x + 1][y] == Floor &&
                                        tileMap[x][y + 1] == Floor &&
                                        tileMap[x][y - 1] == Floor) {

                                    tmpWall.set(Walls[RightUpDown]);

                                    drawWall(batch, x, y);
                                }
                            }
                        }

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

                        batch.draw(spriteSheet, x * CellSize, y * CellSize, CellSize, CellSize, 32, 0, 16, 16, false, false);
                    }
                }
            }
        }
    }

    /**
     * Draws the wall at the given location.
     */
    private void drawWall(SpriteBatch batch, int x, int y) {

        batch.draw(

                spriteSheet,
                x * TileSize,
                y * TileSize,
                TileSize,
                TileSize,
                (int) (SpriteSheetWallPosition.x + tmpWall.x * SpriteSheetTileSize),
                (int) (SpriteSheetWallPosition.y + tmpWall.y * SpriteSheetTileSize),
                SpriteSheetTileSize,
                SpriteSheetTileSize,
                false,
                false
        );
    }
}
