package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Map;
import io.github.simengangstad.defendthecaves.scene.MovableEntity;
import io.github.simengangstad.defendthecaves.scene.RotatableWeapon;
import io.github.simengangstad.defendthecaves.scene.weapons.Axe;
import io.github.simengangstad.defendthecaves.scene.weapons.Shield;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player extends MovableEntity {

    /**
     * The camera of the player.
     */
    public final Camera camera;

    private final TextureRegion mousePointerTextureRegion = new TextureRegion(Game.spriteSheet, 96, 80, 16, 16);

    private final TextureRegion overlayTextureRegion = new TextureRegion(Game.spriteSheet, 80, 80, 16, 16);

    private final Vector3 tmpVec = new Vector3();

    private final Vector2 tmpVec2 = new Vector2();

    private boolean raiseAxe = false;

    private Shield shield = new Shield();

    /**
     * Initializes the player with a camera.
     */
    public Player(Camera camera) {

        super(new Vector2(), new Vector2(80.0f, 80.0f), "assets/animations/PlayerStationary.png", 0.2f, "assets/animations/PlayerWalking.png", 0.075f);

        this.camera = camera;

        leapTextureRegion = new TextureRegion(Game.spriteSheet, 96, 48, 16, 16);

        Axe axe = new Axe(() -> {

            tmpVec2.set(tmpVec.x, tmpVec.y);

            if (selectedSolid && map.lengthBetweenCoordinates(getPosition(), tmpVec2) <= 1) {

                int x = map.toTileCoordinate(tmpVec2.x);
                int y = map.toTileCoordinate(tmpVec2.y);


                if (map.get(x, y) == Map.SolidBroken) {

                    map.set(x, y, Map.Open);
                }
                else {

                    map.set(x, y, map.get(x, y) + 1);
                }
            }
        });

        attachTool(axe);
    }

    @Override
    protected void collides() {

    }

    @Override
    public void tick() {

        if (shield.parent == null) {

            shield.parent = this;
        }

        delta.set(0.0f, 0.0f);

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {

            currentTool = shield;
        }
        else {

            currentTool = tools.get(0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            delta.add(0.0f, 1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            delta.add(0.0f, -1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            delta.add(-1.0f, 0.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            delta.add(1.0f, 0.0f);
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

            tmpVec.set(camera.unproject(tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f)));

            tmpPosition.set(tmpVec.x, tmpVec.y);

            tmpPosition.sub(getPosition());

            interact(tmpPosition);
        }

        // Left
        if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2.0f && facingRight) {

            facingRight = false;
        }
        // Right
        else if (Gdx.graphics.getWidth() / 2.0f < Gdx.input.getX() && !facingRight) {

            facingRight = true;
        }

        super.tick();

        updateCameraPosition();

        int animationIndex = currentAnimation.getKeyFrameIndex(stateTime % currentAnimation.getAnimationDuration());

        if (currentAnimation == stationaryAnimation) {

            if (animationIndex == 1 || animationIndex == 2) {

                raiseAxe = true;
            }
            else {

                raiseAxe = false;
            }
        }
        else if (currentAnimation == walkingAnimation) {

            if (animationIndex == 1 || animationIndex == 2 || animationIndex == 4) {

                raiseAxe = true;
            }
            else {

                raiseAxe = false;
            }
        }

        currentTool.flip = facingRight;

        tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);

        tmpVec.set(camera.unproject(tmpVec));

        if (currentTool instanceof RotatableWeapon) {

            ((RotatableWeapon) currentTool).setRotation((int) (Math.atan((tmpVec.y - getPosition().y) / (tmpVec.x - getPosition().x)) * 180 / Math.PI));
        }
    }

    private boolean selectedSolid = false;

    Vector2 tmpPosition = new Vector2();

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        tmpPosition.set(position);

        if (raiseAxe) {

            currentTool.offset.set(0.0f, (size.y / 16.0f));
        }
        else {

            currentTool.offset.set(0.0f, 0.0f);
        }

        super.draw(batch, position, size);

        if (map.isSolid((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace), (int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace))) {

            if (map.get((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace), (int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace)) < Map.SpawnIntact) {

                selectedSolid = true;

                batch.draw(overlayTextureRegion, ((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, ((int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace);
            }
        }
        else {

            selectedSolid = false;
        }

        batch.draw(mousePointerTextureRegion, tmpVec.x - (Map.TileSizeInPixelsInWorldSpace / 2.0f) / 2.0f, tmpVec.y - (Map.TileSizeInPixelsInWorldSpace / 2.0f) / 2.0f, (Map.TileSizeInPixelsInWorldSpace / 2.0f), (Map.TileSizeInPixelsInWorldSpace / 2.0f));
    }

    public void updateCameraPosition() {

        camera.position.set(Math.round(getPosition().x), Math.round(getPosition().y), 0.0f);
        camera.update();

        getPosition().set(camera.position.x, camera.position.y);
    }
}
