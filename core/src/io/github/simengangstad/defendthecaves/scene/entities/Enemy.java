package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.pathfinding.Coordinate;
import io.github.simengangstad.defendthecaves.scene.Map;
import io.github.simengangstad.defendthecaves.scene.MovableEntity;
import io.github.simengangstad.defendthecaves.scene.Scene;
import io.github.simengangstad.defendthecaves.scene.weapons.Axe;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 08/12/15
 */
public class Enemy extends MovableEntity {

    private final Vector2 playerPositionReference, playerSizeReference;

    private final Vector2 tmpVector = new Vector2();

    private final int coverageRadius;

    private final Coordinate destination = new Coordinate();

    private boolean followingPlayer = false;

    private Coordinate[][] cameFrom;

    public Enemy(Vector2 position, Vector2 playerPositionReference, Vector2 playerSizeReference, int coverageRadius) {

        super(position, new Vector2(80.0f, 80.0f), "assets/animations/PlayerStationary.png", 0.2f, "assets/animations/PlayerWalking.png", 0.075f);

        this.playerPositionReference = playerPositionReference;
        this.playerSizeReference = playerSizeReference;

        this.coverageRadius = coverageRadius;

        leapTextureRegion = new TextureRegion(Game.spriteSheet, 96, 48, 16, 16);

        attachTool(new Axe(new Callback() {

            @Override
            public void callback() {}
        }));

        destination.set((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace));

        lastPosition.set(getPosition());
    }

    float scaleFactor = 0.9f;

    float timeLeftStationary = -1.0f;

    @Override
    protected void collides() {

        // TODO: Replace with pathfinding?
    }

    final Vector2 lastPosition = new Vector2();

    final ArrayList<Coordinate> path = new ArrayList<>();

    int currentIndex = 0;

    float timePassedGoingInTheGivenDirection = 0.0f;

    @Override
    public void tick() {

        if (cameFrom == null) {

            cameFrom = new Coordinate[((Scene) host).pathfindingGrid.width][((Scene) host).pathfindingGrid.height];

            for (int x = 0; x < cameFrom.length; x++) {

                for (int y = 0; y < cameFrom[0].length; y++) {

                    cameFrom[x][y] = new Coordinate();
                }
            }
        }

        if (!isParalysed()) {

            if (tmpVector.set(playerPositionReference.x - getPosition().x, playerPositionReference.y - getPosition().y).len() < coverageRadius * map.TileSizeInPixelsInWorldSpace) {

                if (tmpVector.len() < playerSizeReference.x) {

                    interact(tmpVector);
                }
                else {

                    delta.set(tmpVector).nor().scl(scaleFactor);
                }

                destination.set((int) (getPosition().x / Map.TileSizeInPixelsInWorldSpace), (int) (getPosition().y / Map.TileSizeInPixelsInWorldSpace));

                lastPosition.set(getPosition());
            }
            else {

                // Walk randomly
                // Pick a position from its surroundings and go there over a set amount of seconds
                if (!((int) (getPosition().x / Map.TileSizeInPixelsInWorldSpace) == destination.x && (int) (getPosition().y / Map.TileSizeInPixelsInWorldSpace) == destination.y)) {

                    // Get the next coordinate on the path, but this needs to change as the current position was the next coordinate
                    // on the path, therefore we store a last position which is a reference to the last coordinate on the path
                    Coordinate next = path.get(currentIndex);

                    // Set the direction of movement to the next coordinate of the path
                    delta.set(next.x * Map.TileSizeInPixelsInWorldSpace - lastPosition.x, next.y * Map.TileSizeInPixelsInWorldSpace - lastPosition.y).nor().scl(scaleFactor);

                    timeLeftStationary = 0.0f;

                    // TOOD: Cheeky solution, but I who cares?
                    if (timePassedGoingInTheGivenDirection > 3.0f) {

                        destination.set((int) (getPosition().x / Map.TileSizeInPixelsInWorldSpace), (int) (getPosition().y / Map.TileSizeInPixelsInWorldSpace));
                    }

                    timePassedGoingInTheGivenDirection += Gdx.graphics.getDeltaTime();

                    // If there's a next cooridnate
                    if (currentIndex + 1 < path.size()) {

                        if ((int) (lastPosition.x / Map.TileSizeInPixelsInWorldSpace) != (int) ((getPosition().x - Map.TileSizeInPixelsInWorldSpace / 2.0f) / Map.TileSizeInPixelsInWorldSpace) || (int) (lastPosition.y / Map.TileSizeInPixelsInWorldSpace) != (int) ((getPosition().y + Map.TileSizeInPixelsInWorldSpace / 2.0f) / Map.TileSizeInPixelsInWorldSpace)) {

                            lastPosition.set(((int) ((getPosition().x - Map.TileSizeInPixelsInWorldSpace / 2.0f) / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, ((int) ((getPosition().y + Map.TileSizeInPixelsInWorldSpace / 2.0f) / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace);

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

                        int x = (int) getPosition().x / Map.TileSizeInPixelsInWorldSpace;
                        int y = (int) getPosition().y / Map.TileSizeInPixelsInWorldSpace;

                        int xs, ys;

                        boolean foundDestination = false;

                        while (!foundDestination) {

                            xs = (x - radius) + MathUtils.random(radius * 2);
                            ys = (y - radius) + MathUtils.random(radius * 2);

                            if (map.isValidTile(xs, ys) && !map.isSolid(xs, ys) && (xs != x || ys != y)) {

                                foundDestination = true;

                                destination.set(xs, ys);

//                                System.out.println("Start: " + x + ", " + y);
//                                System.out.println("Destination: " + destination);

                                for (int xp = 0; xp < ((Scene) host).pathfindingGrid.width; xp++) {

                                    for (int yp = 0; yp < ((Scene) host).pathfindingGrid.height; yp++) {

                                        cameFrom[xp][yp].set(-1, -1);
                                    }
                                }

                                if (!((Scene) host).pathfindingGrid.performSearch((int) (getPosition().x / Map.TileSizeInPixelsInWorldSpace), (int) (getPosition().y / Map.TileSizeInPixelsInWorldSpace), (int) destination.x, (int) destination.y, cameFrom)) {

                                    foundDestination = false;

                                    continue;
                                }

                                path.clear();
//
//                                for (int i = cameFrom.length - 1; i >= 0; i--) {
//
//                                    for (int j = 0; j < cameFrom.length; j++) {
//
//                                        System.out.print(cameFrom[j][i] + "\t");
//                                    }
//
//                                    System.out.println();
//                                }

                                Coordinate coordinate = null;

                                while (!destination.equals(coordinate)) {

                                    if (coordinate == null) {

                                        coordinate = cameFrom[x][y];
                                    }
                                    else {

                                        coordinate = cameFrom[coordinate.x][coordinate.y];
                                    }


                                    // TOOD: Temp solution because somehow the path turns recursive upon one coordinate
                                    if (path.size() > 0 && coordinate.equals(path.get(path.size() - 1))) {

                                        destination.set(coordinate);

                                        break;
                                    }

                                    path.add(coordinate);
                                }

//                                System.out.println(path.toString());

                                lastPosition.set(getPosition());
                                currentIndex = 0;
                            }
                        }
                    }
                }
            }

            if (followingPlayer) {

                facingRight = playerPositionReference.x > getPosition().x;
            }
            else {

                if (delta.x != 0.0f) {

                    facingRight = 0.0f < delta.x;
                }
            }

            currentTool.flip = facingRight;

        }

        super.tick();

        if (forceApplied.x != 0.0f || forceApplied.y != 0.0f) {

            destination.set((int) (getPosition().x / Map.TileSizeInPixelsInWorldSpace), (int) (getPosition().y / Map.TileSizeInPixelsInWorldSpace));

            lastPosition.set(getPosition());
        }
    }
}
