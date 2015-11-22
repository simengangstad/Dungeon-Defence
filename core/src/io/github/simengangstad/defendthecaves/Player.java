package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player extends GameObject implements Drawable {

    /**
     * The camera of the player.
     */
    public final Camera camera;

    /**
     * The speed of the player in pixels per second.
     */
    public int speed = 200;

    /**
     * Reference to the drawable texture region of the player.
     */
    private final TextureRegion textureRegion;

    /**
     * The map the player is located in.
     */
    private Map map;

    /**
     * Vector used for calculating direction.
     */
    private Vector2 tmpDirection = new Vector2();

    /**
     * Initializes the player with a camera.
     */
    public Player(Camera camera) {

        super(new Vector2(), new Vector2(25.0f, 25.0f));

        this.camera = camera;

        textureRegion = new TextureRegion(Game.spriteSheet, 8, 16, 2, 2);
    }

    /**
     * Sets the map the player resolves its collision against.
     */
    public void setMap(Map map) {

        this.map = map;
    }

    @Override
    public void tick() {

        if (map == null) {

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        tmpDirection.set(0.0f, 0.0f);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            tmpDirection.add(0.0f, 1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            tmpDirection.add(0.0f, -1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            tmpDirection.add(-1.0f, 0.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            tmpDirection.add(1.0f, 0.0f);
        }

        if (tmpDirection.x != 0.0f || tmpDirection.y != 0.0f) {

            tmpDirection.nor();

            map.resolveCollision(getPosition(), tmpDirection, speed);

            updateCameraPosition();
        }
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        batch.draw(textureRegion, camera.position.x, camera.position.y, size.x, size.y);
    }

    public void updateCameraPosition() {

        camera.position.set(getPosition().x, getPosition().y, 0.0f);
        camera.update();
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public void dispose() {}
}
