package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.pathfinding.Coordinate;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Map;
import io.github.simengangstad.defendthecaves.scene.Scene;
import io.github.simengangstad.defendthecaves.scene.items.Torch;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 08/12/15
 */
public abstract class Enemy extends Entity {

    protected final Vector2 playerPositionReference, playerSizeReference;

    private Player playerReference;

    private final Vector2 tmpVector = new Vector2();

    /**
     * Divided by two when player deoesn't have a torch lit.
     */
    private final int coverageRadius;

    private final Coordinate destination = new Coordinate();

    private boolean followingPlayer = false;

    private Coordinate[][] cameFrom;

    private Coordinate lastPlayerPos = new Coordinate();

    float scaleFactor = 0.9f;

    float timeLeftStationary = -1.0f;

    final Vector2 lastPosition = new Vector2();

    final ArrayList<Coordinate> path = new ArrayList<>();

    int currentIndex = 0;

    float timePassedGoingInTheGivenDirection = 0.0f;

    public Enemy(Vector2 position, Player player, int coverageRadius, Vector2 size, Animation stationaryAnimation, Animation movingAnimation) {

        super(position, size, stationaryAnimation, movingAnimation);

        this.playerPositionReference = player.position;
        this.playerSizeReference = player.size;
        playerReference = player;

        this.coverageRadius = coverageRadius;

        destination.set((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace));

        lastPosition.set(position);
    }

    @Override
    public void create() {

        super.create();

        cameFrom = new Coordinate[((Scene) host).pathfindingGrid.width][((Scene) host).pathfindingGrid.height];

        for (int x = 0; x < cameFrom.length; x++) {

            for (int y = 0; y < cameFrom[0].length; y++) {

                cameFrom[x][y] = new Coordinate();
            }
        }
    }

    protected abstract void hurtPlayer(Vector2 direction);

    /**
     * Called once the enemy notices the player, and not when the enemy is heading towards the player.
     *
     * @param direction the direction of the player in relative to the enemy.
     */
    protected abstract void noticedPlayer(Vector2 direction);

    @Override
    protected void collides() {

    }

    @Override
    public void tick() {

        if (!isParalysed()) {

            if (tmpVector.set(playerPositionReference.x - position.x, playerPositionReference.y - position.y).len() < coverageRadius * map.TileSizeInPixelsInWorldSpace / (playerReference.currentItem instanceof Torch ? 1 : 2)) {

                if (tmpVector.len() < playerSizeReference.x) {

                    hurtPlayer(tmpVector);
                }
                else {

                    if ((int) (playerPositionReference.x / Map.TileSizeInPixelsInWorldSpace) != lastPlayerPos.x || (int) (playerPositionReference.y / Map.TileSizeInPixelsInWorldSpace) != lastPlayerPos.y) {

                        // Re-construct path

                        for (int xp = 0; xp < ((Scene) host).pathfindingGrid.width; xp++) {

                            for (int yp = 0; yp < ((Scene) host).pathfindingGrid.height; yp++) {

                                cameFrom[xp][yp].set(-1, -1);
                            }
                        }

                        lastPlayerPos.set((int) (playerPositionReference.x / Map.TileSizeInPixelsInWorldSpace), (int) (playerPositionReference.y / Map.TileSizeInPixelsInWorldSpace));

                        destination.set(lastPlayerPos);

                        ((Scene) host).pathfindingGrid.performSearch((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace), destination.x, destination.y, cameFrom);

                        path.clear();

                        Coordinate coordinate = null;

                        while (!destination.equals(coordinate)) {

                            if (coordinate == null) {

                                coordinate = cameFrom[(int) (position.x / Map.TileSizeInPixelsInWorldSpace)][(int) (position.y / Map.TileSizeInPixelsInWorldSpace)];
                            }
                            else {

                                if (coordinate.x == -1 || coordinate.y == -1 || (cameFrom[coordinate.x][coordinate.y].x == -1 && cameFrom[coordinate.x][coordinate.y].y == -1)) {

                                    System.err.println("Came from is null...");

                                    break;
                                }

                                coordinate = cameFrom[coordinate.x][coordinate.y];
                            }

                            // TOOD: Temp solution because somehow the path turns recursive upon one coordinate
                            if (path.size() > 0 && coordinate.equals(path.get(path.size() - 1))) {

                                destination.set(coordinate);

                                break;
                            }

                            path.add(coordinate);
                        }

                        lastPosition.set(position);
                        currentIndex = 0;
                    }

                    // Get the next coordinate on the path, but this needs to change as the current position was the next coordinate
                    // on the path, therefore we store a last position which is a reference to the last coordinate on the path
                    Coordinate next = path.get(currentIndex);

                    // Set the direction of movement to the next coordinate of the path
                    delta.set(next.x * Map.TileSizeInPixelsInWorldSpace - lastPosition.x, next.y * Map.TileSizeInPixelsInWorldSpace - lastPosition.y).nor().scl(scaleFactor);

                    //System.out.println(lastPosition + " -> " + (next.x * Map.TileSizeInPixelsInWorldSpace) + ", " + (next.y * Map.TileSizeInPixelsInWorldSpace) + ": " + delta);

                    // If there's a next cooridnate
                    if (currentIndex + 1 < path.size()) {

                        float deltaX = next.x * Map.TileSizeInPixelsInWorldSpace - position.x;
                        float deltaY = next.y * Map.TileSizeInPixelsInWorldSpace - position.y;

                        float length = (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

                        if (length <= Map.TileSizeInPixelsInWorldSpace / 1.5f) {

                            lastPosition.set(((int) (position.x / Map.TileSizeInPixelsInWorldSpace) + 0.5f) * Map.TileSizeInPixelsInWorldSpace, ((int) (position.y / Map.TileSizeInPixelsInWorldSpace) + 0.5f) * Map.TileSizeInPixelsInWorldSpace);

                            currentIndex++;
                        }
                    }

                    noticedPlayer(tmpVector);
                }
            }
            else {

                // Walk randomly
                // Pick a position from its surroundings and go there over a set amount of seconds
                if (!((int) (position.x / Map.TileSizeInPixelsInWorldSpace) == destination.x && (int) (position.y / Map.TileSizeInPixelsInWorldSpace) == destination.y)) {

                    // Get the next coordinate on the path, but this needs to change as the current position was the next coordinate
                    // on the path, therefore we store a last position which is a reference to the last coordinate on the path
                    Coordinate next = path.get(currentIndex);

                    // Set the direction of movement to the next coordinate of the path
                    delta.set(next.x * Map.TileSizeInPixelsInWorldSpace - lastPosition.x, next.y * Map.TileSizeInPixelsInWorldSpace - lastPosition.y).nor().scl(scaleFactor);

                    timeLeftStationary = 0.0f;

                    // TOOD: Cheeky solution, but who cares?
                    if (timePassedGoingInTheGivenDirection > 3.0f) {

                        destination.set((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace));
                    }

                    timePassedGoingInTheGivenDirection += Gdx.graphics.getDeltaTime();

                    // If there's a next cooridnate
                    if (currentIndex + 1 < path.size()) {

                        float deltaX = next.x * Map.TileSizeInPixelsInWorldSpace - position.x;
                        float deltaY = next.y * Map.TileSizeInPixelsInWorldSpace - position.y;

                        float length = (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

                        if (length <= Map.TileSizeInPixelsInWorldSpace / 1.5f) {

                            lastPosition.set(((int) (position.x / Map.TileSizeInPixelsInWorldSpace) + 0.5f) * Map.TileSizeInPixelsInWorldSpace, ((int) (position.y / Map.TileSizeInPixelsInWorldSpace) + 0.5f) * Map.TileSizeInPixelsInWorldSpace);

                            currentIndex++;

                            timePassedGoingInTheGivenDirection = 0.0f;
                        }
                    }
                }
                else {

                    // Stand still for n amount of seconds.

                    if (timeLeftStationary == 0.0f) {

                        timeLeftStationary = MathUtils.random(5) + 2;
                    }
                    else if (0.0f < timeLeftStationary) {

                        timeLeftStationary -= Gdx.graphics.getDeltaTime();
                    }
                    else {

                        int radius = 4;

                        int x = (int) position.x / Map.TileSizeInPixelsInWorldSpace;
                        int y = (int) position.y / Map.TileSizeInPixelsInWorldSpace;

                        int xs, ys;

                        boolean foundDestination = false;

                        while (!foundDestination) {

                            xs = (x - radius) + MathUtils.random(radius * 2);
                            ys = (y - radius) + MathUtils.random(radius * 2);

                            if (map.isValidTile(xs, ys) && !map.isSolid(xs, ys) && (xs != x || ys != y)) {

                                foundDestination = true;

                                destination.set(xs, ys);

                                for (int xp = 0; xp < ((Scene) host).pathfindingGrid.width; xp++) {

                                    for (int yp = 0; yp < ((Scene) host).pathfindingGrid.height; yp++) {

                                        cameFrom[xp][yp].set(-1, -1);
                                    }
                                }

                                if (!((Scene) host).pathfindingGrid.performSearch((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace), destination.x, destination.y, cameFrom)) {

                                    foundDestination = false;

                                    continue;
                                }

                                path.clear();

                                Coordinate coordinate = null;

                                while (!destination.equals(coordinate)) {

                                    if (coordinate == null) {

                                        coordinate = cameFrom[x][y];
                                    }
                                    else {

                                        if (cameFrom[coordinate.x][coordinate.y].x == -1 && cameFrom[coordinate.x][coordinate.y].y == -1) {

                                            System.err.println("Came from is null...");

                                            break;
                                        }

                                        coordinate = cameFrom[coordinate.x][coordinate.y];
                                    }


                                    // TOOD: Temp solution because somehow the path turns recursive upon one coordinate
                                    if (path.size() > 0 && coordinate.equals(path.get(path.size() - 1))) {

                                        destination.set(coordinate);

                                        break;
                                    }

                                    path.add(coordinate);
                                }

                                lastPosition.set(position);
                                currentIndex = 0;
                            }
                        }
                    }
                }
            }

            if (followingPlayer) {

                facingRight = playerPositionReference.x > position.x;
            }
            else {

                if (delta.x != 0.0f) {

                    facingRight = 0.0f < delta.x;
                }
            }
        }

        super.tick();

        int animationIndex = currentAnimation.getKeyFrameIndex(stateTime % currentAnimation.getAnimationDuration());

        if (currentAnimation == stationaryAnimation) {

            raiseItem = animationIndex == 1 || animationIndex == 2;
        }
        else if (currentAnimation == movingAnimation) {

            raiseItem = animationIndex == 1 || animationIndex == 2 || animationIndex == 4;
        }

        if (forceApplied.x != 0.0f || forceApplied.y != 0.0f) {

            destination.set((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace));

            lastPosition.set(position);
        }
    }
}
